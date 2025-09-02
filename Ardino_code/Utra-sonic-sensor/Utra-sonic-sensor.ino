// #define IR1 A0
#define IR2 A1
#define IR3 A2
#define IR4 A3
// #define IR5 A4
#define moto1 2
#define moto2 3
#define moto3 4
#define moto4 5
#define but1 6
#define but2 7
#define but3 8

const int m1 = moto1;
const int m2 = moto2;
const int m3 = moto3;
const int m4 = moto4;
const int buzz = 13;
const int f = 300;
const int n = 301;
int IR1v = 0;
int IR2v = 0;
int IR3v = 0;
int IR4v = 0;
int IR5v = 0;
int but1v = 0;
int but2v = 0;
int but3v = 0;
int supbuts = 9;
void setup() {
  // pinMode(IR1, INPUT);
  pinMode(IR2, INPUT);
  pinMode(IR3, INPUT);
  pinMode(IR4, INPUT);
  // pinMode(IR5, INPUT);
  pinMode(moto1, OUTPUT);
  pinMode(moto2, OUTPUT);
  pinMode(moto3, OUTPUT);
  pinMode(moto4, OUTPUT);
  pinMode(but1, INPUT);
  pinMode(but2, INPUT);
  pinMode(but3, INPUT);
  Serial.begin(9600);
}
void forward() {
  digitalWrite(m1, HIGH);
  digitalWrite(m3, HIGH);
  digitalWrite(m2, LOW);
  digitalWrite(m4, LOW);
}
void right() {
  digitalWrite(m3, HIGH);
  digitalWrite(m4, LOW);
  digitalWrite(m2, LOW);
  digitalWrite(m1, LOW);
}
void left() {
  digitalWrite(m1, HIGH);
  digitalWrite(m4, LOW);
  digitalWrite(m2, LOW);
  digitalWrite(m3, LOW);
}
void backward() {
  digitalWrite(m2, HIGH);
  digitalWrite(m4, HIGH);
  digitalWrite(m3, LOW);
  digitalWrite(m1, LOW);
}
void stop() {
  digitalWrite(m1, LOW);
  digitalWrite(m2, LOW);
  digitalWrite(m3, LOW);
  digitalWrite(m4, LOW);
}
void buzzs() {
  for (int i = 0; i < 2; i++) {
    digitalWrite(buzz, HIGH);
    delay(500);
    digitalWrite(buzz, LOW);
  }
}
void loop() {
  analogWrite(supbuts,200);
  // int IR1v = analogRead(IR1);
  int IR2v = analogRead(IR2);
  int IR3v = analogRead(IR3);
  int IR4v = analogRead(IR4);
  // int IR5v = analogRead(IR5);
  int but1v = analogRead(but1);
  int but2v = analogRead(but2);
  int but3v = analogRead(but3);
  // Serial.print("Value from IR1: ");
  // Serial.println(IR1v);
  // Serial.print("Value from IR2: ");
  Serial.print(IR2v);
  // Serial.print("Value from IR3: ");
  Serial.print(" ");
  Serial.print(IR3v);
  // Serial.print("Value from IR4: ");
  Serial.print(" ");
  Serial.println(IR4v);
  // Serial.print("Value from IR5: ");
  // Serial.println(IR5v);
  // Serial.println("buttons");
  // Serial.print(but1v);
  // Serial.print(" ");
  // Serial.print(but2v);
  // Serial.print(" ");
  // Serial.println(but3v);
  if (IR2v >= n && IR3v <= f && IR4v >= n) {
    forward();
    Serial.println("moving forward");
  } else if (IR2v <= f && IR3v >= n && IR4v >= n) {
    left();
    Serial.println("moving left");
  } else if (IR2v >= n && IR3v >= n && IR4v <= f) {
    right();
    Serial.println("Moving rigth");
  } else if (IR2v >= n && IR3v >= n && IR4v >= n) {
    backward();
    Serial.println("moving backword");
     //else if (IR2v <= f && IR3v <= f && IR4v >= n) {
  //   Serial.println("press button");
  //   if (but1 == 0 && but2 == 1 && but3 == 0) {
  //     Serial.println("moving forward");
  //     forward();
  //   }
  //   if (but1 == 1 && but2 == 0 && but3 == 0) {
  //     Serial.println("Moving rigth");
  //     right();
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 1) {
  //     Serial.println("stopped!");
  //     stop();
  //     buzzs();
  //     delay(1000);
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 0) {
  //     Serial.println("stopped!");
  //     stop();
  //     buzzs();
  //     delay(1000);
  //   }
  // } else if (IR2v >= n && IR3v <= f && IR4v <= f) {
  //   Serial.println("press the buttom");
  //   delay(2000);
  //   if (but1 == 0 && but2 == 1 && but3 == 0) {
  //     Serial.println("moving forward");
  //     forward();
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 1) {
  //     Serial.println("Moving left");
  //     left();
  //   }
  //   if (but1 == 1 && but2 == 0 && but3 == 0) {
  //     stop();
  //     buzzs();
  //     delay(2000);
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 0) {
  //     Serial.println("stopped!");
  //     stop();
  //     buzzs();
  //     delay(1000);
  //   }
  // } else if (IR2v <= f && IR3v >= n && IR4v <= f) {
  //   Serial.println("press the buttom");
  //   delay(2000);
  //   if (but1 == 1 && but2 == 0 && but3 == 0) {
  //     Serial.println("moving rigth");
  //     right();
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 1) {
  //     Serial.println("Moving left");
  //     left();
  //   }
  //   if (but1 == 0 && but2 == 1 && but3 == 0) {
  //     Serial.println("stopped!");
  //     stop();
  //     buzzs();
  //     delay(1000);
  //   }
  //   if (but1 == 0 && but2 == 0 && but3 == 0) {
  //     Serial.println("stopped!");
  //     stop();
  //     buzzs();
  //     delay(1000);
  //   }
  } else {
    Serial.println("What is this???");
    stop();
  }
  delay(500);
}