package com.example.alarmgame.platform

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.content.ContextCompat

class AlarmForegroundService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        val notification = AlarmNotificationManager(this).buildRingingNotification(alarmId)
        startForeground(FOREGROUND_ID, notification)
        startAlarmSound()
        return START_STICKY
    }

    override fun onDestroy() {
        stopAlarmSound()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startAlarmSound() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mediaPlayer = runCatching {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmForegroundService, uri)
                isLooping = true
                prepare()
                start()
            }
        }.getOrNull()
    }

    private fun stopAlarmSound() {
        mediaPlayer?.run {
            stop()
            release()
        }
        mediaPlayer = null
    }

    companion object {
        private const val FOREGROUND_ID = 2001
        private const val EXTRA_ALARM_ID = "extra_alarm_id"

        fun start(context: Context, alarmId: Long) {
            val intent = Intent(context, AlarmForegroundService::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmForegroundService::class.java))
        }
    }
}
