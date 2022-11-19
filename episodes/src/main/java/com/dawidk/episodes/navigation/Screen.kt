package com.dawidk.episodes.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    data class EpisodeDetails(val id: String) : Screen()
    data class CharacterDetails(val id: String) : Screen()
    data class VideoPlayer(val id: String) : Screen()
}