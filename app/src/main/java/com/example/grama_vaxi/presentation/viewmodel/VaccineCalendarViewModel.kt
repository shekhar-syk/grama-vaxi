package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.repository.AlertRepository
import com.example.grama_vaxi.domain.usecase.ObserveUpcomingVaccinesUseCase
import com.example.grama_vaxi.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class VaccineCalendarUiState(
    val upcomingAnimals: List<Animal> = emptyList(),
    val alerts: List<HealthAlert> = emptyList(),
    val isLoading: Boolean = true,
    val selectedDate: LocalDate? = null,
    val selectedDateVaccinations: List<Animal> = emptyList(),
    val selectedDateCamps: List<HealthAlert> = emptyList()
)

@HiltViewModel
class VaccineCalendarViewModel @Inject constructor(
    private val observeUpcomingVaccinesUseCase: ObserveUpcomingVaccinesUseCase,
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaccineCalendarUiState())
    val uiState: StateFlow<VaccineCalendarUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun start(ownerUid: String) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                observeUpcomingVaccinesUseCase(ownerUid, DateUtils.currentEpochDayUtc()),
                alertRepository.observeAlerts(ownerUid)
            ) { animals, alerts ->
                Pair(animals, alerts)
            }.collect { (animals, alerts) ->
                _uiState.updateWithData(animals, alerts)
            }
        }
    }

    private fun MutableStateFlow<VaccineCalendarUiState>.updateWithData(
        animals: List<Animal>,
        alerts: List<HealthAlert>
    ) {
        val selected = value.selectedDate
        val (vaccines, camps) = if (selected != null) {
            val epochDay = selected.toEpochDay()
            Pair(
                animals.filter { it.nextVaccineEpochDay == epochDay },
                alerts.filter { it.campDateEpochDay == epochDay }
            )
        } else {
            Pair(emptyList(), emptyList())
        }

        value = value.copy(
            upcomingAnimals = animals,
            alerts = alerts,
            isLoading = false,
            selectedDateVaccinations = vaccines,
            selectedDateCamps = camps
        )
    }

    fun selectDate(date: LocalDate) {
        val animals = _uiState.value.upcomingAnimals
        val alerts = _uiState.value.alerts
        val epochDay = date.toEpochDay()

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedDateVaccinations = animals.filter { it.nextVaccineEpochDay == epochDay },
            selectedDateCamps = alerts.filter { it.campDateEpochDay == epochDay }
        )
    }
}
