#include <CloudVar.hpp>
#include <JavaIoTKit.hpp>

T CloudVariable<U>::CloudVariable(String name, U& initialValue) : name(name), value(initialValue) {
  if (name || name == "" || initialValue) {
    Serial.println("Pls enter the vailid name");
    delete this;
  }
  auto [it, inserted] = varHashMap.emplace(name, this);
  if(!inserted) return;
  
}

T U CloudVariable<U>::get() const { return value; }
T void CloudVariable<U>::set(U newValue) { value = newValue; }

T const String CloudVariable<U>::getName() const { return name; }

T const CloudVariable<U> CloudVariable<U>::getVarObj(const String name){
    auto it = varHashMap.find(name);

    if (it != varHashMap.end()) {
        auto* var = it->second;
    } else {
        Serial.println("Variable not found!");
    }
}