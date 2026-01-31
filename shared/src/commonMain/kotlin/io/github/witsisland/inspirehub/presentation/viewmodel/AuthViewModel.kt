package io.github.witsisland.inspirehub.presentation.viewmodel

import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import io.github.witsisland.inspirehub.domain.model.User
import io.github.witsisland.inspirehub.domain.repository.AuthRepository
import io.github.witsisland.inspirehub.domain.store.UserStore
import kotlinx.coroutines.flow.StateFlow

/**
 * 認証ViewModel
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userStore: UserStore
) : ViewModel() {

    // UserStore の状態を監視
    val currentUser: StateFlow<User?> = userStore.currentUser

    val isAuthenticated: StateFlow<Boolean> = userStore.isAuthenticated

    // 画面固有の状態
    val isLoading = MutableStateFlow(viewModelScope, false)

    val error = MutableStateFlow(viewModelScope, null as String?)

    val authUrl = MutableStateFlow(viewModelScope, null as String?)

    /**
     * Google OAuth URL を取得
     */
    fun getGoogleAuthUrl() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = authRepository.getGoogleAuthUrl()
            if (result.isSuccess) {
                authUrl.value = result.getOrNull()
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to get auth URL"
            }

            isLoading.value = false
        }
    }

    /**
     * OAuth認可コードでログイン
     */
    fun loginWithAuthCode(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = authRepository.loginWithAuthCode(code)
            if (result.isFailure) {
                error.value = result.exceptionOrNull()?.message ?: "Login failed"
            }

            isLoading.value = false
        }
    }

    /**
     * 現在のユーザー情報を取得
     */
    fun fetchCurrentUser() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = authRepository.getCurrentUser()
            if (result.isFailure) {
                error.value = result.exceptionOrNull()?.message ?: "Failed to fetch user"
            }

            isLoading.value = false
        }
    }

    /**
     * ログアウト
     */
    fun logout() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val result = authRepository.logout()
            if (result.isFailure) {
                error.value = result.exceptionOrNull()?.message ?: "Logout failed"
            }

            isLoading.value = false
        }
    }

    /**
     * エラーをクリア
     */
    fun clearError() {
        error.value = null
    }
}
