package com.dawidk.home.model

data class CarouselItems(
    var name: String,
    var items: List<CarouselItem>
) : HomeItem()