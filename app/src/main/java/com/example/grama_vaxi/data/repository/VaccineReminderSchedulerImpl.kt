package com.example.grama_vaxi.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.grama_vaxi.domain.repository.VaccineReminderScheduler
import com.example.grama_vaxi.worker.VaccineReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VaccineReminderScheduler using WorkManager.
 * Schedules a daily vaccine reminder check at a fixed time.
 */
@Singleton
class VaccineReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VaccineReminderScheduler {

    override fun scheduleVaccineReminders() {
        val request = PeriodicWorkRequestBuilder<VaccineReminderWorker>(
            1, // Repeat interval
            TimeUnit.DAYS // Every day
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .setInitialDelay(1, TimeUnit.HOURS) // Start after 1 hour
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                VACCINE_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing if already scheduled
                request
            )
    }

    override fun cancelVaccineReminders() {
        WorkManager.getInstance(context).cancelUniqueWork(VACCINE_REMINDER_WORK_NAME)
    }

    companion object {
        private const val VACCINE_REMINDER_WORK_NAME = "grama_vaxi_vaccine_reminder"
    }
}
