package com.example.alarmgame.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmgame.platform.AlarmNotificationManager
import com.example.alarmgame.platform.AlarmForegroundService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        Log.d(TAG, "onReceive alarmId=$alarmId, starting ringing activity")
        AlarmForegroundService.start(context, alarmId)
        AlarmNotificationManager(context).showRingingNotification(alarmId)
        val ringIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_ALARM_ID, alarmId)
            action = ACTION_RING
        }
        context.startActivity(ringIntent)
    }

    companion object {
        const val ACTION_RING = "com.example.alarmgame.action.RING_INTERNAL"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        private const val TAG = "AlarmReceiver"
    }
}
