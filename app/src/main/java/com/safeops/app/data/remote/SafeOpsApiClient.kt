package com.safeops.app.data.remote

import com.safeops.app.core.network.SafeOpsEnvironment
import com.safeops.app.data.remote.dto.AdminRegistrationRequestDto
import com.safeops.app.data.remote.dto.AdminRegistrationResponseDto
import com.safeops.app.data.remote.dto.AuthMeDto
import com.safeops.app.data.remote.dto.MessageResponseDto
import com.safeops.app.data.remote.dto.ReferralCodeDto
import com.safeops.app.data.remote.dto.StaffRegistrationRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

open class SafeOpsApiClient(
    private val baseUrl: String = SafeOpsEnvironment.activeBaseUrl,
    private val okHttpClient: OkHttpClient = defaultOkHttpClient()
) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        fun defaultOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
    }

    private fun buildUrl(endpoint: String): String {
        val cleanBase = baseUrl.trimEnd('/')
        val cleanEndpoint = endpoint.trimStart('/')
        return "$cleanBase/$cleanEndpoint"
    }

    suspend fun validateReferralCode(code: String): ReferralCodeDto = withContext(Dispatchers.IO) {
        val url = buildUrl("auth/referral/$code")
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext ReferralCodeDto(
                    valid = false,
                    errorMessage = "Referral code lookup failed (HTTP ${response.code})"
                )
            }
            json.decodeFromString<ReferralCodeDto>(bodyString)
        }
    }

    suspend fun getMe(idToken: String): AuthMeDto = withContext(Dispatchers.IO) {
        val url = buildUrl("auth/me")
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $idToken")
            .get()
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                throw IOException("Backend auth failed with HTTP ${response.code}: $bodyString")
            }
            try {
                val wrapped = json.decodeFromString<com.safeops.app.data.remote.dto.ApiResponseDto<AuthMeDto>>(bodyString)
                if (wrapped.data != null) {
                    return@withContext wrapped.data
                }
            } catch (_: Exception) {}
            json.decodeFromString<AuthMeDto>(bodyString)
        }
    }

    suspend fun registerAdmin(
        idToken: String,
        payload: AdminRegistrationRequestDto
    ): AdminRegistrationResponseDto = withContext(Dispatchers.IO) {
        val url = buildUrl("auth/register-admin")
        val requestBody = json.encodeToString(payload).toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $idToken")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                throw IOException("Admin registration failed with HTTP ${response.code}: $bodyString")
            }
            json.decodeFromString<AdminRegistrationResponseDto>(bodyString)
        }
    }

    suspend fun registerStaff(
        idToken: String,
        payload: StaffRegistrationRequestDto
    ): MessageResponseDto = withContext(Dispatchers.IO) {
        val url = buildUrl("auth/register-staff")
        val requestBody = json.encodeToString(payload).toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $idToken")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                throw IOException("Staff registration failed with HTTP ${response.code}: $bodyString")
            }
            json.decodeFromString<MessageResponseDto>(bodyString)
        }
    }
}

