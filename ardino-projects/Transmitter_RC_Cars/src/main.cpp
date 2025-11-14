#include <Arduino.h>
#include <SoftwareSerial.h>

// Motor-driver joystick input pins
#define m1 2
#define m2 3
#define m3 4
#define m4 5

// Bluetooth
SoftwareSerial BT(6, 7);  // RX, TX

String lastCmd = "";   // Prevent duplicate sending

void setup() {
  pinMode(m1, INPUT);
  pinMode(m2, INPUT);
  pinMode(m3, INPUT);
  pinMode(m4, INPUT);

  Serial.begin(9600);
  BT.begin(9600);

  Serial.println("Transmitter Ready!");
}

void loop() {

  int s1 = digitalRead(m1);
  int s2 = digitalRead(m2);
  int s3 = digitalRead(m3);
  int s4 = digitalRead(m4);

  String cmd = "S";   // Default STOP

  // ---------------------------------------------------
  //               MOVEMENT PATTERN DETECTION
  // ---------------------------------------------------

  // ---- FORWARD ----
  // m1=1 m2=0 m3=1 m4=0
  if (s1==1 && s2==0 && s3==1 && s4==0)
    cmd = "F";

  // ---- BACKWARD ----
  // m1=0 m2=1 m3=0 m4=1
  else if (s1==0 && s2==1 && s3==0 && s4==1)
    cmd = "B";

  // ---- RIGHT ----
  // m1=1 m2=0 m3=1 m4=1
  else if (s1==1 && s2==0 && s3==1 && s4==1)
    cmd = "R";

  // ---- LEFT ----
  // m1=1 m2=1 m3=0 m4=1
  else if (s1==1 && s2==1 && s3==0 && s4==1)
    cmd = "L";

  // ---- ROTATE CLOCKWISE ----
  // m1=0 m2=1 m3=0 m4=1  (opposite sticks)
  else if (s1==0 && s2==1 && s3==0 && s4==1)
    cmd = "C";

  // ---- ROTATE ANTI-CLOCKWISE ----
  // m1=1 m2=0 m3=1 m4=0  (opposite sticks)
  else if (s1==1 && s2==0 && s3==1 && s4==0)
    cmd = "A";

  // ---------------------------------------------------
  //       SEND ONLY IF COMMAND CHANGES
  // ---------------------------------------------------
  if (cmd != lastCmd) {
    BT.print(cmd);
    Serial.print("TX: ");
    Serial.println(cmd);
    lastCmd = cmd;
  }

  delay(50); // smooth but responsive
}
