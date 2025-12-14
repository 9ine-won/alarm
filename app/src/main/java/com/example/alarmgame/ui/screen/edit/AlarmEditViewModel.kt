package com.example.alarmgame.ui.screen.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmgame.domain.model.Alarm
import com.example.alarmgame.domain.model.Difficulty
import com.example.alarmgame.domain.model.GameType
import com.example.alarmgame.domain.model.SoundType
import com.example.alarmgame.domain.repository.AlarmRepository
import com.example.alarmgame.domain.util.RepeatDays
import com.example.alarmgame.ui.navigation.ARG_ALARM_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlarmFormState(
    val id: Long? = null,
    val hour: Int = 7,
    val minute: Int = 0,
    val repeatDaysMask: Int = 0,
    val enabled: Boolean = true,
    val label: String = "",
    val soundType: SoundType = SoundType.RINGTONE,
    val soundUri: String? = null,
    val vibrate: Boolean = true,
    val snoozeEnabled: Boolean = true,
    val snoozeMinutes: Int = 5,
    val snoozeMaxCount: Int = 3,
    val gameType: GameType = GameType.MOLE,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val createdAt: Long = System.currentTimeMillis()
)

data class AlarmEditUiState(
    val isNew: Boolean = true,
    val loading: Boolean = false,
    val saving: Boolean = false,
    val form: AlarmFormState = AlarmFormState(),
    val error: String? = null
)

sealed interface AlarmEditEvent {
    data class Saved(val id: Long) : AlarmEditEvent
    data class Error(val message: String) : AlarmEditEvent
}

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val alarmId: Long? = savedStateHandle.get<Long>(ARG_ALARM_ID)?.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(
        AlarmEditUiState(
            isNew = alarmId == null,
            loading = alarmId != null
        )
    )
    val uiState: StateFlow<AlarmEditUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AlarmEditEvent>()
    val events: SharedFlow<AlarmEditEvent> = _events.asSharedFlow()

    init {
        alarmId?.let { loadAlarm(it) }
    }

    private fun loadAlarm(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val alarm = alarmRepository.get(id)
            _uiState.update { state ->
                if (alarm == null) {
                    state.copy(loading = false, error = "알람을 찾을 수 없어요.")
                } else {
                    state.copy(
                        loading = false,
                        form = alarm.toForm(),
                        isNew = false,
                        error = null
                    )
                }
            }
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(form = it.form.copy(hour = hour.coerceIn(0, 23), minute = minute.coerceIn(0, 59))) }
    }

    fun toggleRepeat(day: DayOfWeek) {
        _uiState.update { state ->
            val days = RepeatDays.daysFrom(state.form.repeatDaysMask).toMutableSet()
            if (days.contains(day)) days.remove(day) else days.add(day)
            state.copy(form = state.form.copy(repeatDaysMask = RepeatDays.maskOf(days)))
        }
    }

    fun updateLabel(label: String) {
        _uiState.update { it.copy(form = it.form.copy(label = label.take(40))) }
    }

    fun updateEnabled(enabled: Boolean) {
        _uiState.update { it.copy(form = it.form.copy(enabled = enabled)) }
    }

    fun updateVibrate(vibrate: Boolean) {
        _uiState.update { it.copy(form = it.form.copy(vibrate = vibrate)) }
    }

    fun updateSoundSelection(label: String) {
        val type = if (label == "커스텀...") SoundType.CUSTOM else SoundType.RINGTONE
        _uiState.update { it.copy(form = it.form.copy(soundType = type, soundUri = label)) }
    }

    fun updateSnoozeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(form = it.form.copy(snoozeEnabled = enabled)) }
    }

    fun updateSnoozeMinutes(minutes: Int) {
        _uiState.update { it.copy(form = it.form.copy(snoozeMinutes = minutes.coerceIn(1, 60))) }
    }

    fun updateSnoozeMaxCount(count: Int) {
        _uiState.update { it.copy(form = it.form.copy(snoozeMaxCount = count.coerceIn(1, 10))) }
    }

    fun updateGameType(type: GameType) {
        _uiState.update { it.copy(form = it.form.copy(gameType = type)) }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(form = it.form.copy(difficulty = difficulty)) }
    }

    fun save() {
        viewModelScope.launch {
            val form = _uiState.value.form
            _uiState.update { it.copy(saving = true, error = null) }
            try {
                val alarm = form.toDomain(computeNextTrigger(form))
                val id = alarmRepository.upsert(alarm)
                _events.emit(AlarmEditEvent.Saved(id))
            } catch (t: Throwable) {
                _uiState.update { it.copy(error = t.message, saving = false) }
                _events.emit(AlarmEditEvent.Error(t.message ?: "알람을 저장하지 못했습니다."))
            } finally {
                _uiState.update { it.copy(saving = false) }
            }
        }
    }

    private fun Alarm.toForm(): AlarmFormState = AlarmFormState(
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
        createdAt = createdAt
    )

    private fun AlarmFormState.toDomain(nextTriggerAt: Long): Alarm = Alarm(
        id = id ?: 0L,
        hour = hour,
        minute = minute,
        repeatDaysMask = repeatDaysMask,
        enabled = enabled,
        label = label.trim(),
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
        updatedAt = System.currentTimeMillis()
    )

    private fun computeNextTrigger(form: AlarmFormState): Long {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val targetToday = now.withHour(form.hour).withMinute(form.minute).withSecond(0).withNano(0)
        if (form.repeatDaysMask == 0) {
            val candidate = if (targetToday.isAfter(now)) targetToday else targetToday.plusDays(1)
            return candidate.toInstant().toEpochMilli()
        }

        val repeatDays = RepeatDays.daysFrom(form.repeatDaysMask)
        var best: ZonedDateTime? = null
        for (offset in 0L..6L) {
            val day = now.plusDays(offset)
            if (repeatDays.contains(day.dayOfWeek)) {
                val candidate = day.withHour(form.hour).withMinute(form.minute)
                    .withSecond(0).withNano(0)
                val isAfterNow = candidate.isAfter(now)
                if (offset > 0 || isAfterNow) {
                    if (best == null || candidate.isBefore(best)) {
                        best = candidate
                    }
                }
            }
        }
        return (best ?: targetToday.plusDays(1)).toInstant().toEpochMilli()
    }
}
