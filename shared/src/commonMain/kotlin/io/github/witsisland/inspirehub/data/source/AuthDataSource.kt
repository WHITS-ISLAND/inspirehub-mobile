package io.github.witsisland.inspirehub.data.source

import io.github.witsisland.inspirehub.data.dto.TokenResponseDto
import io.github.witsisland.inspirehub.data.dto.UserDto

/**
 * 認証データソースインターフェース
 */
interface AuthDataSource {
    /**
     * Google OAuth URL を取得
     * @return OAuth認証URL
     */
    suspend fun getGoogleAuthUrl(): String

    /**
     * OAuth認可コードを交換してトークンを取得
     * @param code 認可コード
     * @return トークンレスポンス
     */
    suspend fun exchangeAuthCode(code: String): TokenResponseDto

    /**
     * Google ID Tokenを検証してトークンを取得（SDK方式）
     * @param idToken Google ID Token
     * @return トークンレスポンス
     */
    suspend fun verifyGoogleToken(idToken: String): TokenResponseDto

    /**
     * アクセストークンを更新
     * @param refreshToken リフレッシュトークン
     * @return トークンレスポンス
     */
    suspend fun refreshToken(refreshToken: String): TokenResponseDto

    /**
     * 現在のユーザー情報を取得
     * @return ユーザー情報
     */
    suspend fun getCurrentUser(): UserDto

    /**
     * ログアウト
     */
    suspend fun logout()
}
