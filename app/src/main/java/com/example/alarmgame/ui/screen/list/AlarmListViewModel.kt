package com.example.alarmgame.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmgame.domain.model.Alarm
import com.example.alarmgame.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlarmListUiState(
    val alarms: List<Alarm> = emptyList()
)

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmListUiState())
    val uiState: StateFlow<AlarmListUiState> = _uiState.asStateFlow()

    init {
        observeAlarms()
    }

    private fun observeAlarms() {
        viewModelScope.launch {
            alarmRepository.observeAlarms().collect { alarms ->
                _uiState.update { it.copy(alarms = alarms) }
            }
        }
    }

    fun toggleEnabled(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch {
            alarmRepository.toggleEnabled(
                id = alarmId,
                enabled = enabled,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun delete(alarmId: Long) {
        viewModelScope.launch {
            alarmRepository.delete(alarmId)
        }
    }
}
