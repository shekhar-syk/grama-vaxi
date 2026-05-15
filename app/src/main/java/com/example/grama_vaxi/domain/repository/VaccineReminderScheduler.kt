package com.example.grama_vaxi.domain.repository

/**
 * Interface for managing vaccine reminder scheduling.
 */
interface VaccineReminderScheduler {
    /**
     * Schedules daily vaccine reminder notifications.
     * Should be called on app startup.
     */
    fun scheduleVaccineReminders()

    /**
     * Cancels all vaccine reminder notifications.
     */
    fun cancelVaccineReminders()
}
