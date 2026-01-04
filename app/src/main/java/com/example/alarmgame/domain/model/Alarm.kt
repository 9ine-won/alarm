package com.example.alarmgame.domain.model

data class Alarm(
    val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val repeatDaysMask: Int,
    val enabled: Boolean,
    val label: String,
    val soundType: SoundType,
    val soundUri: String?,
    val vibrate: Boolean,
    val snoozeEnabled: Boolean,
    val snoozeMinutes: Int,
    val snoozeMaxCount: Int,
    val gameEnabled: Boolean,
    val gameType: GameType,
    val difficulty: Difficulty,
    val nextTriggerAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
