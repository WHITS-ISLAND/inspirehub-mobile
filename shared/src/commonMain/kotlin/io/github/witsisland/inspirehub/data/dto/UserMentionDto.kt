package io.github.witsisland.inspirehub.data.dto

import kotlinx.serialization.Serializable

/**
 * API User Mention レスポンス（コメント内のメンション用）
 */
@Serializable
data class UserMentionDto(
    val id: String,
    val name: String,
    val picture: String? = null
)
