package com.example.grama_vaxi.data.remote.notifications

import android.util.Log
import com.example.grama_vaxi.data.local.preferences.SessionLocalDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTokenSyncManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val notificationSyncApi: NotificationSyncApi
) {

    suspend fun syncCurrentToken(forceToken: String? = null): Result<Unit> {
        return runCatching {
            val user = firebaseAuth.currentUser
                ?: throw IllegalStateException("User must be signed in before syncing FCM token")

            val session = sessionLocalDataSource.sessionFlow.first()
            val village = session.location.trim().ifBlank {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()
                    .getString("village")
                    .orEmpty()
                    .trim()
            }
            if (village.isBlank()) {
                throw IllegalStateException("Village/location is missing in session. Update profile first.")
            }

            val idToken = user.getIdToken(true).await().token
                ?: throw IllegalStateException("Unable to fetch Firebase ID token")

            val token = forceToken ?: FirebaseMessaging.getInstance().token.await()
            notificationSyncApi.registerToken(idToken, token, village).getOrThrow()
        }.onFailure {
            Log.e(TAG, "syncCurrentToken failed", it)
        }
    }

    suspend fun unregisterCurrentToken(forceToken: String? = null): Result<Unit> {
        return runCatching {
            val user = firebaseAuth.currentUser ?: return@runCatching
            val idToken = user.getIdToken(true).await().token
                ?: throw IllegalStateException("Unable to fetch Firebase ID token")

            val token = forceToken ?: FirebaseMessaging.getInstance().token.await()
            notificationSyncApi.unregisterToken(idToken, token).getOrThrow()
        }.onFailure {
            Log.e(TAG, "unregisterCurrentToken failed", it)
        }
    }

    companion object {
        private const val TAG = "NotificationTokenSync"
    }
}
