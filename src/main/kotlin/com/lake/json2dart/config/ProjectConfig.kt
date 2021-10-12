package com.lake.json2dart.config

import com.lake.json2dart.model.strategy.DefaultValueStrategy
import com.lake.json2dart.model.strategy.PropertyTypeStrategy

const val PLUGIN_NAME = "JSON To Dart"

object ProjectConfig {
    var enableTestModel = false
    var isCommentOff = false
    var isOrderByAlphabetical = false
    var isNestedClassModel = true
    var isFinalProperty = true
    var enableCamelCaseSupport = true
    var enableSafeConvert = true
    var generateSafeConvertFile = true
    var enableNullSafety = true
    var useGeneric = false
    var indent: Int = 2
    var propertyTypeStrategy = PropertyTypeStrategy.NotNullable
    var defaultValueStrategy = DefaultValueStrategy.AvoidNull

    fun setupTestInitState() {
        enableTestModel = true
        indent = 2
    }
}