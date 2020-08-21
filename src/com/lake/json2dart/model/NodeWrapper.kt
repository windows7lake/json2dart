package com.lake.json2dart.model

import com.fasterxml.jackson.databind.JsonNode
import com.lake.json2dart.generator.toClassName
import com.lake.json2dart.generator.toHumpCase
import com.lake.json2dart.generator.toSneakCase

data class NodeWrapper(
    val node: JsonNode?,
    val fieldName: String,
    val sneakCaseName: String = toSneakCase(fieldName),
    val humpCaseName: String = toHumpCase(fieldName),
    val className: String = toClassName(fieldName)
)