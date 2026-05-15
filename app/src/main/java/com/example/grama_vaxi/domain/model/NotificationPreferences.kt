package com.example.grama_vaxi.domain.model

data class NotificationPreferences(
    val campAlertsEnabled: Boolean = true,
    val vaccineRemindersEnabled: Boolean = true,
    val systemNotificationsEnabled: Boolean = false,
    val defaultLeadTimeDays: Int = 3
)
