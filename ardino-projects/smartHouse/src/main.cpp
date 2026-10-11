#include <Arduino.h>
#include <WiFi.h>
#include <WebServer.h>

#define TRIG_PIN 5
#define ECHO_PIN 18

// Set up your Access Point credentials
const char* ssid = "ESP32_AP_Network";

WebServer server(80);

void handleRoot() {
  server.send(200, "text/plain", "ESP32 AP Mode is online!");
}

// Endpoint to receive data sent FROM the Java app
void handleReceiveFromJava() {
  if (server.hasArg("msg")) {
    String dataFromJava = server.arg("msg");
    Serial.println("Received from Java: " + dataFromJava);
    
    // Send a response back (Bidirectional acknowledgment)
    server.send(200, "text/plain", "ACK from ESP32: Data processed successfully");
  } else {
    server.send(400, "text/plain", "Missing 'msg' parameter");
  }
}

void setup() {
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  Serial.begin(115200);
  Serial.begin(115200);
  WiFi.begin("Wokwi-GUEST", "");
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(250);
    Serial.print(".");
  }
  
  Serial.println("\nConnected! IP: " + WiFi.localIP().toString());

  server.on("/", handleRoot);
  server.begin();
}

void loop() {
  // Send 10µs trigger pulse
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  // Measure pulse duration (timeout 30ms = ~5m max)
  long duration = pulseIn(ECHO_PIN, HIGH, 30000);

  // Speed of sound = 343 m/s → distance = duration * 0.0343 / 2
  float distance = (duration * 0.0343 / 2.0) - 3.47;

  if (duration == 0) {
    server.send(400, "text/plain", "Not Working");
  } else {
    server.send(200, "text/plain", std::to_string(distance).c_str());
    delay(100);
  }

  server.handleClient();
}