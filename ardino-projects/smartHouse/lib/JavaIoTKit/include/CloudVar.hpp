#pragma once

#include <ArduinoJson.h>

#include <functional>
#include <string>
#include <VarRegister/templates.hpp>
#include <utility>

enum Permission { RO = 0, RW, WO };


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
  virtual Permission permissionMod() const = 0;
  virtual Type type() const = 0 ;
  void setChangeCallback(std::function<void(const CloudVarBase&)> callback) { changeCallback = std::move(callback); }
};

void registerCloudVar(const std::string& name, CloudVarBase* variable);

_T class CloudVar : public CloudVarBase {
 private:
  Value<T> value;
  const std::string name;
  Permission permission;

 public:
  CloudVar(const std::string& variableName, const T& initialValue, Permission permission) : value(initialValue), name(variableName), permission(permission) {
    if (!name.empty()) registerCloudVar(name, this);
  }

  T get() const { return value.get(); }

  void set(T newValue) {
    value = std::move(newValue);
    notifyChanged();
  }

  // Let arithmetic CloudVars participate in normal expressions, e.g.
  // `int next = temperature + 1` and `temperature += 1`.
  _V operator V() const { return value; }

  CloudVar& operator=(const T& newValue) {
    set(newValue);
    return *this;
  }

  _V CloudVar& operator+=(const V& amount) {
    value += amount;
    notifyChanged();
    return *this;
  }

  _V CloudVar& operator-=(const V& amount) {
    value -= amount;
    notifyChanged();
    return *this;
  }

  _V CloudVar& operator*=(const V& amount) {
    value *= amount;
    notifyChanged();
    return *this;
  }

  _V CloudVar& operator/=(const V& amount) {
    value /= amount;
    notifyChanged();
    return *this;
  }

  _V CloudVar& operator%=(const V& amount) {
    value %= amount;
    notifyChanged();
    return *this;
  }

  _V CloudVar& operator++() {
    ++value;
    notifyChanged();
    return *this;
  }

  _V T operator++(int) {
    T previous = value.get();
    ++value;
    notifyChanged();
    return previous;
  }

  _V CloudVar& operator--() {
    --value;
    notifyChanged();
    return *this;
  }

  _V T operator--(int) {
    T previous = value.get();
    --value;
    notifyChanged();
    return previous;
  }

  const std::string& getName() const override { return name; }

  void toJson(JsonObject& dst, const String& key) const override { dst[key].set(value.get()); }

  bool fromJson(JsonVariantConst src) override {
    if (!src.is<T>()) return false;

    set(src.as<T>());
    return true;
  }

  Type type() const override { return value.types(); };

  Permission permissionMod() const override { return permission; }
};
