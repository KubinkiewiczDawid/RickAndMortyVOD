package com.dawidk.characters.di

import com.dawidk.characters.characterDetails.CharacterDetailsItemsProvider
import com.dawidk.characters.characterDetails.CharacterDetailsViewModel
import com.dawidk.characters.characterDetails.navigation.CharacterDetailsNavigator
import com.dawidk.characters.charactersHome.CharacterHomeViewModel
import com.dawidk.characters.charactersHome.navigation.CharactersNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val charactersModule = module {
    viewModel { CharacterHomeViewModel(get(), get()) }
    viewModel { CharacterDetailsViewModel(get(), get()) }
    single { CharactersNavigator() }
    single { CharacterDetailsNavigator() }
    single { CharacterDetailsItemsProvider() }
}