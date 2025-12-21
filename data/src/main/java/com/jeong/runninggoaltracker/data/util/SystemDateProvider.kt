package com.jeong.runninggoaltracker.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.jeong.runninggoaltracker.domain.util.DateProvider
import java.time.LocalDate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SystemDateProvider(private val context: Context) : DateProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodayFlow(): Flow<LocalDate> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_DATE_CHANGED) {
                    trySend(LocalDate.now())
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_DATE_CHANGED))
        trySend(LocalDate.now())
        awaitClose { context.unregisterReceiver(receiver) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getToday(): LocalDate {
        return LocalDate.now()
    }
}
