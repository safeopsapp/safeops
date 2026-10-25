package com.safeops.app.data.remote

import com.safeops.app.core.network.SafeOpsEnvironment
import com.safeops.app.data.remote.dto.AdminRegistrationRequestDto
import com.safeops.app.data.remote.dto.AdminRegistrationResponseDto
import com.safeops.app.data.remote.dto.ApiResponseDto
import com.safeops.app.data.remote.dto.AuthMeDto
import com.safeops.app.data.remote.dto.MessageResponseDto
import com.safeops.app.data.remote.dto.ReferralCodeDto
import com.safeops.app.data.remote.dto.StaffRegistrationRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

open class SafeOpsApiClient(
    private val primaryBaseUrl: String = SafeOpsEnvironment.activeBaseUrl,
    private val fallbackBaseUrl: String = "http://10.156.145.213:8080/api/v1/",
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
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
    }

    private fun buildUrl(base: String, endpoint: String): String {
        val cleanBase = base.trimEnd('/')
        val cleanEndpoint = endpoint.trimStart('/')
        return "$cleanBase/$cleanEndpoint"
    }

    private fun extractErrorMessage(bodyString: String, fallback: String): String {
        return try {
            val root = json.parseToJsonElement(bodyString)
            if (root is JsonObject) {
                val msg = root["message"]?.jsonPrimitive?.content
                if (!msg.isNullOrBlank()) return msg
                val err = root["error"]?.jsonPrimitive?.content
                if (!err.isNullOrBlank()) return err
            }
            fallback
        } catch (_: Exception) {
            fallback
        }
    }

    private suspend fun <T> executeWithFallback(block: suspend (baseUrl: String) -> T): T {
        return try {
            block(primaryBaseUrl)
        } catch (e: Exception) {
            if ((e is ConnectException || e is SocketTimeoutException) && primaryBaseUrl != fallbackBaseUrl) {
                block(fallbackBaseUrl)
            } else {
                throw e
            }
        }
    }

    suspend fun validateReferralCode(code: String): ReferralCodeDto = withContext(Dispatchers.IO) {
        executeWithFallback { base ->
            val url = buildUrl(base, "auth/referral/$code")
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = extractErrorMessage(bodyString, "Referral code lookup failed (HTTP ${response.code})")
                    return@executeWithFallback ReferralCodeDto(
                        valid = false,
                        errorMessage = errMsg
                    )
                }
                try {
                    val wrapped = json.decodeFromString<ApiResponseDto<ReferralCodeDto>>(bodyString)
                    if (wrapped.data != null) {
                        return@executeWithFallback wrapped.data
                    }
                } catch (_: Exception) {}
                json.decodeFromString<ReferralCodeDto>(bodyString)
            }
        }
    }

    suspend fun getMe(idToken: String): AuthMeDto = withContext(Dispatchers.IO) {
        executeWithFallback { base ->
            val url = buildUrl(base, "auth/me")
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $idToken")
                .get()
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = extractErrorMessage(bodyString, "Backend auth failed with HTTP ${response.code}")
                    throw IOException(errMsg)
                }
                try {
                    val wrapped = json.decodeFromString<ApiResponseDto<AuthMeDto>>(bodyString)
                    if (wrapped.data != null) {
                        return@executeWithFallback wrapped.data
                    }
                } catch (_: Exception) {}
                json.decodeFromString<AuthMeDto>(bodyString)
            }
        }
    }

    suspend fun registerAdmin(
        idToken: String,
        payload: AdminRegistrationRequestDto
    ): AdminRegistrationResponseDto = withContext(Dispatchers.IO) {
        executeWithFallback { base ->
            val url = buildUrl(base, "auth/register-admin")
            val requestBody = json.encodeToString(payload).toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $idToken")
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = extractErrorMessage(bodyString, "Admin registration failed with HTTP ${response.code}")
                    throw IOException(errMsg)
                }
                try {
                    val wrapped = json.decodeFromString<ApiResponseDto<AdminRegistrationResponseDto>>(bodyString)
                    if (wrapped.data != null) {
                        return@executeWithFallback wrapped.data
                    }
                } catch (_: Exception) {}
                json.decodeFromString<AdminRegistrationResponseDto>(bodyString)
            }
        }
    }

    suspend fun registerStaff(
        idToken: String,
        payload: StaffRegistrationRequestDto
    ): MessageResponseDto = withContext(Dispatchers.IO) {
        executeWithFallback { base ->
            val url = buildUrl(base, "auth/register-staff")
            val requestBody = json.encodeToString(payload).toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $idToken")
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errMsg = extractErrorMessage(bodyString, "Staff registration failed with HTTP ${response.code}")
                    throw IOException(errMsg)
                }
                try {
                    val wrapped = json.decodeFromString<ApiResponseDto<String>>(bodyString)
                    if (wrapped.message != null || wrapped.data != null) {
                        return@executeWithFallback MessageResponseDto(status = wrapped.data, message = wrapped.message)
                    }
                } catch (_: Exception) {}
                json.decodeFromString<MessageResponseDto>(bodyString)
            }
        }
    }
}

