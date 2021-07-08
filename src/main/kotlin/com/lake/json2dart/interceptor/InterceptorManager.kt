package com.lake.json2dart.interceptor

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.strategy.DefaultValueStrategy

object InterceptorManager {

    fun getEnabledDartDataClassInterceptors(): List<IDartClassInterceptor<DartClass>> {

        return mutableListOf<IDartClassInterceptor<DartClass>>().apply {
            add(FilterUnselectedPropertyInterceptor())
            add(PropertyTypeNullableStrategyInterceptor())

            if (ConfigManager.defaultValueStrategy != DefaultValueStrategy.None) {
                add(InitWithDefaultValueInterceptor())
            }

            if (ConfigManager.isCommentOff) {
                add(CommentOffInterceptor())
            }

            if (ConfigManager.isOrderByAlphabetical) {
                add(OrderPropertyByAlphabeticalInterceptor())
            }

            if (ConfigManager.enableCamelCaseSupport) {
                add(CamelCaseSupportInterceptor())
            }
        }
    }
}