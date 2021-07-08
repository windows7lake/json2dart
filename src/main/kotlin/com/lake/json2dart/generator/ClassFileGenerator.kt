package com.lake.json2dart.generator

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ClassFileGenerator(
    private val destinyDirectory: File,
    private val fileName: String,
    private val text: String,
) {

    @Throws(IOException::class)
    fun generate() {
        val targetOutputStream = FileOutputStream(File(destinyDirectory, fileName))
        targetOutputStream.writeText(text)
        targetOutputStream.close()
    }

    private fun FileOutputStream.writeText(text: String): FileOutputStream {
        write(text.toByteArray(Charsets.UTF_8))
        return this
    }
}