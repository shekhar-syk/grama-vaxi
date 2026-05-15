package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.PrimaryButton
import com.example.grama_vaxi.presentation.components.SecondaryButton
import org.json.JSONObject
import androidx.compose.ui.unit.dp

data class CampQrPayload(
    val campId: String?,
    val village: String?,
    val date: String?,
    val time: String?,
    val location: String?,
    val services: String?,
    val message: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampQrResultScreen(
    payload: String,
    onBack: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAlerts: () -> Unit
) {
    val decodedPayload = remember(payload) { parsePayload(payload) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.camp_qr_result)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
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
                if (decodedPayload != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimens.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.unit)
                        ) {
                            Text(
                                text = stringResource(R.string.camp_qr_schedule),
                                style = MaterialTheme.typography.titleLarge
                            )
                            ResultRow(label = stringResource(R.string.village), value = decodedPayload.village ?: stringResource(R.string.not_scheduled))
                            ResultRow(label = stringResource(R.string.location), value = decodedPayload.location ?: stringResource(R.string.not_scheduled))
                            ResultRow(label = stringResource(R.string.time), value = decodedPayload.time ?: stringResource(R.string.not_scheduled))
                            ResultRow(label = stringResource(R.string.services), value = decodedPayload.services ?: stringResource(R.string.not_scheduled))
                            ResultRow(label = stringResource(R.string.date), value = decodedPayload.date ?: stringResource(R.string.not_scheduled))
                            if (!decodedPayload.message.isNullOrBlank()) {
                                Text(decodedPayload.message, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimens.cardPadding),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.unit)
                        ) {
                            Text(
                                text = stringResource(R.string.camp_qr_payload_unknown),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(payload, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                PrimaryButton(
                    text = stringResource(R.string.camp_qr_open_calendar),
                    onClick = onOpenCalendar,
                    icon = Icons.Rounded.CalendarMonth
                )
            }

            item {
                SecondaryButton(
                    text = stringResource(R.string.camp_qr_open_alerts),
                    onClick = onOpenAlerts,
                    icon = Icons.Rounded.NotificationsActive
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun parsePayload(rawPayload: String): CampQrPayload? {
    return runCatching {
        val json = JSONObject(rawPayload)
        if (json.optString("type") != "camp-schedule") {
            return null
        }

        CampQrPayload(
            campId = json.optString("campId").takeIf { it.isNotBlank() },
            village = json.optString("village").takeIf { it.isNotBlank() },
            date = json.optString("date").takeIf { it.isNotBlank() },
            time = json.optString("time").takeIf { it.isNotBlank() },
            location = json.optString("location").takeIf { it.isNotBlank() },
            services = json.optString("services").takeIf { it.isNotBlank() },
            message = json.optString("message").takeIf { it.isNotBlank() }
        )
    }.getOrNull()
}