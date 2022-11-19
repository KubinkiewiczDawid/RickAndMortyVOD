package com.dawidk.rickandmortyvod.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dawidk.rickandmortyvod.R
import com.dawidk.rickandmortyvod.splashScreen.SplashActivity

class NotificationProvider(
    private val context: Context,
    private val deepLink: String? = null,
    private val messageBody: String,
    private val notificationIconId: Int
) {

    fun sendNotification() {
        val notification: Notification =
            createNotification()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(createChannel())
        }
        return NotificationCompat.Builder(context, "1")
            .setSmallIcon(notificationIconId)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(createPendingIntent())
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }

    private fun createPendingIntent(): PendingIntent? {
        deepLink?.let {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(it)
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        } ?: return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(): NotificationChannel {
        val name = context.getString(R.string.notifications_channel)
        val description = context.getString(R.string.notifications_channel)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("1", name, importance)
        channel.description = description
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        return channel
    }
}