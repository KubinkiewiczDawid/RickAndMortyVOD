package com.dawidk.location.state

import com.dawidk.common.mvi.ViewAction

sealed class LocationsAction: ViewAction {
    object Init : LocationsAction()
    object Load : LocationsAction()
    object DataLoaded : LocationsAction()
    data class HandleError(val error: Throwable) : LocationsAction()
}