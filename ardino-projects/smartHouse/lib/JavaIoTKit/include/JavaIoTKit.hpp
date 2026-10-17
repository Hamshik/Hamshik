#pragma once
#include <Arduino.h>

class IoTServer;  // Forward declaration

class JavaIoTKit {
  String ssid = "";
  String password = "";
  String hostName = "";
  IoTServer& ioTServer;

 public:
  JavaIoTKit(String, String, String);

  // Getter methods if IoTServer needs access to protected fields
  String getSsid() const { return ssid; }
  String getPassword() const { return password; }
  String getHostName() const { return hostName; }

  void begin();
  void loop();  
};