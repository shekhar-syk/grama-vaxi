package com.example.grama_vaxi.data.remote.notifications

import android.util.Log
import com.example.grama_vaxi.BuildConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSyncApi @Inject constructor() {

    fun registerToken(idToken: String, token: String, village: String, platform: String = "android"): Result<Unit> {
        return runCatching {
            val endpoint = endpointUrl("/api/notifications/token")
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $idToken")
            }

            val body = JSONObject().apply {
                put("token", token)
                put("village", village)
                put("platform", platform)
            }.toString()

            connection.outputStream.use { out ->
                out.write(body.toByteArray())
            }

            val code = connection.responseCode
            if (code !in 200..299) {
                throw IllegalStateException("Token register failed with HTTP $code")
            }
            connection.disconnect()
        }.onFailure {
            Log.e(TAG, "Failed to register FCM token", it)
        }
    }

    fun unregisterToken(idToken: String, token: String): Result<Unit> {
        return runCatching {
            val endpoint = endpointUrl("/api/notifications/token")
            val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $idToken")
            }

            val body = JSONObject().apply {
                put("token", token)
            }.toString()

            connection.outputStream.use { out ->
                out.write(body.toByteArray())
            }

            val code = connection.responseCode
            if (code !in 200..299) {
                throw IllegalStateException("Token unregister failed with HTTP $code")
            }
            connection.disconnect()
        }.onFailure {
            Log.e(TAG, "Failed to unregister FCM token", it)
        }
    }

    private fun endpointUrl(path: String): String {
        val base = BuildConfig.ADMIN_API_BASE_URL.trim().trimEnd('/')
        require(base.isNotBlank()) {
            "BuildConfig.ADMIN_API_BASE_URL is empty. Set GRAMA_VAXI_ADMIN_API_BASE_URL in local.properties"
        }
        return "$base$path"
    }

    companion object {
        private const val TAG = "NotificationSyncApi"
    }
}
