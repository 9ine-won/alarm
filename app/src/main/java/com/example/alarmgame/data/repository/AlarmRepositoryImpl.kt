package com.example.alarmgame.data.repository

import com.example.alarmgame.data.local.dao.AlarmDao
import com.example.alarmgame.domain.model.Alarm
import com.example.alarmgame.domain.repository.AlarmRepository
import com.example.alarmgame.domain.scheduler.AlarmScheduler
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler
) : AlarmRepository {

    override fun observeAlarms(): Flow<List<Alarm>> =
        alarmDao.observeAlarms().map { entities -> entities.map { it.toDomain() } }

    override suspend fun get(id: Long): Alarm? = alarmDao.getById(id)?.toDomain()

    override suspend fun upsert(alarm: Alarm): Long {
        val now = System.currentTimeMillis()
        val entity = alarm.toEntity(now)
        val id = if (alarm.id == 0L) {
            alarmDao.insert(entity)
        } else {
            alarmDao.update(entity)
            alarm.id
        }
        alarmScheduler.schedule(entity.copy(id = id).toDomain())
        return id
    }

    override suspend fun delete(id: Long) {
        val existing = alarmDao.getById(id) ?: return
        alarmDao.delete(existing)
        alarmScheduler.cancel(id)
    }

    override suspend fun toggleEnabled(id: Long, enabled: Boolean, updatedAt: Long) {
        val alarm = alarmDao.getById(id) ?: return
        alarmDao.updateEnabled(id, enabled, updatedAt)
        val updated = alarm.copy(enabled = enabled, updatedAt = updatedAt).toDomain()
        if (enabled) {
            alarmScheduler.schedule(updated)
        } else {
            alarmScheduler.cancel(id)
        }
    }

    override suspend fun updateNextTrigger(id: Long, nextTriggerAt: Long, updatedAt: Long) {
        val alarm = alarmDao.getById(id) ?: return
        alarmDao.updateNextTrigger(id, nextTriggerAt, updatedAt)
        alarmScheduler.schedule(alarm.copy(nextTriggerAt = nextTriggerAt).toDomain())
    }

    override suspend fun enabledAlarms(): List<Alarm> =
        alarmDao.getEnabled().map { it.toDomain() }
}
