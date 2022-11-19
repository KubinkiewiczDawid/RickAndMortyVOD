package com.dawidk.videoplayer.model

data class Video(
    val name: String,
    val details: String? = null,
    val moreDetails: String? = null,
    val description: String? = null
)