package com.dawidk.characters.characterDetails.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    data class EpisodeDetails(val id: String) : Screen()
}