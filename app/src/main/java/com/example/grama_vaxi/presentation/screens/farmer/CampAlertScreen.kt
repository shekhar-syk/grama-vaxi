package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Vaccines
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
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.domain.model.AlertLevel
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.presentation.components.AlertBanner
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.components.SecondaryButton

@Composable
fun CampAlertScreen(
    alert: HealthAlert?,
    onMarkRead: () -> Unit
) {
    val safeAlert = alert ?: HealthAlert(
        id = "",
        ownerUid = "all",
        title = stringResource(R.string.health_camp_tomorrow),
        message = stringResource(R.string.health_camp_desc),
        level = AlertLevel.URGENT,
        targetVillage = stringResource(R.string.palhalli_village),
        campLocation = stringResource(R.string.temple_square),
        campTime = stringResource(R.string.camp_time_sample)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.edge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        item {
            AlertBanner(
                title = safeAlert.title,
                description = safeAlert.message,
                level = safeAlert.level
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDimens.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.unit)
                ) {
                    RowItem(icon = Icons.Rounded.LocationOn, label = stringResource(R.string.location), value = safeAlert.campLocation ?: stringResource(R.string.temple_square))
                    RowItem(icon = Icons.Rounded.Schedule, label = stringResource(R.string.time), value = safeAlert.campTime ?: stringResource(R.string.camp_time_sample))
                    RowItem(icon = Icons.Rounded.Vaccines, label = stringResource(R.string.services), value = stringResource(R.string.fmd_boosters))
                }
            }
        }

        item {
            PrimaryButton(text = stringResource(R.string.mark_as_seen), onClick = onMarkRead)
        }

        item {
            SecondaryButton(text = stringResource(R.string.view_directions), onClick = {})
        }
    }
}

@Composable
private fun RowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.unit)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
