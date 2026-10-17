#pragma once

#include <ArduinoJson.h>
#include <string>


#define T template <typename U>
class CloudVarBase {
 public:
  virtual ~CloudVarBase() = default;

  virtual const std::string& getName() const = 0;
  virtual void toJson(JsonObject dst, const char* key) const = 0;
  virtual bool fromJson(JsonVariantConst src) = 0;
};

void registerCloudVar(const std::string& name, CloudVarBase* variable);

template <typename U>
class CloudVar : public CloudVarBase {
 private:
  U value;
  const std::string name;

 public:
  CloudVar(const std::string& variableName, const U& initialValue)
      : value(initialValue), name(variableName) {
    if (!name.empty()) registerCloudVar(name, this);
  }

  U get() const { return value; }

  void set(U newValue) { value = newValue; }

  const std::string& getName() const override { return name; }

  void toJson(JsonObject dst, const char* key) const override { dst[key].set(value); }

  bool fromJson(JsonVariantConst src) override {
    if (!src.is<U>()) return false;

    value = src.as<U>();
    return true;
  }
};
