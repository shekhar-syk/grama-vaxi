package com.example.grama_vaxi.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.grama_vaxi.MainActivity
import com.example.grama_vaxi.R
import com.example.grama_vaxi.domain.repository.AnimalRepository
import com.example.grama_vaxi.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

/**
 * Worker that sends daily vaccine reminder notifications.
 * Sends reminders for animals with vaccinations scheduled within 3 days.
 */
@HiltWorker
class VaccineReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val animalRepository: AnimalRepository,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: run {
                    Log.w(TAG, "No authenticated user for vaccine reminders")
                    return Result.success()
                }

            val today = DateUtils.currentEpochDayUtc()
            val in3Days = today + 3

            // Get all animals owned by the user using Flow.first() to get the current value
            val allAnimalsFlow = animalRepository.observeAnimals(uid)
            val allAnimals = allAnimalsFlow.first()

            // Filter for animals with vaccinations within next 3 days
            val upcomingAnimals = allAnimals.filter { animal ->
                val vaccineDay = animal.nextVaccineEpochDay
                vaccineDay in today..in3Days
            }

            if (upcomingAnimals.isNotEmpty()) {
                sendVaccineReminderNotification(upcomingAnimals)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in VaccineReminderWorker", e)
            Result.retry()
        }
    }

    private fun sendVaccineReminderNotification(animals: List<com.example.grama_vaxi.domain.model.Animal>) {
        ensureVaccineReminderChannel()

        val title = if (animals.size == 1) {
            "Vaccine Reminder: ${animals[0].name}"
        } else {
            "Vaccine Reminders (${animals.size} animals)"
        }

        val bodyText = if (animals.size == 1) {
            val animal = animals[0]
            val date = DateUtils.epochDayToDisplay(animal.nextVaccineEpochDay)
            "${animal.name} needs vaccination on $date"
        } else {
            animals.take(2).joinToString(", ") { it.name } +
                    if (animals.size > 2) " and ${animals.size - 2} more" else ""
        }

        val launchIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "vaccineCalendar")
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            VACCINE_REMINDER_REQUEST_CODE,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, VACCINE_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(applicationContext).notify(
            VACCINE_REMINDER_NOTIFICATION_ID,
            builder.build()
        )
    }

    private fun ensureVaccineReminderChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = manager.getNotificationChannel(VACCINE_REMINDER_CHANNEL_ID)
        if (existing != null) return

        val channel = NotificationChannel(
            VACCINE_REMINDER_CHANNEL_ID,
            "Vaccine Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily reminders for upcoming animal vaccinations"
        }

        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "VaccineReminderWorker"
        const val VACCINE_REMINDER_CHANNEL_ID = "vaccine_reminders"
        const val VACCINE_REMINDER_NOTIFICATION_ID = 2001
        const val VACCINE_REMINDER_REQUEST_CODE = 2001
    }
}
