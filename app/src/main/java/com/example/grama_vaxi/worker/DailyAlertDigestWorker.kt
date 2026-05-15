package com.example.grama_vaxi.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.grama_vaxi.MainActivity
import com.example.grama_vaxi.R
import com.example.grama_vaxi.data.local.dao.AlertDao
import com.example.grama_vaxi.data.local.dao.AnimalDao
import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.repository.AuthRepository
import com.example.grama_vaxi.utils.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyAlertDigestWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val alertDao: AlertDao,
    private val animalDao: AnimalDao,
    private val authRepository: AuthRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val session = authRepository.sessionState().first()
        if (!session.isLoggedIn) return Result.success()

        val ownerUid = session.userUid
        val todayEpoch = DateUtils.currentEpochDayUtc()

        val todayCamps = alertDao.getAlertsForDate(ownerUid, todayEpoch)
        val todayVaccines = animalDao.getVaccinesForDate(ownerUid, todayEpoch)

        if (todayCamps.isNotEmpty() || todayVaccines.isNotEmpty()) {
            val title = "Daily Health Summary"
            val message = buildString {
                if (todayCamps.isNotEmpty()) {
                    append("${todayCamps.size} health camp(s) today. ")
                }
                if (todayVaccines.isNotEmpty()) {
                    append("${todayVaccines.size} vaccination(s) scheduled.")
                }
            }
            sendNotification(title, message)
        }

        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val context = applicationContext
        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "calendar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Digest"
            val descriptionText = "Morning and evening health summaries"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "daily_digest"
        const val NOTIFICATION_ID = 9001
    }
}
