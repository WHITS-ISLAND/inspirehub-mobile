package io.github.witsisland.inspirehub.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * MainDispatcherをテスト用に置き換えるルール
 *
 * Android公式推奨のパターン:
 * https://developer.android.com/kotlin/coroutines/test
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) {
    @BeforeTest
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }
}
