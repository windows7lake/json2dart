package com.lake.json2dart.model.clazz

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.extension.toCamelCaseWithoutFirstCharacter
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.treetable.CellProvider
import com.lake.json2dart.treetable.Selector

data class Property(
    val originName: String,
    val originJsonValue: String? = "",
    var name: String = originName.toCamelCaseWithoutFirstCharacter(),
    var type: String,
    var value: String = "",
    var comment: String = "",
    var typeObject: DartClass,
    var selected: Boolean = true
) : Selector, CellProvider {

    fun propertyCode(): String {
        return buildString {
            append(type)
            append(if (!ConfigManager.enableNullSafety && !isPrimitiveType()) "?" else "")
            append(" ").append(name).append(";")
        }
    }

    fun constructorCode(): String {
        return buildString {
            append("this.").append(name)
            if (value != "") append(" = ").append(value)
            append(",")
        }
    }

    fun copyWithCode(): String {
        return buildString {
            append(type)
            append("? ").append(name).append(",")
        }
    }

    fun copyWithConstructorCode(): String {
        return buildString {
            append(name).append(": ")
            append(name).append(" ?? this.")
            append(name).append(",")
        }
    }

    override fun getCellValue(columnIndex: Int): String {
        return when (columnIndex) {
            // index 0: Json Key
            0 -> originName
            // index 1: Class Type
            1 -> type
            // index 2: Dart Class/Property Name
            2 -> name
            // index 3: Default Value
            3 -> value
            // index 4: Comment
            4 -> comment

            else -> ""
        }
    }

    override fun setValueAt(columnIndex: Int, value: String) {
        when (columnIndex) {
            1 -> {
                this.type = value
            }

            2 -> {
                this.name = value
            }

            3 -> {
                this.value = value
            }

            4 -> {
                this.comment = value
            }
        }
    }

    override fun changeState(selected: Boolean) {
        this.selected = selected
    }

    private fun isPrimitiveType(): Boolean {
        return typeObject == DartClass.INT ||
                typeObject == DartClass.DOUBLE ||
                typeObject == DartClass.STRING ||
                typeObject == DartClass.BOOLEAN
    }
}