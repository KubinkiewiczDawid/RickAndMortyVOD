package com.dawidk.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val name: String,
    var items: List<PlaylistItem>
) : HomeItem(), Parcelable