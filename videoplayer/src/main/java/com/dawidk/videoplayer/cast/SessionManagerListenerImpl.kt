package com.dawidk.videoplayer.cast

import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener

class SessionManagerListenerImpl(
    private val sessionManagerListenerHandler: SessionManagerListenerHandler
) : SessionManagerListener<CastSession> {

    override fun onSessionStarted(session: CastSession, sessionId: String) {
        sessionManagerListenerHandler.onApplicationConnected(session)
    }

    override fun onSessionStartFailed(session: CastSession, error: Int) {
        sessionManagerListenerHandler.onApplicationDisconnected()
    }

    override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
        sessionManagerListenerHandler.onApplicationConnected(session)
    }

    override fun onSessionResumeFailed(session: CastSession, error: Int) {
        sessionManagerListenerHandler.onApplicationDisconnected()
    }

    override fun onSessionEnded(session: CastSession, error: Int) {
        sessionManagerListenerHandler.onApplicationDisconnected()
    }

    override fun onSessionStarting(session: CastSession) {
    }

    override fun onSessionEnding(session: CastSession) {
    }

    override fun onSessionResuming(session: CastSession, error: String) {
    }

    override fun onSessionSuspended(session: CastSession, reason: Int) {
    }
}

interface SessionManagerListenerHandler {

    fun onApplicationConnected(session: CastSession)
    fun onApplicationDisconnected()
}