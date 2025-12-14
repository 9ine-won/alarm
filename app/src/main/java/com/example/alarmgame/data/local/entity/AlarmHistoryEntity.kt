package com.example.alarmgame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_history")
data class AlarmHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val alarmId: Long,
    val firedAt: Long,
    val dismissedAt: Long?,
    val snoozedCount: Int,
    val gameSuccess: Boolean
)
