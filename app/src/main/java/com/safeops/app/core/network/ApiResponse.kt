package com.safeops.app.core.network

import com.safeops.app.core.error.SafeOpsError

/**
 * Generic API Response wrapper.
 */
sealed interface ApiResponse<out T> {
    data class Success<T>(val body: T, val statusCode: Int) : ApiResponse<T>
    data class HttpError(val statusCode: Int, val message: String, val errorBody: String? = null) : ApiResponse<Nothing>
    data class NetworkFailure(val error: SafeOpsError) : ApiResponse<Nothing>

    val isSuccessful: Boolean get() = this is Success
}

/**
 * Centralized HTTP Client contract for SafeOps remote data sources.
 */
interface HttpClient {
    suspend fun <T> get(endpoint: String, responseType: Class<T>): ApiResponse<T>
    suspend fun <B, T> post(endpoint: String, body: B, responseType: Class<T>): ApiResponse<T>
    suspend fun <B, T> put(endpoint: String, body: B, responseType: Class<T>): ApiResponse<T>
    suspend fun <T> delete(endpoint: String, responseType: Class<T>): ApiResponse<T>
}
