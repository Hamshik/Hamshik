#include <WiFi.h>
#include <ESPmDNS.h> // Required so Java can find it via mDNS!

// --- REPLACE WITH YOUR HOME ROUTER CREDENTIALS ---
const char *ssid = "INNOVATION HUB";
const char *password = "prsc$123";
const char *hostName = "Hamshik";

// Set web server port number to 80
WiFiServer server(80);

// Variable to store the HTTP request
String header;

// Most ESP32 DevKits use GPIO 2 for the onboard blue LED
const int ledPin = 2;
String ledState = "OFF";

void setup()
{
  Serial.begin(115200);

  // Initialize the output variable as an output
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  // Connect to Wi-Fi network
  Serial.print("Connecting to "); Serial.println(ssid);

  WiFi.setHostname(hostName);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
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

void loop(){
  WiFiClient client = server.available();

  if (client) {
    Serial.println("New Client Connected.");
    String clientData = "";

    while (client.connected()) {
      if (client.available()){
        char c = client.read();
        header += c;
      }
    }

    // --- CHECK WHAT THE CLIENT ASKED FOR ---
    if (header.indexOf("GET /ack") >= 0)
    {
      // The Java app explicitly asked for verification/acknowledgment
      Serial.println("Sending ACK to client...");
      client.println("HTTP/1.1 200 OK");
      client.println("Content-Type: application/json");
      client.println("Connection: close");
      client.println();
      client.println(("{\"name\":\"" + std::string(hostName) + "\"\"device\": \"esp32-devkit\", \"status\": \"ready\"}").c_str());
    }
  }
}