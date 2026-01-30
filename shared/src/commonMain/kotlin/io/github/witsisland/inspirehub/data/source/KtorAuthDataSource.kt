package io.github.witsisland.inspirehub.data.source

import io.github.witsisland.inspirehub.data.dto.TokenResponseDto
import io.github.witsisland.inspirehub.data.dto.UserDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Ktor Client を使用した AuthDataSource 実装
 */
class KtorAuthDataSource(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun getGoogleAuthUrl(): String {
        val response: Map<String, String> = httpClient.get("/auth/google/url").body()
        return response["url"] ?: throw IllegalStateException("OAuth URL not found in response")
    }

    override suspend fun exchangeAuthCode(code: String): TokenResponseDto {
        return httpClient.get("/auth/google/callback") {
            parameter("code", code)
        }.body()
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponseDto {
        return httpClient.post("/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refresh_token" to refreshToken))
        }.body()
    }

    override suspend fun getCurrentUser(): UserDto {
        return httpClient.get("/auth/me").body()
    }

    override suspend fun logout() {
        httpClient.post("/auth/logout")
    }
}
