package com.lake.json2dart.interceptor

import com.lake.json2dart.extension.getOutType
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass

class PropertyTypeNullableStrategyInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val propertyTypeAppliedWithNullableStrategyProperties = dartClass.properties.map {
                val propertyTypeAppliedWithNullableStrategy = getOutType(it.type, it.originJsonValue)
                it.copy(type = propertyTypeAppliedWithNullableStrategy)
            }
            dartClass.copy(properties = propertyTypeAppliedWithNullableStrategyProperties)
        } else {
            dartClass
        }
    }
}