#include <WiFi.h>
#include <ESPmDNS.h> // Required so Java can find it via mDNS!

// --- REPLACE WITH YOUR HOME ROUTER CREDENTIALS ---
const char *ssid = nullptr;
const char *password = nullptr;
const char *hostName = "Hamshik";

// Set web server port number to 80
WiFiServer server(80);

// Most ESP32 DevKits use GPIO 2 for the onboard blue LED
const int ledPin = 2;

void sendJsonResponse(WiFiClient &client, const char *status, const char *body)
{
  client.print("HTTP/1.1 ");
  client.println(status);
  client.println("Content-Type: application/json");
  client.print("Content-Length: ");
  client.println(strlen(body));
  client.println("Connection: close");
  client.println();
  client.print(body);
  client.stop();
}

void setup()
{
  Serial.begin(115200);

  // Initialize the output variable as an output
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  // Connect to Wi-Fi network
  Serial.print("Connecting to "); Serial.println(ssid);

  WiFi.setHostname(hostName);

  WiFi.onEvent([](WiFiEvent_t event, WiFiEventInfo_t info) {
    if (event == ARDUINO_EVENT_WIFI_STA_DISCONNECTED) {
      Serial.printf("Disconnect reason: %u\n",
                    info.wifi_sta_disconnected.reason);
    }
  });
  WiFi.begin(ssid, password);

  const unsigned long start = millis();
  while (WiFi.status() != WL_CONNECTED && millis() - start < 20000) {
    delay(500);
    Serial.print(".");
  }

  if (WiFi.status() != WL_CONNECTED) {
    Serial.printf("\nWi-Fi failed; status=%d\n", WiFi.status());
    return;
  }

  // Print local IP address
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  if (!MDNS.begin("esp32")) {
    Serial.println("Error setting up MDNS responder!");
  }
  else {
    Serial.println("mDNS responder started: http://esp32.local");
    // Add service to mDNS so _http._tcp discovery works
    MDNS.addService("http", "tcp", 80);
  }

  // Start the server
  server.begin();
  Serial.println("Web server started.");
}

void loop()
{
  WiFiClient client = server.available();
  if (!client)
    return;

  Serial.println("New Client Connected.");
  client.setTimeout(2000);

  String requestLine = client.readStringUntil('\n');
  requestLine.trim();

  int pathStart = requestLine.indexOf(' ');
  int pathEnd = pathStart < 0 ? -1 : requestLine.indexOf(' ', pathStart + 1);
  if (pathStart < 0 || pathEnd < 0) {
    sendJsonResponse(client, "400 Bad Request", "{\"status\":\"error\"}");
    return;
  }

  String path = requestLine.substring(pathStart + 1, pathEnd);
  int queryStart = path.indexOf('?');
  if (queryStart >= 0) {
    path = path.substring(0, queryStart);
  }

  if (path == "/ack") {
    Serial.println("Sending ACK to client...");
    sendJsonResponse(client, "200 OK",
                     "{\"status\":\"ready\",\"device\":\"esp32\",\"message\":\"ESP32 is ready\"}");
  } else if (path == "/data") {
    Serial.println("Received data request from Java client.");
    sendJsonResponse(client, "200 OK",
                     "{\"status\":\"received\",\"device\":\"esp32\",\"message\":\"Data received\"}");
  } else {
    sendJsonResponse(client, "404 Not Found", "{\"status\":\"not_found\"}");
  }
}