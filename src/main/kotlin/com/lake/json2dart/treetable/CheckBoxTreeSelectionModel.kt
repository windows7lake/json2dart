/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lake.json2dart.treetable

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.util.*
import javax.swing.tree.DefaultTreeSelectionModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

/**
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 */
class CheckBoxTreeSelectionModel(private val model: TreeModel) : DefaultTreeSelectionModel() {

    init {
        setSelectionMode(DISCONTIGUOUS_TREE_SELECTION)
    }

    fun addPathsByNodes(selectedNodes: List<TreeNode>) {
        addSelectionPaths(selectedNodes.map {
            TreePath(getPathToRoot(it as DefaultMutableTreeTableNode))
        }.toTypedArray())
    }

    private fun getPathToRoot(node: TreeNode): Array<TreeNode?> {
        var aNode: TreeNode? = node
        val retNodes: Array<TreeNode?>
        val temp = ArrayList<TreeNode>()

        /* Check for null, in case someone passed in a null node, or
        they passed in an element that isn't rooted at root. */
        while (aNode != null) {
            temp.add(aNode)
            aNode = aNode.parent
        }
        val num = temp.size
        retNodes = arrayOfNulls(num)
        for (i in num - 1 downTo 0) {
            retNodes[num - 1 - i] = temp[i]
        }
        return retNodes
    }

    // tests whether there is any unselected node in the subtree of given path
    fun isPartiallySelected(path: TreePath): Boolean {
        if (isPathSelected(path, true)) return false
        val selectionPaths = selectionPaths ?: return false
        for (selectionPath in selectionPaths) {
            if (isDescendant(selectionPath, path)) {
                return true
            }
        }
        return false
    }

    // tells whether given path is selected.
    // if dig is true, then a path is assumed to be selected, if
    // one of its ancestor is selected.
    fun isPathSelected(treePath: TreePath?, dig: Boolean): Boolean {
        if (!dig) return super.isPathSelected(treePath)
        var path = treePath
        while (path != null && !super.isPathSelected(path)) {
            path = path.parentPath
        }
        return path != null
    }

    // is path1 descendant of path2
    private fun isDescendant(path1: TreePath, path2: TreePath): Boolean {
        val obj1 = path1.path
        val obj2 = path2.path
        for (index in obj2.indices) {
            if (obj1[index] !== obj2[index]) {
                return false
            }
        }
        return true
    }

    override fun addSelectionPaths(paths: Array<TreePath>) {
        // unselect all descendants of paths[]
        for (path in paths) {
            val selectionPaths = selectionPaths ?: break
            val toBeRemoved = ArrayList<TreePath>()
            for (selectionPath in selectionPaths) {
                if (isDescendant(selectionPath, path)) {
                    toBeRemoved.add(selectionPath)
                }
            }
            super.removeSelectionPaths(toBeRemoved.toTypedArray())
        }

        // if all siblings are selected then unselect them and select parent recursively
        // otherwise just select that path.
        for (treePath in paths) {
            var path = treePath
            var temp: TreePath? = null
            while (areSiblingsSelected(path)) {
                temp = path
                if (path.parentPath == null) {
                    break
                }
                path = path.parentPath
            }
            if (temp != null) {
                if (temp.parentPath != null) {
                    addSelectionPath(temp.parentPath)
                } else {
                    if (!isSelectionEmpty) {
                        removeSelectionPaths(selectionPaths)
                    }
                    super.addSelectionPaths(arrayOf(temp))
                }
            } else {
                super.addSelectionPaths(arrayOf(path))
            }
        }
    }

    // tells whether all siblings of given path are selected.
    private fun areSiblingsSelected(path: TreePath): Boolean {
        val parent = path.parentPath ?: return true
        val node = path.lastPathComponent
        val parentNode = parent.lastPathComponent
        val childCount = model.getChildCount(parentNode)
        for (i in 0 until childCount) {
            val childNode = model.getChild(parentNode, i)
            if (childNode === node) {
                continue
            }
            if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                return false
            }
        }
        return true
    }

    override fun removeSelectionPaths(paths: Array<TreePath>) {
        for (path in paths) {
            if (path.pathCount == 1) {
                super.removeSelectionPaths(arrayOf(path))
            } else {
                toggleRemoveSelection(path)
            }
        }
    }

    // if any ancestor node of given path is selected then unselect it
    //  and selection all its descendants except given path and descendants.
    // otherwise just unselect the given path
    private fun toggleRemoveSelection(path: TreePath) {
        val stack = Stack<TreePath>()
        var parent = path.parentPath
        while (parent != null && !isPathSelected(parent)) {
            stack.push(parent)
            parent = parent.parentPath
        }
        if (parent != null) {
            stack.push(parent)
        } else {
            super.removeSelectionPaths(arrayOf(path))
            return
        }
        while (!stack.isEmpty()) {
            val temp = stack.pop()
            val peekPath = if (stack.isEmpty()) path else stack.peek()
            val node = temp.lastPathComponent
            val peekNode = peekPath.lastPathComponent
            val childCount = model.getChildCount(node)
            for (i in 0 until childCount) {
                val childNode = model.getChild(node, i)
                if (childNode !== peekNode) {
                    super.addSelectionPaths(arrayOf(temp.pathByAddingChild(childNode)))
                }
            }
        }
        super.removeSelectionPaths(arrayOf(parent))
    }

}