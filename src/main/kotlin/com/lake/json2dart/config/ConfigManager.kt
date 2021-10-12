package com.lake.json2dart.config

import com.lake.json2dart.model.strategy.DefaultValueStrategy
import com.lake.json2dart.model.strategy.PropertyTypeStrategy

object ConfigManager {

    var indent: Int
        get() = ProjectConfig.indent
        set(value) {
            ProjectConfig.indent = value
        }

    var propertyTypeStrategy: PropertyTypeStrategy
        get() = ProjectConfig.propertyTypeStrategy
        set(value) {
            ProjectConfig.propertyTypeStrategy = value
        }

    var defaultValueStrategy: DefaultValueStrategy
        get() = ProjectConfig.defaultValueStrategy
        set(value) {
            ProjectConfig.defaultValueStrategy = value
        }

    var isCommentOff: Boolean
        get() = ProjectConfig.isCommentOff
        set(value) {
            ProjectConfig.isCommentOff = value
        }

    var isOrderByAlphabetical: Boolean
        get() = ProjectConfig.isOrderByAlphabetical
        set(value) {
            ProjectConfig.isOrderByAlphabetical = value
        }

    var isNestedClassModel: Boolean
        get() = ProjectConfig.isNestedClassModel
        set(value) {
            ProjectConfig.isNestedClassModel = value
        }

    var isFinalProperty: Boolean
        get() = ProjectConfig.isFinalProperty
        set(value) {
            ProjectConfig.isFinalProperty = value
        }

    var enableCamelCaseSupport: Boolean
        get() = ProjectConfig.enableCamelCaseSupport
        set(value) {
            ProjectConfig.enableCamelCaseSupport = value
        }

    var enableSafeConvert: Boolean
        get() = ProjectConfig.enableSafeConvert
        set(value) {
            ProjectConfig.enableSafeConvert = value
        }

    var generateSafeConvertFile: Boolean
        get() = ProjectConfig.generateSafeConvertFile
        set(value) {
            ProjectConfig.generateSafeConvertFile = value
        }

    var enableNullSafety: Boolean
        get() = ProjectConfig.enableNullSafety
        set(value) {
            ProjectConfig.enableNullSafety = value
        }

    var useGeneric: Boolean
        get() = ProjectConfig.useGeneric
        set(value) {
            ProjectConfig.useGeneric = value
        }
}