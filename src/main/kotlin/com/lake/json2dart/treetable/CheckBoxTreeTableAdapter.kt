package com.lake.json2dart.treetable

import org.jdesktop.swingx.JXTreeTable
import org.jdesktop.swingx.renderer.DefaultTreeRenderer
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JCheckBox
import javax.swing.JTree
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

class CheckBoxTreeTableAdapter(private val treeTable: JXTreeTable) : MouseAdapter(), TreeSelectionListener {

    private val tree: JTree = treeTable.getCellRenderer(0, 0) as JTree
    private val selectionModel: CheckBoxTreeSelectionModel = CheckBoxTreeSelectionModel(tree.model)
    private val hotspot = JCheckBox().preferredSize.width

    fun getSelectionModel(): CheckBoxTreeSelectionModel {
        return selectionModel
    }

    init {
        val checkBoxTreeCellProvider = CheckBoxTreeCellProvider(selectionModel)
        tree.cellRenderer = DefaultTreeRenderer(checkBoxTreeCellProvider)
        treeTable.addMouseListener(this)
        selectionModel.addTreeSelectionListener(this)
    }

    override fun mouseClicked(e: MouseEvent) {
        val path = tree.getPathForLocation(e.x, e.y) ?: return
        val rectangle = tree.getPathBounds(path) ?: return
        if (e.x > rectangle.x + hotspot) return

        val selected = selectionModel.isPathSelected(path, true)
        selectionModel.removeTreeSelectionListener(this)
        try {
            if (selected) {
                selectionModel.removeSelectionPath(path)
            } else {
                selectionModel.addSelectionPath(path)
            }
        } finally {
            selectionModel.addTreeSelectionListener(this)
            treeTable.repaint()
        }
    }

    override fun valueChanged(e: TreeSelectionEvent?) {
    }
}