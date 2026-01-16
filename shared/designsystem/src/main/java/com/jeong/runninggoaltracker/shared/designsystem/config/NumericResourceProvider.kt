package com.jeong.runninggoaltracker.shared.designsystem.config

import android.content.Context
import com.jeong.runninggoaltracker.shared.designsystem.R

object NumericResourceProvider {
    fun zeroInt(context: Context): Int {
        return context.resources.getInteger(R.integer.numeric_zero)
    }

    fun zeroLong(context: Context): Long {
        return zeroInt(context).toLong()
    }

    fun zeroDouble(context: Context): Double {
        return zeroInt(context).toDouble()
    }

    fun zeroFloat(context: Context): Float {
        return zeroInt(context).toFloat()
    }

    fun metersInKm(context: Context): Double {
        return context.resources.getInteger(R.integer.record_meters_in_km).toDouble()
    }

    fun updateIntervalMillis(context: Context): Long {
        return context.resources.getInteger(R.integer.record_update_interval_millis).toLong()
    }

    fun elapsedUpdateIntervalMillis(context: Context): Long {
        return context.resources.getInteger(R.integer.record_elapsed_update_interval_millis).toLong()
    }

    fun minDistanceMeters(context: Context): Float {
        return context.resources.getInteger(R.integer.record_min_distance_meters).toFloat()
    }
}
