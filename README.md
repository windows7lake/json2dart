# json2dart

## Description

It's a flutter plugin that use for parsing json to dart class with safe type convert. 

### What it do

It will generate a dart file named "safe_convert.dart" to ensure dart object can be converted to correct type, if failed, it will get a default value.

For example, when you need a `int` type, but the server offer you a `String` type. It will auto convert `String` to `int` and make sure that, when convert failed, it has a default value such as '0'.

## safe_convert.dart

``` dart
int toInt(value, {int defaultValue = 0}) {
  if (value == null) return defaultValue;
  if (value is int) return value;
  if (value is double) return value.toInt();
  if (value is bool) return value ? 1 : 0;
  if (value is String) {
    return int.tryParse(value) ??
        double.tryParse(value)?.toInt() ??
        defaultValue;
  }
  return defaultValue;
}

double toDouble(value, {double defaultValue = 0.0}) {
  if (value == null) return defaultValue;
  if (value is double) return value;
  if (value is int) return value.toDouble();
  if (value is bool) return value ? 1.0 : 0.0;
  if (value is String) return double.tryParse(value) ?? defaultValue;
  return defaultValue;
}

bool toBool(value, {bool defaultValue = false}) {
  if (value == null) return defaultValue;
  if (value is bool) return value;
  if (value is int) return value == 0 ? false : true;
  if (value is double) return value == 0 ? false : true;
  if (value is String) {
    if (value == "1" || value.totoLowerCase() == "true") return true;
    if (value == "0" || value.totoLowerCase() == "false") return false;
  }
  return defaultValue;
}

String toString(value, {String defaultValue = ""}) {
  if (value == null) return defaultValue;
  if (value is String) return value;
  if (value is int) return value.toString();
  if (value is double) return value.toString();
  if (value is bool) return value ? "true" : "false";
  return defaultValue;
}

Map<String, dynamic> toMap(value, {Map<String, dynamic>? defaultValue}) {
  if (value == null) return defaultValue ?? <String, dynamic>{};
  if (value is Map<String, dynamic>) return value;
  return defaultValue ?? <String, dynamic>{};
}

List toList(value, {List? defaultValue}) {
  if (value == null) return defaultValue ?? [];
  if (value is List) return value;
  return defaultValue ?? [];
}

int asInt(Map<String, dynamic>? json, String key, {int defaultValue = 0}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  return toInt(json[key]);
}

double asDouble(Map<String, dynamic>? json, String key,
    {double defaultValue = 0.0}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  return toDouble(json[key]);
}

bool asBool(Map<String, dynamic>? json, String key,
    {bool defaultValue = false}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  return toBool(json[key]);
}

String asString(Map<String, dynamic>? json, String key,
    {String defaultValue = ""}) {
  if (json == null || !json.containsKey(key)) return defaultValue;
  return toString(json[key]);
}

Map<String, dynamic> asMap(Map<String, dynamic>? json, String key,
    {Map<String, dynamic>? defaultValue}) {
  if (json == null || !json.containsKey(key)) {
    return defaultValue ?? <String, dynamic>{};
  }
  return toMap(json[key]);
}

List asList(Map<String, dynamic>? json, String key, {List? defaultValue}) {
  if (json == null || !json.containsKey(key)) return defaultValue ?? [];
  return toList(json[key]);
}

List<int> asListInt(Map<String, dynamic>? json, String key,
    {List? defaultValue}) {
  return asList(json, key, defaultValue: defaultValue)
      .map((e) => toInt(e))
      .toList();
}

List<String> asListString(Map<String, dynamic>? json, String key,
    {List? defaultValue}) {
  return asList(json, key, defaultValue: defaultValue)
      .map((e) => toString(e))
      .toList();
}

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
    return toInt(value, defaultValue: (defaultValue ?? 0) as int) as T;
  } else if (0.0 is T) {
    return toDouble(value, defaultValue: (defaultValue ?? 0.0) as double) as T;
  } else if ('' is T) {
    return toString(value, defaultValue: (defaultValue ?? '') as String) as T;
  } else if (false is T) {
    return toBool(value, defaultValue: (defaultValue ?? false) as bool) as T;
  } else if ([] is T) {
    return toList(value, defaultValue: (defaultValue ?? []) as List) as T;
  } else if (<String, dynamic>{} is T) {
    Object defaultV = defaultValue ?? <String, dynamic>{};
    return toMap(value, defaultValue: defaultV as Map<String, dynamic>) as T;
  }
  return '' as T;
}
```

## Features

* `final` property
* null safety
* `fromJson` and `toJson` method
* `copyWith` method
* use `generic`

## Example

### Example 1: without using generic

``` dart
class Response {
  final int intValue;
  final double doubleValue;
  final bool boolValue;
  final String stringValue;
  final KeyData keyData;
  final List<String> stringData;

  Response({
    this.intValue = 0,
    this.doubleValue = 0.0,
    this.boolValue = false,
    this.stringValue = "",
    required this.keyData,
    required this.stringData,
  });

  factory Response.fromJson(Map<String, dynamic>? json) => Response(
    intValue: asInt(json, 'int_value'),
    doubleValue: asDouble(json, 'double_value'),
    boolValue: asBool(json, 'bool_value'),
    stringValue: asString(json, 'string_value'),
    keyData: KeyData.fromJson(asMap(json, 'key_data')),
    stringData: asList(json, 'string_data').map((e) => e.toString()).toList(),
  );

  Map<String, dynamic> toJson() => {
    'int_value': intValue,
    'double_value': doubleValue,
    'bool_value': boolValue,
    'string_value': stringValue,
    'key_data': keyData.toJson(),
    'string_data': stringData.map((e) => e).toList(),
  };
}
```

### Example 2: using generic

``` dart
class Response {
  final int intValue;
  final double doubleValue;
  final bool boolValue;
  final String stringValue;
  final KeyData keyData;
  final List<String> stringData;

  Response({
    this.intValue = 0,
    this.doubleValue = 0.0,
    this.boolValue = false,
    this.stringValue = "",
    required this.keyData,
    required this.stringData,
  });

  factory Response.fromJson(Map<String, dynamic>? json) => Response(
    intValue: asT<int>(json, 'int_value'),
    doubleValue: asT<double>(json, 'double_value'),
    boolValue: asT<bool>(json, 'bool_value'),
    stringValue: asT<String>(json, 'string_value'),
    keyData: KeyData.fromJson(asT<Map<String, dynamic>>(json, 'key_data')),
    stringData: asT<List>(json, 'string_data').map((e) => e.toString()).toList(),
  );

  Map<String, dynamic> toJson() => {
    'int_value': intValue,
    'double_value': doubleValue,
    'bool_value': boolValue,
    'string_value': stringValue,
    'key_data': keyData.toJson(),
    'string_data': stringData.map((e) => e).toList(),
  };
}
```

### Example 3: generate copyWith method

``` dart
class Response {

  ......
  
  Response copyWith({
    int? intValue,
    double? doubleValue,
    bool? boolValue,
    String? stringValue,
    KeyData? keyData,
    List<String>? stringData,
  }) {
    return Response(
      intValue: intValue ?? this.intValue,
      doubleValue: doubleValue ?? this.doubleValue,
      boolValue: boolValue ?? this.boolValue,
      stringValue: stringValue ?? this.stringValue,
      keyData: keyData ?? this.keyData,
      stringData: stringData ?? this.stringData,
    );
  }
}
```

## Operation demo

![](https://github.com/windows7lake/json2dart/blob/master/gif/example.gif)