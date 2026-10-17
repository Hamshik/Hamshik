#pragma once
#include <Arduino.h>

class IoTServer;  // Forward declaration

class JavaIoTKit {
  String ssid;
  String password;
  String hostName;
  IoTServer& ioTServer;

 public:
  JavaIoTKit(String ssid, String password, String hostName);

  // Getter methods if IoTServer needs access to protected fields
  const String& getSsid() const { return ssid; }
  const String& getPassword() const { return password; }
  const String& getHostName() const { return hostName; }

  void begin();
  void loop();  
};