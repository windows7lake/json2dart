/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lake.json2dart.treetable

import java.awt.event.*
import javax.swing.*
import javax.swing.event.ChangeListener
import javax.swing.plaf.ActionMapUIResource

/**
 * Maintenance tip - There were some tricks to getting this code
 * working:
 *
 * 1. You have to override addMouseListener() to do nothing
 * 2. You have to add a mouse event on mousePressed by calling
 * super.addMouseListener()
 * 3. You have to replace the UIActionMap for the keyboard event
 * "pressed" with your own one.
 * 4. You have to remove the UIActionMap for the keyboard event
 * "released".
 * 5. You have to grab focus when the next state is entered,
 * otherwise clicking on the component won't get the focus.
 * 6. You have to make a TreeTableDecorator as a button model that
 * wraps the original button model and does state management.
 */
class TreeTableCheckBox(text: String, icon: Icon?, selected: Boolean) : JCheckBox(text, icon) {

    private val decorator: TreeTableDecorator = TreeTableDecorator(getModel())
    private var selector: Selector? = null

    init {
        // set the model to the adapted model
        setModel(decorator)
        setState(selected)

        // Add a listener for when the mouse is pressed
        super.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                grabFocus()
                decorator.nextState()
            }
        })
        // Reset the keyboard action map
        val map: ActionMap = ActionMapUIResource()
        map.put("pressed", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                grabFocus()
                decorator.nextState()
            }
        })
        map.put("released", null) //NOI18N
        SwingUtilities.replaceUIActionMap(this, map)
    }

    fun setSelector(selector: Selector) {
        this.selector = selector
    }

    @JvmOverloads
    constructor(text: String = "", selected: Boolean = true) : this(text, null, selected)

    /** No one may add mouse listeners, not even Swing!  */
    override fun addMouseListener(l: MouseListener) {}

    /**
     * Set the new state to either SELECTED, NOT_SELECTED or
     * DONT_CARE.  If state == null, it is treated as DONT_CARE.
     */
    fun setState(state: Boolean?) {
        decorator.state = state
    }

    /**
     * Exactly which Design Pattern is this?  Is it an Adapter,
     * a Proxy or a Decorator?  In this case, my vote lies with the
     * Decorator, because we are extending functionality and
     * "decorating" the original model with a more powerful model.
     */
    private inner class TreeTableDecorator(private val other: ButtonModel) : ButtonModel {
        // normal deselected// don't care grey tick// normal black tick
        /**
         * The current state is embedded in the selection / armed
         * state of the model.
         *
         * We return the SELECTED state when the checkbox is selected
         * but not armed, DONT_CARE state when the checkbox is
         * selected and armed (grey) and NOT_SELECTED when the
         * checkbox is deselected.
         */
        var state: Boolean?
            get() = if (isSelected && !isArmed) {
                // normal black tick
                java.lang.Boolean.TRUE
            } else if (isSelected && isArmed) {
                // don't care grey tick
                null
            } else {
                // normal deselected
                java.lang.Boolean.FALSE
            }
            set(state) {
                when {
                    state === java.lang.Boolean.FALSE -> {
                        other.isArmed = false
                        isPressed = false
                        isSelected = false
                        selector?.changeState(false)
                    }

                    state === java.lang.Boolean.TRUE -> {
                        other.isArmed = false
                        isPressed = false
                        isSelected = true
                        selector?.changeState(true)
                    }

                    else -> {
                        other.isArmed = true
                        isPressed = true
                        isSelected = true
                        selector?.changeState(true)
                    }
                }
            }

        /**
         * We rotate between NOT_SELECTED, SELECTED and DONT_CARE.
         **/
        fun nextState() {
            val current = state
            when {
                current === java.lang.Boolean.FALSE -> {
                    state = java.lang.Boolean.TRUE
                }

                current === java.lang.Boolean.TRUE -> {
                    state = null
                }

                current == null -> {
                    state = java.lang.Boolean.FALSE
                }
            }
        }

        /**
         * Filter: No one may change the armed status except us.
         **/
        override fun setArmed(b: Boolean) {}

        /**
         * We disable focusing on the component when it is not enabled.
         **/
        override fun setEnabled(b: Boolean) {
            other.isEnabled = b
        }

        /**
         * All these methods simply delegate to the "other" model
         * that is being decorated.
         **/
        override fun isArmed(): Boolean {
            return other.isArmed
        }

        override fun isSelected(): Boolean {
            return other.isSelected
        }

        override fun isEnabled(): Boolean {
            return other.isEnabled
        }

        override fun isPressed(): Boolean {
            return other.isPressed
        }

        override fun isRollover(): Boolean {
            return other.isRollover
        }

        override fun setSelected(b: Boolean) {
            other.isSelected = b
        }

        override fun setPressed(b: Boolean) {
            other.isPressed = b
        }

        override fun setRollover(b: Boolean) {
            other.isRollover = b
        }

        override fun setMnemonic(key: Int) {
            other.mnemonic = key
        }

        override fun getMnemonic(): Int {
            return other.mnemonic
        }

        override fun setActionCommand(s: String) {
            other.actionCommand = s
        }

        override fun getActionCommand(): String {
            return other.actionCommand
        }

        override fun addActionListener(l: ActionListener) {
            other.addActionListener(l)
        }

        override fun removeActionListener(l: ActionListener) {
            other.removeActionListener(l)
        }

        override fun addItemListener(l: ItemListener) {
            other.addItemListener(l)
        }

        override fun removeItemListener(l: ItemListener) {
            other.removeItemListener(l)
        }

        override fun addChangeListener(l: ChangeListener) {
            other.addChangeListener(l)
        }

        override fun removeChangeListener(l: ChangeListener) {
            other.removeChangeListener(l)
        }

        override fun getSelectedObjects(): Array<Any> {
            return other.selectedObjects
        }

        override fun setGroup(group: ButtonGroup?) {
        }
    }
}