package com.lake.json2dart.extension

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.model.dart.DClassName
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.strategy.PropertyTypeStrategy

const val BACKSTAGE_NULLABLE_POSTFIX = "_&^#"

fun JsonPrimitive.toDartType(): DartClass {
    return when {
        isBoolean -> DartClass.BOOLEAN
        isNumber -> when {
            asString.contains(".") -> DartClass.DOUBLE
            else -> DartClass.INT
        }

        isString -> DartClass.STRING
        else -> DartClass.STRING
    }
}

/**
 * array only has object element
 */
fun JsonArray.allItemAreNullElement(): Boolean {
    forEach {
        if (it.isJsonNull.not()) {
            return false
        }
    }
    return true
}

/**
 * array only has object element
 */
fun JsonArray.allItemAreObjectElement(): Boolean {
    forEach {
        if (it.isJsonObject.not()) {
            return false
        }
    }
    return true
}

/**
 * array only has array element
 */
fun JsonArray.allItemAreArrayElement(): Boolean {
    forEach {
        if (it.isJsonArray.not()) {
            return false
        }
    }
    return true
}

fun theSamePrimitiveType(first: JsonPrimitive, second: JsonPrimitive): Boolean {
    val sameBoolean = first.isBoolean && second.isBoolean
    val sameNumber = first.isNumber && second.isNumber
    val sameString = first.isString && second.isString
    return sameBoolean || sameNumber || sameString
}

fun JsonArray.allElementAreSamePrimitiveType(): Boolean {
    forEach {
        if (it.isJsonPrimitive.not()) {
            return false
        }
        if (theSamePrimitiveType(this[0].asJsonPrimitive, it.asJsonPrimitive).not()) {
            return false
        }
    }
    return true
}

/**
 * array only has one element
 */
private fun JsonArray.onlyHasOneElement(): Boolean {
    return size() == 1
}

/**
 * if Multidimensional Arrays only has one element
 */
tailrec fun JsonArray.onlyHasOneElementRecursive(): Boolean {
    if (size() == 0) {
        return false
    }
    if (onlyHasOneElement().not()) {
        return false
    }
    if (get(0).isJsonPrimitive || get(0).isJsonObject || get(0).isJsonNull) {
        return true
    }
    return get(0).asJsonArray.onlyHasOneElementRecursive()
}


/**
 * if Multidimensional Arrays only has one element
 */
tailrec fun JsonArray.onlyHasOneObjectElementRecursive(): Boolean {
    if (size() == 0) {
        return false
    }
    if (onlyHasOneElement().not()) {
        return false
    }
    if (get(0).isJsonPrimitive || get(0).isJsonNull) {
        return false
    }
    if (get(0).isJsonObject) {
        return true
    }
    return get(0).asJsonArray.onlyHasOneObjectElementRecursive()
}

/**
 * if Multidimensional Arrays only has one dimension contains element and the elements  all are object element
 */
tailrec fun JsonArray.onlyHasOneSubArrayAndAllItemsAreObjectElementRecursive(): Boolean {
    if (size() == 0) {
        return false
    }
    if (onlyHasOneElement().not()) {
        return false
    }
    if (get(0).isJsonPrimitive || get(0).isJsonNull) {
        return false
    }
    if (get(0).isJsonArray && get(0).asJsonArray.allItemAreObjectElement()) {
        return true
    }
    return get(0).asJsonArray.onlyHasOneSubArrayAndAllItemsAreObjectElementRecursive()
}

/**
 * get the type string without '?' character
 */
fun getRawType(outputType: String): String = outputType.replace("?", "").replace(".*\\.".toRegex(), "")

fun getJsonObjectType(type: String): String = DClassName.getName(type)

/**
 * filter out all null json element of JsonArray
 */
fun JsonArray.filterOutNullElement(): JsonArray {
    val jsonElements = filter { it.isJsonNull.not() }
    return JsonArray().apply {
        jsonElements.forEach { jsonElement ->
            add(jsonElement)
        }
    }
}

/**
 * get the indent when generate dart class code
 */
fun getIndent(): String {

    return buildString {

        for (i in 1..ConfigManager.indent) {
            append(" ")
        }
    }
}

/**
 * get the type output to the edit file
 */
fun getOutType(rawType: String, value: Any?): String {
    return when (ConfigManager.propertyTypeStrategy) {
        PropertyTypeStrategy.Nullable -> {
            val innerRawType = rawType.replace("?", "").replace(">", "?>")
            innerRawType.plus("?")
        }

        (PropertyTypeStrategy.AutoDeterMineNullableOrNot) -> {
            if (value == null) {
                rawType.plus("?")
            } else {
                rawType
            }
        }

        else -> {
            rawType.replace("?", "")
        }
    }
}

fun getDefaultValue(propertyType: String): String {
    return when (getRawType(propertyType)) {
        DartClass.INT.name -> 0.toString()
        DartClass.STRING.name -> "\"\""
        DartClass.DOUBLE.name -> 0.0.toString()
        DartClass.BOOLEAN.name -> false.toString()
        else -> ""
    }
}

fun getDefaultValueAllowNull(propertyType: String) =
    if (propertyType.endsWith("?")) "null" else getDefaultValue(propertyType)

fun String.addIndent(n: Int = 1): String =
    this.lines().joinToString("\n") { if (it.isBlank()) it else "${getIndent().repeat(n)}$it" }

fun String.addSymbol(symbol: String): String = "$this$symbol"

fun getCommentCode(comment: String): String {
    return comment.replace(Regex("[\n\r]"), "")
}