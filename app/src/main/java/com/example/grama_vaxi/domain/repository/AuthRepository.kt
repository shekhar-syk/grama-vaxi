package com.example.grama_vaxi.domain.repository

import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.model.SessionState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sessionState(): Flow<SessionState>
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setTheme(theme: AppTheme)
    suspend fun updateProfile(
        userName: String,
        location: String,
        email: String,
        phoneNumber: String,
        age: String,
        roleLabel: String
    )
    suspend fun sendOtp(phoneNumber: String): Result<String>
    suspend fun sendEmailOtp(email: String): Result<String>
    suspend fun verifyOtp(
        verificationToken: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>>
    suspend fun verifyEmailOtp(
        email: String,
        otpCode: String
    ): Result<Pair<SessionState, Boolean>>
    suspend fun signInWithGoogle(idToken: String): Result<Pair<SessionState, Boolean>>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
