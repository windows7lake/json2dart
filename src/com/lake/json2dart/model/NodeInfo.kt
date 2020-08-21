package com.lake.json2dart.model

import com.lake.json2dart.generator.toHumpCase

data class NodeInfo(
    val stringRepresentation: String,
    val node: NodeWrapper?,
    val mapDeserialization: String?,
    val mapSerialization: String?,
    val defaultValue: String
) {
    constructor(stringRepresentation: String, name: String, defaultValue: String) : this(
            stringRepresentation,
            null,
            "json['$name']",
            "'$name': this.${toHumpCase(name)}",
            defaultValue
    )
}