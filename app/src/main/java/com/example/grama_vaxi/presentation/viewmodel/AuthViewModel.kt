package com.example.grama_vaxi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.usecase.ObserveSessionUseCase
import com.example.grama_vaxi.domain.usecase.SendOtpUseCase
import com.example.grama_vaxi.domain.usecase.SendEmailOtpUseCase
import com.example.grama_vaxi.domain.usecase.VerifyEmailOtpUseCase
import com.example.grama_vaxi.domain.usecase.DeleteAccountUseCase
import com.example.grama_vaxi.domain.usecase.SetLanguageUseCase
import com.example.grama_vaxi.domain.usecase.SetThemeUseCase
import com.example.grama_vaxi.domain.usecase.SignInWithGoogleUseCase
import com.example.grama_vaxi.domain.usecase.SignOutUseCase
import com.example.grama_vaxi.domain.usecase.UpdateProfileUseCase
import com.example.grama_vaxi.domain.usecase.VerifyOtpUseCase
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val sendEmailOtpUseCase: SendEmailOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val verifyEmailOtpUseCase: VerifyEmailOtpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { session ->
                _uiState.update { state ->
                    state.copy(session = session)
                }
            }
        }
    }

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch {
            setLanguageUseCase(language)
        }
    }

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun updateProfile(
        userName: String,
        location: String,
        email: String,
        phoneNumber: String,
        age: String,
        roleLabel: String
    ) {
        viewModelScope.launch {
            updateProfileUseCase(
                userName = userName,
                location = location,
                email = email,
                phoneNumber = phoneNumber,
                age = age,
                roleLabel = roleLabel
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    fun deleteAccount(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true) }
            val result = deleteAccountUseCase()
            _uiState.update { it.copy(isDeletingAccount = false) }
            onResult(result.isSuccess, result.exceptionOrNull()?.message)
        }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone.take(10), errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otp = otp.take(6), errorMessage = null) }
    }

    fun setLoginMethod(isEmail: Boolean) {
        _uiState.update { it.copy(isEmailLogin = isEmail, errorMessage = null) }
    }

    fun sendOtp(onSent: () -> Unit = {}) {
        if (_uiState.value.otpCooldownSeconds > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (_uiState.value.isEmailLogin) {
                sendEmailOtpUseCase(_uiState.value.email)
            } else {
                sendOtpUseCase("+91${_uiState.value.phoneNumber}")
            }

            result.fold(
                onSuccess = { token ->
                    _uiState.update {
                        it.copy(
                            verificationToken = token,
                            isLoading = false
                        )
                    }
                    startOtpCooldown()
                    onSent()
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to send OTP"
                        )
                    }
                }
            )
        }
    }

    fun verifyOtp(onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value

            val result = if (state.isEmailLogin) {
                verifyEmailOtpUseCase(state.email, state.otp)
            } else {
                verifyOtpUseCase(
                    verificationToken = state.verificationToken,
                    otpCode = state.otp
                )
            }

            result.fold(
                onSuccess = { resultPair ->
                    val (_, isNewUser) = resultPair
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    onSuccess(isNewUser)
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Invalid OTP"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signInWithGoogle(
        context: Context,
        webClientId: String,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(googleSignInLoading = true, errorMessage = null) }
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context, request)
                val googleIdToken = GoogleIdTokenCredential
                    .createFrom(result.credential.data)
                    .idToken
                val signInResult = signInWithGoogleUseCase(googleIdToken)
                signInResult.fold(
                    onSuccess = { resultPair ->
                        val (_, isNewUser) = resultPair
                        _uiState.update { it.copy(googleSignInLoading = false) }
                        onSuccess(isNewUser)
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                googleSignInLoading = false,
                                errorMessage = e.message ?: "Google sign-in failed"
                            )
                        }
                    }
                )
            } catch (e: GetCredentialException) {
                _uiState.update {
                    it.copy(
                        googleSignInLoading = false,
                        errorMessage = e.message ?: "Google sign-in cancelled"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        googleSignInLoading = false,
                        errorMessage = e.message ?: "Google sign-in failed"
                    )
                }
            }
        }
    }

    private fun startOtpCooldown(seconds: Int = 30) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in seconds downTo 0) {
                _uiState.update { it.copy(otpCooldownSeconds = remaining) }
                delay(1_000)
            }
        }
    }
}

data class AuthUiState(
    val session: SessionState = SessionState(),
    val phoneNumber: String = "",
    val email: String = "",
    val isEmailLogin: Boolean = false,
    val otp: String = "",
    val verificationToken: String = "",
    val otpCooldownSeconds: Int = 0,
    val isLoading: Boolean = false,
    val googleSignInLoading: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val errorMessage: String? = null
)
