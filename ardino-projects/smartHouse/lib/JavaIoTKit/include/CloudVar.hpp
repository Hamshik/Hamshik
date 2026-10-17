#include <unordered_map>
#include "WString.h"

template <typename U>
class CloudVariable {
 private:
  U value;
  const String name;
  static std::unordered_map<String, CloudVariable&> varHashMap;

 public:
  CloudVariable(const String name, U& initialValue);

  U get() const;
  void set(U newValue);
  const String getName() const;

  static const CloudVariable& getVarObj(const String name);
};

#define T template <typename U>