package com.lake.json2dart.builder

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.extension.addIndent
import com.lake.json2dart.extension.addSymbol
import com.lake.json2dart.extension.getCommentCode
import com.lake.json2dart.extension.getIndent
import com.lake.json2dart.model.dart.DartClass
import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.ListClass
import com.lake.json2dart.model.clazz.Property

data class DartCodeBuilder(
    override val name: String,
    override val properties: List<Property>,
    override val comments: String,
    override val excludedProperties: List<String> = listOf(),
) : BaseClassCodeBuilder(
    name,
    properties,
    comments,
    excludedProperties,
) {
    constructor(clazz: DataClass) : this(
        clazz.name,
        clazz.properties,
        clazz.comment,
        clazz.excludedProperties,
    )

    private val referencedClasses: List<DartClass>
        get() {
            return properties
                .filter { it.typeObject is DataClass || it.typeObject is ListClass }
                .flatMap { property ->
                    mutableListOf(property.typeObject).apply {
                        if (property.typeObject == DartClass.ANY) {
                            addAll(property.typeObject.getAllRefClassesRecursively())
                        }
                    }
                }
        }

    override fun getOnlyCurrentCode(): String {
        val newProperties = properties.map { it.copy(typeObject = DartClass.ANY) }
        return copy(properties = newProperties).getCode()
    }

    override fun getCode(): String {
        return buildString {
            generateClassName(this)
            generateClassProperties(this)
            generateClassConstructor(this)
            generateFromJson(this)
            generateToJson(this)
            generateClassNested(this)
        }
    }

    private fun generateClassName(sb: StringBuilder) {
        sb.append("class ").append(name).append(" {\n")
    }

    private fun generateClassProperties(sb: StringBuilder) {
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val addIndentCode = property.propertyCode()
            val commentCode = getCommentCode(property.comment)

            if (commentCode.isNotBlank()) {
                sb.append(getIndent()).append("// ").append(commentCode).append("\n")
            }
            sb.append(getIndent())
            if (ConfigManager.isFinalProperty) sb.append("final ")
            sb.append(addIndentCode).append("\n")
        }
    }

    private fun generateClassConstructor(sb: StringBuilder) {
        val header = name.addIndent().addSymbol("({")
        val footer = "});".addIndent()
        sb.append("\n").append(header)
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val code = property.constructorCode().addIndent(2)
            sb.append("\n").append(code)
        }
        sb.append("\n").append(footer).append("\n")
    }

    private fun generateFromJson(sb: StringBuilder) {
        val header = "factory $name.fromJson(Map<String, dynamic> json) => $name(".addIndent()
        val footer = ");".addIndent()
        sb.append("\n").append(header)
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val name = property.name.addIndent(2).addSymbol(": ")
            sb.append("\n").append(name)
            if (ConfigManager.enableSafeConvert) {
                generateFromJsonSafeProperties(sb, property)
            } else {
                generateFromJsonProperties(sb, property)
            }
        }
        sb.append("\n").append(footer).append("\n")
    }

    private fun generateFromJsonSafeProperties(sb: StringBuilder, property: Property) {
        when (property.typeObject) {
            is DataClass -> {
                sb.append(property.type).append(".fromJson(")
                sb.append("asT<Map>(json, '${property.originName}')),")
            }
            is ListClass -> {
                sb.append("asT<List>(json, '${property.originName}')?.map((e) => ")
                generateFromJsonListConvert(sb, property)
                sb.append(")?.toList(),")
            }
            else -> {
                sb.append("asT<${property.type}>(json, '${property.originName}'),")
            }
        }
    }

    private fun generateFromJsonProperties(sb: StringBuilder, property: Property) {
        when (property.typeObject) {
            is DataClass -> {
                sb.append(property.type).append(".fromJson(json['${property.originName}']),")
            }
            is ListClass -> {
                sb.append("json['${property.originName}']?.map((e) => ")
                generateFromJsonListConvert(sb, property)
                sb.append(")?.toList(),")
            }
            else -> {
                sb.append("json['${property.originName}'],")
            }
        }
    }

    private fun generateFromJsonListConvert(sb: StringBuilder, property: Property) {
        when (val typeObject = (property.typeObject as ListClass).generic) {
            DartClass.INT -> sb.append("int.tryParse(e.toString()) ?? 0")
            DartClass.DOUBLE -> sb.append("double.tryParse(e.toString()) ?? 0.0")
            DartClass.STRING -> sb.append("e.toString()")
            DartClass.BOOLEAN -> sb.append("bool.fromEnvironment(e.toString()) ?? false")
            is DataClass -> sb.append("${typeObject.name}.fromJson(e)")
        }
    }

    private fun generateToJson(sb: StringBuilder) {
        val header = "Map<String, dynamic> toJson() => {".addIndent()
        val footer = "};".addIndent()
        sb.append("\n").append(header)
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val originName = "'${property.originName}': this.".addIndent(2)
            sb.append("\n").append(originName).append(property.name)
            if (property.typeObject is DataClass) sb.append("?.toJson()")
            if (property.typeObject is ListClass) sb.append("?.map((e) => e.toJson())")
            sb.append(",")
        }
        sb.append("\n").append(footer).append("\n")
    }

    private fun generateClassNested(sb: StringBuilder) {
        sb.append("}").append("\n\n")
        if (referencedClasses.isNotEmpty()) {
            val nestedClassesCode = referencedClasses.map { it.getCode() }.filter { it.isNotBlank() }.joinToString("\n")
            sb.append(nestedClassesCode)
        }
    }
}
