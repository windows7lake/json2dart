package com.lake.json2dart.interceptor

import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.dart.DartClass

class CommentOffInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val newProperty = dartClass.properties.map {
                it.copy(comment = "")
            }

            dartClass.copy(properties = newProperty)
        } else {
            dartClass
        }
    }
}