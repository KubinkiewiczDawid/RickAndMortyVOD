package com.dawidk.core

import okio.BufferedSource
import okio.buffer
import okio.source

class FileUtils {
    companion object {

        fun getBufferedSource(path: String): BufferedSource =
            FileUtils::class.java.getResourceAsStream(path)!!.source().buffer()
    }
}