package com.jeong.runninggoaltracker.feature.reminder.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jeong.runninggoaltracker.domain.model.RunningReminder
import com.jeong.runninggoaltracker.feature.reminder.contract.ReminderAlarmContract
import com.jeong.runninggoaltracker.feature.reminder.notification.ReminderNotifier
import com.jeong.runninggoaltracker.shared.designsystem.config.AppNumericTokens
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val payload = intent.toAlarmPayload()

        ReminderNotifier.showNow(context, payload.hour, payload.minute)

        val reminder = intent.toRunningReminderOrNull() ?: return

        getEntryPoint(context)
            .reminderScheduler()
            .scheduleIfNeeded(reminder)
    }

    private fun getEntryPoint(context: Context): ReminderAlarmReceiverEntryPoint =
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReminderAlarmReceiverEntryPoint::class.java
        )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ReminderAlarmReceiverEntryPoint {
    fun reminderScheduler(): ReminderScheduler
}

private data class AlarmPayload(
    val id: Int,
    val hour: Int,
    val minute: Int,
    val dayOfWeekRaw: Int,
)

private fun Intent.toAlarmPayload(): AlarmPayload {
    val zeroInt = AppNumericTokens.zeroInt

    return AlarmPayload(
        id = getIntExtra(ReminderAlarmContract.EXTRA_ID, zeroInt),
        hour = getIntExtra(ReminderAlarmContract.EXTRA_HOUR, zeroInt),
        minute = getIntExtra(ReminderAlarmContract.EXTRA_MINUTE, zeroInt),
        dayOfWeekRaw = getIntExtra(ReminderAlarmContract.EXTRA_DAY_OF_WEEK, zeroInt)
    )
}

private fun Intent.toRunningReminderOrNull(): RunningReminder? {
    val zeroInt = AppNumericTokens.zeroInt
    val minDay = AppNumericTokens.reminderDayOfWeekMin
    val maxDay = AppNumericTokens.reminderDayOfWeekMax

    val id = getIntExtra(ReminderAlarmContract.EXTRA_ID, zeroInt)
        .takeIf { it != zeroInt }
        ?: return null

    val dayOfWeekRaw = getIntExtra(ReminderAlarmContract.EXTRA_DAY_OF_WEEK, zeroInt)
    if (dayOfWeekRaw !in minDay..maxDay) return null

    val hour = getIntExtra(ReminderAlarmContract.EXTRA_HOUR, zeroInt)
    val minute = getIntExtra(ReminderAlarmContract.EXTRA_MINUTE, zeroInt)

    return RunningReminder(
        id = id,
        hour = hour,
        minute = minute,
        enabled = true,
        days = setOf(dayOfWeekRaw)
    )
}
