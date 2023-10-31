package com.lake.json2dart.extension

fun String.toCamelCase(): String {
    val value = this
    return StringBuilder().run {
        val list = value.split("_")
        if (list.size == 1) {
            val item = list[0]
            if (item.length > 1) {
                append(item.substring(0, 1).toUpperCase())
                append(item.substring(1))
            } else {
                append(item.toUpperCase())
            }
        } else {
            for (item in list) {
                if (item.length < 2) {
                    append(item.toUpperCase())
                } else {
                    append(item.substring(0, 1).toUpperCase())
                    append(item.substring(1).toLowerCase())
                }
            }
        }
        toString()
    }
}

fun String.firstCharacterUpperCase(): String {
    val value = this
    return StringBuilder().run {
        append(value.substring(0, 1).toUpperCase())
        append(value.substring(1))
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