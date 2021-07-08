package com.lake.json2dart.model.dart

import com.lake.json2dart.extension.IgnoreCaseStringSet
import com.lake.json2dart.extension.Log
import com.lake.json2dart.interceptor.IDartClassInterceptor

/**
 * Dart Class Interface
 */
interface DartClass {
    /**
     * the name of this class
     */
    val name: String

    /**
     *  the imported classes used in this class
     */
    val referencedClasses: List<DartClass>

    /**
     * get the code (include referenced classes) string for writing into file or printing out
     */
    fun getCode(): String

    /**
     * only the current class code not include the referenced class for writing into file or printing out
     */
    fun getOnlyCurrentCode(): String

    /**
     * rename this class
     */
    fun rename(newName: String): DartClass

    /**
     * Obtain all the dart class reference by this class and referenced class by referenced class Recursively
     */
    fun getAllRefClassesRecursively(): List<DartClass> {
        val allRefClasses = mutableListOf<DartClass>()
        if (referencedClasses.isEmpty()) {
            return allRefClasses
        }
        allRefClasses.addAll(referencedClasses)
        Log.i("getAllRefClassesRecursively added referenced class ${referencedClasses.map { it.name }}")
        allRefClasses.addAll(referencedClasses.flatMap { it.getAllRefClassesRecursively() })
        return allRefClasses
    }

    /**
     * Keep all class name inside this Dart Data Class unique against the [existClassNames]
     */
    fun resolveNameConflicts(existClassNames: IgnoreCaseStringSet = IgnoreCaseStringSet()): DartClass {
        var thisNoneConflictName = name
        if (existClassNames.contains(thisNoneConflictName)) {
            thisNoneConflictName = getNoneConflictClassName(existClassNames, name)
        }
        existClassNames.add(thisNoneConflictName)
        val classReplaceRule = referencedClasses.associateWith { it.resolveNameConflicts(existClassNames) }
        return rename(thisNoneConflictName)//.replaceReferencedClasses(classReplaceRule)
    }

    fun <T : DartClass> applyInterceptors(enabledDartClassInterceptors: List<IDartClassInterceptor<T>>): DartClass =
        this

    fun <T : DartClass> applyInterceptor(classInterceptor: IDartClassInterceptor<T>): DartClass =
        applyInterceptors(listOf(classInterceptor))

    private fun getNoneConflictClassName(existClassNames: Set<String>, conflictClassName: String): String {
        var newNoneConflictClassName = conflictClassName
        while (existClassNames.contains(newNoneConflictClassName)) {
            newNoneConflictClassName += "Rename"
        }
        return newNoneConflictClassName
    }

    companion object {
        val ANY = object : DartTypeClass() {
            override val name: String = "dynamic"
        }
        val STRING = object : DartTypeClass() {
            override val name: String = "String"
        }
        val BOOLEAN = object : DartTypeClass() {
            override val name: String = "bool"
        }
        val INT = object : DartTypeClass() {
            override val name: String = "int"
        }
        val DOUBLE = object : DartTypeClass() {
            override val name: String = "double"
        }
        val OBJECT = object : DartTypeClass() {
            override val name: String = "object"
        }
    }
}