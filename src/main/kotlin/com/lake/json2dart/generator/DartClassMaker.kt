package com.lake.json2dart.generator

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.lake.json2dart.model.dart.DartClass

class DartClassMaker(private val rootClassName: String, private val json: String) {

    fun makeDartClass(): DartClass {
        return when {
            json.isJSONObject() -> DataClassGenerator(
                rootClassName,
                Gson().fromJson(json, JsonObject::class.java)
            ).generate()

            json.isJSONArray() -> ListClassGenerator(rootClassName, json).generate()
            else -> throw IllegalStateException("Can't generate Dart Class from a no JSON Object/Array")
        }
    }

    private fun String.isJSONObject(): Boolean {
        val jsonElement = Gson().fromJson(this, JsonElement::class.java)
        return jsonElement.isJsonObject
    }

    private fun String.isJSONArray(): Boolean {
        val jsonElement = Gson().fromJson(this, JsonElement::class.java)
        return jsonElement.isJsonArray
    }
}