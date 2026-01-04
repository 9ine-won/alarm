package com.example.alarmgame.data.repository

import com.example.alarmgame.data.local.dao.AlarmHistoryDao
import com.example.alarmgame.data.local.entity.AlarmHistoryEntity
import com.example.alarmgame.domain.model.AlarmHistory
import com.example.alarmgame.domain.repository.AlarmHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmHistoryRepositoryImpl
    @Inject
    constructor(
        private val historyDao: AlarmHistoryDao,
    ) : AlarmHistoryRepository {
        override suspend fun insert(history: AlarmHistory): Long = historyDao.insert(history.toEntity())

        override fun observeByAlarm(
            alarmId: Long,
            limit: Int,
        ): Flow<List<AlarmHistory>> =
            historyDao.observeByAlarm(alarmId, limit).map { entities ->
                entities.map { it.toDomain() }
            }
    }

private fun AlarmHistoryEntity.toDomain(): AlarmHistory =
    AlarmHistory(
        id = id,
        alarmId = alarmId,
        firedAt = firedAt,
        dismissedAt = dismissedAt,
        snoozedCount = snoozedCount,
        gameSuccess = gameSuccess,
    )

private fun AlarmHistory.toEntity(): AlarmHistoryEntity =
    AlarmHistoryEntity(
        id = id,
        alarmId = alarmId,
        firedAt = firedAt,
        dismissedAt = dismissedAt,
        snoozedCount = snoozedCount,
        gameSuccess = gameSuccess,
    )
