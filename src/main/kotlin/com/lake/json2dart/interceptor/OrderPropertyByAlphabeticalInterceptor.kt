package com.lake.json2dart.interceptor

import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.dart.DartClass

class OrderPropertyByAlphabeticalInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val orderByAlphabeticalProperties = dartClass.properties.sortedBy { it.name }
            return dartClass.copy(properties = orderByAlphabeticalProperties)
        } else {
            dartClass
        }
    }
}