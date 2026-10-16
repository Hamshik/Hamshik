#include "WifiSender.hpp"
#include "IotServer/IoTServer.hpp"
#include "JavaIoTKit.hpp"

WifiSender::WifiSender(IoTServer& server, JavaIoTKit& kit): server(server), kit(kit) {}
