package com.lake.json2dart.generator

import com.google.gson.*
import com.lake.json2dart.extension.*
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.ListClass

class ListClassGenerator(private val className: String, jsonArrayString: String) {

    private val tag = "ListClassGenerator"
    private val jsonArray: JsonArray = Gson().fromJson(jsonArrayString, JsonArray::class.java)

    fun generate(): ListClass {
        when {
            jsonArray.size() == 0 -> {
                Log.i("$tag jsonArray size is 0, return ListClass with generic type ANY")
                return ListClass(name = className, generic = DartClass.ANY)
            }
            jsonArray.allItemAreNullElement() -> {
                Log.i("$tag jsonArray allItemAreNullElement, return ListClass with generic type ${DartClass.ANY.name}")
                return ListClass(name = className, generic = DartClass.ANY)
            }
            jsonArray.allElementAreSamePrimitiveType() -> {
                val elementDartClass = jsonArray[0].asJsonPrimitive.toDartType()
                Log.i("$tag jsonArray allElementAreSamePrimitiveType, return ListClass with generic type ${elementDartClass.name}")
                return ListClass(name = className, generic = elementDartClass)
            }
            jsonArray.allItemAreObjectElement() -> {
                val fatJsonObject = getFatJsonObject(jsonArray)
                val itemObjClassName = "${className}Item"
                val dataClassFromJsonObj = DataClassGenerator(itemObjClassName, fatJsonObject).generate()
                Log.i("$tag jsonArray allItemAreObjectElement, return ListClass with generic type ${dataClassFromJsonObj.name}")
                return ListClass(className, dataClassFromJsonObj)
            }
            jsonArray.allItemAreArrayElement() -> {
                val fatJsonArray = getFatJsonArray(jsonArray)
                val itemArrayClassName = "${className}SubList"
                val listClassFromFatJsonArray =
                    ListClassGenerator(itemArrayClassName, fatJsonArray.toString()).generate()
                Log.i("$tag jsonArray allItemAreArrayElement, return ListClass with generic type ${listClassFromFatJsonArray.name}")
                return ListClass(className, listClassFromFatJsonArray)
            }
            else -> {
                Log.i("$tag jsonArray exception shouldn't come here, return ListClass with generic type ANY")
                return ListClass(name = className, generic = DartClass.ANY)
            }
        }
    }

    private fun getFatJsonArray(jsonArray: JsonArray): JsonArray {
        if (jsonArray.size() == 0 || !jsonArray.allItemAreArrayElement()) {
            throw IllegalStateException("input arg jsonArray must not be empty and all element should be json array! ")
        }
        val fatJsonArray = JsonArray()
        jsonArray.forEach {
            fatJsonArray.addAll(it.asJsonArray)
        }
        return fatJsonArray
    }

    /**
     * get a Fat JsonObject whose fields contains all the objects' fields around the objects of the json array
     */
    private fun getFatJsonObject(jsonArray: JsonArray): JsonObject {
        if (jsonArray.size() == 0 || !jsonArray.allItemAreObjectElement()) {
            throw IllegalStateException("input arg jsonArray must not be empty and all element should be json object! ")
        }
        val allFields = jsonArray.flatMap { it.asJsonObject.entrySet().map { entry -> Pair(entry.key, entry.value) } }
        val fatJsonObject = JsonObject()
        allFields.forEach { (key, value) ->
            if (value is JsonNull && fatJsonObject.has(key)) {
                //if the value is null and pre added the same key into the fatJsonObject,
                // then translate it to a new special property to indicate that the property is nullable
                //later will consume this property (do it here[DataClassGeneratorByJSONObject#consumeBackstageProperties])
                // delete it or translate it back to normal property without [BACKSTAGE_NULLABLE_POSTFIX] when consume it
                // and will not be generated in final code
                fatJsonObject.add(key + BACKSTAGE_NULLABLE_POSTFIX, value)
            } else if (value is JsonPrimitive && value.isNumber && fatJsonObject.has(key) && fatJsonObject[key].isJsonPrimitive) {
                // update the number value only when the previous one is not a double value
                // otherwise a Double property could be rewritten to an Int value, then generate wrong property type
                val oldNum: Number = fatJsonObject[key].asJsonPrimitive.asNumber
                if (oldNum.toString().contains('.').not()) {
                    fatJsonObject.add(key, value)
                }
            } else {
                fatJsonObject.add(key, value)
            }
        }
        return fatJsonObject
    }

}