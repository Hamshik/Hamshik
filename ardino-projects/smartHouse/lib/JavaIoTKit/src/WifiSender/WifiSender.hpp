
#include "IotServer/IoTServer.hpp"
#include "JavaIoTKit.hpp"
class WifiSender {
    IoTServer &server;
    JavaIoTKit &kit;
public:
    WifiSender(IoTServer&, JavaIoTKit&);
};