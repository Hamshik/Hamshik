package in.hamshik;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WifiSender {

    public static void sendDataToESP32(String ip, String message) {
        try {
            // Construct the URL with the query parameter
            String urlString = "http://" + ip + "/data?msg=" + message.replace(" ", "%20");
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response from the ESP32
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response from ESP32: " + response.toString());
        } catch (Exception e) {
            System.out.println("Communication failed: " + e.getMessage());
        }
    }
}