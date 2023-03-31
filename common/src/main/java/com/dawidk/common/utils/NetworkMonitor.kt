package com.dawidk.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.getScopeName
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private const val REACHABILITY_SERVER = "https://google.com"

class NetworkMonitor(private val context: Context) :
    DefaultLifecycleObserver {

    private var connectivityManager: ConnectivityManager? = null
    private val _state: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val state: StateFlow<Boolean?> = _state
    private var isNetworkCallbackRegistered: Boolean = false
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            _state.value = false
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _state.value = isConnectedToInternet()
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _state.value = false
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onStart(owner: LifecycleOwner) {
        registerNetworkCallback()
    }

    @RequiresApi(VERSION_CODES.O)
    private fun registerNetworkCallback() {
        if (!isNetworkCallbackRegistered) {
            val networkRequest =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
            connectivityManager?.requestNetwork(networkRequest, networkCallback, 1000)
            isNetworkCallbackRegistered = true
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        unregisterNetworkCallback(owner)
    }

    private fun unregisterNetworkCallback(owner: LifecycleOwner) {
        if (owner.getScopeName().toString()
                .contains("MainActivity") && isNetworkCallbackRegistered
        ) {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
            isNetworkCallbackRegistered = false
        }
    }

    private fun isConnectedToInternet(): Boolean {
        return try {
            val connection = URL(REACHABILITY_SERVER).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Test")
            connection.setRequestProperty("Connection", "close")
            connection.connectTimeout = 1500
            connection.connect()
            (connection.responseCode == 200)
        } catch (e: IOException) {
            false
        }
    }
}