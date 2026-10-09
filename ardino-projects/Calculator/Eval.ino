#include "Eval.h"
#include "DisplayUnit.h"

MathEval::MathEval() : expr(nullptr), pos(0), displayPtr(nullptr), hasError(false) {}

void MathEval::triggerError(const char* msg) {
    if (!hasError) {
        hasError = true;
        if (displayPtr) {
            displayPtr->displayError(msg);
        }
    }
}

void MathEval::skipWhitespace() {
    while (expr[pos] == ' ' || expr[pos] == '\t' || expr[pos] == '\r' || expr[pos] == '\n') {
        pos++;
    }
}

double MathEval::parseNumber() {
    skipWhitespace();
    size_t start = pos;
    
    if (!isdigit(expr[pos]) && expr[pos] != '.') {
        triggerError("Syntax Error");
        return 0.0;
    }

    while (isdigit(expr[pos]) || expr[pos] == '.') {
        pos++;
    }
    return atof(&expr[start]);
}

double MathEval::parseFunctionOrPrimary() {
    skipWhitespace();

    if (expr[pos] == '(') {
        pos++; 
        double result = parseExpression();
        skipWhitespace();
        if (expr[pos] == ')') {
            pos++; 
        } else {
            triggerError("Missing ')'");
        }
        return result;
    }

    if (expr[pos] == '-') {
        pos++;
        return -parseFunctionOrPrimary();
    }

    if (isalpha(expr[pos])) {
        vectorChar funcName;
        while (isalpha(expr[pos])) {
            funcName.push_back(expr[pos++]);
        }

        skipWhitespace();
        if (expr[pos] == '(') {
            pos++; 
            double arg = parseExpression();
            skipWhitespace();
            if (expr[pos] == ')') {
                pos++; 
            } else {
                triggerError("Missing ')'");
            }

            if (strcmp(funcName.c_str(), "sqrt") == 0) {
                if (arg < 0) {
                    triggerError("Domain Error");
                    return 0.0;
                }
                return sqrt(arg);
            }
            if (strcmp(funcName.c_str(), "cbrt") == 0) return cbrt(arg);

            triggerError("Unknown Func");
            return 0.0;
        }
        triggerError("Expected '('");
        return 0.0;
    }

    return parseNumber();
}

double MathEval::parseTerm() {
    double left = parseFunctionOrPrimary();
    skipWhitespace();

    while (expr[pos] == '*' || expr[pos] == '/') {
        char op = expr[pos++];
        double right = parseFunctionOrPrimary();
        if (op == '*') {
            left *= right;
        } else if (op == '/') {
            if (right == 0.0) {
                triggerError("Div by Zero");
                return 0.0;
            }
            left /= right;
        }
        skipWhitespace();
    }
    return left;
}

double MathEval::parseExpression() {
    skipWhitespace();
    double left = parseTerm();
    skipWhitespace();

    while (expr[pos] == '+' || expr[pos] == '-') {
        char op = expr[pos++];
        double right = parseTerm();
        if (op == '+') {
            left += right;
        } else if (op == '-') {
            left -= right;
        }
        skipWhitespace();
    }
    return left;
}

double MathEval::evaluate(const vectorChar& input, Display& display) {
    expr = input.c_str();
    pos = 0;
    hasError = false;
    displayPtr = &display;

    if (input.length() == 0) {
        triggerError("Empty Input");
        return 0.0;
    }

    double result = parseExpression();
    skipWhitespace();

    if (expr[pos] != '\0' && !hasError) {
        triggerError("Syntax Error");
    }

    return result;
}

bool MathEval::isError() {
    return hasError;
}

// FIX 3: Changed return type to void to match Eval.h declaration
void MathEval::setHasError(bool error) {
    hasError = error;
}