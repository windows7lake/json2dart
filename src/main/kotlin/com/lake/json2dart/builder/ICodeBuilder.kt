package com.lake.json2dart.builder

/**
 * Code generator interface
 *
 * Created by Nstd on 2020/6/29 15:27.
 */
interface ICodeBuilder {

    /**
     * the name of this class
     */
    val name: String

    /**
     * only the current class code not include the referenced class for writing into file or printing out
     */
    fun getOnlyCurrentCode(): String

    /**
     * get the code (include referenced classes) string for writing into file or printing out
     */
    fun getCode(): String

    fun <T> getConfig(key: String, default: T): T {
        return CodeBuilderConfig.instance.getConfig(key, default)
    }
}