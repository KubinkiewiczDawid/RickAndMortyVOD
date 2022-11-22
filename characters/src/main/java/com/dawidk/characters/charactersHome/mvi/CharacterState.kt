package com.dawidk.characters.charactersHome.mvi

import androidx.paging.PagingData
import com.dawidk.common.mvi.ViewState
import com.dawidk.core.domain.model.Character

sealed class CharacterState: ViewState {
    object Loading : CharacterState()
    data class DataLoaded(val data: PagingData<Character>) : CharacterState()
    data class Error(val exception: Throwable) : CharacterState()
}