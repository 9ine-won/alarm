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
    onGameComplete: () -> Unit,
    onSnooze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "알람 + 게임",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "전체화면 Activity에서 게임을 시작합니다.\n두더지/망치 게임 구현 예정",
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onGameComplete
        ) {
            Text("게임 성공 (테스트용)")
        }
        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = onSnooze
        ) {
            Text("스누즈 (테스트용)")
        }
    }
}
