package com.lake.json2dart

import com.lake.json2dart.config.ProjectConfig
import com.lake.json2dart.generator.DartClassCodeMaker
import com.lake.json2dart.generator.DartClassMaker
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Test002 {

    private val rawJson = """[
    {
        "item1": "1111",
        "item2": 2222
    },
    {
        "item1": "3333",
        "item2": 4444
    },
    {
        "item1": "1111",
        "item3": 5555
    }
  ]"""

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            ProjectConfig.setupTestInitState();
        }
    }

    @Test
    fun test002() {
        val dartClass = DartClassMaker("Test", rawJson).makeDartClass()
        val result = DartClassCodeMaker(dartClass).makeDartClassCode()
        println(result)
    }
}