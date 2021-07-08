package com.lake.json2dart.interceptor

import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass

class FilterUnselectedPropertyInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val newProperties = dartClass.properties.filter { property ->
                property.selected
            }
            dartClass.copy(properties = newProperties)
        } else {
            dartClass
        }
    }
}