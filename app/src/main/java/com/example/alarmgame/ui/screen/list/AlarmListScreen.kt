package com.example.alarmgame.ui.screen.list

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alarmgame.domain.model.Alarm
import com.example.alarmgame.domain.util.RepeatDays
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun AlarmListScreen(
    onAddAlarm: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditAlarm: (Long) -> Unit,
    viewModel: AlarmListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlarmListContent(
        alarms = uiState.alarms,
        onAddAlarm = onAddAlarm,
        onOpenSettings = onOpenSettings,
        onEditAlarm = onEditAlarm,
        onToggle = viewModel::toggleEnabled,
        onDelete = viewModel::delete,
    )
}

@Composable
private fun AlarmListContent(
    alarms: List<Alarm>,
    onAddAlarm: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditAlarm: (Long) -> Unit,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val nextAlarm = alarms.filter { it.enabled }.minByOrNull { it.nextTriggerAt }
    val gradient =
        Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.background,
            ),
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(gradient),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "알람") },
                    navigationIcon = {
                        IconButton(onClick = onOpenSettings) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "설정")
                        }
                    },
                    actions = {
                        IconButton(onClick = onAddAlarm) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "알람 추가")
                        }
                    },
                    colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                )
            },
        ) { padding ->
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    HeroCard(onAddAlarm, nextAlarm)
                }

                if (alarms.isEmpty()) {
                    item {
                        EmptyState(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            onAddAlarm = onAddAlarm,
                        )
                    }
                } else {
                    items(items = alarms, key = { it.id }) { alarm ->
                        AlarmRow(
                            alarm = alarm,
                            onToggle = { enabled -> onToggle(alarm.id, enabled) },
                            onDelete = { onDelete(alarm.id) },
                            onClick = { onEditAlarm(alarm.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmRow(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
            ) {
                Text(
                    text = formatTime(alarm.hour, alarm.minute),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alarm.label.ifBlank { "알람 #${alarm.id}" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = describeRepeat(alarm.repeatDaysMask),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    if (alarm.enabled) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = calculateRemainingTime(alarm),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = onToggle,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "편집",
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "삭제",
                        )
                    }
                }
            }
        }
    }
}

private fun calculateRemainingTime(alarm: Alarm): String {
    val now = ZonedDateTime.now(ZoneId.systemDefault())
    val targetToday = now.withHour(alarm.hour).withMinute(alarm.minute).withSecond(0).withNano(0)

    val nextTrigger =
        if (alarm.repeatDaysMask == 0) {
            if (targetToday.isAfter(now)) targetToday else targetToday.plusDays(1)
        } else {
            val repeatDays = RepeatDays.daysFrom(alarm.repeatDaysMask)
            var best: ZonedDateTime? = null
            for (offset in 0L..7L) {
                val day = now.plusDays(offset)
                if (repeatDays.contains(day.dayOfWeek)) {
                    val candidate =
                        day.withHour(alarm.hour).withMinute(alarm.minute)
                            .withSecond(0).withNano(0)
                    if (candidate.isAfter(now)) {
                        if (best == null || candidate.isBefore(best)) {
                            best = candidate
                        }
                    }
                }
            }
            best ?: targetToday.plusDays(1) // Fallback
        }

    val duration = Duration.between(now, nextTrigger)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60

    return if (hours > 0) "${hours}시간 ${minutes}분 후" else "${minutes}분 후"
}

@Composable
private fun EmptyState(
    modifier: Modifier,
    onAddAlarm: () -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "등록된 알람이 없어요",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "플로팅 버튼을 눌러 새 알람을 추가하세요.",
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FilledIconButton(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onAddAlarm,
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "알람 추가")
        }
    }
}

private fun formatTime(
    hour: Int,
    minute: Int,
): String = String.format("%02d:%02d", hour, minute)

private fun describeRepeat(mask: Int): String {
    if (mask == 0) return "한 번 울림"

    val days = RepeatDays.daysFrom(mask).sorted()
    if (days.size == 7) return "매일"

    val isWeekend = days.size == 2 && days.contains(java.time.DayOfWeek.SATURDAY) && days.contains(java.time.DayOfWeek.SUNDAY)
    if (isWeekend) return "주말"

    val isWeekdays = days.size == 5 && !days.contains(java.time.DayOfWeek.SATURDAY) && !days.contains(java.time.DayOfWeek.SUNDAY)
    if (isWeekdays) return "주중"

    return days.joinToString(" ") {
        when (it) {
            java.time.DayOfWeek.MONDAY -> "월"
            java.time.DayOfWeek.TUESDAY -> "화"
            java.time.DayOfWeek.WEDNESDAY -> "수"
            java.time.DayOfWeek.THURSDAY -> "목"
            java.time.DayOfWeek.FRIDAY -> "금"
            java.time.DayOfWeek.SATURDAY -> "토"
            java.time.DayOfWeek.SUNDAY -> "일"
        }
    }
}

@Composable
private fun HeroCard(
    onAddAlarm: () -> Unit,
    nextAlarm: Alarm?,
) {
    val gradient =
        Brush.linearGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                ),
        )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .background(gradient, shape = RoundedCornerShape(20.dp))
                    .padding(18.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text("Good vibes, good morning", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = nextAlarm?.let { "다음 알람 ${formatTime(it.hour, it.minute)}" } ?: "알람이 없어요",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = if (nextAlarm != null) "게임 클리어해야 해제됩니다." else "알람을 추가해보세요.",
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = onAddAlarm) {
                    Text("알람 추가")
                }
            }
        }
    }
}
