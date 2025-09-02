#include <LiquidCrystal.h>
#include <DHT11.h>
#define RainSensor A0
int led1 = 2;
int led2 = 3;
int led3 = 4;
int led4 = 5;
int AnoWind = 6;
int DHT = 7;
int ligth = 9;
int buzz = 13;
int DHTv = 0;
int Rainv = 0;
int Anomov = 0;
void setup() {
  pinMode(RainSensor, INPUT);
  // pinMode()
}

void loop() {
  // put your main code here, to run repeatedly:

}
