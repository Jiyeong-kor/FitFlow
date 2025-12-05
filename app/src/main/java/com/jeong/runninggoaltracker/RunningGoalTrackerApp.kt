package com.jeong.runninggoaltracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.jeong.runninggoaltracker.presentation.reminder.ReminderNotifier
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RunningGoalTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = ReminderNotifier.CHANNEL_ID
            val channelName = getString(R.string.reminder_channel_name)
            val channelDescription = getString(R.string.reminder_channel_description)

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
