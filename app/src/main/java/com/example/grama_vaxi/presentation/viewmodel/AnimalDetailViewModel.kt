package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.VaccinationRecord
import com.example.grama_vaxi.domain.usecase.ObserveAnimalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalDetailViewModel @Inject constructor(
    private val observeAnimalsUseCase: ObserveAnimalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalDetailUiState())
    val uiState: StateFlow<AnimalDetailUiState> = _uiState.asStateFlow()

    fun loadAnimal(ownerUid: String, animalId: String) {
        viewModelScope.launch {
            observeAnimalsUseCase(ownerUid).collect { animals ->
                val animal = animals.firstOrNull { it.id == animalId }
                // TODO: wire observeVaccinationHistoryUseCase when available
                _uiState.update { it.copy(animal = animal, vaccinationHistory = emptyList(), isLoading = false) }
            }
        }
    }
}

data class AnimalDetailUiState(
    val animal: Animal? = null,
    val vaccinationHistory: List<VaccinationRecord> = emptyList(),
    val isLoading: Boolean = true
)
