#ifndef VECTOR_CHAR_H
#define VECTOR_CHAR_H

#include <Arduino.h>

class vectorChar {
private:
    char* data;
    size_t capacity;
    size_t size;
    void resize(size_t new_capacity);

public:
    vectorChar();
    ~vectorChar();
    void push_back(char c);
    void pop_back();
    void append_str(const char* str);
    void clear();
    size_t length() const;
    const char* c_str() const;
};

#endif