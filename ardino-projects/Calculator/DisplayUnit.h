#ifndef DISPLAY_H
#define DISPLAY_H

#include <LiquidCrystal_I2C.h>
#include "VectorChar.h"

class Display {
  private:
  vectorChar& inputBuf;
  LiquidCrystal_I2C& lcd;
  public:
  Display(LiquidCrystal_I2C& lcd, vectorChar& in): inputBuf(in), lcd(lcd){}

  void printInputBufferLCD();
  void updateLCDDisplay();
  void append(char key);
  void LCDClear();
  void handleCMD(char key);
  void displayError(String error_msg);
};

#endif