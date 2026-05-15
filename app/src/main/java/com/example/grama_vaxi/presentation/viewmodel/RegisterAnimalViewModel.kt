package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.AnimalType
import com.example.grama_vaxi.domain.usecase.RegisterAnimalUseCase
import com.example.grama_vaxi.utils.DateUtils
import com.example.grama_vaxi.utils.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterAnimalViewModel @Inject constructor(
    private val registerAnimalUseCase: RegisterAnimalUseCase,
    private val idGenerator: IdGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterAnimalUiState())
    val uiState: StateFlow<RegisterAnimalUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onBreedChanged(value: String) {
        _uiState.update { it.copy(breed = value) }
    }

    fun onVillageChanged(value: String) {
        _uiState.update { it.copy(village = value) }
    }

    fun onPhotoChanged(value: String?) {
        _uiState.update { it.copy(photoUri = value) }
    }

    fun onAgeChanged(value: Int) {
        _uiState.update { it.copy(ageMonths = value.coerceIn(1, 300)) }
    }

    fun onTypeChanged(type: AnimalType) {
        _uiState.update { it.copy(type = type) }
    }

    fun register(ownerUid: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val animal = Animal(
                id = idGenerator.newId(),
                ownerUid = ownerUid,
                tagId = "TAG-${Random.nextInt(10000, 99999)}",
                name = state.name.ifBlank { defaultNameForType(state.type) },
                type = state.type,
                breed = state.breed,
                ageMonths = state.ageMonths,
                village = state.village.ifBlank { "Temple Square" },
                photoUri = state.photoUri,
                nextVaccineEpochDay = 0L // Disabled auto-scheduling as per user request
            )

            val result = registerAnimalUseCase(animal)
            result.fold(
                onSuccess = {
                    _uiState.value = RegisterAnimalUiState(saved = true)
                    onSuccess()
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = throwable.message ?: "Failed to register animal"
                        )
                    }
                }
            )
        }
    }

    private fun defaultNameForType(type: AnimalType): String = when (type) {
        AnimalType.COW -> "Gauri"
        AnimalType.GOAT -> "Raju"
        AnimalType.SHEEP -> "Chandi"
    }
}

data class RegisterAnimalUiState(
    val name: String = "",
    val breed: String = "",
    val village: String = "",
    val ageMonths: Int = 12,
    val type: AnimalType = AnimalType.COW,
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null
)
