package com.lake.json2dart.builder

import com.lake.json2dart.model.clazz.Property

abstract class BaseClassCodeBuilder(
    override val name: String,
    open val properties: List<Property> = listOf(),
    open val comments: String = "",
    open val excludedProperties: List<String> = listOf(),
) : ICodeBuilder