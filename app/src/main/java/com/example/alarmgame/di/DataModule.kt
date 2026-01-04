package com.example.alarmgame.di

import android.content.Context
import androidx.room.Room
import com.example.alarmgame.data.local.dao.AlarmDao
import com.example.alarmgame.data.local.dao.AlarmHistoryDao
import com.example.alarmgame.data.local.db.AlarmDatabase
import com.example.alarmgame.data.repository.AlarmHistoryRepositoryImpl
import com.example.alarmgame.data.repository.AlarmRepositoryImpl
import com.example.alarmgame.domain.repository.AlarmHistoryRepository
import com.example.alarmgame.domain.repository.AlarmRepository
import com.example.alarmgame.domain.scheduler.AlarmScheduler
import com.example.alarmgame.platform.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindAlarmHistoryRepository(impl: AlarmHistoryRepositoryImpl): AlarmHistoryRepository

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): AlarmDatabase =
            Room.databaseBuilder(context, AlarmDatabase::class.java, "alarm-db")
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        fun provideAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()

        @Provides
        fun provideAlarmHistoryDao(db: AlarmDatabase): AlarmHistoryDao = db.alarmHistoryDao()
    }
}
