package com.dawidk.videoplayer

data class Sample(val name: String, val uri: String)

data class Media(val name: String, val samples: List<Sample>)
data class AdTag(val name: String, val uri: String)