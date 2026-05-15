package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.domain.model.DiseaseReport
import com.example.grama_vaxi.domain.usecase.ClassifySymptomsUseCase
import com.example.grama_vaxi.domain.usecase.ObserveAnimalsUseCase
import com.example.grama_vaxi.domain.usecase.SubmitDiseaseReportUseCase
import com.example.grama_vaxi.utils.IdGenerator
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
class DiseaseReportViewModel @Inject constructor(
    private val observeAnimalsUseCase: ObserveAnimalsUseCase,
    private val submitDiseaseReportUseCase: SubmitDiseaseReportUseCase,
    private val classifySymptomsUseCase: ClassifySymptomsUseCase,
    private val idGenerator: IdGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiseaseReportUiState())
    val uiState: StateFlow<DiseaseReportUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun start(ownerUid: String) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeAnimalsUseCase(ownerUid).collectLatest { animals ->
                _uiState.update {
                    it.copy(
                        animals = animals,
                        selectedAnimalId = it.selectedAnimalId.ifBlank { animals.firstOrNull()?.id.orEmpty() }
                    )
                }
            }
        }
    }

    fun onAnimalSelected(id: String) {
        _uiState.update { it.copy(selectedAnimalId = id) }
    }

    fun onSymptomsChanged(value: String) {
        _uiState.update { it.copy(symptoms = value) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onAffectedCountChanged(value: Int) {
        _uiState.update { it.copy(affectedCount = value.coerceIn(1, 200)) }
    }

    fun classifySymptoms() {
        viewModelScope.launch {
            val text = _uiState.value.symptoms
            if (text.isBlank()) return@launch
            val classification = classifySymptomsUseCase(text)
            _uiState.update { it.copy(triageHint = classification) }
        }
    }

    fun submit(ownerUid: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.selectedAnimalId.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Select an animal first") }
                return@launch
            }

            val report = DiseaseReport(
                id = idGenerator.newId(),
                ownerUid = ownerUid,
                animalId = state.selectedAnimalId,
                symptoms = state.symptoms,
                affectedCount = state.affectedCount,
                notes = state.notes
            )

            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val result = submitDiseaseReportUseCase(report)
            result.fold(
                onSuccess = {
                    _uiState.value = DiseaseReportUiState(submitted = true)
                    onSuccess()
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = throwable.message ?: "Failed to submit report"
                        )
                    }
                }
            )
        }
    }
}

data class DiseaseReportUiState(
    val animals: List<Animal> = emptyList(),
    val selectedAnimalId: String = "",
    val symptoms: String = "",
    val notes: String = "",
    val affectedCount: Int = 1,
    val triageHint: String = "",
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val errorMessage: String? = null
)
