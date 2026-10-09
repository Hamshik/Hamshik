#include "VectorChar.h"
#include <Arduino.h>

vectorChar::vectorChar() : capacity(16), size(0) {
    data = new char[capacity];
    data[0] = '\0';
}

vectorChar::~vectorChar() {
    delete[] data;
}

void vectorChar::resize(size_t new_capacity) {
    char* new_data = new char[new_capacity];
    if (data != nullptr) {
        memcpy(new_data, data, size + 1); 
        delete[] data;
    }
    data = new_data;
    capacity = new_capacity;
}

void vectorChar::push_back(char c) {
    if (size + 1 >= capacity) resize(capacity * 2);
    data[size++] = c;
    data[size] = '\0';
}

void vectorChar::pop_back() {
    if (size > 0) {
        size--;
        data[size] = '\0';
    }
}

void vectorChar::append_str(const char* str) {
    for (size_t i = 0; str[i] != '\0'; ++i) push_back(str[i]);
}

void vectorChar::clear() {
    size = 0;
    if (data) data[0] = '\0';
}

size_t vectorChar::length() const { return size; }
const char* vectorChar::c_str() const { return data; }
