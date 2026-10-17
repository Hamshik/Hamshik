package in.hamshik.WifiHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import in.hamshik.DNSHandler.WiFiScanner.EspDevice;

public class WifiSender {

    public static void sendDataToESP32(EspDevice device, String message) {
        HttpURLConnection conn = null;
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            URL url = URI.create("http://" + device.host() + "/data?msg=" + encodedMessage).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            int status = conn.getResponseCode();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    status >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("ESP32 HTTP " + status + ": " + response);
            }
        } catch (Exception e) {
            System.out.println("Communication failed: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}