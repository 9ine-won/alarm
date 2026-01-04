package com.example.alarmgame.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.alarmgame.platform.AlarmForegroundService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        Log.d(TAG, "onReceive alarmId=$alarmId")

        // WakeLock을 사용하여 CPU를 잠시 깨웁니다 (서비스 시작 보장)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        val wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "AlarmGame:WakeLock")
        // 10 seconds
        wakeLock.acquire(10 * 1000L)

        // 포그라운드 서비스를 시작하여 사운드 재생 및 알림 표시
        AlarmForegroundService.start(context, alarmId)
    }

    companion object {
        const val ACTION_RING = "com.example.alarmgame.action.RING_INTERNAL"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        private const val TAG = "AlarmReceiver"
    }
}
