package com.lake.json2dart.model.clazz

import com.lake.json2dart.builder.CodeBuilderFactory
import com.lake.json2dart.builder.ICodeBuilder
import com.lake.json2dart.builder.TypeClass
import com.lake.json2dart.interceptor.IDartClassInterceptor
import com.lake.json2dart.model.dart.DartClass

data class DataClass(
    override val name: String,
    val properties: List<Property> = listOf(),
    val comment: String = "",
    val excludedProperties: List<String> = listOf()
) : DartClass {
    private val codeBuilder: ICodeBuilder by lazy { CodeBuilderFactory.get(TypeClass, this) }

    override val referencedClasses: List<DartClass>
        get() {
            return properties.flatMap { property ->
                mutableListOf(property.typeObject).apply {
                    addAll(property.typeObject.getAllRefClassesRecursively())
                }
            }
        }

    override fun getOnlyCurrentCode(): String {
        return codeBuilder.getOnlyCurrentCode()
    }

    override fun getCode(): String {
        return codeBuilder.getCode()
    }

    override fun rename(newName: String): DartClass = copy(name = newName)

    override fun <T : DartClass> applyInterceptors(enabledDartClassInterceptors: List<IDartClassInterceptor<T>>): DartClass {
        val newProperties = mutableListOf<Property>()
        properties.forEach {
            newProperties.add(it.copy(typeObject = it.typeObject.applyInterceptors(enabledDartClassInterceptors)))
        }
        var newDartDataClass: DartClass = copy(properties = newProperties)
        enabledDartClassInterceptors.forEach {
            newDartDataClass = it.intercept(newDartDataClass)
        }
        return newDartDataClass
    }
}