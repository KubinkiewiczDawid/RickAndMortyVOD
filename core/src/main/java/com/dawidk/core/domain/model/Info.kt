package com.dawidk.core.domain.model

data class Info(
    val count: Int,
    val pages: Int,
    val next: Int?,
    val prev: Int?
) {

    companion object {

        val EMPTY = Info(
            count = 0,
            pages = 0,
            next = null,
            prev = null
        )
    }
}