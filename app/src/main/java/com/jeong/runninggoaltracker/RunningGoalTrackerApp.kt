package com.jeong.runninggoaltracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.jeong.runninggoaltracker.data.local.RunningDatabase
import com.jeong.runninggoaltracker.data.repository.RunningRepositoryImpl
import com.jeong.runninggoaltracker.domain.repository.RunningRepository

class RunningGoalTrackerApp : Application() {

    // 앱 전체에서 함께 쓸 DB / Repository
    lateinit var runningRepository: RunningRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Room DB 인스턴스 생성
        val db = Room.databaseBuilder(
            applicationContext,
            RunningDatabase::class.java,
            RunningDatabase.NAME
        ).fallbackToDestructiveMigration(false) // ← 버전 바뀌면 DB 날리고 다시 생성
            .build()

        // Repository 생성
        runningRepository = RunningRepositoryImpl(db.runningDao())

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "running_reminder"
            val name = "러닝 알림"
            val description = "러닝 리마인더 알림 채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
