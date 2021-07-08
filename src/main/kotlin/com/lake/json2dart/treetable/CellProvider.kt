package com.lake.json2dart.treetable

interface CellProvider {
    fun getCellValue(columnIndex: Int): String
    fun setValueAt(columnIndex: Int, value: String)
}