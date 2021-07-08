package com.lake.json2dart.generator

import com.google.gson.JsonObject
import com.lake.json2dart.extension.*
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.Property

class DataClassGenerator(private val className: String, private val jsonObject: JsonObject) {

    fun generate(): DataClass {
        val properties = mutableListOf<Property>()

        jsonObject.entrySet().forEach { (jsonKey, jsonValue) ->
            when {
                jsonValue.isJsonNull -> {
                    val jsonValueNullProperty =
                        Property(
                            originName = jsonKey,
                            originJsonValue = null,
                            type = DartClass.ANY.name,
                            comment = "null",
                            typeObject = DartClass.ANY
                        )
                    properties.add(jsonValueNullProperty)
                }
                jsonValue.isJsonPrimitive -> {
                    val type = jsonValue.asJsonPrimitive.toDartType()
                    val jsonValuePrimitiveProperty =
                        Property(
                            originName = jsonKey,
                            originJsonValue = jsonValue.asString,
                            type = type.name,
                            comment = jsonValue.asString,
                            typeObject = type
                        )
                    properties.add(jsonValuePrimitiveProperty)
                }
                jsonValue.isJsonArray -> {
                    val arrayType = ListClassGenerator(jsonKey.toCamelCase(), jsonValue.toString()).generate()
                    val arrayTypeName = "List<${arrayType.generic.name}>"
                    val jsonValueArrayProperty =
                        Property(originName = jsonKey, value = "", type = arrayTypeName, typeObject = arrayType)
                    properties.add(jsonValueArrayProperty)
                }
                jsonValue.isJsonObject -> {
                    jsonValue.asJsonObject.run {
                        var refDataClass: DataClass? = null
                        val type = getJsonObjectType(jsonKey)
                        val targetJsonElement =
                            TargetJsonElement(this).getTargetJsonElementForGeneratingCode()
                        if (targetJsonElement.isJsonObject) {
                            refDataClass = DataClassGenerator(
                                getRawType(type),
                                targetJsonElement.asJsonObject
                            ).generate()
                        } else {
                            throw IllegalStateException("Don't support No JSON Object Type for Generate Dart Data Class")
                        }

                        val jsonValueObjectProperty = Property(
                            originName = jsonKey,
                            originJsonValue = "",
                            type = type,
                            typeObject = refDataClass
                        )
                        properties.add(jsonValueObjectProperty)
                    }
                }
            }
        }

        val propertiesAfterConsumeBackStageProperties = properties.consumeBackstageProperties()
        return DataClass(name = className, properties = propertiesAfterConsumeBackStageProperties)
    }

    /**
     * Consume the properties whose name end with [BACKSTAGE_NULLABLE_POSTFIX],
     * After call this method, all properties's name end with [BACKSTAGE_NULLABLE_POSTFIX] will be removed
     * And the corresponding properies whose name without 【BACKSTAGE_NULLABLE_POSTFIX】it's value will be set
     * to null,for example:
     * remove property -> name = demoProperty__&^#
     * set null value  -> demoProperty.value = null
     */
    private fun List<Property>.consumeBackstageProperties(): List<Property> {
        val newProperties = mutableListOf<Property>()
        val nullableBackstagePropertiesNames = filter { it.name.endsWith(BACKSTAGE_NULLABLE_POSTFIX) }.map { it.name }
        val nullablePropertiesNames =
            nullableBackstagePropertiesNames.map { it.removeSuffix(BACKSTAGE_NULLABLE_POSTFIX) }
        forEach {
            when {
                nullablePropertiesNames.contains(it.name) -> newProperties.add(
                    it.copy(
                        originJsonValue = null,
                        value = ""
                    )
                )
                nullableBackstagePropertiesNames.contains(it.name) -> {
                    //when hit the backstage property just continue, don't add it to new properties
                }
                else -> newProperties.add(it)
            }
        }
        return newProperties
    }
}