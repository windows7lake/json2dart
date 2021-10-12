package com.lake.json2dart

import com.lake.json2dart.config.ProjectConfig
import com.lake.json2dart.generator.DartClassCodeMaker
import com.lake.json2dart.generator.DartClassMaker
import com.lake.json2dart.generator.DisplayClassMaker
import com.lake.json2dart.model.clazz.DataClass
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Test003 {

    private val rawJson = """{
  "id": 1441,
  "parent_id": null,
  "name": "New World",
  "link": "https://www.xxxxxx.com",
  "date_created": "2018-11-12T10:51:24",
  "date_created_gmt": "2018-11-12T10:51:24",
  "date_modified": "2018-12-04T06:35:46",
  "date_modified_gmt": "2018-12-04T06:35:46",
  "data": {
    "data1": 111,
    "data2": "222",
    "data3": 33.33,
    "meta": {
        "total": 1
    }
  },
  "list": [
    {
        "item1": "1111",
        "item2": 2222
    }
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
    fun test003() {
        val dartClass = DartClassMaker("Test", rawJson).makeDartClass()
        println((dartClass as DataClass).properties.map { "$it\n" })
        val displayList = DisplayClassMaker(dartClass).makeDisplayClassList()
//        println(displayList.map { "$it\n" })
        val result = DartClassCodeMaker(dartClass).makeDartClassCode()
//        println(result)


    }
}