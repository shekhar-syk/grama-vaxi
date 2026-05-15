package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.usecase.ObserveAlertsUseCase
import com.example.grama_vaxi.domain.usecase.ObserveAnimalsUseCase
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
class HomeViewModel @Inject constructor(
    private val observeAnimalsUseCase: ObserveAnimalsUseCase,
    private val observeUpcomingVaccinesUseCase: ObserveUpcomingVaccinesUseCase,
    private val observeAlertsUseCase: ObserveAlertsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var ownerUid: String = ""
    private var observeJob: Job? = null

    fun start(ownerUid: String) {
        if (this.ownerUid == ownerUid) return
        this.ownerUid = ownerUid
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            combine(
                observeAnimalsUseCase(ownerUid),
                observeUpcomingVaccinesUseCase(ownerUid, DateUtils.currentEpochDayUtc()),
                observeAlertsUseCase(ownerUid)
            ) { animals, upcoming, alerts ->
                HomeUiState(
                    totalAnimals = animals.size,
                    upcomingVaccinations = upcoming.size,
                    unreadAlerts = alerts.count { !it.isRead },
                    highlightedAlerts = alerts.take(3),
                    isLoading = false
                )
            }.collect { next ->
                _uiState.value = next
            }
        }
    }
}

data class HomeUiState(
    val totalAnimals: Int = 0,
    val upcomingVaccinations: Int = 0,
    val unreadAlerts: Int = 0,
    val highlightedAlerts: List<HealthAlert> = emptyList(),
    val isLoading: Boolean = true
)
