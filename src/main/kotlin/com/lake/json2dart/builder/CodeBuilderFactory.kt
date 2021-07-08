package com.lake.json2dart.builder

import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.ListClass

/**
 * support element type
 */
sealed class ElementType
object TYPE_CLASS : ElementType()
object TYPE_LIST  : ElementType()

/**
 * support language
 */
sealed class Language
object LANG_DART : Language()

/**
 * Created by Nstd on 2020/6/30 17:50.
 */
object CodeBuilderFactory {

    /**
     * language config key, if new language is added, extract this key to the extension
     */
    private const val CONF_LANGUAGE = "code.builder.language"

    fun <T : DartClass> get(type: ElementType, data: T): ICodeBuilder {
        val lang: Language = CodeBuilderConfig.instance.getConfig(CONF_LANGUAGE, LANG_DART)
        return when(type) {
            TYPE_CLASS -> getClassCodeBuilder(lang, data as DataClass)
            TYPE_LIST  -> getListCodeBuilder(lang, data as ListClass)
        }
    }

    private fun getClassCodeBuilder(lang: Language, data: DataClass): ICodeBuilder {
        return when(lang) {
            LANG_DART -> DartCodeBuilder(data)
        }
    }

    private fun getListCodeBuilder(lang: Language, data: ListClass): ICodeBuilder {
        return when(lang) {
            LANG_DART -> DartListCodeBuilder(data)
        }
    }
}