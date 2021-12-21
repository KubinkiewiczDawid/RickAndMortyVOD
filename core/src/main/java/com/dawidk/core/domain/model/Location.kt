package com.dawidk.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: String,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<Character>,
    val created: String
) : Parcelable {

    companion object {

        val EMPTY = Location("", "", "", "", emptyList(), "")
    }
}