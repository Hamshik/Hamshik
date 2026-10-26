#pragma once

#include <ArduinoJson.h>

#include <functional>
#include <string>
#include <type_traits>
#include <utility>

#define T template <typename U>
#define E template <typename V = U, typename std::enable_if<std::is_arithmetic<V>::value, int>::type = 0>

class CloudVarBase {
 private:
  std::function<void(const CloudVarBase&)> changeCallback;

 protected:
  void notifyChanged() const {
    if (changeCallback) changeCallback(*this);
  }

 public:
  virtual ~CloudVarBase() = default;

  virtual const std::string& getName() const = 0;
  virtual void toJson(JsonObject& dst, const String& key) const = 0;
  virtual bool fromJson(JsonVariantConst src) = 0;

  void setChangeCallback(std::function<void(const CloudVarBase&)> callback) { changeCallback = std::move(callback); }
};

void registerCloudVar(const std::string& name, CloudVarBase* variable);

T class CloudVar : public CloudVarBase {
 private:
  U value;
  const std::string name;

 public:
  CloudVar(const std::string& variableName, const U& initialValue) : value(initialValue), name(variableName) {
    if (!name.empty()) registerCloudVar(name, this);
  }

  U get() const { return value; }

  void set(U newValue) {
    value = std::move(newValue);
    notifyChanged();
  }

  // Let arithmetic CloudVars participate in normal expressions, e.g.
  // `int next = temperature + 1` and `temperature += 1`.
  E operator V() const { return value; }

  CloudVar& operator=(const U& newValue) {
    set(newValue);
    return *this;
  }

  E CloudVar& operator+=(const V& amount) {
    value += amount;
    notifyChanged();
    return *this;
  }

  E CloudVar& operator-=(const V& amount) {
    value -= amount;
    notifyChanged();
    return *this;
  }

  E CloudVar& operator*=(const V& amount) {
    value *= amount;
    notifyChanged();
    return *this;
  }

  E CloudVar& operator/=(const V& amount) {
    value /= amount;
    notifyChanged();
    return *this;
  }

  E CloudVar& operator%=(const V& amount) {
    value %= amount;
    notifyChanged();
    return *this;
  }

  E CloudVar& operator++() {
    ++value;
    notifyChanged();
    return *this;
  }

  E U operator++(int) {
    U previous = value;
    ++value;
    notifyChanged();
    return previous;
  }

  E CloudVar& operator--() {
    --value;
    notifyChanged();
    return *this;
  }

  E U operator--(int) {
    U previous = value;
    --value;
    notifyChanged();
    return previous;
  }

  const std::string& getName() const override { return name; }

  void toJson(JsonObject& dst, const String& key) const override { dst[key].set(value); }

  bool fromJson(JsonVariantConst src) override {
    if (!src.is<U>()) return false;

    set(src.as<U>());
    return true;
  }
};
