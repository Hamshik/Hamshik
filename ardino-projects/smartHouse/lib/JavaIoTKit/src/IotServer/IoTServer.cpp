#include "IoTServer.hpp"

#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <ESPmDNS.h>
#include <WiFi.h>

#include <string>

#include "JavaIoTKit.hpp"

// Initialize the parent reference properly
IoTServer::IoTServer(JavaIoTKit& kit)
    : parentKit(kit), ssid(kit.getSsid()), password(kit.getPassword()), hostName(kit.getHostName()) {}

void IoTServer::handleWebSocketMessage(uint8_t* data, size_t len) {
  std::string message(reinterpret_cast<char*>(data), len);

  if (message == "toggle_light") {
    Serial.println("Action: Toggling light...");
    ws.textAll("{\"status\":\"success\",\"action\":\"toggled\"}");
  }
}

void IoTServer::onWebSocketEvent(AsyncWebSocket* server, AsyncWebSocketClient* client, AwsEventType type, void* arg,
                                 uint8_t* data, size_t len) {
  switch (type) {
    case WS_EVT_CONNECT:
      Serial.printf("[%u] Connected from %s\n", client->id(), client->remoteIP().toString().c_str());
      client->text("{\"status\":\"connected\",\"message\":\"Hello from ESP32 WebSocket\"}");
      break;

    case WS_EVT_DISCONNECT: Serial.printf("[%u] Disconnected!\n", client->id()); break;

    case WS_EVT_DATA: {
      AwsFrameInfo* info = reinterpret_cast<AwsFrameInfo*>(arg);
      if (info->final && info->index == 0 && info->len == len && info->opcode == WS_TEXT) {
        Serial.printf("[%u] Received text: %.*s\n", client->id(), (int)len, data);
        handleWebSocketMessage(data, len);
      }
      break;
    }
    case WS_EVT_PONG:
    case WS_EVT_ERROR: break;
  }
}

void IoTServer::serverSetup() {
  // 1. Root route
  server.on("/", HTTP_GET, [](AsyncWebServerRequest* request) {
    request->send(200, "text/plain", "ESP32 Web & WebSocket Server Active");
  });

  // 2. JSON Acknowledgment route for Java app discovery
  String ackJson = "{\n";
  ackJson += "  \"status\": \"ready\",\n";
  ackJson += "  \"device\": \"esp32\",\n";
  ackJson += "  \"hostName\": \"" + hostName + "\",\n";
  ackJson += "  \"message\": \"Hello, Java\"\n";
  ackJson += "}";

  server.on("/ack", HTTP_GET,
            [ackJson](AsyncWebServerRequest* request) { request->send(200, "application/json", ackJson); });

  // 3. Attach modern WebSocket event handler
  ws.onEvent([this](AsyncWebSocket* server, AsyncWebSocketClient* client, AwsEventType type, void* arg, uint8_t* data,
                    size_t len) { this->onWebSocketEvent(server, client, type, arg, data, len); });

  server.on("/data", HTTP_GET, [](AsyncWebServerRequest* request) {
    String message = "";

    // Check if the query parameter "msg" exists and extract it
    if (request->hasParam("msg")) {
      message = request->getParam("msg")->value();
      Serial.println("Received message from Java: " + message);
    }

    // Always send a response back so the client connection closes cleanly
    request->send(200, "text/plain", "ESP32 received: " + message);
  });

  server.addHandler(&ws);
}

void IoTServer::begin() {
  WiFi.begin(ssid, password);
  WiFi.setSleep(false);  // Keeps Wi-Fi radio fully awake for Linux ARP/ping

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi connected.");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // Initialize mDNS
  if (!MDNS.begin(hostName))
    Serial.println("Error setting up MDNS responder!");
  else {
    Serial.println("mDNS responder started: http://" + hostName + ".local");
    MDNS.addService("http", "tcp", PORT);
    MDNS.addServiceTxt("http", "tcp", "path", ROOT_PATH);  // Advertise the path
  }
  serverSetup();
  server.begin();
}

void IoTServer::loop() { ws.cleanupClients(); }
