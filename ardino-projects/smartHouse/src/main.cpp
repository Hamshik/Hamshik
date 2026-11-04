#include <JavaIoTKit.hpp>
#include <CloudVar.hpp>

JavaIoTKit ioTKit("Anugraha", "Coolvinu6395", "Hamshik");
CloudVar<int> temp("temperature", 100, Permission::RO);
CloudVar<int> ramdonSense("randSense", 1, Permission::RW);

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