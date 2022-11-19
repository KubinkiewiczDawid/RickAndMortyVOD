package com.dawidk.videoplayer.cast.service.state

sealed class CastVideoState {
    object Idle : CastVideoState()
    object ApplicationConnected : CastVideoState()
    object ApplicationDisconnected : CastVideoState()
}