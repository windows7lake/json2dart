package com.lake.json2dart.interceptor

import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass

class CamelCaseSupportInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val originProperties = dartClass.properties
            val newProperties = originProperties.map {
                val oldName = it.name
                if (oldName.isNotEmpty() && oldName.contains("_")) {
                    val newName = StringBuilder().run {
                        val list = oldName.split("_")
                        for (s in list) {
                            if (this.isEmpty()) {
                                append(s)
                            } else {
                                append(s.substring(0, 1).toUpperCase())
                                append(s.substring(1).toLowerCase())
                            }
                        }
                        if (this.toString() == "Map" || this.toString() == "map" ||
                            this.toString() == "List" || this.toString() == "list") {
                            append("2")
                        }
                        toString()
                    }
                    it.copy(name = newName)
                } else it
            }
            dartClass.copy(properties = newProperties)
        } else {
            dartClass
        }
    }
}