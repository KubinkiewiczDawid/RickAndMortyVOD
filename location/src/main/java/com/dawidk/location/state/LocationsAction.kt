package com.dawidk.location.state

sealed class LocationsAction {
    object Init : LocationsAction()
    object Load : LocationsAction()
    object DataLoaded : LocationsAction()
    data class HandleError(val error: Throwable) : LocationsAction()
}