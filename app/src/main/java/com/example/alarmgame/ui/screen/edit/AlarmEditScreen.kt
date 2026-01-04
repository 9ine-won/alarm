package com.example.alarmgame.ui.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alarmgame.domain.model.Difficulty
import com.example.alarmgame.domain.model.GameType
import com.example.alarmgame.domain.model.SoundType
import com.example.alarmgame.domain.util.RepeatDays
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AlarmEditScreen(
    alarmId: Long?,
    onBack: () -> Unit,
    viewModel: AlarmEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val background = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.background
        )
    )

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AlarmEditEvent.Saved -> onBack()
                is AlarmEditEvent.Error -> { /* TODO: snackbar later */ }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (uiState.isNew) "알람 추가" else "알람 편집") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::save, enabled = !uiState.saving) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "저장")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TimePickerCard(
                        hour = uiState.form.hour,
                        minute = uiState.form.minute,
                        repeatDaysMask = uiState.form.repeatDaysMask,
                        onHourChange = { h -> viewModel.updateTime(h, uiState.form.minute) },
                        onMinuteChange = { m -> viewModel.updateTime(uiState.form.hour, m) }
                    )
                }
                item {
                    LabelCard(
                        label = uiState.form.label,
                        onLabelChange = viewModel::updateLabel
                    )
                }
                item {
                    RepeatCard(
                        selectedMask = uiState.form.repeatDaysMask,
                        onToggle = viewModel::toggleRepeat
                    )
                }
                item {
                    SoundCard(
                        selected = uiState.form.soundUri ?: "기본 알람음",
                        soundType = uiState.form.soundType,
                        onSelect = viewModel::updateSoundSelection
                    )
                }
                item {
                    VibrationCard(
                        vibrate = uiState.form.vibrate,
                        onToggle = viewModel::updateVibrate
                    )
                }
                item {
                    GameCard(
                        gameEnabled = uiState.form.gameEnabled,
                        onGameToggle = viewModel::updateGameEnabled,
                        game = uiState.form.gameType,
                        onGameSelect = viewModel::updateGameType,
                        difficulty = uiState.form.difficulty,
                        onDifficultySelect = viewModel::updateDifficulty
                    )
                }
                item {
                    SnoozeCard(
                        snoozeEnabled = uiState.form.snoozeEnabled,
                        snoozeMinutes = uiState.form.snoozeMinutes,
                        snoozeCount = uiState.form.snoozeMaxCount,
                        onSnoozeToggle = viewModel::updateSnoozeEnabled,
                        onMinutesChange = viewModel::updateSnoozeMinutes,
                        onCountChange = viewModel::updateSnoozeMaxCount
                    )
                }
                if (uiState.error != null) {
                    item {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        onClick = viewModel::save,
                        enabled = !uiState.saving && !uiState.loading
                    ) {
                        Text(if (uiState.isNew) "알람 저장" else "알람 업데이트")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimePickerCard(
    hour: Int,
    minute: Int,
    repeatDaysMask: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = formattedTime(hour, minute),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Light)
            )
            Text(
                text = repeatSummary(repeatDaysMask, hour, minute),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberStepper(
                    value = hour,
                    label = "시",
                    modifier = Modifier.weight(1f),
                    onValueChange = onHourChange,
                    onIncrement = { onHourChange((hour + 1) % 24) },
                    onDecrement = { onHourChange((hour - 1 + 24) % 24) }
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
                NumberStepper(
                    value = minute,
                    label = "분",
                    modifier = Modifier.weight(1f),
                    onValueChange = onMinuteChange,
                    onIncrement = { onMinuteChange((minute + 1) % 60) },
                    onDecrement = { onMinuteChange((minute - 1 + 60) % 60) }
                )
            }
        }
    }
}

@Composable
private fun NumberStepper(
    value: Int,
    label: String,
    onValueChange: (Int) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onIncrement) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "증가")
        }
        
        var isFocused by remember { mutableStateOf(false) }
        var textValue by remember { 
            mutableStateOf(TextFieldValue(value.toString().padStart(2, '0'))) 
        }

        // 외부에서 숫자가 바뀌면 (증감 버튼 등) 텍스트 동기화
        LaunchedEffect(value) {
            if (!isFocused) {
                val formatted = value.toString().padStart(2, '0')
                textValue = textValue.copy(text = formatted)
            }
        }

        BasicTextField(
            value = textValue,
            onValueChange = { newValue ->
                val digits = newValue.text.filter { it.isDigit() }
                
                // 새로운 숫자가 들어오면 마지막 2자리만 유지
                val processed = if (digits.length > 2) digits.takeLast(2) else digits
                
                // 텍스트 상태 업데이트
                textValue = newValue.copy(
                    text = processed,
                    selection = TextRange(processed.length)
                )

                // 유효한 숫자인 경우 즉시 ViewModel에 전달
                processed.toIntOrNull()?.let { onValueChange(it) }
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(60.dp)
                .onFocusChanged { 
                    isFocused = it.isFocused
                    if (it.isFocused) {
                        // 포커스를 얻을 때 텍스트가 전체 선택되거나 초기화되도록 설정
                        val currentText = value.toString().padStart(2, '0')
                        textValue = TextFieldValue(
                            text = currentText,
                            selection = TextRange(0, currentText.length)
                        )
                    } else {
                        // 포커스를 잃을 때 0 패딩 처리
                        textValue = TextFieldValue(value.toString().padStart(2, '0'))
                    }
                }
        )

        IconButton(onClick = onDecrement) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "감소")
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LabelCard(
    label: String,
    onLabelChange: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(label.isNotBlank()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text("알람 이름", style = MaterialTheme.typography.titleMedium)
                        if (!expanded && label.isBlank()) {
                            Text(
                                text = "탭하여 추가 (선택사항)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (!expanded && label.isNotBlank()) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "펼치기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (expanded) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = label,
                    onValueChange = onLabelChange,
                    placeholder = { Text("알람 이름 (예: 기상, 약 먹기)") },
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun RepeatCard(
    selectedMask: Int,
    onToggle: (DayOfWeek) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Outlined.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("반복", style = MaterialTheme.typography.titleMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DayOfWeek.values().forEach { day ->
                    val selected = (selectedMask and (1 shl ((day.ordinal + 1) % 7))) != 0
                    AssistChip(
                        modifier = Modifier.weight(1f),
                        onClick = { onToggle(day) },
                        label = { Text(dayLabel(day)) },
                        colors = if (selected) {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    )
                }
            }
            Text(
                text = repeatSummary(selectedMask),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoundCard(
    selected: String,
    soundType: SoundType,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "기본 알람음", 
        "부드러운 종소리", 
        "경쾌한 벨소리", 
        "강한 알람음",
        "🎸 락 기타 리프",
        "🤘 메탈 리프",
        "🎸 Tough Times",
        "커스텀..."
    )
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Outlined.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("알람음", style = MaterialTheme.typography.titleMedium)
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selected,
                    onValueChange = {},
                    label = { Text(if (soundType == SoundType.CUSTOM) "커스텀" else "사전 설정") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onSelect(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VibrationCard(
    vibrate: Boolean,
    onToggle: (Boolean) -> Unit
) {
    var intensity by rememberSaveable { mutableStateOf("MEDIUM") }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Outlined.Vibration, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("진동", style = MaterialTheme.typography.titleMedium)
                }
                Switch(
                    checked = vibrate,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
            if (vibrate) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("약함", "보통", "강함").forEach { label ->
                        val selected = when (label) {
                            "약함" -> intensity == "LIGHT"
                            "보통" -> intensity == "MEDIUM"
                            else -> intensity == "STRONG"
                        }
                        AssistChip(
                            onClick = {
                                intensity = when (label) {
                                    "약함" -> "LIGHT"
                                    "보통" -> "MEDIUM"
                                    else -> "STRONG"
                                }
                            },
                            label = { Text(label) },
                            colors = if (selected) {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    gameEnabled: Boolean,
    onGameToggle: (Boolean) -> Unit,
    game: GameType,
    onGameSelect: (GameType) -> Unit,
    difficulty: Difficulty,
    onDifficultySelect: (Difficulty) -> Unit
) {
    val gameCards = listOf(
        GameType.MOLE to "두더지 잡기",
        GameType.SMASH to "망치 깨기"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Outlined.Gamepad, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("알람 해제 게임", style = MaterialTheme.typography.titleMedium)
                }
                Switch(checked = gameEnabled, onCheckedChange = onGameToggle)
            }
            if (gameEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameType.values().forEach { type ->
                        val selected = game == type
                        val label = when (type) {
                            GameType.MOLE -> "두더지 (클래식)"
                            GameType.MOLE_HELL -> "두더지 (지옥)"
                            GameType.SMASH -> "스매시"
                        }
                        val emoji = when (type) {
                            GameType.MOLE -> "🐹"
                            GameType.MOLE_HELL -> "👿"
                            GameType.SMASH -> "🔨"
                        }
                        
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.weight(1f).clickable { onGameSelect(type) },
                            onClick = { onGameSelect(type) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 14.dp), // 패딩 조정
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = emoji,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall, // 글자 크기 조정
                                    maxLines = 1,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("난이도", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Difficulty.values().forEach { diff ->
                            val selected = difficulty == diff
                            val color = difficultyColor(diff)
                            AssistChip(
                                onClick = { onDifficultySelect(diff) },
                                label = { Text(difficultyLabel(diff)) },
                                colors = if (selected) {
                                    AssistChipDefaults.assistChipColors(
                                        containerColor = color,
                                        labelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
                InfoCard(
                    title = "게임 성공 시 알람이 꺼집니다",
                    body = "선택한 난이도에 따라 게임 진행 속도가 달라집니다."
                )
            }
        }
    }
}

@Composable
private fun SnoozeCard(
    snoozeEnabled: Boolean,
    snoozeMinutes: Int,
    snoozeCount: Int,
    onSnoozeToggle: (Boolean) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("스누즈", style = MaterialTheme.typography.titleMedium)
                Switch(checked = snoozeEnabled, onCheckedChange = onSnoozeToggle)
            }
            if (snoozeEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StepperRow(
                        title = "간격",
                        valueLabel = "${snoozeMinutes}분",
                        onMinus = { onMinutesChange((snoozeMinutes - 1).coerceAtLeast(1)) },
                        onPlus = { onMinutesChange((snoozeMinutes + 1).coerceAtMost(30)) }
                    )
                    StepperRow(
                        title = "최대 횟수",
                        valueLabel = "${snoozeCount}회",
                        onMinus = { onCountChange((snoozeCount - 1).coerceAtLeast(1)) },
                        onPlus = { onCountChange((snoozeCount + 1).coerceAtMost(10)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepperRow(
    title: String,
    valueLabel: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMinus) {
                Text(text = "-", style = MaterialTheme.typography.headlineSmall)
            }
            Text(
                text = valueLabel,
                modifier = Modifier.width(64.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onPlus) {
                Text(text = "+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

private fun dayLabel(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
}

private fun difficultyLabel(difficulty: Difficulty): String = when (difficulty) {
    Difficulty.EASY -> "쉬움"
    Difficulty.NORMAL -> "보통"
    Difficulty.HARD -> "어려움"
    Difficulty.HELL -> "지옥"
}

private fun difficultyColor(difficulty: Difficulty): Color = when (difficulty) {
    Difficulty.EASY -> Color(0xFF4CAF50)
    Difficulty.NORMAL -> Color(0xFF2196F3)
    Difficulty.HARD -> Color(0xFFFF9800)
    Difficulty.HELL -> Color(0xFFF44336)
}

private fun formattedTime(hour: Int, minute: Int): String {
    val period = if (hour >= 12) "오후" else "오전"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$period ${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private fun repeatSummary(mask: Int, hour: Int? = null, minute: Int? = null): String {
    if (mask == 0) {
        if (hour != null && minute != null) {
            val now = ZonedDateTime.now(ZoneId.systemDefault())
            val targetToday = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
            val nextAlarm = if (targetToday.isAfter(now)) targetToday else targetToday.plusDays(1)
            
            val month = nextAlarm.monthValue
            val day = nextAlarm.dayOfMonth
            val dayOfWeek = dayLabel(nextAlarm.dayOfWeek)
            
            return "${month}월 ${day}일(${dayOfWeek}) 한 번만 울립니다"
        }
        return "한 번만 울립니다"
    }
    val days = RepeatDays.daysFrom(mask).sortedBy { it.ordinal }
    if (days.size == 7) return "매일 반복"
    return days.joinToString(", ") { dayLabel(it) } + " 반복"
}
