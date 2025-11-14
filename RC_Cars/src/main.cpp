#include <Arduino.h>
#include <SoftwareSerial.h>

#define motor1 4
#define motor2 6
#define motor3 7
#define motor4 8
#define buzz 13
#define green 12
#define red 11
#define echo 3
#define trig 5
#define ena 9
#define enb 10

SoftwareSerial BT(2, 4); // RX, TX

void startInit(String read);
void motorRun(int m1, int m2, int m3, int m4, String PLACE_HOLDER = "");
boolean isToStop();

void setup() {

  pinMode(motor1, OUTPUT);
  pinMode(motor2, OUTPUT);
  pinMode(motor3, OUTPUT);
  pinMode(motor4, OUTPUT);
  pinMode(buzz, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(red, OUTPUT);
  pinMode(echo, INPUT);
  pinMode(trig, OUTPUT);
  pinMode(ena, OUTPUT);
  pinMode(enb, OUTPUT);

  Serial.begin(9600);
  BT.begin(9600);

  Serial.println("Bluetooth Receiver Ready!");

  digitalWrite(red, HIGH);
  digitalWrite(green, LOW);
}

void startInit(String read) {

  Serial.print("Received CMD: ");
  Serial.println(read);

  // --- Obstacle stop ---
  if (isToStop()) {
    motorRun(0, 0, 0, 0);
    digitalWrite(buzz, HIGH);
    delay(150);
    digitalWrite(buzz, LOW);
    Serial.println("⚠ Obstacle detected — STOPPING");
    return;
  }

  // --- Motor commands ---
  if (read == "F") {
    Serial.println("→ Forward");
    motorRun(1, 0, 1, 0);

  } else if (read == "B") {
    Serial.println("← Backward");
    motorRun(0, 1, 0, 1);

  } else if (read == "L") {
    Serial.println("↺ Left");
    motorRun(0, 1, 1, 0);

  } else if (read == "R") {
    Serial.println("↻ Right");
    motorRun(1, 0, 0, 1);

  } else if (read == "C") {
    Serial.println("⟳ Rotate Clockwise");
    motorRun(1, 0, 0, 1);

  } else if (read == "A") {
    Serial.println("⟲ Rotate Anti-Clockwise");
    motorRun(0, 1, 1, 0);

  } else if (read == "S") {
    Serial.println("■ Stop");
    motorRun(0, 0, 0, 0);
  }
}

void motorRun(int m1, int m2, int m3, int m4, String PLACE_HOLDER) {

  // m1 & m3 are digital direction pins
  digitalWrite(motor1, m1);
  digitalWrite(motor3, m3);

  // m2 & m4 are on PWM pins – correct for speed control
  analogWrite(motor2, m2 ? 255 : 0);
  analogWrite(motor4, m4 ? 255 : 0);

  // Enable channels fully
  analogWrite(ena, 255);
  analogWrite(enb, 255);
}

boolean isToStop() {

  long duration;
  double distance;

  digitalWrite(trig, LOW);
  delayMicroseconds(2);

  digitalWrite(trig, HIGH);
  delayMicroseconds(10);

  digitalWrite(trig, LOW);

  duration = pulseIn(echo, HIGH, 25000); // 25ms timeout
  distance = duration * 0.034 / 2;

  Serial.print("Distance: ");
  Serial.println(distance);

  if (distance > 1 && distance < 12) {
    return true;
  }

  return false;
}

void loop() {

  if (BT.available()) {

    char c = BT.read();
    String s = String(c);

    Serial.print("Bluetooth Received: ");
    Serial.println(s);

    digitalWrite(red, LOW);
    digitalWrite(green, HIGH);

    startInit(s);
  }

  if (Serial.available()) {
    char c = Serial.read();
    String s = String(c);

    BT.print(s);
    startInit(s);
  }

  if (!BT.available() && !Serial.available()) {
    digitalWrite(red, HIGH);
    digitalWrite(green, LOW);
  }

  delay(40);
}
