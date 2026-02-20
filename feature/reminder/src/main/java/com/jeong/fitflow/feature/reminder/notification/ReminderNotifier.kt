package com.jeong.fitflow.feature.reminder.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import com.jeong.fitflow.feature.reminder.R
import com.jeong.fitflow.feature.reminder.contract.ReminderNotificationContract
import com.jeong.fitflow.shared.designsystem.notification.NotificationPermissionGate

object ReminderNotifier {

    fun showNow(context: Context, hour: Int, minute: Int) {
        NotificationPermissionGate.notifyIfAllowed(
            context,
            ReminderNotificationContract.NOTIFICATION_ID
        ) {
            val text = context.getString(
                R.string.reminder_notification_text_format,
                hour,
                minute
            )

            NotificationCompat.Builder(
                context,
                ReminderNotificationContract.NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
        }
    }
}
