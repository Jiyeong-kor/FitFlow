package com.jeong.runninggoaltracker.shared.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
    fun isConnected(): Boolean
}
