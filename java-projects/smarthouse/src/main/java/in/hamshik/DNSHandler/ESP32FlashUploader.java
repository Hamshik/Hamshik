package in.hamshik.DNSHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

/**
 * Uploads a firmware .bin to the ESP32 via a plain HTTP POST (multipart/form-data).
 * The ESP32 exposes POST /update — same approach used by the Arduino IDE OTA web UI.
 * No WebSocket complexity; uses only the JDK's built-in java.net.http.HttpClient.
 */
public class ESP32FlashUploader {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT  = Duration.ofSeconds(120);

    /**
     * Uploads {@code binFilePath} to the ESP32 at {@code device} via HTTP POST /update.
     *
     * @return the success message from the ESP32 (e.g. "Update Success")
     * @throws IOException          if the file is missing, the request fails, or ESP32 reports an error
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public static String uploadBin(EspDevice device, String binFilePath)
            throws IOException, InterruptedException {

        Path firmwarePath = Path.of(binFilePath);
        if (!Files.isRegularFile(firmwarePath) || !Files.isReadable(firmwarePath)) {
            throw new IOException("Firmware file is missing or unreadable: " + binFilePath);
        }

        // Build the multipart body manually — JDK HttpClient doesn't have a multipart helper,
        // but it's just a few lines of boundary framing.
        String boundary = UUID.randomUUID().toString().replace("-", "");
        byte[] fileBytes  = Files.readAllBytes(firmwarePath);
        String fileName   = firmwarePath.getFileName().toString();

        // Multipart/form-data body:
        //   --<boundary>\r\n
        //   Content-Disposition: form-data; name="firmware"; filename="firmware.bin"\r\n
        //   Content-Type: application/octet-stream\r\n
        //   \r\n
        //   <binary data>
        //   \r\n--<boundary>--\r\n
        byte[] prefix = ("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"firmware\"; filename=\"" + fileName + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n"
                + "\r\n").getBytes();
        byte[] suffix = ("\r\n--" + boundary + "--\r\n").getBytes();

        byte[] body = new byte[prefix.length + fileBytes.length + suffix.length];
        System.arraycopy(prefix,    0, body, 0,                              prefix.length);
        System.arraycopy(fileBytes, 0, body, prefix.length,                  fileBytes.length);
        System.arraycopy(suffix,    0, body, prefix.length + fileBytes.length, suffix.length);

        String url = "http://" + device.host() + ":" + device.port() + "/update";

        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("ESP32 returned HTTP " + response.statusCode() + ": " + response.body());
        }

        // Parse the JSON response: {"status":"success","message":"Update Success"}
        try {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String status  = json.has("status")  ? json.get("status").getAsString()  : "";
            String message = json.has("message") ? json.get("message").getAsString() : response.body();

            if ("error".equalsIgnoreCase(status)) {
                throw new IOException("ESP32 OTA error: " + message);
            }
            return message; // "Update Success"
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new IOException("Unexpected response from ESP32: " + response.body(), e);
        }
    }
}
