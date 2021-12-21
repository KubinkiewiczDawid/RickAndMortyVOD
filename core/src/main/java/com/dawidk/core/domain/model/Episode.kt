package com.dawidk.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode(
    val id: String,
    val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<Character>,
    val created: String
) : Parcelable {

    companion object {

        val EMPTY = Episode(
            "",
            "",
            "",
            "",
            emptyList(),
            ""
        )
    }
}



