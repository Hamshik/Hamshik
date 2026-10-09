#include "DisplayUnit.h"

void Display::printInputBufferLCD() {
    const char* str = inputBuf.c_str();
    size_t len = inputBuf.length();

    // Calculate starting position to keep display right-aligned (last 13 characters)
    size_t start = (len > 13) ? (len - 13) : 0;

    for (size_t i = start; i < len; i++) {
        // Check if current position starts "sqrt("
        if (str[i] == 's' && str[i+1] == 'q' && str[i+2] == 'r' && str[i+3] == 't') {
            lcd.write(0xE8); // Print '√' symbol (or byte(0) if using custom char)
            i += 3;          // Skip "qrt"
        } 
        // Check if current position starts "cbrt("
        else if (str[i] == 'c' && str[i+1] == 'b' && str[i+2] == 'r' && str[i+3] == 't') {
            lcd.print("3");
            lcd.write(0xE8); // Print '3√' symbol
            i += 3;          // Skip "brt"
        } 
        else {
            lcd.write(str[i]);
        }
    }
}

void Display::updateLCDDisplay() {
    lcd.clear();
    if (!inCmdMode) {
        lcd.setCursor(0, 0);
        lcd.print("In:");
        
        if (inputBuf.length() > 0) {
            printInputBufferLCD();
        }
        
        lcd.setCursor(0, 1);
        lcd.print(" Press * for CMD ");
    } else {
        lcd.setCursor(0, 0);
        lcd.print("1:+ 2:- 3:* 4:/");
        lcd.setCursor(0, 1);
        lcd.print("5:");
        lcd.write(0xE8);
        lcd.print(" 6:3");
        lcd.write(0xE8);
        lcd.print(" 7:C 8:B");
    }
}

//Appends in both inputBuf & lcd display
void Display::append(char key){
    lcd.print(key);
    inputBuf.push_back(key);
}

void Display::LCDClear(){
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("In: ");
    lcd.setCursor(0,1);
    lcd.print("Press '*' for CMD");
    lcd.setCursor(0,5);
}

void Display::handleCMD(char key) {
    switch (key) {
        case '1': 
            inputBuffer.append_str("+"); 
            break;
        case '2': 
            inputBuffer.append_str("-"); 
            break;
        case '3': 
            inputBuffer.append_str("*"); 
            break;
        case '4': 
            inputBuffer.append_str("/"); 
            break;
        case '5': 
            inputBuffer.append_str("sqrt("); 
            break;
        case '6': 
            inputBuffer.append_str("cbrt("); 
            break;
        case '7': 
            inputBuffer.clear();
            lcd.clear();
            break;
        case '8': 
            inputBuffer.pop_back(); 
            break;
        case '9':
            inputBuf.append_str(")");
        default: 
            break;
    }

    // Exit CMD mode and return to main interface after processing command
    inCmdMode = false;
    updateLCDDisplay();
}

void Display::displayError(String error_msg) {
    lcd.clear();
    
    // Top Row: Error Header
    lcd.setCursor(0, 0);
    lcd.print("Error!");

    // Bottom Row: Specific Error Message
    lcd.setCursor(0, 1);
    
    // Truncate message if it exceeds 16 characters
    if (error_msg.length() > 16) {
        lcd.print(error_msg.substring(0, 16));
    } else {
        lcd.print(error_msg);
    }
}