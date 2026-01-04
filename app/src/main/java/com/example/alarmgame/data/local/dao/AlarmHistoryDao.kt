package com.example.alarmgame.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alarmgame.data.local.entity.AlarmHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: AlarmHistoryEntity): Long

    @Query("SELECT * FROM alarm_history WHERE alarmId = :alarmId ORDER BY firedAt DESC LIMIT :limit")
    fun observeByAlarm(
        alarmId: Long,
        limit: Int = 20,
    ): Flow<List<AlarmHistoryEntity>>
}
