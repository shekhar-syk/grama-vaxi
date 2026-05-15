package com.example.grama_vaxi.domain.repository

import com.example.grama_vaxi.domain.model.HealthAlert

interface CampReminderScheduler {
    fun scheduleCampReminder(alert: HealthAlert)
    fun cancelCampReminder(alertId: String)
    fun scheduleDailyAlertsDigest()
}
