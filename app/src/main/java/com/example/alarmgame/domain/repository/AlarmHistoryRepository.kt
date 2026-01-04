package com.example.alarmgame.domain.repository

import com.example.alarmgame.domain.model.AlarmHistory
import kotlinx.coroutines.flow.Flow

interface AlarmHistoryRepository {
    suspend fun insert(history: AlarmHistory): Long

    fun observeByAlarm(
        alarmId: Long,
        limit: Int = 20,
    ): Flow<List<AlarmHistory>>
}
