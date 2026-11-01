#pragma once

#include <cstddef>
#include <AsyncWebSocket.h>
#include "CloudVar.hpp"

// Forward declaration to avoid circular include issues
class JavaIoTKit;

class IoTServer
{
    const String ROOT_PATH = "/ws";
    const uint8_t PORT = 80;
    AsyncWebServer server{PORT};
    AsyncWebSocket ws{ROOT_PATH};
    JavaIoTKit& parentKit; // Reference back to the main kit
    const String ssid;
    const String password;
    const String hostName;
    AsyncWebSocketClient* otaClient = nullptr;
    size_t totalBytesReceived = 0;
    bool otaUpdateStarted = false;

    void handleWebSocketMessage(uint8_t *data, size_t len);
    void onWebSocketEvent(AsyncWebSocket *server, AsyncWebSocketClient *client, AwsEventType type, void *arg, uint8_t *data, size_t len);
    void writeBin(void* arg, uint8_t* data, size_t len, AsyncWebSocketClient* client);
public:
    IoTServer(JavaIoTKit& kit);
    void publish(const CloudVarBase& val);
    void serverSetup();
    void begin();
    void loop();
};