package com.dawidk.rickandmortyvod.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dawidk.rickandmortyvod.service.NewEpisodeCheckerService

fun Context.createUpdateServiceIntervalAlarm() {
    val alarmIntent =
        PendingIntent.getService(
            this,
            0,
            Intent(this, NewEpisodeCheckerService::class.java),
            0
        )
    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarm.cancel(alarmIntent)
    alarm.setRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis(),
        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
        alarmIntent
    )
}