package io.github.witsisland.inspirehub.domain.repository

import io.github.witsisland.inspirehub.domain.model.User

/**
 * 認証リポジトリ
 */
interface AuthRepository {
    /**
     * Google OAuth 認証URLを取得
     */
    suspend fun getGoogleAuthUrl(): Result<String>

    /**
     * OAuth認可コードを交換してログイン
     * @param code 認可コード
     * @return ログインしたユーザー
     */
    suspend fun loginWithAuthCode(code: String): Result<User>

    /**
     * アクセストークンを更新
     */
    suspend fun refreshAccessToken(): Result<Unit>

    /**
     * 現在のユーザー情報を取得
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * ログアウト
     */
    suspend fun logout(): Result<Unit>
}
