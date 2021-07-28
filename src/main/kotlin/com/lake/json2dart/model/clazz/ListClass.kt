package com.lake.json2dart.model.clazz

import com.lake.json2dart.builder.CodeBuilderFactory
import com.lake.json2dart.builder.ICodeBuilder
import com.lake.json2dart.builder.TYPE_LIST
import com.lake.json2dart.interceptor.IDartClassInterceptor
import com.lake.json2dart.model.dart.DartClass

data class ListClass(
    override val name: String,
    val generic: DartClass,
    override val referencedClasses: List<DartClass> = listOf(generic)
) : DartClass {

    private val codeBuilder: ICodeBuilder by lazy { CodeBuilderFactory.get(TYPE_LIST, this) }

    override fun getOnlyCurrentCode(): String {
        return codeBuilder.getOnlyCurrentCode()
    }

    override fun rename(newName: String) = copy(name = newName)

    override fun getCode(): String {
        return codeBuilder.getCode()
    }

    override fun <T : DartClass> applyInterceptors(enabledDartClassInterceptors: List<IDartClassInterceptor<T>>): DartClass {
        val newGenerics = generic.applyInterceptors(enabledDartClassInterceptors)
        val newImportedClasses = referencedClasses.map { it.applyInterceptors(enabledDartClassInterceptors) }
        return copy(generic = newGenerics, referencedClasses = newImportedClasses)
    }
}