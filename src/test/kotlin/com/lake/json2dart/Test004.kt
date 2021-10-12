package com.lake.json2dart

import com.lake.json2dart.config.ProjectConfig
import com.lake.json2dart.generator.DartClassCodeMaker
import com.lake.json2dart.generator.DartClassMaker
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Test004 {

    private val rawJson = """{
  "list": [
    {
        "item1": "1111",
        "item2": 2222
    }
  ],
  "list_str": [
    "111", "222", "333"
  ]
}"""

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            ProjectConfig.setupTestInitState()
        }
    }

    @Test
    fun test004() {
        val dartClass = DartClassMaker("Test", rawJson).makeDartClass()
        println(dartClass)
        val result = DartClassCodeMaker(dartClass).makeDartClassCode()
        println(result)
    }
}