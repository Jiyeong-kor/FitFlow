package com.jeong.runninggoaltracker.feature.record.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.jeong.runninggoaltracker.domain.usecase.AddRunningRecordUseCase
import com.jeong.runninggoaltracker.domain.util.DateProvider
import com.jeong.runninggoaltracker.feature.record.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class RunningTrackerService : Service() {

    @Inject
    lateinit var stateUpdater: RunningTrackerStateUpdater

    @Inject
    lateinit var addRunningRecordUseCase: AddRunningRecordUseCase

    @Inject
    lateinit var dateProvider: DateProvider

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var startTimeMillis: Long = 0L
    private var distanceMeters: Double = 0.0
    private var lastLocation: Location? = null
    private var tracking: Boolean = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startTracking() {
        if (tracking) return

        tracking = true
        distanceMeters = 0.0
        lastLocation = null
        startTimeMillis = System.currentTimeMillis()
        stateUpdater.markTracking()

        startForeground(NOTIFICATION_ID, createNotification(0.0, 0L))
        startLocationUpdates()
    }

    private fun stopTracking() {
        if (!tracking) {
            stopSelf()
            return
        }
        tracking = false
        stopLocationUpdates()
        val elapsed = System.currentTimeMillis() - startTimeMillis
        val distanceKm = distanceMeters / METERS_IN_KM
        stateUpdater.stop()

        serviceScope.launch {
            val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsed).toInt()
            if (distanceKm > 0 && durationMinutes > 0) {
                addRunningRecordUseCase(
                    date = dateProvider.getToday(),
                    distanceKm = distanceKm,
                    durationMinutes = durationMinutes
                )
            }
            stopSelf()
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stateUpdater.markPermissionRequired()
            stopTracking()
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_MILLIS
        ).setMinUpdateIntervalMillis(UPDATE_INTERVAL_MILLIS)
            .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
            .build()

        locationCallback = object : LocationCallback() {
            @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                updateDistance(location)
                val elapsed = System.currentTimeMillis() - startTimeMillis
                stateUpdater.update(distanceMeters / METERS_IN_KM, elapsed)
                NotificationManagerCompat.from(this@RunningTrackerService)
                    .notify(NOTIFICATION_ID, createNotification(distanceMeters / METERS_IN_KM, elapsed))
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
    }

    private fun updateDistance(newLocation: Location) {
        lastLocation?.let { previous ->
            distanceMeters += previous.distanceTo(newLocation).toDouble()
        }
        lastLocation = newLocation
    }

    private fun createNotification(distanceKm: Double, elapsedMillis: Long): Notification {
        val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
        val content = getString(
            R.string.record_notification_content,
            String.format("%.2f", distanceKm),
            elapsedMinutes
        )

        val stopIntent = Intent(this, RunningTrackerService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStopIntent = PendingIntent.getService(
            this,
            REQUEST_CODE_STOP,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.record_notification_title))
            .setContentText(content)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_media_pause,
                getString(R.string.button_stop_tracking),
                pendingStopIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.record_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = getString(R.string.record_notification_channel_description)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopLocationUpdates()
        stateUpdater.stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.jeong.runninggoaltracker.action.START_TRACKING"
        const val ACTION_STOP = "com.jeong.runninggoaltracker.action.STOP_TRACKING"

        private const val METERS_IN_KM = 1000.0
        private const val UPDATE_INTERVAL_MILLIS = 2_000L
        private const val MIN_DISTANCE_METERS = 5f
        private const val NOTIFICATION_ID = 4001
        private const val CHANNEL_ID = "running_tracker_channel"
        private const val REQUEST_CODE_STOP = 4002
    }
}
