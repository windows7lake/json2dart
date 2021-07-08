package com.lake.json2dart.extension

import com.google.gson.JsonElement

/**
 * Target Json Element Maker form a String or an Element for generating Code
 * Created by Seal.Wu on 2017/9/19.
 */
interface ITargetJsonElement {
    /**
     * get expected jsonElement for generating dart code
     */
    fun getTargetJsonElementForGeneratingCode(): JsonElement
}
