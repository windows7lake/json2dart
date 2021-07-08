package com.lake.json2dart.extension

import com.lake.json2dart.config.PLUGIN_NAME
import com.lake.json2dart.config.ProjectConfig
import java.util.logging.Logger

object Log {

    fun i(info: String) {
        if (ProjectConfig.enableTestModel) {
            Logger.getLogger(PLUGIN_NAME).info(info);
        }
    }

    fun w(warning: String) {
        if (ProjectConfig.enableTestModel) {
            Logger.getLogger(PLUGIN_NAME).warning(warning);
        }
    }

    fun e(e: Throwable) {
        if (ProjectConfig.enableTestModel) {
            e.printStackTrace();
        }
    }
}