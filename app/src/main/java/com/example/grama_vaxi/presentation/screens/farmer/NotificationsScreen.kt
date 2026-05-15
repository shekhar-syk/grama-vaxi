package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AlertBanner
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.viewmodel.NotificationsUiState
import com.example.grama_vaxi.utils.DateUtils

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    onMarkRead: (String) -> Unit,
    onOpenAlert: (String) -> Unit,
    onOpenVaccine: (String) -> Unit,
    onScanCampQr: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onScanCampQr) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = stringResource(R.string.scan_camp_qr))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppDimens.edge),
            verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
        ) {
            item {
                Text(stringResource(R.string.active_alerts), style = MaterialTheme.typography.headlineLarge)
                Text(
                    stringResource(R.string.critical_updates),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.upcomingVaccines.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.scheduled_vaccinations),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = AppDimens.gutter)
                    )
                }

                items(uiState.upcomingVaccines) { animal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenVaccine(animal.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        shape = RoundedCornerShape(AppDimens.radiusLarge)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimens.gutter),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
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

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${animal.name} (${animal.type.name.lowercase().replaceFirstChar { it.uppercase() }})",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    DateUtils.epochDayToDisplay(animal.nextVaccineEpochDay),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.alerts.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.notifications),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = AppDimens.gutter)
                    )
                }

                uiState.alerts.firstOrNull { !it.isRead }?.let { top ->
                    item {
                        AlertBanner(
                            title = top.title,
                            description = top.message,
                            level = top.level
                        )
                    }
                }

                items(uiState.alerts) { alert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAlert(alert.id) },
                        shape = RoundedCornerShape(AppDimens.radiusLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.isRead) {
                                MaterialTheme.colorScheme.surfaceContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimens.cardPadding),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(AppDimens.unit)
                            ) {
                                Text(alert.title, style = MaterialTheme.typography.titleSmall)
                                Text(alert.message, style = MaterialTheme.typography.bodyMedium)
                            }

                            Icon(
                                imageVector = if (alert.isRead) Icons.Rounded.Done else Icons.Rounded.Notifications,
                                contentDescription = stringResource(R.string.mark_read),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(start = AppDimens.unit)
                                    .clickable { onMarkRead(alert.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
