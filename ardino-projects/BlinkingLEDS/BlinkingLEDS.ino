// Blinking LEDs
void setup() {
  pinMode(11, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(2, OUTPUT);
}

void loop() {
  //For red leds to glow
  digitalWrite(2,HIGH);
  delay(100);
  digitalWrite(2,LOW);
  delay(100);
  digitalWrite(3,HIGH);
  delay(100);
  digitalWrite(3,LOW);
  delay(100);
  digitalWrite(4,HIGH);
  delay(100);
  digitalWrite(4,LOW);
  delay(100);
  digitalWrite(5,HIGH);
  delay(100);
  digitalWrite(5,LOW);
  delay(100);
  digitalWrite(6,HIGH);
  delay(100);
  digitalWrite(6,LOW);
  delay(100);
  digitalWrite(7,HIGH);
  delay(100);
  digitalWrite(7,LOW);
  delay(100);
  digitalWrite(8,HIGH);
  delay(100);
  digitalWrite(8,LOW);
  delay(100);
  digitalWrite(9,HIGH);
  delay(100);
  digitalWrite(9,LOW);
  delay(100);
  digitalWrite(10,HIGH);
  delay(100);
  digitalWrite(10,LOW);
  delay(100);
  digitalWrite(11,HIGH);
  delay(100);
  digitalWrite(11,LOW);
  delay(100);
  Serial.println("Completed");
}