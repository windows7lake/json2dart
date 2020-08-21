package com.lake.json2dart.generator

import java.io.File

fun toClassName(value: String) =
        value[0].toUpperCase() + toHumpCase(value.slice(1 until value.length))

fun toSneakCase(value: String): String {
    return value.fold(StringBuilder()) { builder, c ->
        if (c.isUpperCase()) {
            builder.append("_").append(c.toLowerCase())
        } else {
            builder.append(c)
        }
    }.toString()
}

fun toHumpCase(value: String): String {
    var shouldUpperCase: Boolean = false
    return value.fold(StringBuilder()) { builder, c ->
        if (c == '_') {
            shouldUpperCase = true
            builder.append("")
        } else {
            val result = if (shouldUpperCase) c.toUpperCase() else c
            shouldUpperCase = false
            builder.append(result)
        }
    }.toString()
}

fun extractPackageName(file: File): String {
    val absolutePath = file.absolutePath
    val splitPathList = absolutePath.split(if (isWindows()) "\\" else "/")
    val libIndex = splitPathList.indexOf("lib")
    if (libIndex == -1) throw NotFlutterProject()
    val fold = splitPathList
            .subList(libIndex + 1, splitPathList.size)
            .fold(StringBuilder()) { builder, s -> builder.append(s).append("/") }
    return "package:${splitPathList[libIndex - 1]}/$fold"
}

fun isWindows(): Boolean =
        System.getProperty("os.name").contains("Windows")