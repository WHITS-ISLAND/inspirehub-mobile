package io.github.witsisland.inspirehub.presentation.viewmodel

import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import io.github.witsisland.inspirehub.domain.model.Node
import io.github.witsisland.inspirehub.domain.model.User
import io.github.witsisland.inspirehub.domain.repository.NodeRepository
import io.github.witsisland.inspirehub.domain.store.UserStore
import kotlinx.coroutines.flow.StateFlow

/**
 * マイページViewModel
 */
class MyPageViewModel(
    private val userStore: UserStore,
    private val nodeRepository: NodeRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = userStore.currentUser

    val myNodes = MutableStateFlow(viewModelScope, emptyList<Node>())

    val isLoading = MutableStateFlow(viewModelScope, false)

    val error = MutableStateFlow(viewModelScope, null as String?)

    fun loadMyNodes() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val userId = currentUser.value?.id
            if (userId == null) {
                error.value = "User not authenticated"
                isLoading.value = false
                return@launch
            }

            val result = nodeRepository.getNodes()
            if (result.isSuccess) {
                myNodes.value = result.getOrThrow().filter { it.authorId == userId }
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to load nodes"
            }

            isLoading.value = false
        }
    }

    fun refresh() = loadMyNodes()
}
