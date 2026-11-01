#include <Arduino.h>
#include <ArduinoJson.h>  // Ensure ArduinoJson library is included
#include <ESPAsyncWebServer.h>
#include <Update.h>

#include "AsyncWebSocket.h"
#include "IotServer/IoTServer.hpp"

void sendJsonResponse(AsyncWebSocketClient* client, bool success, const String& message) {
  if (!client) return;

  JsonDocument doc;
  doc["status"] = success ? "success" : "error";
  doc["message"] = message;

  String jsonString;
  serializeJson(doc, jsonString);
  client->text(jsonString);
}

void IoTServer::writeBin(void* arg, uint8_t* data, size_t len, AsyncWebSocketClient* client) {
  AwsFrameInfo* info = static_cast<AwsFrameInfo*>(arg);
  if (info->message_opcode != WS_BINARY && info->message_opcode != WS_CONTINUATION) return;

  // index is per frame; num identifies the first fragment of the message.
  if (info->num == 0 && info->index == 0) {
    if (otaUpdateStarted) Update.abort();
    otaClient = client;
    totalBytesReceived = 0;
    otaUpdateStarted = false;
    if (!Update.begin(UPDATE_SIZE_UNKNOWN)) {
      Update.printError(Serial);
      sendJsonResponse(client, false, "Failed to begin OTA update");
      otaClient = nullptr;
      return;
    }
    otaUpdateStarted = true;
    sendJsonResponse(client, true, "OTA update started");
  }

  if (!otaUpdateStarted || otaClient != client) return;

  if (Update.write(data, len) != len) {
    sendJsonResponse(client, false, "Flash write failed at chunk");
    Update.abort();
    otaClient = nullptr;
    totalBytesReceived = 0;
    otaUpdateStarted = false;
    return;
  } else {
    totalBytesReceived += len;
  }

  if (info->final && info->index + len == info->len) {
    if (Update.end(true)) {
      sendJsonResponse(client, true, "Update Success");
      otaClient = nullptr;
      totalBytesReceived = 0;
      otaUpdateStarted = false;
      delay(500);
      ESP.restart();
    } else {
      Update.printError(Serial);
      sendJsonResponse(client, false, "Firmware validation or flash finalize failed");
      Update.abort();
      otaClient = nullptr;
      totalBytesReceived = 0;
      otaUpdateStarted = false;
    }
  }
}