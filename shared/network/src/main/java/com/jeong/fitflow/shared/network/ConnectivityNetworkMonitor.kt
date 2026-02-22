package com.jeong.fitflow.shared.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.jeong.fitflow.shared.logging.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class ConnectivityNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
    private val appLogger: AppLogger
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isOnline: Flow<Boolean> = callbackFlow {
        trySend(isConnected())
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isConnected())
            }

            override fun onLost(network: Network) {
                trySend(isConnected())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(isConnected(networkCapabilities))
            }
        }
        runCatching {
            connectivityManager.registerDefaultNetworkCallback(callback)
        }.onFailure { throwable ->
            appLogger.warning(
                TAG,
                "Network callback registration failed",
                throwable
            )
            close(throwable)
        }
        awaitClose {
            runCatching {
                connectivityManager.unregisterNetworkCallback(callback)
            }.onFailure { throwable ->
                appLogger.warning(
                    TAG,
                    "Network callback unregistration failed",
                    throwable
                )
            }
        }
    }.conflate()
        .distinctUntilChanged()

    override fun isConnected(): Boolean =
        isConnected(
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        )

    private fun isConnected(capabilities: NetworkCapabilities?): Boolean =
        capabilities?.let { networkCapabilities ->
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false

    private companion object {
        const val TAG = "ConnectivityNetworkMonitor"
    }
}
