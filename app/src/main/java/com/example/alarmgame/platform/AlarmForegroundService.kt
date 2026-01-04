package com.example.alarmgame.platform

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.alarmgame.R
import com.example.alarmgame.app.AlarmReceiver
import com.example.alarmgame.app.AlarmRingingActivity
import com.example.alarmgame.domain.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmForegroundService : Service() {
    @Inject
    lateinit var repository: AlarmRepository

    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        Log.d("AlarmService", "Starting foreground service for alarm: $alarmId")

        val notification = AlarmNotificationManager(this).buildRingingNotification(alarmId)
        startForeground(FOREGROUND_ID, notification)

        // 알람 정보 가져와서 소리 재생
        startAlarmSound(alarmId)

        // 백그라운드에서 활동을 시작하기 위한 인텐트
        val ringIntent =
            Intent(this, AlarmRingingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            }

        try {
            startActivity(ringIntent)
            Log.d("AlarmService", "Activity start intent sent successfully")
        } catch (e: Exception) {
            Log.e("AlarmService", "Failed to start activity from service", e)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        stopAlarmSound()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startAlarmSound(alarmId: Long) {
        serviceScope.launch {
            val alarm =
                withContext(Dispatchers.IO) {
                    repository.get(alarmId)
                }

            val soundName = alarm?.soundUri // 여기서는 soundUri에 선택된 이름이 저장되어 있다고 가정

            val uri =
                when (soundName) {
                    "🎸 락 기타 리프" -> android.net.Uri.parse("android.resource://$packageName/${R.raw.alarm_rock_guitar}")
                    "🤘 메탈 리프" -> android.net.Uri.parse("android.resource://$packageName/${R.raw.alarm_metal_riff}")
                    "🎸 Tough Times" -> android.net.Uri.parse("android.resource://$packageName/${R.raw.alarm_tough_times}")
                    else -> {
                        // 기본값이거나 매칭되지 않는 경우
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    }
                }

            Log.d("AlarmService", "Playing sound: $soundName, Uri: $uri")

            mediaPlayer =
                runCatching {
                    MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build(),
                        )
                        setDataSource(this@AlarmForegroundService, uri)
                        isLooping = true
                        prepare()
                        start()
                    }
                }.getOrNull()
        }
    }

    private fun stopAlarmSound() {
        mediaPlayer?.run {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }

    companion object {
        private const val FOREGROUND_ID = 2001
        private const val EXTRA_ALARM_ID = "extra_alarm_id"

        fun start(
            context: Context,
            alarmId: Long,
        ) {
            val intent =
                Intent(context, AlarmForegroundService::class.java).apply {
                    putExtra(EXTRA_ALARM_ID, alarmId)
                }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmForegroundService::class.java))
        }
    }
}
