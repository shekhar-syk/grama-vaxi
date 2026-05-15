package com.example.grama_vaxi.presentation.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grama_vaxi.R
import com.example.grama_vaxi.presentation.components.AppDimens
import com.example.grama_vaxi.presentation.viewmodel.VaccineCalendarUiState
import com.example.grama_vaxi.utils.DateUtils
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VaccineCalendarScreen(
    uiState: VaccineCalendarUiState,
    onScanCampQr: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val displayedMonth = remember { mutableStateOf(YearMonth.now()) }
    val monthTitle = displayedMonth.value.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    
    val vaccineDays = uiState.upcomingAnimals
        .map { LocalDate.ofEpochDay(it.nextVaccineEpochDay) }
        .filter { it.year == displayedMonth.value.year && it.monthValue == displayedMonth.value.monthValue }
        .map { it.dayOfMonth }
        .toSet()

    val alertDays = uiState.alerts
        .filter { it.campDateEpochDay != null }
        .map { LocalDate.ofEpochDay(it.campDateEpochDay!!) }
        .filter { it.year == displayedMonth.value.year && it.monthValue == displayedMonth.value.monthValue }
        .map { it.dayOfMonth }
        .toSet()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onScanCampQr, containerColor = MaterialTheme.colorScheme.primary) {
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
                Text(stringResource(R.string.vaccination_calendar), style = MaterialTheme.typography.headlineLarge)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(monthTitle, style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.unit)) {
                        IconButton(onClick = { displayedMonth.value = displayedMonth.value.minusMonths(1) }) {
                            Icon(Icons.Rounded.ChevronLeft, contentDescription = null)
                        }
                        IconButton(onClick = { displayedMonth.value = displayedMonth.value.plusMonths(1) }) {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }

            item {
                CalendarGrid(
                    yearMonth = displayedMonth.value,
                    vaccineDays = vaccineDays,
                    alertDays = alertDays,
                    selectedDate = uiState.selectedDate,
                    onDateClick = onDateSelected
                )
            }

            if (uiState.selectedDate != null) {
                item {
                    Text(
                        text = "Schedule for ${uiState.selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = AppDimens.unit)
                    )
                }

                if (uiState.selectedDateCamps.isEmpty() && uiState.selectedDateVaccinations.isEmpty()) {
                    item {
                        Text(
                            "No events scheduled for this day.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(uiState.selectedDateCamps) { camp ->
                    CampScheduleCard(camp)
                }

                items(uiState.selectedDateVaccinations) { animal ->
                    VaccinationCard(animal)
                }
            } else {
                item {
                    Text(
                        "Select a date to see details.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    vaccineDays: Set<Int>,
    alertDays: Set<Int>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday if we want, but ISO is 1=Mon

    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.unit)) {
        val days = (1..daysInMonth).toList()
        val rows = (days.size + firstDayOfWeek + 6) / 7
        
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.unit)
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayIndex)
                        val isSelected = selectedDate == date
                        
                        DayCell(
                            day = dayIndex,
                            isSelected = isSelected,
                            hasVaccine = vaccineDays.contains(dayIndex),
                            hasAlert = alertDays.contains(dayIndex),
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isSelected: Boolean,
    hasVaccine: Boolean,
    hasAlert: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (hasVaccine) {
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else MaterialTheme.colorScheme.primary))
                }
                if (hasAlert) {
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else Color(0xFFE91E63))) // Pink for alerts
                }
            }
        }
    }
}

@Composable
fun CampScheduleCard(camp: com.example.grama_vaxi.domain.model.HealthAlert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)), // Light pink
        shape = RoundedCornerShape(AppDimens.radiusLarge)
    ) {
        Column(modifier = Modifier.padding(AppDimens.gutter)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Event, contentDescription = null, tint = Color(0xFFC2185B))
                Spacer(modifier = Modifier.size(AppDimens.unit))
                Text(camp.title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF880E4F))
            }
            Spacer(modifier = Modifier.size(AppDimens.unit))
            Text(camp.message, style = MaterialTheme.typography.bodyMedium)
            if (camp.campLocation != null) {
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Place, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(camp.campLocation!!, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun VaccinationCard(animal: com.example.grama_vaxi.domain.model.Animal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        shape = RoundedCornerShape(AppDimens.radiusLarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDimens.gutter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.gutter)
        ) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Rounded.Vaccines, contentDescription = null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column {
                Text(animal.name, style = MaterialTheme.typography.titleMedium)
                Text("Vaccination due", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
