package com.dawidk.common.utils

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.fetchClick(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        this.trySend(Unit).isSuccess
    }
    awaitClose { setOnClickListener(null) }
}
