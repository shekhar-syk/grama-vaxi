package com.example.grama_vaxi.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.domain.model.AppTheme
import com.example.grama_vaxi.domain.model.SessionState
import com.example.grama_vaxi.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore(name = "grama_vaxi_session")

@Singleton
class SessionLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val isLoggedIn = booleanPreferencesKey("is_logged_in")
        val userUid = stringPreferencesKey("user_uid")
        val role = stringPreferencesKey("role")
        val roleLabel = stringPreferencesKey("role_label")
        val language = stringPreferencesKey("language")
        val theme = stringPreferencesKey("theme")
        val phone = stringPreferencesKey("phone")
        val userName = stringPreferencesKey("user_name")
        val location = stringPreferencesKey("location")
        val email = stringPreferencesKey("email")
        val age = stringPreferencesKey("age")
    }

    val sessionFlow: Flow<SessionState> = context.sessionDataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map(::toSessionState)

    suspend fun setLanguage(language: AppLanguage) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.language] = language.name
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.theme] = theme.name
        }
    }

    suspend fun saveSession(
        uid: String,
        role: UserRole,
        phoneNumber: String,
        email: String = "",
        userName: String = ""
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.isLoggedIn] = true
            prefs[Keys.userUid] = uid
            prefs[Keys.role] = role.name
            prefs[Keys.phone] = phoneNumber
            if (email.isNotBlank()) {
                prefs[Keys.email] = email
            }
            if (userName.isNotBlank()) {
                prefs[Keys.userName] = userName
            }
        }
    }

    suspend fun updateProfile(
        userName: String,
        location: String,
        email: String,
        phoneNumber: String,
        age: String,
        roleLabel: String
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.userName] = userName
            prefs[Keys.location] = location
            prefs[Keys.email] = email
            prefs[Keys.phone] = phoneNumber
            prefs[Keys.age] = age
            prefs[Keys.roleLabel] = roleLabel
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.isLoggedIn] = false
            prefs[Keys.userUid] = ""
            prefs[Keys.phone] = ""
            prefs[Keys.userName] = ""
            prefs[Keys.location] = ""
            prefs[Keys.email] = ""
            prefs[Keys.age] = ""
            prefs[Keys.roleLabel] = ""
        }
    }

    private fun toSessionState(preferences: Preferences): SessionState {
        val language = preferences[Keys.language]
            ?.let { runCatching { AppLanguage.valueOf(it) }.getOrNull() }
            ?: AppLanguage.ENGLISH

        val role = preferences[Keys.role]
            ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
            ?: UserRole.FARMER

        val theme = preferences[Keys.theme]
            ?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() }
            ?: AppTheme.SYSTEM

        return SessionState(
            isLoggedIn = preferences[Keys.isLoggedIn] ?: false,
            userUid = preferences[Keys.userUid].orEmpty(),
            role = role,
            roleLabel = preferences[Keys.roleLabel].orEmpty(),
            language = language,
            theme = theme,
            phoneNumber = preferences[Keys.phone].orEmpty(),
            userName = preferences[Keys.userName].orEmpty(),
            location = preferences[Keys.location].orEmpty(),
            email = preferences[Keys.email].orEmpty(),
            age = preferences[Keys.age].orEmpty()
        )
    }
}
