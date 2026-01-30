package io.github.witsisland.inspirehub.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * ユーザー
 * 投稿やリアクションの主体となるユーザー
 */
@Serializable
data class User(
    val id: String,
    val handle: String,
    val roleTag: String? = null, // "Backend", "Frontend", "Designer" など（Phase 2〜）
    val createdAt: Instant
)
