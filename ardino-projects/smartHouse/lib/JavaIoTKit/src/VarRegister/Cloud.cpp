#include "Cloud.hpp"

#include <ArduinoJson.h>

#include <stdexcept>
#include <utility>

#include "ArduinoJson/Array/JsonArray.hpp"
#include "ArduinoJson/Object/JsonObject.hpp"
#include "CloudVar.hpp"

static std::function<void(const CloudVarBase&)>& cloudChangeCallback() {
  static std::function<void(const CloudVarBase&)> callback;
  return callback;
}

void registerCloudVar(const std::string& name, CloudVarBase* variable) {
  auto [it, inserted] = CloudHandler::variables().emplace(name, variable);
  if (inserted) it->second->setChangeCallback(cloudChangeCallback());
}

void CloudHandler::setChangeCallback(std::function<void(const CloudVarBase&)> callback) {
  cloudChangeCallback() = std::move(callback);
  for (auto& [name, variable] : variables()) {
    (void)name;
    variable->setChangeCallback(cloudChangeCallback());
  }
}

JsonDocument CloudHandler::dispatchReq(JsonDocument& json) {
  JsonDocument response;

  auto msg = json["msg"];

  if (!msg.is<int>()) return response;

  switch (msg.as<int>()) {
    case ReqTy::GET_VAR: {
      std::string name = (json["VarName"] | String("")).c_str();

      auto& variables = CloudHandler::variables();
      auto it = variables.find(name);

      if (it == variables.end()) {
        response["error"] = "Variable not found";
        return response;
      }

      response["msg"] = ReqTy::SET_VAR;
      response["VarName"] = name;

      JsonObject responseObject = response.as<JsonObject>();
      it->second->toJson(responseObject, "Value");

      return response;
    }

    case ReqTy::SET_VAR: {
      std::string name = (json["VarName"] | String("")).c_str();

      auto& variables = CloudHandler::variables();
      auto it = variables.find(name);

      if (it == variables.end()) {
        response["error"] = "Variable not found";
        return response;
      }

      if (!it->second->fromJson(json["Value"])) {
        response["error"] = "Invalid value";
        return response;
      }

      response["msg"] = ReqTy::SET_VAR;
      response["VarName"] = name.c_str();
      response["Value"] = json["Value"];
      Serial.println("Updated the val seccusefully: ");
      return response;
    }

    case ReqTy::SEND_ALL_VAR: {
      response["msg"] = ReqTy::SEND_ALL_VAR;

      JsonObject vars = response["Variables"].to<JsonObject>();

      for (const auto& [name, variable] : CloudHandler::variables()) {
        variable->toJson(vars, name.c_str());
      }
      return response;
    }

    default: return response;
  }
}

void CloudHandler::set(CloudVarBase& newValue, std::string name) { variables()[std::move(name)] = &newValue; }

const CloudVarBase& CloudHandler::get(const std::string name) {
  auto& variables = CloudHandler::variables();
  auto it = variables.find(name);

  if (it != variables.end()) return *it->second;

  throw std::runtime_error("Variable not found");
}
