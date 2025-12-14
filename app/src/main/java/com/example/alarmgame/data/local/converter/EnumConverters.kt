package com.example.alarmgame.data.local.converter

import androidx.room.TypeConverter
import com.example.alarmgame.domain.model.Difficulty
import com.example.alarmgame.domain.model.GameType
import com.example.alarmgame.domain.model.SoundType

class EnumConverters {

    @TypeConverter
    fun fromGameType(type: GameType): String = type.name

    @TypeConverter
    fun toGameType(raw: String): GameType = GameType.valueOf(raw)

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String = difficulty.name

    @TypeConverter
    fun toDifficulty(raw: String): Difficulty = Difficulty.valueOf(raw)

    @TypeConverter
    fun fromSoundType(type: SoundType): String = type.name

    @TypeConverter
    fun toSoundType(raw: String): SoundType = SoundType.valueOf(raw)
}
