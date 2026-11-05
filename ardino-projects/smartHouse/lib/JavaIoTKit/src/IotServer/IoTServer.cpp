#include "IoTServer.hpp"

#include <ArduinoJson.h>
#include <AsyncTCP.h>
#include <ESPAsyncWebServer.h>
#include <ESPmDNS.h>
#include <Update.h>
#include <WiFi.h>

#include <cstddef>
#include <string>

#include "ArduinoJson/Deserialization/DeserializationError.hpp"
#include "ArduinoJson/Document/JsonDocument.hpp"
#include "ArduinoJson/Json/JsonSerializer.hpp"
#include "ArduinoJson/Object/JsonObject.hpp"
#include "AsyncWebSocket.h"
#include "CloudVar.hpp"
#include "HardwareSerial.h"
#include "JavaIoTKit.hpp"
#include "VarRegister/Cloud.hpp"

// Initialize the parent reference properly
IoTServer::IoTServer(JavaIoTKit& kit)
    : parentKit(kit), ssid(kit.getSsid()), password(kit.getPassword()), hostName(kit.getHostName()) {}

// ── Private helpers ───────────────────────────────────────────────────────────

auto IoTServer::jsonResponse(AsyncWebServerRequest* request, bool success, const String& message) {
  JsonDocument doc;
  doc["status"] = success ? "success" : "error";
  doc["message"] = message.isEmpty() ? (success ? "ok" : "error") : message;
  String body;
  serializeJson(doc, body);
  return request->beginResponse(success ? 200 : 400, "application/json", body);
}

void IoTServer::handleOtaUpload(AsyncWebServerRequest* /*request*/, const String& filename, size_t index, uint8_t* data,
                                size_t len, bool final) {
  if (index == 0) {
    Serial.printf("OTA start: %s\n", filename.c_str());
    if (!Update.begin(UPDATE_SIZE_UNKNOWN)) Update.printError(Serial);
  }

  if (Update.isRunning() && Update.write(data, len) != len) Update.printError(Serial);

  if (final) {
    if (Update.end(true))
      Serial.printf("OTA done: %u bytes\n", index + len);
    else
      Update.printError(Serial);
  }
}

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

    case WS_EVT_DISCONNECT:
      if (otaClient == client && otaUpdateStarted) {
        Update.abort();
        otaClient = nullptr;
        totalBytesReceived = 0;
        otaUpdateStarted = false;
      }
      Serial.printf("[%u] Disconnected!\n", client->id());
      break;

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

    if (request->hasParam("msg")) {
      message = request->getParam("msg")->value();
      Serial.println("Received message from Java: " + message);
      JsonDocument req;
      DeserializationError error = deserializeJson(req, message);

      if (error) {
        request->send(400, "text/plain", error.c_str());
        return;
      }

      auto response = CloudHandler::dispatchReq(req);
      String json;
      serializeJson(response, json);
      request->send(200, "application/json", json);
      Serial.printf("Seneded the var json: %s\n", json.c_str());
      return;
    }
  });

  // 4. HTTP OTA firmware update — POST /update with multipart/form-data field "firmware"
  server.on(
      "/update", HTTP_POST,
      [this](AsyncWebServerRequest* request) {
        bool ok = !Update.hasError();
        auto* resp = jsonResponse(request, ok, ok ? "Update Success" : "Firmware validation failed");
        resp->addHeader("Connection", "close");
        request->send(resp);
        if (ok) {
          delay(300);
          ESP.restart();
        }
      },
      [this](AsyncWebServerRequest* request, String filename, size_t index, uint8_t* data, size_t len, bool final) {
        handleOtaUpload(request, filename, index, data, len, final);
      });

  server.addHandler(&ws);
}

void IoTServer::begin() {
  WiFi.begin(ssid, password);
  WiFi.setSleep(false);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nWiFi connected.");
  Serial.print("IP address: http://");
  Serial.println(WiFi.localIP());

  if (!MDNS.begin(hostName))
    Serial.println("Error setting up mDNS responder!");
  else {
    MDNS.addService("http", "tcp", PORT);
    MDNS.addServiceTxt("http", "tcp", "path", ROOT_PATH);
    Serial.println("mDNS responder started: http://" + hostName + ".local");
  }

  serverSetup();
  server.begin();
  CloudHandler::setChangeCallback([this](const CloudVarBase& variable) { publish(variable); });
}

void IoTServer::loop() { ws.cleanupClients(); }

void IoTServer::publish(const CloudVarBase& val) {
  JsonDocument doc;
  doc["msg"] = ReqTy::SET_VAR;
  doc["VarName"] = val.getName().c_str();
  JsonObject obj = doc.as<JsonObject>();
  val.setValJson(obj, "Value");
  String json;
  serializeJson(doc, json);
  ws.textAll(json);
}
