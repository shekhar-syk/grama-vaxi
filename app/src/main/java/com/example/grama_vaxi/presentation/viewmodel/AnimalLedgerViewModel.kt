package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.usecase.DeleteAnimalUseCase
import com.example.grama_vaxi.domain.usecase.ObserveAnimalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalLedgerViewModel @Inject constructor(
    private val observeAnimalsUseCase: ObserveAnimalsUseCase,
    private val deleteAnimalUseCase: DeleteAnimalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalLedgerUiState())
    val uiState: StateFlow<AnimalLedgerUiState> = _uiState.asStateFlow()

    private var ownerUid: String = ""
    private var observeJob: Job? = null

    fun start(ownerUid: String) {
        if (this.ownerUid == ownerUid) return
        this.ownerUid = ownerUid
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            observeAnimalsUseCase(ownerUid).collectLatest { animals ->
                _uiState.update { it.copy(animals = animals, isLoading = false) }
            }
        }
    }

    fun deleteAnimal(animalId: String) {
        viewModelScope.launch {
            val result = deleteAnimalUseCase(ownerUid, animalId)
            result.onFailure { throwable ->
                _uiState.update { it.copy(errorMessage = throwable.message ?: "Failed to delete animal") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class AnimalLedgerUiState(
    val animals: List<Animal> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
