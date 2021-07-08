package com.lake.json2dart.interceptor

import com.lake.json2dart.model.dart.DartClass

/**
 * Interceptor for dart class code transform
 */
interface IDartClassInterceptor<out T : DartClass> {

    /**
     * intercept the dart class and modify the class,the function will return a new Dart Class Object
     * warn: the new returned object is a new object ,not the original
     */
    fun intercept(dartClass: DartClass): T
}