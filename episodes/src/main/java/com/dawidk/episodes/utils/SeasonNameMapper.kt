package com.dawidk.episodes.utils

import android.content.Context
import com.dawidk.episodes.R

fun String.mapToPrettySeasonName(context: Context): String {
    return if (this == context.resources.getString(R.string.all)) {
        context.resources.getString(R.string.all_seasons)
    } else {
        val re = Regex("[^0-9]")
        val item = re.replace(this, "")
        "${context.resources.getString(R.string.season)} ${item.toInt()}"
    }
}