package in.hamshik.DNSHandler;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public final class EspVerifier {
    private static final System.Logger LOGGER = System.getLogger(EspVerifier.class.getName());
    private static final int CONNECT_TIMEOUT_MILLIS = 1_000;
    private static final int READ_TIMEOUT_MILLIS = 1_000;

    public record EspAckResponse(
            String status,
            String device,
            String message,
            String hostName
        ) {
    }

    private final Gson gson = new Gson();
    public volatile EspAckResponse ack;

    /**
     * Pings the ESP32 /ack endpoint and verifies if it returns the correct
     * signature.
     */
    public boolean verifyAndParseEsp(String host, int port) {
        ack = null;
        HttpURLConnection connection = null;
        try {
            URI uri = new URI("http", null, host, port, "/ack", null, null);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);

            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                LOGGER.log(System.Logger.Level.DEBUG,
                        "ESP verification returned HTTP {0} from {1}:{2}",
                        statusCode, host, port);
                return false;
            }

            String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            EspAckResponse parsedAck = gson.fromJson(responseBody, EspAckResponse.class);
            if (parsedAck == null || parsedAck.device() == null
                    || !"esp32".equalsIgnoreCase(parsedAck.device().strip())) {
                LOGGER.log(System.Logger.Level.DEBUG,
                        "ESP verification returned an unexpected response from {0}:{1}",
                        host, port);
                return false;
            }
            ack = parsedAck;
            return true;
        } catch (IOException | URISyntaxException | IllegalArgumentException e) {
            LOGGER.log(System.Logger.Level.DEBUG, "Unable to verify ESP at " + host + ":" + port, e);
        } catch (JsonParseException e) {
            LOGGER.log(System.Logger.Level.WARNING, "ESP returned invalid /ack JSON from " + host + ":" + port, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
        return false;
    }
}