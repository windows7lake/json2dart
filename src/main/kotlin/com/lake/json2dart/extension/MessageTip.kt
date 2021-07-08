package com.lake.json2dart.extension

import com.lake.json2dart.view.MessageDialog

object MessageTip {
    fun show(text: String) {
        val dialog = MessageDialog(text)
        dialog.setSize(200, 200)
        dialog.setLocationRelativeTo(null)
        dialog.isVisible = true
    }
}