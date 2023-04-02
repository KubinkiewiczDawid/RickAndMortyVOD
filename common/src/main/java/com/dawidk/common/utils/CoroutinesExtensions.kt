package com.dawidk.common.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.collectFromState(lifecycleState: Lifecycle.State, event: SharedFlow<T>?, collected: (T) -> Unit) =
    this.lifecycleScope.launch {
        this@collectFromState.repeatOnLifecycle(lifecycleState) {
            event?.collect {
                collected(it)
            }
        }
    }

fun <T> LifecycleOwner.collectFromState(lifecycleState: Lifecycle.State, state: StateFlow<T>?, collected: (T) -> Unit) =
    this.lifecycleScope.launch {
        this@collectFromState.repeatOnLifecycle(lifecycleState) {
            state?.collect {
                collected(it)
            }
        }
    }


fun <T> LifecycleOwner.collectFromState(lifecycleState: Lifecycle.State, flow: Flow<T>?, collected: (T) -> Unit) =
    this.lifecycleScope.launch {
        this@collectFromState.repeatOnLifecycle(lifecycleState) {
            flow?.collect {
                collected(it)
            }
        }
    }
