#pragma once
#include <ArduinoJson.h>

#include <unordered_map>
#include <string>

#include "CloudVar.hpp"

enum ReqTy { GET_VAR, SET_VAR, SEND_ALL_VAR };

class CloudHandler {
 public:
  using VariableMap = std::unordered_map<std::string, CloudVarBase*>;

  static void handleReq(std::string);
  static JsonDocument dispatchReq(JsonDocument&);

  // Function-local static avoids cross-translation-unit initialization order
  // issues when CloudVar objects register during global initialization.
  static VariableMap& variables() {
    static VariableMap registry;
    return registry;
  }

  static void set(CloudVarBase& newValue, std::string name);
  static const CloudVarBase& get(std::string name);

};
