package io.github.witsisland.inspirehub.di

import io.github.witsisland.inspirehub.presentation.viewmodel.AuthViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * iOSからKoinを使うためのヘルパー
 */
object KoinHelper : KoinComponent {
    fun getAuthViewModel(): AuthViewModel {
        val viewModel: AuthViewModel by inject()
        return viewModel
    }
}
