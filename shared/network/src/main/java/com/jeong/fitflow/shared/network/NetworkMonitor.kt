package com.jeong.fitflow.shared.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
    fun isConnected(): Boolean
}
