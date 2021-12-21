package com.dawidk.rickandmortyvod

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dawidk.rickandmortyvod.utils.createUpdateServiceIntervalAlarm

class AutoStartUp : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            context.createUpdateServiceIntervalAlarm()
        }
    }
}