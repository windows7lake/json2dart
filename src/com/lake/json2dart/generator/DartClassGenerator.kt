package com.lake.json2dart.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lake.json2dart.delegates.MessageDelegate
import com.lake.json2dart.model.NodeInfo
import com.lake.json2dart.model.NodeWrapper
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*

class DartClassGenerator(private val message: MessageDelegate = MessageDelegate()) {
    private var safeConvert: Boolean = false

    fun generateFromJson(json: String, destinyFile: File, fileName: String, finalField: Boolean, justOneFile: Boolean,
                         createConstructor: Boolean, createDefaultValue: Boolean, safeConvert: Boolean, safeClass: Boolean) {
        if (json.isEmpty()) throw SyntaxException()
        val nodeStack = Stack<NodeWrapper>()
        try {
            // parse json and save node to stack
            nodeStack.add(NodeWrapper(
                    node = jacksonObjectMapper().readTree(json),
                    fieldName = fileName,
                    sneakCaseName = fileName,
                    humpCaseName = fileName,
                    className = fileName
            ))
        } catch (e: Exception) {
            throw SyntaxException()
        }

        this.safeConvert = safeConvert
        val packageName = extractPackageName(destinyFile)
        val finalMode = if (finalField) "final " else ""
        var buffer: FileOutputStream
        var target: FileOutputStream
        var bufferFile: File
        var constructionSB: StringBuilder
        var deserializationSB: StringBuilder
        var serializationSB: StringBuilder
        var nodeWrapper: NodeWrapper

        if (safeClass) generateSafeConvertClass(destinyFile)

        if (nodeStack.isEmpty()) throw SyntaxException()
        nodeWrapper = nodeStack.peek()
        bufferFile = File(destinyFile, "buffer_${nodeWrapper.sneakCaseName}.dart")
        buffer = FileOutputStream(bufferFile)
        target = FileOutputStream(File(destinyFile, "${nodeWrapper.sneakCaseName}.dart"))

        while (nodeStack.isNotEmpty()) {
            nodeWrapper = nodeStack.pop()

            if (!justOneFile) {
                bufferFile = File(destinyFile, "buffer_${nodeWrapper.sneakCaseName}.dart")
                buffer = FileOutputStream(bufferFile)
                target = FileOutputStream(File(destinyFile, "${nodeWrapper.sneakCaseName}.dart"))
            }

            buffer.writeText("class ${nodeWrapper.className} {\n")

            // start parse
            constructionSB = startConstruction(nodeWrapper)
            deserializationSB = startDeserialization(nodeWrapper)
            serializationSB = startSerialization()

            // during parse
            nodeWrapper.node?.fields()?.forEach { field ->
                val nodeInfo: NodeInfo = processNode(buffer, field.value, field.key, finalMode, createDefaultValue)
                nodeInfo.node?.apply {
                    nodeStack.add(this)
                    if (!justOneFile) target.writeText("import '$packageName$sneakCaseName.dart';\n")
                }

                constructionSB.append("\n\t\tthis.${toHumpCase(field.key)},")
                deserializationSB.append("\t${toHumpCase(field.key)} = ")
                        .append(if (safeConvert) safeCovert(nodeInfo, field.key) else "${nodeInfo.mapDeserialization}")
                        .append(",\n")
                serializationSB.append("\t\t\t\t${nodeInfo.mapSerialization},\n")
            }

            // end parse
            constructionSB.append("\n\t});\n")
            deserializationSB.replace(deserializationSB.length - 2, deserializationSB.length - 1, ";")
            serializationSB.append("\t\t\t};\n")

            if (createConstructor) buffer.writeText(constructionSB.toString())
            buffer.writeText(deserializationSB.toString())
            buffer.writeText(serializationSB.toString())
            buffer.writeText("}\n")

            if (!justOneFile) {
                buffer.close()
                mergeBufferToTarget(target, bufferFile)
            }
        }

        if (justOneFile) mergeBufferToTarget(target, bufferFile)
        buffer.close()
        target.close()
    }

    private fun generateSafeConvertClass(destinyFile: File) {
        val targetFile = FileOutputStream(File(destinyFile, "safe_convert.dart"))
        targetFile.writeText(SAFE_CONVERT_CODE)
        targetFile.close()
    }

    private fun startConstruction(nodeWrapper: NodeWrapper) =
            StringBuilder().append("\n\t${nodeWrapper.className}({")

    private fun startDeserialization(nodeWrapper: NodeWrapper) =
            StringBuilder().append("\n\t${nodeWrapper.className}.fromJson(Map<String, dynamic> json)\n\t\t\t:")

    private fun startSerialization() =
            StringBuilder().append("\n\tMap<String, dynamic> toJson() => {\n")

    private fun processNode(buffer: FileOutputStream, node: JsonNode, name: String, finalMode: String,
                            createDefaultValue: Boolean): NodeInfo {
        val nodeInfo = extractNodeInfo(node, name)
        buffer.writeText("\t$finalMode${nodeInfo.stringRepresentation} ${toHumpCase(name)}")
        buffer.writeText(if (createDefaultValue && nodeInfo.defaultValue.isNotEmpty()) " = ${nodeInfo.defaultValue}" else "")
        buffer.writeText(";\n")
        return nodeInfo
    }

    private fun extractNodeInfo(node: JsonNode, name: String): NodeInfo {
        return when {
            node.isDouble || node.isFloat || node.isBigDecimal ->
                NodeInfo("double", name, "0.0")

            node.isShort || node.isInt || node.isLong || node.isBigInteger ->
                NodeInfo("int", name, "0")

            node.isBoolean ->
                NodeInfo("bool", name, "false")

            node.isTextual ->
                NodeInfo("String", name, "\"\"")

            node.isArray ->
                extractArrayData(node as ArrayNode, name)

            node.isObject ->
                NodeWrapper(node, name).toObjectNodeInfo()

            else -> NodeInfo("Object", name, "")
        }
    }

    private fun extractArrayData(node: ArrayNode, name: String): NodeInfo {
        val iterator = node.iterator()
        if (!iterator.hasNext()) {
            return NodeInfo("List<Object>", name, "[]")
        }
        val elementInfo = extractNodeInfo(iterator.next(), name)
        return NodeInfo(
                "List<${elementInfo.stringRepresentation}>",
                elementInfo.node,
                elementInfo.buildListDeserialization(name),
                elementInfo.buildListSerialization(name),
                "[]"
        )
    }

    private fun NodeInfo.buildListDeserialization(rawName: String) =
            if (node != null) {
                if (safeConvert) "SafeManager.parseList(json, '${node.fieldName}')\n" +
                        "\t\t?.map((e) => ${node.className}.fromJson(e))\n" +
                        "\t\t?.toList()"
                else "List<${node.className}>.from(\n" +
                        "\t\tjson['${node.fieldName}'].map((e) => ${node.className}.fromJson(e))," +
                        "\t\n)"
            } else {
                "List<$stringRepresentation>.from(json[\"$rawName\"])"
            }

    private fun NodeInfo.buildListSerialization(rawName: String) =
            if (node != null) {
                "'$rawName': this.${toHumpCase(node.fieldName)}?.map((e) => e.toJson())?.toList()"
            } else {
                "'$rawName': $rawName,"
            }

    private fun NodeWrapper.toObjectNodeInfo() = NodeInfo(
            className,
            this,
            if (safeConvert) "$className.fromJson(\n\tSafeManager.parseObject(json, '$fieldName'),\n)"
            else "$className.fromJson(json['$fieldName'])",
            "'${this.fieldName}': this.${toHumpCase(this.fieldName)}?.toJson()",
            "")

    private fun mergeBufferToTarget(targetStream: FileOutputStream, bufferFile: File) {
        BufferedReader(FileReader(bufferFile)).useLines { lines ->
            lines.forEach { targetStream.writeText(it).writeText("\n") }
        }
        bufferFile.delete()
    }

    private fun safeCovert(nodeInfo: NodeInfo, name: String): String {
        return when (nodeInfo.stringRepresentation) {
            "double" -> "SafeManager.parseDouble(json, '$name')"
            "int" -> "SafeManager.parseInt(json, '$name')"
            "bool" -> "SafeManager.parseBoolean(json, '$name')"
            "String" -> "SafeManager.parseString(json, '$name')"
            else -> nodeInfo.mapDeserialization ?: "json['$name']"
        }
    }

    private fun FileOutputStream.writeText(text: String): FileOutputStream {
        write(text.toByteArray(Charsets.UTF_8))
        return this
    }
}