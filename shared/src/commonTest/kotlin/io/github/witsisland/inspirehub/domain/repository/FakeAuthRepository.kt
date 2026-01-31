package io.github.witsisland.inspirehub.domain.repository

import io.github.witsisland.inspirehub.domain.model.User

/**
 * AuthRepositoryのFake実装（テスト用）
 */
class FakeAuthRepository : AuthRepository {

    var getGoogleAuthUrlResult: Result<String> = Result.success("https://fake-auth-url.com")
    var loginWithAuthCodeResult: Result<User>? = null
    var refreshAccessTokenResult: Result<Unit> = Result.success(Unit)
    var getCurrentUserResult: Result<User>? = null
    var logoutResult: Result<Unit> = Result.success(Unit)

    // 呼び出し回数をカウント
    var getGoogleAuthUrlCallCount = 0
    var loginWithAuthCodeCallCount = 0
    var refreshAccessTokenCallCount = 0
    var getCurrentUserCallCount = 0
    var logoutCallCount = 0

    // 最後に渡された引数を保存
    var lastAuthCode: String? = null

    override suspend fun getGoogleAuthUrl(): Result<String> {
        getGoogleAuthUrlCallCount++
        return getGoogleAuthUrlResult
    }

    override suspend fun loginWithAuthCode(code: String): Result<User> {
        loginWithAuthCodeCallCount++
        lastAuthCode = code
        return loginWithAuthCodeResult ?: error("loginWithAuthCodeResult not set")
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        refreshAccessTokenCallCount++
        return refreshAccessTokenResult
    }

    override suspend fun getCurrentUser(): Result<User> {
        getCurrentUserCallCount++
        return getCurrentUserResult ?: error("getCurrentUserResult not set")
    }

    override suspend fun logout(): Result<Unit> {
        logoutCallCount++
        return logoutResult
    }

    fun reset() {
        getGoogleAuthUrlCallCount = 0
        loginWithAuthCodeCallCount = 0
        refreshAccessTokenCallCount = 0
        getCurrentUserCallCount = 0
        logoutCallCount = 0
        lastAuthCode = null
    }
}
