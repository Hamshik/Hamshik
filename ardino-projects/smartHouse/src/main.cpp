#include <WiFi.h>
#include <WebServer.h>
#include <WebSocketsServer.h>
#include <ESPmDNS.h>

const char *ssid = nullptr;
const char *password = nullptr;
const char *hostName = "Hamshik";

WebServer server(80);          // Standard HTTP server on port 80
WebSocketsServer webSocket(81); // WebSocket server on port 81

// WebSocket event handler
void webSocketEvent(uint8_t num, WStype_t type, uint8_t* payload, size_t length) {
  switch(type) {
    case WStype_DISCONNECTED:
      Serial.printf("[%u] Disconnected!\n", num);
      break;
    case WStype_CONNECTED: {
        IPAddress ip = webSocket.remoteIP(num);
        Serial.printf("[%u] Connected from %s\n", num, ip.toString().c_str());
        // Send a welcome message to the connected Java client
        webSocket.sendTXT(num, "{\"status\":\"connected\",\"message\":\"Hello from ESP32 WebSocket\"}");
      }
      break;
    case WStype_TEXT:
      Serial.printf("[%u] Received text: %s\n", num, payload);
      
      // Example: Echo back or handle incoming JSON commands from your Java app
      if (strcmp((char*)payload, "toggle_light") == 0) {
        Serial.println("Action: Toggling light...");
        webSocket.sendTXT(num, "{\"status\":\"success\",\"action\":\"toggled\"}");
      }
      break;
    default:
      break;
  }
}

void serverSetup(){
    // Start standard web server routes if needed
  server.on("/", []() {
    server.send(200, "text/plain", "ESP32 Web & WebSocket Server Active");
  });
  // ADD THIS ROUTE FOR JAVA'S /ack REQUEST:
  const std::string ackJson = R"({
    "status": "ready",
    "device": "esp32",
    "hostName": ")" + std::string(hostName) + R"(",
    "message": "Hello, Java"})";
  
  server.on("/ack", [ackJson]() {
    server.send(200, "application/json", ackJson.c_str());
  });
}

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi connected.");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // 2. INITIALIZE mDNS AND ADVERTISE THE HTTP SERVICE
  if (!MDNS.begin(hostName)) {
    Serial.println("Error setting up MDNS responder!");
  } else {
    Serial.println("mDNS responder started: http://" + String(hostName) + ".local");
    // This tells Java's JmDNS listener _http._tcp.local. that this device exists
    MDNS.addService("http", "tcp", 80);
  }

  serverSetup();
  server.begin();

  webSocket.begin();
  webSocket.onEvent(webSocketEvent);
  Serial.println("WebSocket server started on port 81.");
}

void loop() {
  webSocket.loop(); 
  server.handleClient();
}