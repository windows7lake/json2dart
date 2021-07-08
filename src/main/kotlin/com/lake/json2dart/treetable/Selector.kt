package com.lake.json2dart.treetable

/**
 * Maintain a state for each row of treetable, it indicated whether the current row was selected.
 */
interface Selector {
    fun changeState(selected: Boolean)
}