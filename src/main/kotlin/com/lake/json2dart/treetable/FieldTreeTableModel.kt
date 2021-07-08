package com.lake.json2dart.treetable

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import org.jdesktop.swingx.treetable.DefaultTreeTableModel
import org.jdesktop.swingx.treetable.TreeTableNode

class FieldTreeTableModel(node: TreeTableNode?) : DefaultTreeTableModel(node) {
    private val fieldName = arrayOf("Json Key", "Class Type", "Key", "Value", "Comment")
    private val fieldType =
        arrayOf(String::class.java, Any::class.java, String::class.java, String::class.java, String::class.java)

    override fun getColumnClass(column: Int): Class<*> {
        return fieldType[column]
    }

    override fun getColumnCount(): Int {
        return fieldName.size
    }

    override fun getColumnName(column: Int): String {
        return fieldName[column]
    }

    override fun getValueAt(node: Any?, column: Int): Any {
        var value: Any = ""
        if (node is DefaultMutableTreeTableNode) {
            val userObject = node.userObject
            if (userObject is CellProvider) {
                value = userObject.getCellValue(column)
            }
        }
        return value
    }

    override fun setValueAt(value: Any, node: Any?, column: Int) {
        super.setValueAt(value, node, column)
        if (node is DefaultMutableTreeTableNode) {
            val userObject = node.userObject
            if (userObject is CellProvider) {
                userObject.setValueAt(column, value.toString())
            }
        }
    }

    override fun isCellEditable(node: Any?, column: Int): Boolean {
        return column != 0
    }
}