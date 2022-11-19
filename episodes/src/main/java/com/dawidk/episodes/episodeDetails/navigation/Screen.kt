package com.dawidk.episodes.episodeDetails.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    data class CharacterDetails(val id: String) : Screen()
    data class VideoPlayer(val id: String) : Screen()
}