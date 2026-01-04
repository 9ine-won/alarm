package com.example.alarmgame.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.alarmgame.data.local.converter.EnumConverters
import com.example.alarmgame.data.local.dao.AlarmDao
import com.example.alarmgame.data.local.dao.AlarmHistoryDao
import com.example.alarmgame.data.local.entity.AlarmEntity
import com.example.alarmgame.data.local.entity.AlarmHistoryEntity

@Database(
    entities = [
        AlarmEntity::class,
        AlarmHistoryEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(EnumConverters::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    abstract fun alarmHistoryDao(): AlarmHistoryDao
}
