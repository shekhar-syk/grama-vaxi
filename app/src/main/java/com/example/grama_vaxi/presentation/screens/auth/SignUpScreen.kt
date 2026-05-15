package com.example.grama_vaxi.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grama_vaxi.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onRegistrationComplete: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val initialName = currentUser?.displayName ?: ""
    val initialPhone = currentUser?.phoneNumber ?: ""
    val initialEmail = currentUser?.email ?: ""

    var name by remember { mutableStateOf(initialName) }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(initialPhone) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = AppDimens.edge, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        // Header
        Text(
            text = stringResource(R.string.complete_profile),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.welcome_provide_details),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.full_name)) },
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        )

        // Location Field
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.village_location)) },
            leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        )

        // Phone Field
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.take(10) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.phone_number)) },
            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null) },
            supportingText = { Text(stringResource(R.string.verify_phone_later)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.updateProfile(
                    userName = name,
                    location = location,
                    email = initialEmail,
                    phoneNumber = phone,
                    age = "", // Default empty as per requirement
                    roleLabel = "Farmer" // Default role
                )
                onRegistrationComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge),
            enabled = name.isNotBlank() && location.isNotBlank()
        ) {
            Text(stringResource(R.string.complete_registration))
        }
    }
}
