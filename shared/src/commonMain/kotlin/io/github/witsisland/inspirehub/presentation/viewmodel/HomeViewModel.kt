package io.github.witsisland.inspirehub.presentation.viewmodel

import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import io.github.witsisland.inspirehub.domain.model.Node
import io.github.witsisland.inspirehub.domain.repository.NodeRepository
import io.github.witsisland.inspirehub.domain.store.HomeTab
import io.github.witsisland.inspirehub.domain.store.NodeStore
import io.github.witsisland.inspirehub.domain.store.SortOrder
import io.github.witsisland.inspirehub.domain.store.UserStore
import kotlinx.coroutines.flow.StateFlow

/**
 * ホーム画面ViewModel
 */
class HomeViewModel(
    private val nodeStore: NodeStore,
    private val nodeRepository: NodeRepository,
    private val userStore: UserStore
) : ViewModel() {

    val nodes: StateFlow<List<Node>> = nodeStore.nodes

    val isLoading: StateFlow<Boolean> = nodeStore.isLoading

    val currentTab: StateFlow<HomeTab> = nodeStore.currentTab

    val sortOrder: StateFlow<SortOrder> = nodeStore.sortOrder

    val error = MutableStateFlow(viewModelScope, null as String?)

    fun loadNodes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            nodeStore.setLoading(true)
            error.value = null

            val result = nodeRepository.getNodes()
            if (result.isSuccess) {
                nodeStore.updateNodes(result.getOrThrow())
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to load nodes"
            }

            nodeStore.setLoading(false)
        }
    }

    fun refresh() = loadNodes(forceRefresh = true)

    fun setTab(tab: HomeTab) {
        nodeStore.setTab(tab)
        loadNodes()
    }

    fun setSortOrder(order: SortOrder) {
        nodeStore.setSortOrder(order)
    }

    fun toggleLike(nodeId: String) {
        viewModelScope.launch {
            val result = nodeRepository.toggleLike(nodeId)
            if (result.isSuccess) {
                loadNodes()
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to toggle like"
            }
        }
    }
}
