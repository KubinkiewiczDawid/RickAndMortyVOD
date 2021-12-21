package com.dawidk.rickandmortyvod.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.dawidk.rickandmortyvod.R
import com.dawidk.rickandmortyvod.utils.NotificationProvider

class RickAndMortyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationProvider(
            this,
            message.data["deepLink"],
            message.notification?.body ?: "",
            R.drawable.ic_play
        ).sendNotification()
    }
}