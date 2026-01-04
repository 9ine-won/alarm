package com.example.alarmgame.domain.repository

import com.example.alarmgame.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun observeAlarms(): Flow<List<Alarm>>

    suspend fun get(id: Long): Alarm?

    suspend fun upsert(alarm: Alarm): Long

    suspend fun delete(id: Long)

    suspend fun toggleEnabled(
        id: Long,
        enabled: Boolean,
        updatedAt: Long,
    )

    suspend fun updateNextTrigger(
        id: Long,
        nextTriggerAt: Long,
        updatedAt: Long,
    )

    suspend fun enabledAlarms(): List<Alarm>
}
