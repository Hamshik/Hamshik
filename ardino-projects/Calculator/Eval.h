#ifndef MATH_EVAL_H
#define MATH_EVAL_H

#include <Arduino.h>
#include <math.h>
#include <ctype.h>
#include <string.h>
#include "VectorChar.h"

class Display;

class MathEval {
private:
    const char* expr;
    size_t pos;
    Display* displayPtr;
    bool hasError;

    void skipWhitespace();
    double parseNumber();
    double parseFunctionOrPrimary();
    double parseTerm();
    void triggerError(const char* msg);

public:
    MathEval();
    double parseExpression();
    double evaluate(const vectorChar& input, Display& display);
    bool isError();
    void setHasError(bool error); // Matches void return type in Eval.ino
};

#endif