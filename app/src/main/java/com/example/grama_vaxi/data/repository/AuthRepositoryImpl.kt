package com.example.grama_vaxi.data.repository

import android.app.Activity
import com.example.grama_vaxi.data.local.preferences.SessionLocalDataSource
import com.example.grama_vaxi.data.remote.notifications.NotificationTokenSyncManager
import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.model.UserRole
import com.example.grama_vaxi.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val notificationTokenSyncManager: NotificationTokenSyncManager
) : AuthRepository {

    override fun sessionState(): Flow<SessionState> = sessionLocalDataSource.sessionFlow

    override suspend fun setLanguage(language: AppLanguage) {
        sessionLocalDataSource.setLanguage(language)
    }

    override suspend fun setTheme(theme: AppTheme) {
        sessionLocalDataSource.setTheme(theme)
    }

    override suspend fun updateProfile(
        userName: String,
        location: String,
        email: String,
        phoneNumber: String,
        age: String,
        roleLabel: String
    ) {
        sessionLocalDataSource.updateProfile(
            userName = userName,
            location = location,
            email = email,
            phoneNumber = phoneNumber,
            age = age,
            roleLabel = roleLabel
        )

        firebaseAuth.currentUser?.let { user ->
            val profileUpdate = mapOf(
                "name" to userName,
                "phone" to phoneNumber,
                "village" to location,
                "updatedAt" to Timestamp.now()
            )

            runCatching {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .set(profileUpdate, SetOptions.merge())
                    .await()
            }
        }

        notificationTokenSyncManager.syncCurrentToken()
    }

    override suspend fun sendOtp(phoneNumber: String): Result<String> {
        require(phoneNumber.isNotBlank()) { "Phone number must not be blank" }

        // Mock path: If you want to bypass real SMS for all numbers during development
        // You can use a special suffix or just allow it for any number for now.
        if (phoneNumber.endsWith("1234567890") || true) { // Set to true to enable mock for all numbers
             delay(1000)
             return Result.success("mock_phone_verification_id")
        }

        val activity = FirebaseActivityHolder.activity
            ?: return Result.failure(IllegalStateException("No active Activity. Open the app properly."))

        return suspendCancellableCoroutine { cont ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    if (cont.isActive) cont.resume(Result.success("auto::${credential.smsCode ?: ""}"))
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    if (cont.isActive) cont.resume(Result.failure(e))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    FirebaseActivityHolder.resendToken = token
                    if (cont.isActive) cont.resume(Result.success(verificationId))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    override suspend fun sendEmailOtp(email: String): Result<String> {
        delay(1000)
        return if (email.contains("@")) {
            Result.success("email_verification_mock_id")
        } else {
            Result.failure(Exception("Invalid email address"))
        }
    }

    override suspend fun verifyOtp(
        verificationToken: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>> {
        // Mock verification for Phone
        if (verificationToken == "mock_phone_verification_id") {
            return if (otpCode == "123456") {
                val mockUid = "mock_phone_user_uid"
                val session = SessionState(
                    isLoggedIn = true,
                    userUid = mockUid,
                    role = UserRole.FARMER,
                    phoneNumber = "Mock Number"
                )
                MainScope().launch {
                    sessionLocalDataSource.saveSession(
                        uid = mockUid,
                        role = UserRole.FARMER,
                        phoneNumber = "Mock Number"
                    )
                }
                Result.success(Pair(session, false))
            } else {
                Result.failure(Exception("Invalid Phone OTP. Please use 123456 for testing."))
            }
        }

        val credential: PhoneAuthCredential = if (verificationToken.startsWith("auto::")) {
            val autoCode = verificationToken.removePrefix("auto::")
            if (autoCode.isNotBlank()) {
                PhoneAuthProvider.getCredential(verificationToken, autoCode)
            } else {
                return Result.failure(IllegalStateException("Auto-verification failed. Enter OTP manually."))
            }
        } else {
            PhoneAuthProvider.getCredential(verificationToken, otpCode)
        }

        return signInWithCredential(credential)
    }

    override suspend fun verifyEmailOtp(
        email: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>> {
        delay(1000)
        return if (otpCode == "123456") {
            val mockUid = "mock_user_${email.hashCode()}"
            val session = SessionState(
                isLoggedIn = true,
                userUid = mockUid,
                role = UserRole.FARMER,
                phoneNumber = "",
                email = email
            )
            MainScope().launch {
                sessionLocalDataSource.saveSession(
                    uid = mockUid,
                    role = UserRole.FARMER,
                    phoneNumber = "",
                    email = email
                )
            }
            Result.success(Pair(session, false))
        } else {
            Result.failure(Exception("Invalid Email OTP. Please use 123456 for testing."))
        }
    }

    private suspend fun signInWithCredential(credential: PhoneAuthCredential): Result<Pair<SessionState, Boolean>> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user == null) {
                        cont.resume(Result.failure(IllegalStateException("Sign-in succeeded but user is null")))
                        return@addOnSuccessListener
                    }
                    val isNewUser = result.additionalUserInfo?.isNewUser ?: false
                    MainScope().launch {
                        sessionLocalDataSource.saveSession(
                            uid = user.uid,
                            role = UserRole.FARMER,
                            phoneNumber = user.phoneNumber ?: ""
                        )
                        val session = SessionState(
                            isLoggedIn = true,
                            userUid = user.uid,
                            role = UserRole.FARMER,
                            phoneNumber = user.phoneNumber ?: ""
                        )
                        cont.resume(Result.success(Pair(session, isNewUser)))
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<Pair<SessionState, Boolean>> =
        suspendCancellableCoroutine { cont ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user == null) {
                        cont.resume(Result.failure(IllegalStateException("Google sign-in: user is null")))
                        return@addOnSuccessListener
                    }
                    val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                    MainScope().launch {
                        sessionLocalDataSource.saveSession(
                            uid = user.uid,
                            role = UserRole.FARMER,
                            phoneNumber = user.phoneNumber ?: "",
                            email = user.email ?: "",
                            userName = user.displayName ?: ""
                        )
                        runCatching {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .get()
                                .await()
                                .getString("village")
                                ?.takeIf { it.isNotBlank() }
                                ?.let { village ->
                                    sessionLocalDataSource.updateProfile(
                                        userName = user.displayName ?: "",
                                        location = village,
                                        email = user.email ?: "",
                                        phoneNumber = user.phoneNumber ?: "",
                                        age = "",
                                        roleLabel = UserRole.FARMER.name
                                    )
                                }
                        }
                        cont.resume(Result.success(Pair(SessionState(
                            isLoggedIn = true,
                            userUid = user.uid,
                            role = UserRole.FARMER,
                            phoneNumber = user.phoneNumber ?: ""
                        ), isNewUser)))
                    }
                }
                .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
        }

    override suspend fun signOut() {
        notificationTokenSyncManager.unregisterCurrentToken()
        firebaseAuth.signOut()
        sessionLocalDataSource.clearSession()
    }

    override suspend fun deleteAccount(): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                cont.resume(Result.failure(IllegalStateException("No signed-in user")))
                return@suspendCancellableCoroutine
            }

            MainScope().launch {
                runCatching {
                    notificationTokenSyncManager.unregisterCurrentToken()
                    scheduleDashboardCredentialPurge(user.uid)
                }

                user.delete()
                    .addOnSuccessListener {
                        MainScope().launch {
                            firebaseAuth.signOut()
                            sessionLocalDataSource.clearSession()
                            cont.resume(Result.success(Unit))
                        }
                    }
                    .addOnFailureListener { e ->
                        cont.resume(Result.failure(e))
                    }
            }
        }

    private suspend fun scheduleDashboardCredentialPurge(ownerUid: String) {
        val firestore = FirebaseFirestore.getInstance()
        val requestedAt = Timestamp.now()
        val purgeAt = Timestamp(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15)))

        val deletionMarker = mapOf(
            "isDeleted" to true,
            "accountDeletionRequestedAt" to requestedAt,
            "accountPurgeAt" to purgeAt
        )

        firestore.collection("users")
            .document(ownerUid)
            .set(deletionMarker, SetOptions.merge())
            .await()

        suspend fun markCollection(collectionName: String) {
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo("ownerUid", ownerUid)
                .get()
                .await()

            snapshot.documents.chunked(400).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { doc ->
                    batch.set(
                        doc.reference,
                        deletionMarker,
                        SetOptions.merge()
                    )
                }
                batch.commit().await()
            }
        }

        markCollection("animals")
        markCollection("reports")
    }
}

object FirebaseActivityHolder {
    var activity: Activity? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
}
