package io.github.witsisland.inspirehub.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API Node レスポンス
 */
@Serializable
data class NodeDto(
    val id: String,
    val title: String,
    val content: String,
    val type: String, // "idea" | "issue" | "project"
    @SerialName("author_id")
    val authorId: String,
    @SerialName("author_name")
    val authorName: String? = null,
    @SerialName("author_picture")
    val authorPicture: String? = null,
    val tags: List<TagDto> = emptyList(),
    @SerialName("like_count")
    val likeCount: Int = 0,
    @SerialName("is_liked")
    val isLiked: Boolean = false,
    @SerialName("comment_count")
    val commentCount: Int = 0,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)
