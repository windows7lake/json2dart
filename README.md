# json2dart
Flutter plugin use for parsing json to dart class with safe type convert.

It will generate a dart file named "safe_convert.dart" to ensure dart object can get a default value.

When you need a `int` type, but the server give you a `String` type, it will auto convert `String` to `int` and make sure that it has a default value such as '0'.

For Example:
```dart
class Response {
  final int status;
  final String info;
  final Data data;

  Response.fromJson(Map<String, dynamic> json)
      : status = asT<int>(json, 'status'),
        info = asT<String>(json, 'info'),
        data = Data.fromJson(asT<Map>(json, 'data'));

  Map<String, dynamic> toJson() => {
        'status': this.status,
        'info': this.info,
        'data': this.data?.toJson(),
      };
}
```

```dart
/// Type conversion to int <br>
///
/// @param json:  json data <br>
/// @param key:   json key <br>
/// @param defaultValue:   default value when convert fail <br>
/// @return result after convert  <br>
T asT<T>(Map<String, dynamic> json, String key, {T defaultValue}) {
  dynamic value = json[key];
  if (value is T) return value;

  if (0 is T) {
    defaultValue = 0 as T;
    if (value is double)
      return value.toInt() as T;
    else if (value is bool)
      return value ? 1 : 0 as T;
    else if (value is String)
      return int.tryParse(value) ??
          double.tryParse(value)?.toInt() ??
          defaultValue;
    else
      return defaultValue;
  } else if (0.0 is T) {
    defaultValue = 0.0 as T;
    if (value is int)
      return value.toDouble() as T;
    else if (value is bool)
      return value ? 1.0 : 0.0 as T;
    else if (value is String)
      return double.tryParse(value) ?? defaultValue;
    else
      return defaultValue;
  } else if ('' is T) {
    defaultValue = '' as T;
    return value.toString() ?? defaultValue;
  } else if (false is T) {
    defaultValue = false as T;
    String valueS = value.toString();
    if (valueS == '1' || valueS == '1.0' || valueS.toLowerCase() == 'true')
      return true as T;
    return defaultValue;
  } else if (List is T) {
    return [] as T;
  } else if (Map is T) {
    return Map() as T;
  }
  return null;
}
```

![](https://github.com/windows7lake/json2dart/blob/master/example.gif)