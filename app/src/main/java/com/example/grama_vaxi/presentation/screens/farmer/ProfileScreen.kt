package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.SettingsActionRow
import com.example.grama_vaxi.presentation.viewmodel.AuthUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    onEditProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenAppPermissions: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenLanguage: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val contactLine = when {
        uiState.session.email.isNotBlank() -> uiState.session.email
        uiState.session.phoneNumber.isNotBlank() -> uiState.session.phoneNumber
        else -> stringResource(R.string.add_contact_information)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppDimens.gutter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = uiState.session.userName.ifBlank { stringResource(R.string.your_profile) },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = contactLine,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = AppDimens.unit))

            ProfileSectionTitle(title = stringResource(R.string.quick_actions))
            SettingsActionRow(
                icon = Icons.Rounded.Edit,
                title = stringResource(R.string.edit_profile),
                subtitle = stringResource(R.string.update_name_phone_location),
                onClick = onEditProfile
            )
            SettingsActionRow(
                icon = Icons.Rounded.Notifications,
                title = stringResource(R.string.alerts_and_reminders),
                subtitle = stringResource(R.string.open_in_app_notifications),
                onClick = onOpenNotifications
            )
            SettingsActionRow(
                icon = Icons.Rounded.NotificationsActive,
                title = stringResource(R.string.notification_settings),
                subtitle = stringResource(R.string.manage_system_permissions),
                onClick = onOpenNotificationSettings
            )
            SettingsActionRow(
                icon = Icons.Rounded.Security,
                title = stringResource(R.string.app_permissions),
                subtitle = stringResource(R.string.open_app_permissions),
                onClick = onOpenAppPermissions
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = AppDimens.unit))

            ProfileSectionTitle(title = stringResource(R.string.preferences))
            SettingsActionRow(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.language),
                subtitle = stringResource(R.string.change_app_language),
                onClick = onOpenLanguage
            )
            SettingsActionRow(
                icon = Icons.Rounded.Palette,
                title = stringResource(R.string.dark_mode),
                subtitle = stringResource(R.string.choose_light_dark_system),
                onClick = onOpenTheme
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = AppDimens.unit))

            ProfileSectionTitle(title = stringResource(R.string.legal))
            SettingsActionRow(
                icon = Icons.Rounded.Gavel,
                title = stringResource(R.string.terms_and_conditions),
                subtitle = stringResource(R.string.read_terms),
                onClick = onOpenTerms
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(AppDimens.unit))
                Text(stringResource(R.string.logout))
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary
    )
}
