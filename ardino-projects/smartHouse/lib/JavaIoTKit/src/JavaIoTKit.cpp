#include "IoTServer.hpp"
#include "JavaIoTKit.hpp"

JavaIoTKit::JavaIoTKit(String ssid, String password, String hostName):
    hostName(hostName), password(password), ssid(ssid), ioTServer(*new IoTServer(*this)) {}
void JavaIoTKit::begin() { ioTServer.begin(); }
void JavaIoTKit::loop()  { ioTServer.loop(); }
