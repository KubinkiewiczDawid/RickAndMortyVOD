package com.dawidk.videoplayer.cast.service.state

sealed class CastVideoEvent {
    object OpenExpandedControls : CastVideoEvent()
}
