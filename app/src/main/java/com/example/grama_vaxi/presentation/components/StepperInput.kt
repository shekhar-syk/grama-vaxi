package com.example.grama_vaxi.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

@Composable
fun StepperInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    min: Int = 1,
    max: Int = 240,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter)
    ) {
        OutlinedButton(
            onClick = { onValueChange((value - 1).coerceAtLeast(min)) },
            modifier = Modifier
                .heightIn(min = AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Decrease $label")
        }

        OutlinedTextField(
            value = value.toString(),
            onValueChange = { next ->
                next.toIntOrNull()?.let { onValueChange(it.coerceIn(min, max)) }
            },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = AppDimens.minTouch),
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            singleLine = true,
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(AppDimens.radiusLarge),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        )

        OutlinedButton(
            onClick = { onValueChange((value + 1).coerceAtMost(max)) },
            modifier = Modifier
                .heightIn(min = AppDimens.minTouch),
            shape = RoundedCornerShape(AppDimens.radiusLarge)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Increase $label")
        }
    }
}
