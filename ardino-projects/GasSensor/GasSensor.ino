#include <Arduino.h>
#define AnalogGasSensor A0
#define DigitalGasSensor 2
int buzzer = 13;
int DigitalSensorValue = 0;
int AnalogSensorValue = 0;
void setup()
{
 pinMode(AnalogGasSensor,INPUT);
 pinMode(DigitalGasSensor,INPUT);
 pinMode(buzzer,OUTPUT);
 Serial.begin(9600);
}
void loop() 
{
 AnalogSensorValue = analogRead(AnalogGasSensor);
 DigitalSensorValue = digitalRead(DigitalGasSensor);
 Serial.print("Analog Value: ");
 Serial.println(AnalogSensorValue);
 Serial.print("Digital Value: ");
 Serial.println(DigitalSensorValue);
 if (AnalogSensorValue >= 50) {
  for (int i = 0; i < 4; i++){
    tone(buzzer, 500);
    delay(600);
    noTone(buzzer);
    delay(600);
  }
 } else {
  noTone(buzzer);
  delay(100);
 } 
 delay(200);
}
