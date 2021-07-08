package com.lake.json2dart.treetable

import org.jdesktop.swingx.JXTreeTable
import java.awt.event.ItemEvent
import javax.swing.JComboBox
import javax.swing.tree.TreePath

class ComboBoxTreeTableAdapter(treeTable: JXTreeTable) {

    private val names = arrayOf("a1", "b1", "c1")
    private val comboBox = JComboBox<Any>(names)

    init {
        comboBox.addItemListener { itemEvent ->
            if (itemEvent.stateChange == ItemEvent.SELECTED) {
                val item = itemEvent.item
                val path: TreePath = treeTable.getPathForRow(0)
                val node: Any = path.lastPathComponent

            }
        }
    }
}