package com.dawidk.characters.charactersHome

import androidx.paging.PagingData
import com.dawidk.core.domain.model.Character

sealed class CharacterState {
    object Loading : CharacterState()
    data class DataLoaded(val data: PagingData<Character>) : CharacterState()
    data class Error(val exception: Throwable) : CharacterState()
}