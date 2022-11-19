package com.dawidk.videoplayer

import android.content.Context
import java.io.IOException

fun Context.getJsonDataFromAsset(fileName: String): String? {
    val jsonString: String
    try {
        jsonString = this.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}