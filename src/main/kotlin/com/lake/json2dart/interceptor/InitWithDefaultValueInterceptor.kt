package com.lake.json2dart.interceptor

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.extension.getDefaultValue
import com.lake.json2dart.extension.getDefaultValueAllowNull
import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.strategy.DefaultValueStrategy

class InitWithDefaultValueInterceptor : IDartClassInterceptor<DartClass> {

    override fun intercept(dartClass: DartClass): DartClass {
        return if (dartClass is DataClass) {
            val dataClass: DataClass = dartClass

            val initWithDefaultValueProperties = dataClass.properties.map {
                var initDefaultValue =
                    if (ConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) getDefaultValue(it.type)
                    else getDefaultValueAllowNull(it.type)
                if (it.value.isNotEmpty()) initDefaultValue = it.value
                it.copy(value = initDefaultValue)
            }

            dataClass.copy(properties = initWithDefaultValueProperties)
        } else {
            dartClass
        }
    }
}