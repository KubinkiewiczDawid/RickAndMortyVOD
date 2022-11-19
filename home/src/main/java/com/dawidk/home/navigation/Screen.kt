package com.dawidk.home.navigation

import com.dawidk.common.navigation.NavigatorScreen
import com.dawidk.home.model.Playlist

sealed class Screen : NavigatorScreen {
    data class SeeAll(val playlist: Playlist) : Screen()
    data class CharacterDetails(val id: String) : Screen()
    data class EpisodeDetails(val id: String) : Screen()
    data class VideoPlayer(val episodeId: String) : Screen()
}