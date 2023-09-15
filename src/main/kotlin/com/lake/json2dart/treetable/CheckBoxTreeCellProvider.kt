package com.lake.json2dart.treetable

import com.lake.json2dart.model.clazz.Property
import org.jdesktop.swingx.renderer.CellContext
import org.jdesktop.swingx.renderer.ComponentProvider
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree

class CheckBoxTreeCellProvider(private val selectionModel: CheckBoxTreeSelectionModel) : ComponentProvider<JPanel>() {

    private val checkBox: TreeTableCheckBox = TreeTableCheckBox()
    private val label: JLabel = JLabel()

    override fun format(cellContext: CellContext) {
        val tree = cellContext.component as JTree
        val node = cellContext.value as DefaultMutableTreeTableNode
        val userObject = node.userObject
        if (userObject is Property) {
            label.text = userObject.originName
            checkBox.setSelector(userObject)
        }

        val path = tree.getPathForRow(cellContext.row)
        if (path != null) {
            when {
                selectionModel.isPathSelected(path, true) -> {
                    checkBox.setState(java.lang.Boolean.TRUE)
                }

                selectionModel.isPartiallySelected(path) -> {
                    checkBox.setState(null)
                }

                else -> {
                    checkBox.setState(java.lang.Boolean.FALSE)
                }
            }
        }

        rendererComponent.layout = BorderLayout()
        rendererComponent.add(checkBox)
        rendererComponent.add(label, BorderLayout.LINE_END)
    }

    override fun createRendererComponent(): JPanel {
        return JPanel()
    }

    override fun configureState(cellContext: CellContext?) {
        // EMPTY
    }
}