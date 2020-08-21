package com.lake.json2dart

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.DialogBuilder
import com.lake.json2dart.delegates.GeneratorDelegate
import com.lake.json2dart.view.Json2DartForm

const val TITLE = "Convert json to dart code"

class Json2DartAction(private val delegate: GeneratorDelegate = GeneratorDelegate()) : AnAction(TITLE) {
    override fun actionPerformed(e: AnActionEvent) {
        DialogBuilder().apply {
            val form = Json2DartForm()
            form.setOnGeneratorListener { json, fileName, finalField, justOneFile, createConstructor,
                                          defaultValue, safeConvert, safeClass ->
                window.dispose()
                delegate.runGenerator(e, json, fileName, finalField, justOneFile, createConstructor,
                        defaultValue, safeConvert, safeClass)

            }
            setCenterPanel(form.rootView)
            setTitle(TITLE)
            removeAllActions()
            show()
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabledAndVisible = e.getData(CommonDataKeys.VIRTUAL_FILE)?.isDirectory ?: false
    }
}