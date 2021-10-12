package com.lake.json2dart.model.data

const val SafeCodeGeneric: String = """
T asT<T>(Map<String, dynamic>? json, String key, {T? defaultValue}) {
  if (json == null || !json.containsKey(key)) {
    if (defaultValue != null) return defaultValue;
    if (0 is T) return 0 as T;
    if (0.0 is T) return 0.0 as T;
    if ('' is T) return '' as T;
    if (false is T) return false as T;
    if ([] is T) return [] as T;
    if (<String, dynamic>{} is T) return <String, dynamic>{} as T;
    return '' as T;
  }
  dynamic value = json[key];
  if (value is T) return value;

  if (0 is T) {
    if (defaultValue == null) defaultValue = 0 as T;
    if (value is double)
      return value.toInt() as T;
    else if (value is bool)
      return (value ? 1 : 0) as T;
    else if (value is String)
      return (int.tryParse(value) ??
          double.tryParse(value)?.toInt() ??
          defaultValue) as T;
    else
      return defaultValue!;
  } else if (0.0 is T) {
    if (defaultValue == null) defaultValue = 0.0 as T;
    if (value is int)
      return value.toDouble() as T;
    else if (value is bool)
      return (value ? 1.0 : 0.0) as T;
    else if (value is String)
      return (double.tryParse(value) ?? defaultValue) as T;
    else
      return defaultValue!;
  } else if ('' is T) {
    if (defaultValue == null) defaultValue = '' as T;
    if (value is int || value is double)
      return value.toString() as T;
    else if (value is bool)
      return (value ? "true" : "false") as T;
    else
      return defaultValue!;
  } else if (false is T) {
    if (defaultValue == null) defaultValue = false as T;
    String valueS = value.toString();
    if (valueS == '1' || valueS == '1.0' || valueS.toLowerCase() == 'true')
      return true as T;
    return defaultValue!;
  } else if ([] is T) {
    if (defaultValue == null) defaultValue = [] as T;
    return defaultValue!;
  } else if (<String, dynamic>{} is T) {
    if (defaultValue == null) defaultValue = <String, dynamic>{} as T;
    return defaultValue!;
  }
  return '' as T;
}
"""

const val SafeCode: String = """
int asInt(Map<String, dynamic>? json, String key, {int defaultValue = 0}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  var value = json[key];
  if (value == null) return defaultValue;
  if (value is int) return value;
  if (value is double) return value.toInt();
  if (value is bool) return value ? 1 : 0;
  if (value is String) return int.tryParse(value) ?? double.tryParse(value)?.toInt() ?? defaultValue;
  return defaultValue;
}

double asDouble(Map<String, dynamic>? json, String key, {double defaultValue = 0.0}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  var value = json[key];
  if (value == null) return defaultValue;
  if (value is double) return value;
  if (value is int) return value.toDouble();
  if (value is bool) return value ? 1.0 : 0.0;
  if (value is String) return double.tryParse(value) ?? defaultValue;
  return defaultValue;
}

bool asBool(Map<String, dynamic>? json, String key, {bool defaultValue = false}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  var value = json[key];
  if (value == null) return defaultValue;
  if (value is bool) return value;
  if (value is int) return value == 0 ? false : true;
  if (value is double) return value == 0 ? false : true;
  if (value is String) {
    if (value == "1" || value.toLowerCase() == "true") return true;
    if (value == "0" || value.toLowerCase() == "false") return false;
  }
  return defaultValue;
}

String asString(Map<String, dynamic>? json, String key, {String defaultValue = ""}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  var value = json[key];
  if (value == null) return defaultValue;
  if (value is String) return value;
  if (value is int) return value.toString();
  if (value is double) return value.toString();
  if (value is bool) return value ? "true" : "false";
  return defaultValue;
}

Map<String, dynamic> asMap(Map<String, dynamic>? json, String key, {Map<String, dynamic>? defaultValue}) {
  if (json == null || !json.containsKey(key)) return defaultValue ?? <String, dynamic>{};
  var value = json[key];
  if (value == null) return defaultValue ?? <String, dynamic>{};
  if (value is Map<String, dynamic>) return value;
  return defaultValue ?? <String, dynamic>{};
}

List asList(Map<String, dynamic>? json, String key, {List? defaultValue}) {
  if (json == null || !json.containsKey(key)) return defaultValue ?? [];
  var value = json[key];
  if (value == null) return defaultValue ?? [];
  if (value is List) return value;
  return defaultValue ?? [];
}
"""