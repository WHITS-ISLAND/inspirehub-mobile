package io.github.witsisland.inspirehub.domain.model

import kotlinx.serialization.Serializable

/**
 * ノードの種類
 * - ISSUE: 課題（解決したい問題や作りたいもの）
 * - IDEA: アイデア（解決策または単独のアイデア）
 */
@Serializable
enum class NodeType {
    ISSUE,
    IDEA
}
