package com.example.alarmgame.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.alarmgame.ui.screen.ringing.RingingGameScreen
import com.example.alarmgame.ui.theme.AlarmGameTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            AlarmGameTheme {
                RingingGameScreen(
                    onGameComplete = { finish() },
                    onSnooze = { finish() }
                )
            }
        }
    }
}
