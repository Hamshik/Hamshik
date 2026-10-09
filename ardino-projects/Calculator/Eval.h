#ifndef MATH_EVAL_H
#define MATH_EVAL_H

#include <math.h>
#include <ctype.h>
#include <string.h>
#include "VectorChar.h"

class MathEval {
private:
    const char* expr;
    size_t pos;
    bool hasError;

    void skipWhitespace();
    double parseNumber();
    double parseFunctionOrPrimary();
    double parseTerm();
    void triggerError(const char* msg);

public:
    MathEval();
    double parseExpression();
    double evaluate(const vectorChar& input);
    bool isError();
    void setHasError(bool error); // Matches void return type in Eval.ino
};

#endif