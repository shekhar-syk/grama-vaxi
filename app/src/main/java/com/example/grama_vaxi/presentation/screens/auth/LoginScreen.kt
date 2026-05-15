package com.example.grama_vaxi.presentation.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onPhoneChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onSendOtp: () -> Unit,
    onLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onToggleMethod: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val otpFocus = remember { FocusRequester() }
    val otpSent = uiState.verificationToken.isNotBlank()

    LaunchedEffect(otpSent) {
        if (otpSent) {
            kotlinx.coroutines.delay(300)
            otpFocus.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = AppDimens.edge, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.app_name_en),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.app_name_kn),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Google Sign-In ───────────────────────────────────────────────────
        OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            enabled = !uiState.googleSignInLoading && !uiState.isLoading
        ) {
            if (uiState.googleSignInLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.continue_with_google),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // ── Divider ──────────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.or_use_phone),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // ── Method Selector ──────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = if (uiState.isEmailLogin) 1 else 0,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            Tab(
                selected = !uiState.isEmailLogin,
                onClick = { onToggleMethod(false) },
                text = { Text("Phone") }
            )
            Tab(
                selected = uiState.isEmailLogin,
                onClick = { onToggleMethod(true) },
                text = { Text("Email") }
            )
        }

        // ── Input field ──────────────────────────────────────────────────────
        if (!uiState.isEmailLogin) {
            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = { if (it.length <= 10) onPhoneChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.mobile_number)) },
                placeholder = { Text(stringResource(R.string.ten_digit_number)) },
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Icon(Icons.Rounded.Call, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("+91", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (uiState.phoneNumber.length == 10) onSendOtp()
                }),
                shape = RoundedCornerShape(AppDimens.radiusLarge),
                enabled = !uiState.isLoading
            )
        } else {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email Address") },
                placeholder = { Text("example@gmail.com") },
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (uiState.email.contains("@")) onSendOtp()
                }),
                shape = RoundedCornerShape(AppDimens.radiusLarge),
                enabled = !uiState.isLoading
            )
        }

        Button(
            onClick = onSendOtp,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge),
            enabled = (if (uiState.isEmailLogin) uiState.email.contains("@") else uiState.phoneNumber.length == 10)
                    && uiState.otpCooldownSeconds == 0
                    && !uiState.isLoading
        ) {
            if (uiState.isLoading && !otpSent) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = if (uiState.otpCooldownSeconds > 0)
                        stringResource(R.string.resend_in_s, uiState.otpCooldownSeconds)
                    else if (otpSent) stringResource(R.string.resend_otp)
                    else "Send OTP to ${if (uiState.isEmailLogin) "Email" else "Phone"}"
                )
            }
        }

        // ── OTP section (visible after code is sent) ─────────────────────────
        AnimatedVisibility (visible = otpSent) {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)) {
                HorizontalDivider()

                Text(
                    text = "OTP sent to ${if (uiState.isEmailLogin) uiState.email else "+91 " + uiState.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.otp,
                    onValueChange = { if (it.length <= 6) onOtpChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(otpFocus),
                    label = { Text(stringResource(R.string.enter_otp)) },
                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        if (uiState.otp.length == 6) onLogin()
                    }),
                    shape = RoundedCornerShape(AppDimens.radiusLarge),
                    enabled = !uiState.isLoading
                )

                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppDimens.minTouch),
                    shape = RoundedCornerShape(AppDimens.radiusLarge),
                    enabled = uiState.otp.length == 6 && !uiState.isLoading
                ) {
                    if (uiState.isLoading && otpSent) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.login_btn))
                    }
                }
            }
        }

        // ── Error ─────────────────────────────────────────────────────────────
        if (uiState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(AppDimens.radiusMedium)
            ) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(AppDimens.gutter),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
