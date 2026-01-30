package io.github.witsisland.inspirehub.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * コメント
 * ノードに紐づくコメント
 * parentId が設定されている場合、そのコメントへの返信となる
 */
@Serializable
data class Comment(
    val id: String,
    val nodeId: String,
    val parentId: String? = null, // 返信先コメントID（ネストコメント用）
    val authorId: String,
    val content: String,
    val createdAt: Instant
)
