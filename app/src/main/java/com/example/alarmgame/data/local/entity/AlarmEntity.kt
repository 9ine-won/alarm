package com.example.alarmgame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alarmgame.domain.model.Difficulty
import com.example.alarmgame.domain.model.GameType
import com.example.alarmgame.domain.model.SoundType

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
    val gameEnabled: Boolean = false,
    val gameType: GameType,
    val difficulty: Difficulty,
    val nextTriggerAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
