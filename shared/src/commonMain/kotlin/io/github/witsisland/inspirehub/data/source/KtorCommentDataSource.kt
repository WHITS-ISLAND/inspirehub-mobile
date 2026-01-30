package io.github.witsisland.inspirehub.data.source

import io.github.witsisland.inspirehub.data.dto.CommentDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Ktor Client を使用した CommentDataSource 実装
 */
class KtorCommentDataSource(
    private val httpClient: HttpClient
) : CommentDataSource {

    override suspend fun getComments(nodeId: String): List<CommentDto> {
        return httpClient.get("/nodes/$nodeId/comments").body()
    }

    override suspend fun getComment(id: String): CommentDto {
        return httpClient.get("/comments/$id").body()
    }

    override suspend fun createComment(
        nodeId: String,
        content: String,
        parentId: String?
    ): CommentDto {
        return httpClient.post("/nodes/$nodeId/comments") {
            contentType(ContentType.Application.Json)
            setBody(buildMap {
                put("content", content)
                parentId?.let { put("parent_id", it) }
            })
        }.body()
    }

    override suspend fun updateComment(
        id: String,
        content: String
    ): CommentDto {
        return httpClient.put("/comments/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content))
        }.body()
    }

    override suspend fun deleteComment(id: String) {
        httpClient.delete("/comments/$id")
    }
}
