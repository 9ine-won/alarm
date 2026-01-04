package com.example.alarmgame.ui.screen.ringing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alarmgame.R
import com.example.alarmgame.domain.model.Difficulty
import com.example.alarmgame.domain.model.GameType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Whac-A-Mole (두더지 잡기) 게임 화면
 */

@Composable
fun WhacAMoleGameScreen(
    // MOLE or MOLE_HELL
    gameType: GameType,
    difficulty: Difficulty,
    onGameComplete: () -> Unit,
) {
    // 테마 설정
    val isHell = gameType == GameType.MOLE_HELL
    val bgRes = if (isHell) R.drawable.game_bg_hell else R.drawable.game_bg_classic
    val moleUpRes = if (isHell) R.drawable.game_mole_hell_up else R.drawable.game_mole_classic_up
    val moleHitRes = if (isHell) R.drawable.game_mole_hell_hit else R.drawable.game_mole_classic_hit
    val holeColor = if (isHell) Color(0xFF4A0404) else Color(0xFF5D4037)
    val primaryColor = if (isHell) Color(0xFFFF5722) else Color(0xFF4CAF50)

    // 난이도별 설정 (Spawn Interval / Stay Duration)
    val (minInterval, maxInterval, stayDuration) =
        when (difficulty) {
            Difficulty.EASY -> Triple(1000L, 1500L, 1200L)
            Difficulty.NORMAL -> Triple(800L, 1200L, 1000L)
            Difficulty.HARD -> Triple(600L, 1000L, 800L)
            Difficulty.HELL -> Triple(400L, 800L, 600L)
        }

    // 게임 상태
    var score by remember { mutableStateOf(0) }
    val targetScore = 15
    val moleState = remember { List(9) { MutableStateFlow(MoleState.HIDDEN) } }

    // 게임 루프
    LaunchedEffect(Unit) {
        while (score < targetScore) {
            val delayTime = Random.nextLong(minInterval, maxInterval)
            delay(delayTime)

            // 랜덤한 구멍에서 두더지 튀어나오기
            val index = Random.nextInt(9)
            if (moleState[index].value == MoleState.HIDDEN) {
                launch {
                    moleState[index].value = MoleState.UP
                    delay(stayDuration) // 두더지가 머무르는 시간
                    if (moleState[index].value == MoleState.UP) {
                        moleState[index].value = MoleState.HIDDEN
                    }
                }
            }
        }
    }

    // 성공 체크
    LaunchedEffect(score) {
        if (score >= targetScore) {
            delay(500)
            onGameComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // 점수판
            Text(
                text = "SCORE: $score / $targetScore",
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow =
                            androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                blurRadius = 8f,
                            ),
                    ),
            )

            LinearProgressIndicator(
                progress = { score.toFloat() / targetScore },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 48.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp)),
                color = primaryColor,
                trackColor = Color.White.copy(alpha = 0.5f),
            )

            // 3x3 그리드
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                for (row in 0 until 3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            MoleHole(
                                stateFlow = moleState[index],
                                moleUpRes = moleUpRes,
                                moleHitRes = moleHitRes,
                                holeColor = holeColor,
                                onHit = {
                                    score++
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class MoleState {
    HIDDEN,
    UP,
    HIT,
}

@Composable
fun MoleHole(
    stateFlow: MutableStateFlow<MoleState>,
    moleUpRes: Int,
    moleHitRes: Int,
    holeColor: Color,
    onHit: () -> Unit,
) {
    val state by stateFlow.collectAsState()
    val scope = rememberCoroutineScope()

    Box(
        modifier =
            Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(holeColor.copy(alpha = 0.8f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (state == MoleState.UP) {
                        onHit()
                        scope.launch {
                            stateFlow.value = MoleState.HIT
                            delay(200)
                            stateFlow.value = MoleState.HIDDEN
                        }
                    }
                },
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 구멍 그림자 효과 (내부)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(2f)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
        )

        if (state != MoleState.HIDDEN) {
            Image(
                painter = painterResource(id = if (state == MoleState.HIT) moleHitRes else moleUpRes),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 5.dp),
                // 구멍에서 조금 올라온 느낌
                contentScale = ContentScale.Fit,
            )
        }
    }
}
