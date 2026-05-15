package com.example.grama_vaxi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grama_vaxi.data.repository.FirebaseActivityHolder
import com.example.grama_vaxi.data.remote.notifications.NotificationTokenSyncManager
import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.repository.AlertRepository
import com.example.grama_vaxi.domain.repository.SyncScheduler
import com.example.grama_vaxi.domain.repository.VaccineReminderScheduler
import com.example.grama_vaxi.domain.repository.CampReminderScheduler
import com.example.grama_vaxi.presentation.navigation.GramaVaxiNavHost
import com.example.grama_vaxi.presentation.viewmodel.AuthViewModel
import com.example.grama_vaxi.ui.theme.GramaVaxiTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var notificationTokenSyncManager: NotificationTokenSyncManager

    @Inject
    lateinit var vaccineReminderScheduler: VaccineReminderScheduler

    @Inject
    lateinit var campReminderScheduler: CampReminderScheduler

    @Inject
    lateinit var alertRepository: AlertRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                activityScope.launch {
                    notificationTokenSyncManager.syncCurrentToken()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseActivityHolder.activity = this
        syncScheduler.enqueuePeriodicSync()
        vaccineReminderScheduler.scheduleVaccineReminders()
        campReminderScheduler.scheduleDailyAlertsDigest()
        requestNotificationPermissionIfNeeded()
        activityScope.launch {
            notificationTokenSyncManager.syncCurrentToken()
            // Sync alerts from Firestore for the current user
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                (alertRepository as? com.example.grama_vaxi.data.repository.AlertRepositoryImpl)?.syncAlertsFromFirestore(currentUser.uid)
            }
        }
        enableEdgeToEdge()
        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            
            val darkTheme = when (authState.session.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            GramaVaxiTheme(darkTheme = darkTheme) {
                GramaVaxiNavHost(authViewModel = authViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (FirebaseActivityHolder.activity === this) {
            FirebaseActivityHolder.activity = null
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permissionState = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
