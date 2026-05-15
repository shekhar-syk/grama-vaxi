package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.domain.model.Animal
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.viewmodel.AnimalLedgerUiState

@Composable
fun AnimalLedgerScreen(
    uiState: AnimalLedgerUiState,
    onAddAnimal: () -> Unit,
    onDeleteAnimal: (String) -> Unit,
    onOpenAnimal: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.registered_animals),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = AppDimens.edge, vertical = AppDimens.gutter)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = AppDimens.edge, vertical = AppDimens.unit),
            verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
        ) {
            items(uiState.animals) { animal ->
                AnimalLedgerItem(
                    animal = animal,
                    onDelete = { onDeleteAnimal(animal.id) },
                    onOpen = { onOpenAnimal(animal.id) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.edge),
            horizontalArrangement = Arrangement.End
        ) {
            PrimaryButton(
                text = stringResource(R.string.add_animal),
                onClick = onAddAnimal,
                icon = Icons.Rounded.Add,
                height = AppDimens.primaryButtonLarge
            )
        }
    }
}

@Composable
private fun AnimalLedgerItem(
    animal: Animal,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(AppDimens.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpen() }
                .padding(AppDimens.gutter),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Pets,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(AppDimens.radiusMedium)
                    )
                    .padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${animal.name} - ${animal.tagId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${animal.type.name.lowercase().replaceFirstChar { it.uppercase() }} • ${animal.breed}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.unit)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("${animal.ageMonths / 12} ${stringResource(R.string.years)}", style = MaterialTheme.typography.labelMedium)
                }
            }

            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete Animal",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onDelete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
