package com.lake.json2dart.model.dart

abstract class DartTypeClass: DartClass {
    override val referencedClasses: List<DartClass> = listOf()
    override fun getCode() = throw UnsupportedOperationException("Don't support this function called on DartTypeClass Class")
    override fun getOnlyCurrentCode() = throw UnsupportedOperationException("Don't support this function called on DartTypeClass Class")
    override fun rename(newName: String) = throw UnsupportedOperationException("Don't support this function called on DartTypeClass Class")
}