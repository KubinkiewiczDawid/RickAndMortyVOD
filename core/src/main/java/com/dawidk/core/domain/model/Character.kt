package com.dawidk.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Character(
    val id: String,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Location,
    val location: Location,
    val image: String,
    val episode: List<Episode>,
    val created: String
) : Parcelable {

    companion object {

        val EMPTY = Character(
            "",
            "",
            CharacterStatus.fromStatus(""),
            "",
            "",
            "",
            Location.EMPTY,
            Location.EMPTY,
            "",
            emptyList(),
            ""
        )
    }
}



