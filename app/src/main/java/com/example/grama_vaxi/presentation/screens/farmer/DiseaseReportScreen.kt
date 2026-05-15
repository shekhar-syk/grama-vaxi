package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.InputField
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.components.StepperInput
import com.example.grama_vaxi.presentation.viewmodel.DiseaseReportUiState

@Composable
fun DiseaseReportScreen(
    uiState: DiseaseReportUiState,
    onAnimalSelected: (String) -> Unit,
    onSymptomsChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAffectedCountChanged: (Int) -> Unit,
    onClassifySymptoms: () -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.edge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        item {
            Text(stringResource(R.string.report_disease), style = MaterialTheme.typography.headlineLarge)
            Text(
                stringResource(R.string.alert_vet_services),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Text(stringResource(R.string.select_affected_animals), style = MaterialTheme.typography.labelLarge)
        }

        items(uiState.animals) { animal ->
            AnimalChip(
                animal = animal,
                selected = uiState.selectedAnimalId == animal.id,
                onClick = { onAnimalSelected(animal.id) }
            )
        }

        item {
            StepperInput(
                value = uiState.affectedCount,
                onValueChange = onAffectedCountChanged,
                label = stringResource(R.string.number_affected_animals)
            )
        }

        item {
            InputField(
                value = uiState.symptoms,
                onValueChange = onSymptomsChanged,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.symptoms_observed),
                placeholder = stringResource(R.string.describe_symptoms),
                singleLine = false
            )
        }

        item {
            InputField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.additional_notes),
                placeholder = stringResource(R.string.notes_placeholder),
                singleLine = false
            )
        }

        item {
            PrimaryButton(
                text = stringResource(R.string.classify_symptoms),
                onClick = onClassifySymptoms,
                icon = Icons.Rounded.Warning,
                enabled = uiState.symptoms.isNotBlank()
            )
        }

        if (uiState.triageHint.isNotBlank()) {
            item {
                Text(
                    text = uiState.triageHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        item {
            PrimaryButton(
                text = stringResource(R.string.submit_report),
                onClick = onSubmit,
                enabled = uiState.selectedAnimalId.isNotBlank() && uiState.symptoms.isNotBlank()
            )
        }

        if (uiState.errorMessage != null) {
            item {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AnimalChip(
    animal: Animal,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimens.radiusLarge),
        border = BorderStroke(
            2.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLowest
            }
        ),
        onClick = onClick
    ) {
        Text(
            text = "${animal.name} • ${animal.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(AppDimens.gutter)
        )
    }
}
