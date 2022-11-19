package com.dawidk.rickandmortyvod.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.dawidk.rickandmortyvod.R
import com.dawidk.rickandmortyvod.usecase.CheckForNewEpisodeUseCase
import com.dawidk.rickandmortyvod.utils.NotificationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NewEpisodeCheckerService : Service() {

    private val checkForNewEpisodeUseCase: CheckForNewEpisodeUseCase by inject()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            checkIfNewEpisodes()
        }

        return START_STICKY
    }

    private fun checkIfNewEpisodes() {
        scope.launch {
            checkForNewEpisodeUseCase().collect {
                if (it) {
                    NotificationProvider(
                        this@NewEpisodeCheckerService,
                        "https://www.rickandmortyvod.dawidk.com/episodes",
                        getString(R.string.new_episode_message),
                        R.drawable.episode
                    ).sendNotification()
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        job.cancel()
    }
}