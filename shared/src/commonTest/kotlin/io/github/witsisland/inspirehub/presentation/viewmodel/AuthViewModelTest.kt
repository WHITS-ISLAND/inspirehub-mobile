package io.github.witsisland.inspirehub.presentation.viewmodel

import app.cash.turbine.test
import io.github.witsisland.inspirehub.domain.model.User
import io.github.witsisland.inspirehub.domain.repository.FakeAuthRepository
import io.github.witsisland.inspirehub.domain.store.UserStore
import io.github.witsisland.inspirehub.test.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * AuthViewModelの単体テスト
 */
class AuthViewModelTest : MainDispatcherRule() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var userStore: UserStore

    @BeforeTest
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        userStore = UserStore()
        viewModel = AuthViewModel(fakeAuthRepository, userStore)
    }

    @AfterTest
    fun tearDown() {
        // UserStoreの状態をクリア
        userStore.logout()
        fakeAuthRepository.reset()
    }

    // ========================================
    // getGoogleAuthUrl のテスト
    // ========================================

    @Test
    fun `getGoogleAuthUrl - 成功時にauthUrlが更新されること`() = runTest {
        // Given
        val expectedUrl = "https://accounts.google.com/oauth"
        fakeAuthRepository.getGoogleAuthUrlResult = Result.success(expectedUrl)

        // When
        viewModel.getGoogleAuthUrl()

        // Then
        assertEquals(expectedUrl, viewModel.authUrl.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(1, fakeAuthRepository.getGoogleAuthUrlCallCount)
    }

    @Test
    fun `getGoogleAuthUrl - 失敗時にエラーが設定されること`() = runTest {
        // Given
        val errorMessage = "Network error"
        fakeAuthRepository.getGoogleAuthUrlResult = Result.failure(
            Exception(errorMessage)
        )

        // When
        viewModel.getGoogleAuthUrl()

        // Then
        assertNull(viewModel.authUrl.value)
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `getGoogleAuthUrl - 処理完了後にloadingがfalseになること`() = runTest {
        // Given
        fakeAuthRepository.getGoogleAuthUrlResult = Result.success("url")

        // When
        viewModel.getGoogleAuthUrl()

        // Then: UnconfinedTestDispatcherを使用しているため、処理は即座に完了
        assertFalse(viewModel.isLoading.value)
    }

    // ========================================
    // loginWithAuthCode のテスト
    // ========================================

    @Test
    fun `loginWithAuthCode - 認可コードでログインが成功すること`() = runTest {
        // Given
        val authCode = "test-auth-code"
        val mockUser = User(
            id = "user123",
            handle = "testuser",
            roleTag = "Backend",
            createdAt = Clock.System.now()
        )
        fakeAuthRepository.loginWithAuthCodeResult = Result.success(mockUser)

        // When
        viewModel.loginWithAuthCode(authCode)

        // Then
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(1, fakeAuthRepository.loginWithAuthCodeCallCount)
        assertEquals(authCode, fakeAuthRepository.lastAuthCode)
    }

    @Test
    fun `loginWithAuthCode - 失敗時にエラーが設定されること`() = runTest {
        // Given
        val authCode = "invalid-code"
        val errorMessage = "Invalid authorization code"
        fakeAuthRepository.loginWithAuthCodeResult = Result.failure(
            Exception(errorMessage)
        )

        // When
        viewModel.loginWithAuthCode(authCode)

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // ========================================
    // fetchCurrentUser のテスト
    // ========================================

    @Test
    fun `fetchCurrentUser - ユーザー情報の取得が成功すること`() = runTest {
        // Given
        val mockUser = User(
            id = "user123",
            handle = "testuser",
            roleTag = "Frontend",
            createdAt = Clock.System.now()
        )
        fakeAuthRepository.getCurrentUserResult = Result.success(mockUser)

        // When
        viewModel.fetchCurrentUser()

        // Then
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(1, fakeAuthRepository.getCurrentUserCallCount)
    }

    @Test
    fun `fetchCurrentUser - 失敗時にエラーが設定されること`() = runTest {
        // Given
        val errorMessage = "User not found"
        fakeAuthRepository.getCurrentUserResult = Result.failure(
            Exception(errorMessage)
        )

        // When
        viewModel.fetchCurrentUser()

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // ========================================
    // logout のテスト
    // ========================================

    @Test
    fun `logout - ログアウトが成功すること`() = runTest {
        // Given
        fakeAuthRepository.logoutResult = Result.success(Unit)

        // When
        viewModel.logout()

        // Then
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertEquals(1, fakeAuthRepository.logoutCallCount)
    }

    @Test
    fun `logout - 失敗時にエラーが設定されること`() = runTest {
        // Given
        val errorMessage = "Logout failed"
        fakeAuthRepository.logoutResult = Result.failure(
            Exception(errorMessage)
        )

        // When
        viewModel.logout()

        // Then
        assertEquals(errorMessage, viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    // ========================================
    // clearError のテスト
    // ========================================

    @Test
    fun `clearError - エラー状態がクリアされること`() = runTest {
        // Given: エラー状態を作る
        fakeAuthRepository.getGoogleAuthUrlResult = Result.failure(
            Exception("Test error")
        )
        viewModel.getGoogleAuthUrl()
        assertNotNull(viewModel.error.value)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }

    // ========================================
    // UserStore連携のテスト
    // ========================================

    @Test
    fun `currentUser - UserStoreの状態を反映すること`() = runTest {
        // Given
        val mockUser = User(
            id = "user123",
            handle = "testuser",
            roleTag = null,
            createdAt = Clock.System.now()
        )

        // When
        userStore.login(mockUser, "access-token", "refresh-token")

        // Then
        viewModel.currentUser.test {
            assertEquals(mockUser, awaitItem())
        }
    }

    @Test
    fun `isAuthenticated - UserStoreの認証状態を反映すること`() = runTest {
        // When: ログイン前
        viewModel.isAuthenticated.test {
            assertFalse(awaitItem())

            // ログイン
            val mockUser = User(
                id = "user123",
                handle = "testuser",
                roleTag = null,
                createdAt = Clock.System.now()
            )
            userStore.login(mockUser, "access-token", "refresh-token")

            assertTrue(awaitItem())
        }
    }
}
