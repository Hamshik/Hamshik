#include <JavaIoTKit.hpp>

JavaIoTKit ioTKit("Anugraha", "Coolvinu6395", "Hamshik");

void setup() {
  Serial.begin(115200);
  ioTKit.begin();
  Serial.println("Async Web & WebSocket server started on port 80.");
}

void loop() { ioTKit.loop(); }
