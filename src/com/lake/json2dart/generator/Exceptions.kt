package com.lake.json2dart.generator

import java.io.IOException

class SyntaxException : Exception("Wrong json syntax")

class FileIOException : IOException("Cannot read or write file")

class NotFlutterProject : Exception("It does not look like a flutter project, and cannot find 'lib' folder.")