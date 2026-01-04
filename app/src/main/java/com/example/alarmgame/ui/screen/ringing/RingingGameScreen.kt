package com.example.alarmgame.ui.screen.ringing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RingingGameScreen(
    gameType: com.example.alarmgame.domain.model.GameType,
    difficulty: com.example.alarmgame.domain.model.Difficulty,
    onGameComplete: () -> Unit,
    onSnooze: () -> Unit
) {
    // 게임 타입에 따라 화면 분기
    when (gameType) {
        com.example.alarmgame.domain.model.GameType.MOLE,
        com.example.alarmgame.domain.model.GameType.MOLE_HELL -> {
            WhacAMoleGameScreen(
                gameType = gameType,
                difficulty = difficulty,
                onGameComplete = onGameComplete
            )
        }
        else -> {
            // 다른 게임이 추가될 때까지 기본 placeholder 혹은 Fallback
             Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "준비 중인 게임입니다.",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onGameComplete
                ) {
                    Text("게임 확인 (건너뛰기)")
                }
            }
        }
    }
}
