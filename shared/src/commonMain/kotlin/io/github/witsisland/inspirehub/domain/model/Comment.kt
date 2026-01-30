package io.github.witsisland.inspirehub.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * コメント
 * ノードに紐づくコメント
 */
@Serializable
data class Comment(
    val id: String,
    val nodeId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant
)
