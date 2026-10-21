#include "Cloud.hpp"

#include <ArduinoJson.h>

#include <stdexcept>
#include <utility>

#include "CloudVar.hpp"

void registerCloudVar(const std::string& name, CloudVarBase* variable) {
  CloudHandler::variables().emplace(name, variable);
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
      std::string name1 = (json["VarName"] | String("")).c_str();
      auto it1 = CloudHandler::variables().find(name1);

      if (it1 == CloudHandler::variables().end()) {
        response["error"] = "Variable not found";
        return response;
      }

      if (!it1->second->fromJson(json["Value"])) {
        response["error"] = "Invalid value";
        return response;
      }

      Serial.println("Updated the val seccusefully: ");
      return response;
    }

    case ReqTy::SEND_ALL_VAR: {
      response["msg"] = ReqTy::SEND_ALL_VAR;

      JsonObject vars = response["Variables"].to<JsonObject>();

      for (const auto& [name, variable] : CloudHandler::variables()) {
        variable->toJson(vars, name.c_str());
      }
      Serial.println("Sended all the vars!");
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
