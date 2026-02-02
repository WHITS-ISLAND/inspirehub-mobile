package io.github.witsisland.inspirehub.presentation.viewmodel

import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import io.github.witsisland.inspirehub.domain.model.Comment
import io.github.witsisland.inspirehub.domain.model.Node
import io.github.witsisland.inspirehub.domain.repository.CommentRepository
import io.github.witsisland.inspirehub.domain.repository.NodeRepository
import io.github.witsisland.inspirehub.domain.store.NodeStore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow

/**
 * 詳細ViewModel
 * ノード詳細・コメント・子ノード表示を管理
 */
class DetailViewModel(
    private val nodeStore: NodeStore,
    private val nodeRepository: NodeRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    // NodeStoreの選択状態を監視
    val selectedNode: StateFlow<Node?> = nodeStore.selectedNode

    val comments = MutableStateFlow(viewModelScope, emptyList<Comment>())

    val childNodes = MutableStateFlow(viewModelScope, emptyList<Node>())

    val isLoading = MutableStateFlow(viewModelScope, false)

    val error = MutableStateFlow(viewModelScope, null as String?)

    val commentText = MutableStateFlow(viewModelScope, "")

    val isCommentSubmitting = MutableStateFlow(viewModelScope, false)

    /**
     * ノード詳細を読み込み
     * getNode + getComments + getChildNodes を並行実行
     */
    fun loadDetail(nodeId: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null

            val nodeDeferred = async { nodeRepository.getNode(nodeId) }
            val commentsDeferred = async { commentRepository.getComments(nodeId) }
            val childNodesDeferred = async { nodeRepository.getChildNodes(nodeId) }

            val nodeResult = nodeDeferred.await()
            val commentsResult = commentsDeferred.await()
            val childNodesResult = childNodesDeferred.await()

            if (nodeResult.isSuccess) {
                nodeStore.selectNode(nodeResult.getOrNull())
            } else {
                error.value = nodeResult.exceptionOrNull()?.message ?: "Failed to load node"
            }

            if (commentsResult.isSuccess) {
                comments.value = commentsResult.getOrNull() ?: emptyList()
            }

            if (childNodesResult.isSuccess) {
                childNodes.value = childNodesResult.getOrNull() ?: emptyList()
            }

            isLoading.value = false
        }
    }

    /**
     * いいねを切り替え
     */
    fun toggleLike() {
        val node = selectedNode.value ?: return
        viewModelScope.launch {
            val result = nodeRepository.toggleLike(node.id)
            if (result.isSuccess) {
                nodeStore.selectNode(result.getOrNull())
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to toggle like"
            }
        }
    }

    fun updateCommentText(text: String) {
        commentText.value = text
    }

    /**
     * コメントを投稿
     */
    fun submitComment() {
        val nodeId = selectedNode.value?.id ?: return
        val text = commentText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            isCommentSubmitting.value = true
            error.value = null

            val result = commentRepository.createComment(
                nodeId = nodeId,
                content = text
            )

            if (result.isSuccess) {
                commentText.value = ""
                result.getOrNull()?.let { comment ->
                    comments.value = comments.value + comment
                }
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to post comment"
            }

            isCommentSubmitting.value = false
        }
    }

    /**
     * ノードを選択（子ノードへの遷移等）
     */
    fun selectNode(node: Node) {
        nodeStore.selectNode(node)
    }
}
