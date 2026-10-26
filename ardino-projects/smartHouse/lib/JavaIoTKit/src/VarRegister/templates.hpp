#pragma once

// 1. Define allowed return types (arithmetic types + std::string)
#include <string>
#include <type_traits>

#define _O template <typename O>
_O concept PrimitiveOrString = std::is_arithmetic_v<O> || std::is_same_v<O, std::string>;

// 2. Define the Concept enforcing .get() and .set()
_O concept HasGetSet = requires(O obj) {
  // .get() must exist and return a PrimitiveOrString type
  { obj.get() } -> PrimitiveOrString;

  // .set() must accept a parameter matching the return type of .get()
  requires requires(decltype(obj.get()) val) {
    { obj.set(val) };
  };
};

template <typename V>
concept ValidInput = PrimitiveOrString<V> || HasGetSet<V>;

#define _T template <ValidInput T>
#define _V template <ValidInput V = T, typename std::enable_if<std::is_arithmetic<V>::value, int>::type = 0>

enum Type { INT, FLOAT, STRING, BOOL };

_T class Value {
  T item;  // Owns the value (not a reference)
  Type type;

  // Static template helper — allows recursive call with a different type U
  template <typename U>
  static constexpr Type getTy() {
    using CleanU = std::decay_t<U>;

    if constexpr (std::is_same_v<CleanU, bool>)
      return Type::BOOL;
    else if constexpr (std::is_integral_v<CleanU>)
      return Type::INT;
    else if constexpr (std::is_floating_point_v<CleanU>)
      return Type::FLOAT;
    else if constexpr (std::is_same_v<CleanU, std::string>)
      return Type::STRING;
    else {
      // If it's an object, automatically extract what .get() returns
      using RetType = decltype(std::declval<U>().get());
      return getTy<RetType>();
    }
  }

 public:
  explicit Value(const T& item) : item(item), type(getTy<T>()) {}

  T get() const {
    if constexpr (PrimitiveOrString<T>) {
      return item;
    } else {
      return item.get();
    }
  }

  void set(T val) { item = std::move(val); }

  // Allow assignment directly from T
  Value& operator=(T val) {
    set(std::move(val));
    return *this;
  }

  Value& operator+=(const T& val) {
    item += val;
    return *this;
  }
  Value& operator-=(const T& val) {
    item -= val;
    return *this;
  }
  Value& operator*=(const T& val) {
    item *= val;
    return *this;
  }
  Value& operator/=(const T& val) {
    item /= val;
    return *this;
  }
  Value& operator%=(const T& val) {
    item %= val;
    return *this;
  }
  Value& operator++() {
    ++item;
    return *this;
  }
  T operator++(int) {
    T prev = item;
    ++item;
    return prev;
  }
  Value& operator--() {
    --item;
    return *this;
  }
  T operator--(int) {
    T prev = item;
    --item;
    return prev;
  }

  // Implicit conversion to T for use in expressions like `int x = myValue`
  operator T() const { return item; }

  Type types() const { return type; }
};