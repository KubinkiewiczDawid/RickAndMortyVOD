package com.dawidk.characters.charactersHome.navigation

import com.dawidk.common.navigation.NavigatorScreen

sealed class Screen : NavigatorScreen {
    data class CharacterDetails(val id: String) : Screen()
}