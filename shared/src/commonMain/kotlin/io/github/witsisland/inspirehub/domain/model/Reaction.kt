package io.github.witsisland.inspirehub.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * リアクション
 * ノードに対する各ユーザーのリアクション
 */
@Serializable
data class Reaction(
    val id: String,
    val nodeId: String,
    val userId: String,
    val type: ReactionType,
    val createdAt: Instant
)
