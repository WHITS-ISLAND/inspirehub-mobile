package io.github.witsisland.inspirehub.data.mapper

import io.github.witsisland.inspirehub.data.dto.CommentDto
import io.github.witsisland.inspirehub.domain.model.Comment
import kotlinx.datetime.Instant

/**
 * CommentDto → Comment への変換
 */
fun CommentDto.toDomain(): Comment {
    return Comment(
        id = id,
        nodeId = nodeId,
        parentId = parentId,
        authorId = authorId,
        content = content,
        createdAt = Instant.parse(createdAt)
    )
}
