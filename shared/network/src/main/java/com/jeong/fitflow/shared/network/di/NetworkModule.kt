package com.jeong.fitflow.shared.network.di

import com.jeong.fitflow.shared.network.ConnectivityNetworkMonitor
import com.jeong.fitflow.shared.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {
    @Binds
    @Singleton
    fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor
}
