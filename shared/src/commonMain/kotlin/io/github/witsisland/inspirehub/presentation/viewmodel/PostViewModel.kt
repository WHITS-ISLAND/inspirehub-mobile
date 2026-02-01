package io.github.witsisland.inspirehub.presentation.viewmodel

import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.launch
import io.github.witsisland.inspirehub.domain.model.Node
import io.github.witsisland.inspirehub.domain.model.NodeType
import io.github.witsisland.inspirehub.domain.repository.NodeRepository
import io.github.witsisland.inspirehub.domain.store.NodeStore
import kotlinx.coroutines.flow.StateFlow

/**
 * 投稿ViewModel
 * 課題(ISSUE)・アイデア(IDEA)・派生投稿を1つのViewModelで扱う
 */
class PostViewModel(
    private val nodeStore: NodeStore,
    private val nodeRepository: NodeRepository
) : ViewModel() {

    val title = MutableStateFlow(viewModelScope, "")

    val content = MutableStateFlow(viewModelScope, "")

    val tags = MutableStateFlow(viewModelScope, emptyList<String>())

    val parentNode = MutableStateFlow(viewModelScope, null as Node?)

    val isSubmitting = MutableStateFlow(viewModelScope, false)

    val error = MutableStateFlow(viewModelScope, null as String?)

    val isSuccess = MutableStateFlow(viewModelScope, false)

    fun updateTitle(value: String) {
        title.value = value
    }

    fun updateContent(value: String) {
        content.value = value
    }

    fun addTag(tag: String) {
        if (tag !in tags.value) {
            tags.value = tags.value + tag
        }
    }

    fun removeTag(tag: String) {
        tags.value = tags.value - tag
    }

    fun setParentNode(node: Node?) {
        parentNode.value = node
    }

    /**
     * 課題を投稿
     */
    fun submitIssue() {
        submit(NodeType.ISSUE, parentNodeId = null)
    }

    /**
     * アイデアを投稿
     */
    fun submitIdea() {
        submit(NodeType.IDEA, parentNodeId = null)
    }

    /**
     * 派生投稿（親ノードに紐づくアイデア）
     */
    fun submitDerived() {
        submit(NodeType.IDEA, parentNodeId = parentNode.value?.id)
    }

    /**
     * フォームをリセット
     */
    fun reset() {
        title.value = ""
        content.value = ""
        tags.value = emptyList()
        parentNode.value = null
        isSubmitting.value = false
        error.value = null
        isSuccess.value = false
    }

    private fun submit(type: NodeType, parentNodeId: String?) {
        viewModelScope.launch {
            isSubmitting.value = true
            error.value = null
            isSuccess.value = false

            val result = nodeRepository.createNode(
                title = title.value,
                content = content.value,
                type = type,
                parentNodeId = parentNodeId,
                tags = tags.value
            )

            if (result.isSuccess) {
                result.getOrNull()?.let { node ->
                    nodeStore.addNode(node)
                }
                isSuccess.value = true
            } else {
                error.value = result.exceptionOrNull()?.message ?: "Failed to create post"
            }

            isSubmitting.value = false
        }
    }
}
