package com.example.grama_vaxi.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.components.SettingsSelectionRow

/**
 * Onboarding language picker shown once before the Sign-Up (Complete Profile) screen.
 * The "Next" button label is shown in whatever language the user has currently selected.
 */
@Composable
fun OnboardingLanguageScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onNext: () -> Unit
) {
    // Track the local selection so the button reacts immediately before the
    // AppCompatDelegate locale change propagates.
    var selected by rememberSaveable { mutableStateOf(currentLanguage) }

    // "Next" label shown in the chosen language
    val nextLabel = when (selected) {
        AppLanguage.KANNADA -> "ಮುಂದೆ"   // Kannada: "Next"
        else                -> "Next"
    }

    // "Select Language" title in chosen language
    val titleLabel = when (selected) {
        AppLanguage.KANNADA -> "ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ"
        else                -> "Select Language"
    }

    // subtitle in chosen language
    val subtitleLabel = when (selected) {
        AppLanguage.KANNADA -> "ಅಪ್ಲಿಕೇಶನ್‌ಗೆ ನಿಮ್ಮ ಆದ್ಯತೆಯ ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ; ನೀವು ನಂತರ ಮೆನುವಿನಲ್ಲಿ ಅದನ್ನು ಬದಲಾಯಿಸಬಹುದು."
        else                -> "Choose your preferred language for this app; you can adjust it later in the More Menu."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimens.edge, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = titleLabel,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = subtitleLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        SettingsSelectionRow(
            icon = Icons.Rounded.Language,
            label = "English",
            selected = selected == AppLanguage.ENGLISH,
            onClick = {
                selected = AppLanguage.ENGLISH
                onLanguageSelected(AppLanguage.ENGLISH)
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
            }
        )

        SettingsSelectionRow(
            icon = Icons.Rounded.Translate,
            label = "ಕನ್ನಡ",
            selected = selected == AppLanguage.KANNADA,
            onClick = {
                selected = AppLanguage.KANNADA
                onLanguageSelected(AppLanguage.KANNADA)
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("kn"))
            }
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        ) {
            Text(
                text = nextLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
