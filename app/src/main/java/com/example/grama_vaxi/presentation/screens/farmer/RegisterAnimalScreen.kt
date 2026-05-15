package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AnimalTypeSelector
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.InputField
import com.example.grama_vaxi.presentation.components.PhotoPickerBox
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.components.StepperInput
import com.example.grama_vaxi.presentation.viewmodel.RegisterAnimalUiState

@Composable
fun RegisterAnimalScreen(
    uiState: RegisterAnimalUiState,
    onNameChanged: (String) -> Unit,
    onBreedChanged: (String) -> Unit,
    onVillageChanged: (String) -> Unit,
    onAgeChanged: (Int) -> Unit,
    onTypeChanged: (com.example.grama_vaxi.domain.model.AnimalType) -> Unit,
    onPickPhoto: () -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.edge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        item {
            Text(stringResource(R.string.register_animal), style = MaterialTheme.typography.headlineLarge)
        }

        item {
            Text(stringResource(R.string.animal_photo), style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(AppDimens.unit))
            PhotoPickerBox(
                onClick = onPickPhoto,
                title = stringResource(R.string.tap_to_take_photo),
                subtitle = stringResource(R.string.or_select_from_gallery)
            )
        }

        item {
            AnimalTypeSelector(
                selectedType = uiState.type,
                onTypeSelected = onTypeChanged
            )
        }

        item {
            InputField(
                value = uiState.name,
                onValueChange = onNameChanged,
                label = stringResource(R.string.animal_name),
                placeholder = stringResource(R.string.animal_name_hint)
            )
        }

        item {
            InputField(
                value = uiState.breed,
                onValueChange = onBreedChanged,
                label = stringResource(R.string.breed),
                placeholder = stringResource(R.string.breed_hint)
            )
        }

        item {
            InputField(
                value = uiState.village,
                onValueChange = onVillageChanged,
                label = stringResource(R.string.village),
                placeholder = stringResource(R.string.village_hint)
            )
        }

        item {
            StepperInput(
                value = uiState.ageMonths,
                onValueChange = onAgeChanged,
                label = stringResource(R.string.age_months)
            )
        }

        item {
            PrimaryButton(
                text = stringResource(R.string.register_animal),
                onClick = onSubmit,
                icon = Icons.Rounded.AddCircle,
                height = AppDimens.primaryButtonLarge,
                enabled = uiState.breed.isNotBlank() && uiState.village.isNotBlank() && !uiState.isSaving
            )
        }

        if (uiState.errorMessage != null) {
            item {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
