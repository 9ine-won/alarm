package com.example.alarmgame.app

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.lifecycle.coroutineScope
import com.example.alarmgame.platform.AlarmForegroundService
import com.example.alarmgame.platform.AlarmNotificationManager
import com.example.alarmgame.ui.screen.ringing.RingingGameScreen
import com.example.alarmgame.ui.theme.AlarmGameTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {
    private val alarmId: Long by lazy {
        intent?.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1L) ?: -1L
    }

    private val notificationManager by lazy { AlarmNotificationManager(this) }

    @javax.inject.Inject
    lateinit var repository: com.example.alarmgame.domain.repository.AlarmRepository

    private var alarm: com.example.alarmgame.domain.model.Alarm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AlarmRingingActivity", "onCreate started")

        setupLockScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        // 알람 데이터 비동기 로드 및 UI 설정
        lifecycle.coroutineScope.launch {
            alarm = repository.get(alarmId)
            setContent {
                AlarmGameTheme {
                    // 알람 데이터가 로드되면 해당 설정에 맞춰 게임 표시
                    alarm?.let {
                        RingingGameScreen(
                            gameType = it.gameType,
                            difficulty = it.difficulty,
                            onGameComplete = { stopAlarmAndFinish() },
                            onSnooze = { stopAlarmAndFinish() },
                        )
                    } ?: run {
                        // 데이터 로드 실패 혹은 로딩 중.. 기본 화면 또는 로딩 표시
                        // 여기서는 간단히 빈 화면 혹은 기본 게임
                        RingingGameScreen(
                            gameType = com.example.alarmgame.domain.model.GameType.MOLE,
                            difficulty = com.example.alarmgame.domain.model.Difficulty.NORMAL,
                            onGameComplete = { stopAlarmAndFinish() },
                            onSnooze = { stopAlarmAndFinish() },
                        )
                    }
                }
            }
        }
    }

    private fun setupLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(this, null)
        }

        // 구버전 및 일부 OEM 기기 호환성을 위한 플래그 추가
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
        )
    }

    private fun stopAlarmAndFinish() {
        AlarmForegroundService.stop(this)
        notificationManager.cancelRingingNotification()
        finish()
    }
}
