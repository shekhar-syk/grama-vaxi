package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.CrueltyFree
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import com.example.grama_vaxi.domain.model.AlertLevel
import com.example.grama_vaxi.presentation.components.AlertBanner
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.CardStat
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.components.SecondaryButton
import com.example.grama_vaxi.presentation.viewmodel.HomeUiState

@Composable
fun HomeDashboardScreen(
    uiState: HomeUiState,
    onRegisterAnimal: () -> Unit,
    onViewCalendar: () -> Unit,
    onReportDisease: () -> Unit,
    onOpenLedger: () -> Unit,
    onOpenAlerts: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppDimens.edge),
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.unit)) {
                Text(
                    text = stringResource(R.string.namaskara),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            CardStat(
                title = stringResource(R.string.total_animals_registered),
                value = uiState.totalAnimals.toString(),
                subtitle = stringResource(R.string.animals),
                icon = Icons.Rounded.CrueltyFree,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                iconBadgeColor = MaterialTheme.colorScheme.primary,
                valueColor = MaterialTheme.colorScheme.primary,
                onClick = onOpenLedger
            )
        }

        item {
            CardStat(
                title = stringResource(R.string.upcoming_vaccinations),
                value = uiState.upcomingVaccinations.toString(),
                subtitle = stringResource(R.string.scheduled),
                icon = Icons.Rounded.CalendarMonth,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconBadgeColor = MaterialTheme.colorScheme.onSecondaryContainer,
                valueColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onViewCalendar
            )
        }

        item {
            CardStat(
                title = stringResource(R.string.alerts),
                value = uiState.unreadAlerts.toString(),
                subtitle = stringResource(R.string.require_attention),
                icon = Icons.Rounded.NotificationsActive,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                iconBadgeColor = MaterialTheme.colorScheme.error,
                valueColor = MaterialTheme.colorScheme.error,
                onClick = onOpenAlerts
            )
        }

        item {
            Text(
                text = stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = AppDimens.unit)
            )
        }

        item {
            PrimaryButton(
                text = stringResource(R.string.register_animal),
                onClick = onRegisterAnimal,
                icon = Icons.Rounded.AddCircle
            )
        }

        item {
            SecondaryButton(
                text = stringResource(R.string.view_calendar),
                onClick = onViewCalendar,
                icon = Icons.Rounded.CalendarMonth
            )
        }

        item {
            SecondaryButton(
                text = stringResource(R.string.report_disease),
                onClick = onReportDisease,
                icon = Icons.Rounded.Warning
            )
        }

        if (uiState.highlightedAlerts.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.active_alerts),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = AppDimens.unit)
                )
            }

            items(uiState.highlightedAlerts) { alert ->
                AlertBanner(
                    title = alert.title,
                    description = alert.message,
                    level = alert.level.takeIf { it != AlertLevel.INFO } ?: AlertLevel.INFO,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
