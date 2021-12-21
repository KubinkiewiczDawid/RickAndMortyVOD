package com.dawidk.core.domain.model

data class CharacterResponse(
    val info: Info,
    val results: List<Character>
) {

    companion object {

        val EMPTY = CharacterResponse(
            info = Info(count = 0, pages = 0, next = null, prev = null),
            results = emptyList()
        )
    }
}