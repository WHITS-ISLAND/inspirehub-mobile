package io.github.witsisland.inspirehub.data.mapper

import io.github.witsisland.inspirehub.data.dto.UserDto
import io.github.witsisland.inspirehub.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * UserDto → User への変換
 *
 * 注意: APIのUserDtoにはcreatedAtがないため、現在時刻を使用する
 * 実際の createdAt が必要な場合は、別途APIエンドポイントから取得する必要がある
 */
fun UserDto.toDomain(createdAt: Instant = Clock.System.now()): User {
    return User(
        id = id,
        handle = name,
        roleTag = null, // Phase 2で実装
        createdAt = createdAt
    )
}
