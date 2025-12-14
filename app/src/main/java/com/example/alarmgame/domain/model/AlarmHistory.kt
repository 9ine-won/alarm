package com.example.alarmgame.domain.model

data class AlarmHistory(
    val id: Long = 0L,
    val alarmId: Long,
    val firedAt: Long,
    val dismissedAt: Long?,
    val snoozedCount: Int,
    val gameSuccess: Boolean
)
