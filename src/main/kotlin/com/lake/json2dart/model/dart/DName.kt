package com.lake.json2dart.model.dart

interface IDName {
    fun getName(rawName: String): String
}

abstract class DName : IDName {
    private val suffix = "isIllegal"

    private val illegalNameList = listOf(
        "as", "break", "class", "continue", "do", "dynamic", "else", "false", "for", "if", "in", "interface", "is",
        "null", "object", "package", "return", "super", "this", "throw", "true", "try", "var", "when", "while", "while"
    )

    protected val illegalCharacter = listOf(
        "\\+", "\\-", "\\*", "/", "%", "=", "&", "\\|", "!", "\\[", "\\]", "\\{", "\\}", "\\(", "\\)", "\\\\", "\"", "_"
        , ",", ":", "\\?", "\\>", "\\<", "@", ";", "'", "\\`", "\\~", "\\$", "\\^", "#", "\\", "/", " ", "\t", "\n"
    )

    protected val nameSeparator = listOf(" ", "_", "\\-", ":","\\.")

    protected fun toBeLegalName(name: String): String {
        val tempName = name.replace(illegalCharacter.toRegex(), "")
        return if (tempName in illegalNameList) {
            tempName + suffix
        } else {
            tempName
        }
    }

    /**
     * remove the start number or whiteSpace characters in this string
     */
    protected fun removeStartNumberAndIllegalCharacter(it: String): String {
        val numberAndIllegalCharacters = listOf(*illegalCharacter.toTypedArray(), "\\d")
        val firstNumberAndIllegalCharactersRegex = "^(${numberAndIllegalCharacters.toRegex()})+".toRegex()
        return it.trim().replaceFirst(firstNumberAndIllegalCharactersRegex, "")
    }

    /**
     * array string into regex match patten that could match any element of the array
     */
    protected fun Iterable<String>.toRegex() = joinToString(separator = "|").toRegex()
}