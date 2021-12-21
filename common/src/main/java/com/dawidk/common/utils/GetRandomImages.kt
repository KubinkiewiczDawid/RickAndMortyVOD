package com.dawidk.common.utils

import com.dawidk.core.domain.model.Character

fun getRandomImages(characters: List<Character>, imagesNumber: Int): List<String> {
    val characterImages = mutableSetOf<String>()
    while (characterImages.size < imagesNumber) {
        val item = characters.random()
        characterImages.add(item.image)
    }

    return characterImages.toList()
}