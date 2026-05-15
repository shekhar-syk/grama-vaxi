package com.example.grama_vaxi.domain.model

data class SessionState(
    val isLoggedIn: Boolean = false,
    val userUid: String = "",
    val role: UserRole = UserRole.FARMER,
    val roleLabel: String = "",
    val language: AppLanguage = AppLanguage.ENGLISH,
    val theme: AppTheme = AppTheme.SYSTEM,
    val phoneNumber: String = "",
    val userName: String = "",
    val location: String = "",
    val email: String = "",
    val age: String = ""
)
