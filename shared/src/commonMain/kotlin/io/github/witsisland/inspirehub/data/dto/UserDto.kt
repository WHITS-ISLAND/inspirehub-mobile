package io.github.witsisland.inspirehub.data.dto

import kotlinx.serialization.Serializable

/**
 * API User レスポンス
 */
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val picture: String? = null
)
