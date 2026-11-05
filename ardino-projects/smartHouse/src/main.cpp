#include <JavaIoTKit.hpp>
#include <CloudVar.hpp>

JavaIoTKit ioTKit("********", "********", "Hamshik");
CloudVar<int> temp("temperature", 100, Permission::RO);
CloudVar<bool> ramdonSense("randSense", true, Permission::RW);

void setup() {
  Serial.begin(115200);
  ioTKit.begin();
  Serial.println("Async Web & WebSocket server started on port 80.");
}

void loop() {
  ioTKit.loop();
  temp++;
  delay(1000);
}