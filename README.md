# json2dart
Android Studio plugin use for prasing json to dart class with safe type convert.\n
It will generate a dart file named "safe_convert.dart" to ensure dart object can get a default value.\n
When you need a `int` type, but the server give you a `String` type, it will auto convert `String` to `int` and make sure that it has a default value such as '0'.
For Example:
```dart
class Response {
  final int status;
  final String info;
  final Data data;

  Response.fromJson(Map<String, dynamic> json)
      : status = SafeManager.parseInt(json, 'status'),
        info = SafeManager.parseString(json, 'info'),
        data = Data.fromJson(
          SafeManager.parseObject(json, 'data'),
        );

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
/// @param data:  json data <br>
/// @param key:   json key <br>
/// @param defaultValue:   default value when convert fail <br>
/// @return result after convert  <br>
static int parseInt(Map<String, dynamic> data, String key,
    {int defaultValue = 0}) {
  try {
    Object value = data[key];
    if (value == null) return defaultValue;
    if (value is int) return value;
    if (value is double) return value.toInt();
    if (value is bool) return value ? 1 : 0;
    if (value is String) {
      var temp = int.tryParse(value);
      if (temp == null) return defaultValue;
      return temp;
    }
    return defaultValue;
  } catch (error) {
    return defaultValue;
  }
}
```

![](https://github.com/windows7lake/json2dart/blob/master/gif/setup1.gif)

![](https://github.com/windows7lake/json2dart/blob/master/gif/setup2.gif)Response
