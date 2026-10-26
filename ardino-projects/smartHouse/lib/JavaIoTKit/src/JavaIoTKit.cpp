#include "JavaIoTKit.hpp"

#include "IotServer/IoTServer.hpp"

JavaIoTKit::JavaIoTKit(String ssid, String password, String hostName)
    : ssid(ssid), password(password), hostName(hostName), ioTServer(*new IoTServer(*this)) {}
void JavaIoTKit::begin() { ioTServer.begin(); }
void JavaIoTKit::loop() { ioTServer.loop(); }
