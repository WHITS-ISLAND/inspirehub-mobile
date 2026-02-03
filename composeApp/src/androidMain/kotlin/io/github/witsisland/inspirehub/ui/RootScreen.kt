package io.github.witsisland.inspirehub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.witsisland.inspirehub.presentation.viewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootScreen(authViewModel: AuthViewModel = koinViewModel()) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    if (isAuthenticated) {
        // TODO: 足軽5がNavigation/MainScreen構築中。完成後に差し替え。
        MainScreenPlaceholder(onLogout = { authViewModel.logout() })
    } else {
        LoginScreen(viewModel = authViewModel)
    }
}

/**
 * 仮のメイン画面（Navigation完成まで）
 */
@Composable
private fun MainScreenPlaceholder(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ログイン成功！",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "メイン画面は準備中です",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(onClick = onLogout) {
            Text("ログアウト")
        }
    }
}
