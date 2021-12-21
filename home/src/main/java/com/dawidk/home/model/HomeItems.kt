package com.dawidk.home.model

data class HomeItems(
    var carouselItems: List<CarouselItems>,
    val playlistItems: List<Playlist>
)