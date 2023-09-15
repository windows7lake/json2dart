package com.lake.json2dart.generator

import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.ListClass
import com.lake.json2dart.model.dart.DartClass

class DisplayClassMaker(private val clazz: DartClass) {

    private var list = mutableListOf<DisplayClass>()

    fun makeDisplayClassList(): List<DisplayClass> {
        parseDartClass(clazz)
        return list
    }

    private fun parseDartClass(dartClass: DartClass) {
        val displayClass: DisplayClass = when (dartClass) {
            is DataClass -> {
                dartClass.properties
                    .filterNot { dartClass.excludedProperties.contains(it.name) }
                    .forEach { property ->
                        if (property.typeObject is DataClass || property.typeObject is ListClass) {
                            parseDartClass(property.typeObject)
                        } else {
                            list.add(
                                DisplayClass(
                                    originName = property.originName,
                                    name = property.name,
                                    type = property.type,
                                    value = property.value,
                                    comment = property.comment,
                                    typeObject = property.typeObject
                                )
                            )
                        }
                    }
                DisplayClass(
                    originName = dartClass.name,
                    name = dartClass.name,
                    type = "object",
                    comment = dartClass.comment,
                    typeObject = DartClass.OBJECT
                )
            }
            is ListClass -> {
                parseDartClass(dartClass.generic)
                DisplayClass(
                    originName = dartClass.name,
                    name = dartClass.name,
                    type = "list",
                    typeObject = DartClass.OBJECT
                )
            }
            else -> {
                DisplayClass(
                    originName = dartClass.name,
                    name = dartClass.name,
                    type = "dynamic",
                    typeObject = DartClass.ANY
                )
            }
        }
        list.add(displayClass)
    }
}

data class DisplayClass(
    val originName: String,
    val name: String = originName,
    val type: String,
    val value: String = "",
    val comment: String = "",
    val typeObject: DartClass
)