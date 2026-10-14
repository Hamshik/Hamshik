package in.hamshik.DNSHandler;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class EspVerifier {
    public record EspAckResponse(
            String status,
            String device,
            String message) {
    }

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    private static final Gson gson = new Gson();

    /**
     * Pings the ESP32 /ack endpoint and verifies if it returns the correct
     * signature.
     */
    public static boolean verifyAndParseEsp(String host, int port) {
        String url = "http://" + host + ":" + port + "/ack";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if status code is HTTP 200 OK
            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                // Decode the JSON into our Java record
                EspAckResponse ack = gson.fromJson(jsonBody, EspAckResponse.class);

                // Strict validation: Ensure it's actually an ESP32 and not a home router
                if (ack != null && "esp32".equalsIgnoreCase(ack.device())) {
                    System.out.println("Successfully verified ESP32! Message: " + ack.message());
                    return true;
                }
            }
        } catch (Exception e) {
            // Fails silently if the port is closed or it's a regular router web page
        }
        return false;
    }
}