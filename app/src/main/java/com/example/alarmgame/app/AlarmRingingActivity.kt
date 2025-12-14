package com.example.alarmgame.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.alarmgame.ui.screen.ringing.RingingGameScreen
import com.example.alarmgame.ui.theme.AlarmGameTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.alarmgame.platform.AlarmForegroundService
import com.example.alarmgame.platform.AlarmNotificationManager

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    private val alarmId: Long by lazy {
        intent?.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L) ?: -1L
    }

    private val notificationManager by lazy { AlarmNotificationManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            AlarmGameTheme {
                RingingGameScreen(
                    onGameComplete = { stopAlarmAndFinish() },
                    onSnooze = { stopAlarmAndFinish() }
                )
            }
        }
    }

    private fun stopAlarmAndFinish() {
        AlarmForegroundService.stop(this)
        notificationManager.cancelRingingNotification()
        finish()
    }
}
