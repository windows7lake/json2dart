package com.lake.json2dart.extension

fun String.toCamelCase(): String {
    val value = this
    return StringBuilder().run {
        val list = value.split("_")
        for (item in list) {
            append(item.substring(0, 1).toUpperCase())
            append(item.substring(1).toLowerCase())
        }
        toString()
    }
}

fun String.toCamelCaseWithoutFirstCharacter(): String {
    val value = this
    return StringBuilder().run {
        val list = value.split("_")
        for (item in list) {
            if (this.isEmpty()) {
                append(item)
            } else {
                append(item.substring(0, 1).toUpperCase())
                append(item.substring(1).toLowerCase())
            }
        }
        toString()
    }
}