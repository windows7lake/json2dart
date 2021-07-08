package com.lake.json2dart.builder

import com.lake.json2dart.model.dart.DartClass

abstract class BaseListCodeBuilder(
    override val name: String,
    val referencedClasses: List<DartClass>
): ICodeBuilder