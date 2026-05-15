package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.viewmodel.AnimalDetailUiState
import com.example.grama_vaxi.utils.DateUtils

@Composable
fun AnimalDetailScreen(
    uiState: AnimalDetailUiState,
    onBack: () -> Unit
) {
    if (uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimens.edge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val animal = uiState.animal
    if (animal == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimens.edge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Animal not found", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.edge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        item {
            Text(
                animal.name,
                style = MaterialTheme.typography.headlineLarge
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.radiusLarge)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
                ) {
                    DetailRow(
                        icon = Icons.Rounded.LocalFlorist,
                        label = "Type",
                        value = animal.type.name.lowercase().replaceFirstChar { it.uppercase() }
                    )

                    DetailRow(
                        icon = Icons.Rounded.Tag,
                        label = "Breed",
                        value = animal.breed
                    )

                    DetailRow(
                        icon = Icons.Rounded.Cake,
                        label = "Age",
                        value = "${animal.ageMonths} months"
                    )
                }
            }
        }

        item {
            Text(
                "Vaccination Schedule",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = AppDimens.gutter)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.radiusLarge)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.cardPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Vaccines,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Column {
                        Text(
                            "Next Vaccination",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            if (animal.nextVaccineEpochDay > 0) {
                                DateUtils.epochDayToDisplay(animal.nextVaccineEpochDay)
                            } else {
                                stringResource(R.string.not_scheduled)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Vaccination History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = AppDimens.gutter)
            )
        }

        if (uiState.vaccinationHistory.isEmpty()) {
            item {
                Text(
                    "No vaccination records found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.vaccinationHistory) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(AppDimens.radiusLarge)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimens.cardPadding),
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(record.vaccineName, style = MaterialTheme.typography.titleSmall)
                            Text(DateUtils.epochDayToDisplay(record.epochDay), style = MaterialTheme.typography.bodyMedium)
                        }

                        Text(record.administeredBy ?: "—", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.unit)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
