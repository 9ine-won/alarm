package com.example.alarmgame.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmgame.domain.model.Alarm
import com.example.alarmgame.domain.scheduler.AlarmScheduler
import com.example.alarmgame.app.AlarmReceiver
import com.example.alarmgame.app.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    override fun schedule(alarm: Alarm) {
        if (alarm.nextTriggerAt <= 0) return
        val triggerAt = alarm.nextTriggerAt
        val operation = alarmPendingIntent(alarm.id, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        val showIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val info = AlarmManager.AlarmClockInfo(triggerAt, showIntent)
        Log.d(TAG, "schedule(id=${alarm.id}, trigger=$triggerAt)")
        alarmManager.setAlarmClock(info, operation)
    }

    override fun cancel(alarmId: Long) {
        val operation = alarmPendingIntent(alarmId, PendingIntent.FLAG_NO_CREATE)
        if (operation != null) {
            Log.d(TAG, "cancel(id=$alarmId)")
            alarmManager.cancel(operation)
            operation.cancel()
        }
    }

    override fun rescheduleAll(alarms: List<Alarm>) {
        Log.d(TAG, "rescheduleAll(size=${alarms.size})")
        alarms.forEach(::schedule)
    }

    private fun alarmPendingIntent(alarmId: Long, flags: Int): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_RING
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        val combinedFlags = PendingIntent.FLAG_IMMUTABLE or flags
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            combinedFlags
        )
    }

    companion object {
        private const val TAG = "AlarmScheduler"
    }
}
