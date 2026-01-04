package com.example.alarmgame.platform

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.alarmgame.R
import com.example.alarmgame.app.AlarmReceiver
import com.example.alarmgame.app.AlarmRingingActivity

class AlarmNotificationManager(private val context: Context) {
    private val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel =
            NotificationChannel(
                CHANNEL_ID_ALARM,
                "알람",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "알람 알림 및 전체화면 알림용 채널"
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
        notificationManager.createNotificationChannel(channel)
    }

    fun showRingingNotification(alarmId: Long) {
        notificationManager.notify(NOTIFICATION_ID_RING, buildRingingNotification(alarmId))
    }

    fun buildRingingNotification(alarmId: Long): Notification {
        val fullScreenIntent =
            Intent(context, AlarmRingingActivity::class.java).apply {
                putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val fullScreenPendingIntent =
            PendingIntent.getActivity(
                context,
                alarmId.toInt(),
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        return NotificationCompat.Builder(context, CHANNEL_ID_ALARM)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("알람이 울리는 중")
            .setContentText("알람을 해제하려면 게임을 완료하세요.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(false)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()
    }

    fun cancelRingingNotification() {
        notificationManager.cancel(NOTIFICATION_ID_RING)
    }

    companion object {
        const val CHANNEL_ID_ALARM = "alarm_channel"
        private const val NOTIFICATION_ID_RING = 1001
    }
}
