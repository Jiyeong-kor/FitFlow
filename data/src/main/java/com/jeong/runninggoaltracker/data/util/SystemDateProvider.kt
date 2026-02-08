package com.jeong.runninggoaltracker.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.jeong.runninggoaltracker.domain.contract.DateTimeContract
import com.jeong.runninggoaltracker.domain.util.DateProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Calendar

class SystemDateProvider(private val context: Context) : DateProvider {

    override fun getToday(): Long = System.currentTimeMillis()

    override fun getTodayFlow(): Flow<Long> = callbackFlow {
        val appContext = context.applicationContext
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_DATE_CHANGED ||
                    intent?.action == Intent.ACTION_TIMEZONE_CHANGED
                ) {
                    trySend(getToday())
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }

        appContext.registerReceiver(receiver, filter)

        trySend(getToday())

        awaitClose {
            appContext.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()

    override fun getStartOfWeek(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            firstDayOfWeek = DateTimeContract.WEEK_START_DAY
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
