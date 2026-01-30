package io.github.witsisland.inspirehub.domain.model

import kotlinx.serialization.Serializable

/**
 * リアクションの種類
 * - LIKE: 👍 いいね
 * - INSIGHT: 💡 共感
 * - CURIOUS: 👀 気になる
 * - DEV_INTEREST: 🤝 作ってみたい
 */
@Serializable
enum class ReactionType {
    LIKE,
    INSIGHT,
    CURIOUS,
    DEV_INTEREST
}
