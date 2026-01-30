package io.github.witsisland.inspirehub.data.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

/**
 * Android用 HttpClient 実装（OkHttp エンジン）
 */
actual fun createHttpClient(
    baseUrl: String,
    enableLogging: Boolean,
    tokenProvider: (() -> String?)?
): HttpClient {
    return HttpClient(OkHttp).configureClient(
        baseUrl = baseUrl,
        enableLogging = enableLogging,
        tokenProvider = tokenProvider
    )
}
