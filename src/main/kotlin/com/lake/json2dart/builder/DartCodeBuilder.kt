package com.lake.json2dart.builder

import com.lake.json2dart.config.ConfigManager
import com.lake.json2dart.extension.*
import com.lake.json2dart.model.clazz.DataClass
import com.lake.json2dart.model.clazz.ListClass
import com.lake.json2dart.model.clazz.Property
import com.lake.json2dart.model.dart.DartClass

data class DartCodeBuilder(
    override val name: String,
    override val properties: List<Property>,
    override val comments: String,
    override val excludedProperties: List<String> = listOf()
) : BaseClassCodeBuilder(
    name,
    properties,
    comments,
    excludedProperties
) {
    constructor(clazz: DataClass) : this(
        clazz.name,
        clazz.properties,
        clazz.comment,
        clazz.excludedProperties
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

    private val nameCamelCase: String
        get() {
            return StringBuilder().run {
                append(name.substring(0, 1).toUpperCase())
                append(name.substring(1))
                toString()
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
            generateCopyWith(this)
            generateClassNested(this)
        }
    }

    private fun generateClassName(sb: StringBuilder) {
        sb.append("class ").append(name.toCamelCase()).append(" {\n")
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
        val header = nameCamelCase.addIndent().addSymbol("({")
        val footer = "});".addIndent()
        sb.append("\n").append(header)
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            var requiredAnnotation = ""
            if (property.typeObject is DataClass || property.typeObject is ListClass) {
                requiredAnnotation = if (ConfigManager.enableNullSafety) "required " else ""
            }
            val code = requiredAnnotation + property.constructorCode()
            sb.append("\n").append(code.addIndent(2))
        }
        sb.append("\n").append(footer).append("\n")
    }

    private fun generateFromJson(sb: StringBuilder) {
        val nullSafety = if (ConfigManager.enableNullSafety) "?" else ""
        val header =
            "factory $nameCamelCase.fromJson(Map<String, dynamic>$nullSafety json) => $nameCamelCase(".addIndent()
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
                sb.append(if (ConfigManager.useGeneric) "asT<Map<String, dynamic>>" else "asMap")
                sb.append("(json, '${property.originName}')),")
            }

            is ListClass -> {
                sb.append(if (ConfigManager.useGeneric) "asT<List>" else "asList")
                sb.append("(json, '${property.originName}').map((e) => ")
                generateFromJsonListSafeConvert(sb, property)
                sb.append(").toList(),")
            }

            else -> {
                if (ConfigManager.useGeneric) {
                    sb.append("asT<${property.type}>(json, '${property.originName}'),")
                } else {
                    when (property.typeObject) {
                        DartClass.INT -> sb.append("asInt")
                        DartClass.DOUBLE -> sb.append("asDouble")
                        DartClass.STRING -> sb.append("asString")
                        DartClass.BOOLEAN -> sb.append("asBool")
                        else -> sb.append("asString")
                    }
                    sb.append("(json, '${property.originName}'),")
                }
            }
        }
    }

    private fun generateFromJsonListSafeConvert(sb: StringBuilder, property: Property) {
        when (val typeObject = (property.typeObject as ListClass).generic) {
            DartClass.INT -> sb.append("int.tryParse(e.toString()) ?? 0")
            DartClass.DOUBLE -> sb.append("double.tryParse(e.toString()) ?? 0.0")
            DartClass.STRING -> sb.append("e.toString()")
            DartClass.BOOLEAN -> sb.append("bool.fromEnvironment(e.toString()) ?? false")
            is DataClass -> sb.append("${typeObject.name.toCamelCase()}.fromJson(e)")
            else -> sb.append("e.toString()")
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
            is DataClass -> sb.append("${typeObject.name.toCamelCase()}.fromJson(e)")
            else -> sb.append("e")
        }
    }

    private fun generateToJson(sb: StringBuilder) {
        val header = "Map<String, dynamic> toJson() => {".addIndent()
        val footer = "};".addIndent()
        sb.append("\n").append(header)
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val originName = "'${property.originName}': ".addIndent(2)
            sb.append("\n").append(originName).append(property.name)
            if (property.typeObject is DataClass) {
                if (!ConfigManager.enableNullSafety) sb.append("?")
                sb.append(".toJson()")
            }
            if (property.typeObject is ListClass) {
                val typeObject = (property.typeObject as ListClass).generic
                val element = if (typeObject is DataClass) "e.toJson()" else "e"
                if (!ConfigManager.enableNullSafety) sb.append("?")
                sb.append(".map((e) => $element).toList()")
            }
            sb.append(",")
        }
        sb.append("\n").append(footer).append("\n")
    }

    private fun generateCopyWith(sb: StringBuilder) {
        if (!ConfigManager.needCopyWithMethod) return
        sb.append("\n")
        sb.append(nameCamelCase.addIndent()).append(" copyWith({")
        sb.append("\n")
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val addIndentCode = property.copyWithCode()
            sb.append(addIndentCode.addIndent(2)).append("\n")
        }
        sb.append("}) {".addIndent())
        sb.append("\n")
        sb.append("return ".addIndent(2)).append(nameCamelCase.addSymbol("("))
        sb.append("\n")
        properties.filterNot { excludedProperties.contains(it.name) }.forEach { property ->
            val addIndentCode = property.copyWithConstructorCode()
            sb.append(addIndentCode.addIndent(3)).append("\n")
        }
        sb.append(");".addIndent(2))
        sb.append("\n")
        sb.append("}".addIndent())
        sb.append("\n")
    }

    private fun generateClassNested(sb: StringBuilder) {
        sb.append("}").append("\n\n")
        if (referencedClasses.isNotEmpty()) {
            val nestedClassesCode = referencedClasses.map { it.getCode() }.filter { it.isNotBlank() }.joinToString("\n")
            sb.append(nestedClassesCode)
        }
    }
}
