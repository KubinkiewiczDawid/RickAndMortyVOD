package com.dawidk.common.video

import java.util.*

data class MediaItem(
    var title: String? = null,
    var subTitle: String? = null,
    var studio: String? = null,
    var url: String? = null,
    var contentType: String? = null,
    var duration: Long = 0,
    val images: ArrayList<String> = ArrayList<String>()
) {

    companion object {

        val EMPTY = MediaItem(
            title = "",
            subTitle = "",
            studio = "",
            url = "",
            contentType = "",
        )
    }
}