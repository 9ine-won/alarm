package com.example.alarmgame.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.alarmgame.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms ORDER BY enabled DESC, nextTriggerAt ASC")
    fun observeAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AlarmEntity?

    @Query("SELECT * FROM alarms WHERE enabled = 1")
    suspend fun getEnabled(): List<AlarmEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity): Long

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("UPDATE alarms SET enabled = :enabled, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateEnabled(id: Long, enabled: Boolean, updatedAt: Long)

    @Query("UPDATE alarms SET nextTriggerAt = :nextTriggerAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateNextTrigger(id: Long, nextTriggerAt: Long, updatedAt: Long)
}
