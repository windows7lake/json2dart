package com.lake.json2dart.builder

import com.lake.json2dart.model.clazz.ListClass

data class DartListCodeBuilder(
    val clazz: ListClass
) : BaseListCodeBuilder(
    clazz.name,
    clazz.referencedClasses
) {
    private val dartClass = clazz.generic

    override fun getOnlyCurrentCode(): String {
        return dartClass.getOnlyCurrentCode()
    }

    override fun getCode(): String {
        return dartClass.getCode()
    }
}