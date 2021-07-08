package com.lake.json2dart.generator

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.interceptor.IDartClassInterceptor
import com.lake.json2dart.interceptor.InterceptorManager
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.ListClass

class DartClassCodeMaker(private val dartClass: DartClass) {

    fun makeDartClassCode(): String {
        val interceptors = InterceptorManager.getEnabledDartDataClassInterceptors()
        return makeDartClassCode(interceptors)
    }

    private fun makeDartClassCode(interceptors: List<IDartClassInterceptor<DartClass>>): String {
        var dartClassForCodeGenerate = dartClass
        dartClassForCodeGenerate = dartClassForCodeGenerate.applyInterceptors(interceptors)
        val listCode = makeListClassRootCode()
        val code = if (ConfigManager.isNestedClassModel) {
            dartClassForCodeGenerate.getCode()
        } else {
            val resolveNameConflicts = dartClassForCodeGenerate.resolveNameConflicts()
            val allRefClassesRecursively = resolveNameConflicts.getAllRefClassesRecursively()
            allRefClassesRecursively.joinToString("\n\n") { it.getOnlyCurrentCode() }
        }
        return "$listCode\n\n$code"
    }

    private fun makeListClassRootCode(): String {
        return if (dartClass is ListClass) {
            """
            final jsonList = json.decode(jsonStr) as List;
            final list = jsonList.map((e) => ${dartClass.generic.name}.fromJson(e)).toList();
            """.trimIndent()
        } else ""
    }
}