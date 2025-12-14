package com.example.alarmgame.data.repository

import com.example.alarmgame.data.local.entity.AlarmEntity
import com.example.alarmgame.domain.model.Alarm

internal fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    repeatDaysMask = repeatDaysMask,
    enabled = enabled,
    label = label,
    soundType = soundType,
    soundUri = soundUri,
    vibrate = vibrate,
    snoozeEnabled = snoozeEnabled,
    snoozeMinutes = snoozeMinutes,
    snoozeMaxCount = snoozeMaxCount,
    gameType = gameType,
    difficulty = difficulty,
    nextTriggerAt = nextTriggerAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

internal fun Alarm.toEntity(now: Long): AlarmEntity = AlarmEntity(
    id = id,
    hour = hour,
    minute = minute,
    repeatDaysMask = repeatDaysMask,
    enabled = enabled,
    label = label,
    soundType = soundType,
    soundUri = soundUri,
    vibrate = vibrate,
    snoozeEnabled = snoozeEnabled,
    snoozeMinutes = snoozeMinutes,
    snoozeMaxCount = snoozeMaxCount,
    gameType = gameType,
    difficulty = difficulty,
    nextTriggerAt = nextTriggerAt,
    createdAt = if (id == 0L) now else createdAt,
    updatedAt = now
)
