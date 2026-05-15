package com.example.grama_vaxi.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.grama_vaxi.domain.model.HealthAlert
import com.example.grama_vaxi.domain.repository.CampReminderScheduler
import com.example.grama_vaxi.utils.DateUtils
import com.example.grama_vaxi.worker.CampReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampReminderSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CampReminderScheduler {

    override fun scheduleCampReminder(alert: HealthAlert) {
        val epochDay = alert.campDateEpochDay ?: return
        val millis = TimeUnit.DAYS.toMillis(epochDay)
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
        
        val scheduledTime = DateUtils.parseDateTimeToEpoch(dateStr, alert.campTime) 
            ?: return // If we can't parse, don't schedule. 

        val delay = (scheduledTime - System.currentTimeMillis()).coerceAtLeast(0)

        val data = Data.Builder()
            .putString(CampReminderWorker.KEY_TITLE, alert.title)
            .putString(CampReminderWorker.KEY_MESSAGE, alert.message)
            .putString(CampReminderWorker.KEY_ALERT_ID, alert.id)
            .build()

        val request = OneTimeWorkRequestBuilder<CampReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("camp_reminder_${alert.id}")
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    override fun cancelCampReminder(alertId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag("camp_reminder_$alertId")
    }

    override fun scheduleDailyAlertsDigest() {
        val workManager = WorkManager.getInstance(context)
        
        val data = Data.Builder().build()
        val request = androidx.work.PeriodicWorkRequestBuilder<com.example.grama_vaxi.worker.DailyAlertDigestWorker>(
            12, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelayFor9am9pm(), TimeUnit.MILLISECONDS)
            .addTag("daily_digest")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_digest",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun calculateInitialDelayFor9am9pm(): Long {
        val now = java.util.Calendar.getInstance()
        val nineAm = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 9)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }
        val ninePm = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 21)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        return when {
            now.before(nineAm) -> nineAm.timeInMillis - now.timeInMillis
            now.before(ninePm) -> ninePm.timeInMillis - now.timeInMillis
            else -> {
                nineAm.add(java.util.Calendar.DAY_OF_YEAR, 1)
                nineAm.timeInMillis - now.timeInMillis
            }
        }
    }
}
