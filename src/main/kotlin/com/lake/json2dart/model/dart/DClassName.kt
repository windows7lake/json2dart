package com.lake.json2dart.model.dart

interface IDClassName {
    fun getLegalClassName(rawClassName: String): String
}

object DClassName : DName(), IDClassName {

    override fun getName(rawName: String): String {
        return getLegalClassName(rawName)
    }

    override fun getLegalClassName(rawClassName: String): String {
        val upperCamelCaseLegalName = getUpperCamelCaseLegalName(rawClassName)
        return upperCamelCaseLegalName.ifEmpty {
            getUpperCamelCaseLegalName("${rawClassName}Rename")
        }
    }

    private fun getUpperCamelCaseLegalName(rawClassName: String): String {
        /**
         * keep " " character
         */
        val pattern = illegalCharacter.toMutableList().apply { removeAll(nameSeparator) }.toRegex()
        val temp = rawClassName.replace(pattern, "").let {
            return@let removeStartNumberAndIllegalCharacter(it)
        }
        val upperCamelCase = toUpperCamelCase(temp)
        return toBeLegalName(upperCamelCase)
    }

    /**
     * this function can remove the rest white space
     */
    private fun toUpperCamelCase(temp: String): String {
        val stringBuilder = StringBuilder()
        temp.split(nameSeparator.toRegex()).forEach {
            if (it.isNotBlank()) {
                stringBuilder.append(it.substring(0, 1).toUpperCase().plus(it.substring(1)))
            }
        }
        return stringBuilder.toString()

    }
}