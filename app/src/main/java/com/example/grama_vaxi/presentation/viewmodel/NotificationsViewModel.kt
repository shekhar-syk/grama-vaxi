package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.usecase.MarkAlertReadUseCase
import com.example.grama_vaxi.domain.usecase.ObserveAlertsUseCase
import com.example.grama_vaxi.domain.usecase.ObserveUpcomingVaccinesUseCase
import com.example.grama_vaxi.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val observeAlertsUseCase: ObserveAlertsUseCase,
    private val observeUpcomingVaccinesUseCase: ObserveUpcomingVaccinesUseCase,
    private val markAlertReadUseCase: MarkAlertReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun start(ownerUid: String) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                observeAlertsUseCase(ownerUid),
                observeUpcomingVaccinesUseCase(ownerUid, DateUtils.currentEpochDayUtc())
            ) { alerts, upcomingVaccines ->
                NotificationsUiState(
                    alerts = alerts,
                    upcomingVaccines = upcomingVaccines,
                    isLoading = false
                )
            }.collect { next ->
                _uiState.value = next
            }
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            val result = markAlertReadUseCase(alertId)
            result.onFailure { throwable ->
                _uiState.update { it.copy(errorMessage = throwable.message ?: "Unable to update alert") }
            }
        }
    }
}

data class NotificationsUiState(
    val alerts: List<HealthAlert> = emptyList(),
    val upcomingVaccines: List<Animal> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
