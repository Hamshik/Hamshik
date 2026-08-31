#include <Arduino.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Keypad.h>
#include <math.h>
#include <ctype.h>
#include <string.h>
#include "VectorChar.h"
#include "Eval.h"
#include "DisplayUnit.h"

// -------------------------------------------------------------------
// Hardware Setup
// -------------------------------------------------------------------
LiquidCrystal_I2C lcd(0x27, 16, 2); 
const byte ROWS = 4;
const byte COLS = 3;
char keys[ROWS][COLS] = {
  {'1', '2', '3'},
  {'4', '5', '6'},
  {'7', '8', '9'},
  {'*', '0', '#'}
};

byte rowPins[ROWS] = {3, 4, 5, 6}; 
byte colPins[COLS] = {7, 8, 9};

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

vectorChar inputBuffer;
Display display(lcd, inputBuffer);

// FIX 1: Removed empty parentheses to instantiate object, not function declaration
MathEval calculator; 

bool inCmdMode = false;
int menuIndex = 0;

void setup() {
    lcd.init();
    lcd.backlight();

    lcd.setCursor(0, 0);
    lcd.print("  Math  Engine  ");
    lcd.setCursor(0, 1);
    lcd.print(" Initializing...");
    delay(1500);

    display.updateLCDDisplay();
}

void showResult(){
    if (inputBuffer.length() > 0) {
        // FIX 2: Pass display reference into evaluate()
        double result = calculator.evaluate(inputBuffer, display); 
        
        if (calculator.isError()) return;

        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Ans:");
        lcd.print(result, 3);
        
        inputBuffer.clear();
        char resStr[16];
        dtostrf(result, 1, 3, resStr);
        inputBuffer.append_str(resStr);
        
        lcd.setCursor(0, 1);
        lcd.print("Press * for CMD");
        delay(2500);
    }
}

void loop() {
    char key = keypad.getKey();

    if (!key) return;
    
    if (calculator.isError()){
        display.updateLCDDisplay();
        calculator.setHasError(false);
    }
    
    if (key == '*') {
        inCmdMode = !inCmdMode;
        menuIndex = 0;
        display.updateLCDDisplay();
        return;
    }

    if (inCmdMode) {
        display.handleCMD(key);
    } else {
        if (key >= '0' && key <= '9') {
            inputBuffer.push_back(key);
            display.updateLCDDisplay();
        } else if (key == '#') {
            showResult();
            display.updateLCDDisplay();
        }
    }
}