package com.lake.json2dart.delegates

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.lake.json2dart.generator.DartClassGenerator
import com.lake.json2dart.generator.FileIOException
import java.io.File
import java.io.IOException

class GeneratorDelegate(private val message: MessageDelegate = MessageDelegate()) {
    fun runGenerator(event: AnActionEvent, json: String, fileName: String, finalField: Boolean, justOneFile: Boolean,
                     createConstructor: Boolean, defaultValue: Boolean, safeConvert: Boolean, safeClass: Boolean) {
        ProgressManager.getInstance().run(
                object : Task.Backgroundable(event.project, "Dart file is generating", false) {
                    override fun run(indicator: ProgressIndicator) {
                        try {
                            // To generate dart class from json
                            DartClassGenerator(message).generateFromJson(
                                    json,
                                    File(event.getData(CommonDataKeys.VIRTUAL_FILE)?.path ?: ""),
                                    fileName.takeIf { it.isNotBlank() } ?: "bean",
                                    finalField, justOneFile, createConstructor, defaultValue, safeConvert, safeClass
                            )
                            message.showMessage("Dart class has been generated.")
                        } catch (e: Throwable) {
                            when (e) {
                                is IOException -> message.onException(FileIOException())
                                else -> message.onException(e)
                            }
                        } finally {
                            indicator.stop()
                            // refresh current project after generate dart class
                            ProjectView.getInstance(event.project).refresh()
                            event.getData(LangDataKeys.VIRTUAL_FILE)?.refresh(false, true)
                        }
                    }
                }
        )
    }
}