package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.usecase.MarkAlertReadUseCase
import com.example.grama_vaxi.domain.usecase.ObserveAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CampAlertViewModel @Inject constructor(
    private val observeAlertsUseCase: ObserveAlertsUseCase,
    private val markAlertReadUseCase: MarkAlertReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampAlertUiState())
    val uiState: StateFlow<CampAlertUiState> = _uiState.asStateFlow()

    fun load(ownerUid: String, alertId: String) {
        viewModelScope.launch {
            observeAlertsUseCase(ownerUid).collect { alerts ->
                val alert = alerts.firstOrNull { it.id == alertId }
                if (alert != null) {
                    _uiState.update { it.copy(alert = alert) }
                }
            }
        }
    }

    fun markRead() {
        val alertId = _uiState.value.alert?.id ?: return
        viewModelScope.launch {
            markAlertReadUseCase(alertId)
        }
    }
}

data class CampAlertUiState(
    val alert: HealthAlert? = null
)
