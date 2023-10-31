package com.lake.json2dart.builder

import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.ListClass
import com.lake.json2dart.model.dart.DartClass

/**
 * support element type
 */
sealed class ElementType
object TypeClass : ElementType()
object TypeList : ElementType()

/**
 * support language
 */
sealed class Language
object LanguageDart : Language()

/**
 * Created by Nstd on 2020/6/30 17:50.
 */
object CodeBuilderFactory {

    /**
     * language config key, if new language is added, extract this key to the extension
     */
    private const val CONF_LANGUAGE = "code.builder.language"

    fun <T : DartClass> get(type: ElementType, data: T): ICodeBuilder {
        val lang: Language = CodeBuilderConfig.instance.getConfig(CONF_LANGUAGE, LanguageDart)
        return when (type) {
            TypeClass -> getClassCodeBuilder(lang, data as DataClass)
            TypeList -> getListCodeBuilder(lang, data as ListClass)
        }
    }

    private fun getClassCodeBuilder(lang: Language, data: DataClass): ICodeBuilder {
        return when (lang) {
            LanguageDart -> DartCodeBuilder(data)
        }
    }

    private fun getListCodeBuilder(lang: Language, data: ListClass): ICodeBuilder {
        return when (lang) {
            LanguageDart -> DartListCodeBuilder(data)
        }
    }
}