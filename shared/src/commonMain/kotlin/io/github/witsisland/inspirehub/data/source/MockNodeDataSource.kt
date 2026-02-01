package io.github.witsisland.inspirehub.data.source

import io.github.witsisland.inspirehub.data.dto.NodeDto
import kotlin.random.Random

/**
 * モック実装のNodeDataSource
 * 開発・テスト用にインメモリでノードデータを管理
 */
class MockNodeDataSource : NodeDataSource {

    private val nodes: MutableList<NodeDto> = mutableListOf()
    private var nextId = 11

    init {
        nodes.addAll(generateMockNodes())
    }

    override suspend fun getNodes(
        type: String?,
        limit: Int,
        offset: Int
    ): List<NodeDto> {
        val filtered = if (type != null) {
            nodes.filter { it.type == type }
        } else {
            nodes.toList()
        }
        return filtered
            .drop(offset)
            .take(limit)
    }

    override suspend fun getNode(id: String): NodeDto {
        return nodes.find { it.id == id }
            ?: throw NoSuchElementException("Node not found: $id")
    }

    override suspend fun createNode(
        title: String,
        content: String,
        type: String,
        tags: List<String>
    ): NodeDto {
        val now = "2026-02-01T12:00:00Z"
        val newNode = NodeDto(
            id = "node_${nextId++}",
            title = title,
            content = content,
            type = type,
            authorId = "user_mock",
            authorName = "テストユーザー",
            authorPicture = null,
            tags = emptyList(),
            likeCount = 0,
            isLiked = false,
            commentCount = 0,
            createdAt = now,
            updatedAt = now
        )
        nodes.add(0, newNode)
        return newNode
    }

    override suspend fun updateNode(
        id: String,
        title: String,
        content: String,
        tags: List<String>
    ): NodeDto {
        val index = nodes.indexOfFirst { it.id == id }
        if (index == -1) throw NoSuchElementException("Node not found: $id")
        val updated = nodes[index].copy(
            title = title,
            content = content,
            updatedAt = "2026-02-01T12:00:00Z"
        )
        nodes[index] = updated
        return updated
    }

    override suspend fun deleteNode(id: String) {
        nodes.removeAll { it.id == id }
    }

    override suspend fun toggleLike(id: String): NodeDto {
        val index = nodes.indexOfFirst { it.id == id }
        if (index == -1) throw NoSuchElementException("Node not found: $id")
        val current = nodes[index]
        val toggled = current.copy(
            isLiked = !current.isLiked,
            likeCount = if (current.isLiked) current.likeCount - 1 else current.likeCount + 1
        )
        nodes[index] = toggled
        return toggled
    }

    private fun generateMockNodes(): List<NodeDto> {
        val random = Random(42)
        return listOf(
            // 課題 5件
            NodeDto(
                id = "node_1",
                title = "通勤時間の有効活用ができていない",
                content = "毎日の通勤で往復2時間を費やしているが、満員電車でスマホを見る程度しかできていない。この時間をもっと有意義に使いたい。",
                type = "issue",
                authorId = "user_1",
                authorName = "田中太郎",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-20T09:00:00Z",
                updatedAt = "2026-01-20T09:00:00Z"
            ),
            NodeDto(
                id = "node_2",
                title = "地域の高齢者の孤立問題",
                content = "近所に一人暮らしの高齢者が増えている。買い物や病院への移動手段がなく、社会的に孤立しているケースが多い。",
                type = "issue",
                authorId = "user_2",
                authorName = "佐藤花子",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-19T14:30:00Z",
                updatedAt = "2026-01-19T14:30:00Z"
            ),
            NodeDto(
                id = "node_3",
                title = "フードロスが多すぎる",
                content = "スーパーやコンビニで毎日大量の食品が廃棄されている。まだ食べられる食品を必要な人に届ける仕組みが必要。",
                type = "issue",
                authorId = "user_3",
                authorName = "鈴木一郎",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-18T11:00:00Z",
                updatedAt = "2026-01-18T11:00:00Z"
            ),
            NodeDto(
                id = "node_4",
                title = "子どもの学習格差",
                content = "家庭の経済状況によって子どもの学習機会に大きな差が生まれている。塾に通えない子どもたちへのサポートが不足している。",
                type = "issue",
                authorId = "user_4",
                authorName = "高橋美咲",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-17T16:00:00Z",
                updatedAt = "2026-01-17T16:00:00Z"
            ),
            NodeDto(
                id = "node_5",
                title = "リモートワークでのコミュニケーション不足",
                content = "在宅勤務が増えてチーム内の雑談が減った。業務連絡だけでは信頼関係が築きにくく、新人の定着率にも影響が出ている。",
                type = "issue",
                authorId = "user_5",
                authorName = "山田健二",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-16T10:00:00Z",
                updatedAt = "2026-01-16T10:00:00Z"
            ),
            // アイデア 5件（うち2件は派生アイデア）
            NodeDto(
                id = "node_6",
                title = "音声学習アプリで通勤時間を活用",
                content = "AIが興味関心に合わせて学習コンテンツを音声で読み上げてくれるアプリ。満員電車でも耳だけで学べる。ポッドキャスト形式で5分単位のレッスン。",
                type = "idea",
                authorId = "user_2",
                authorName = "佐藤花子",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-21T08:00:00Z",
                updatedAt = "2026-01-21T08:00:00Z"
            ),
            NodeDto(
                id = "node_7",
                title = "シニア向け相乗りマッチングサービス",
                content = "近所の住民同士で買い物や通院の相乗りをマッチングするアプリ。高齢者の移動支援と地域コミュニティの活性化を同時に実現。",
                type = "idea",
                authorId = "user_1",
                authorName = "田中太郎",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-20T15:00:00Z",
                updatedAt = "2026-01-20T15:00:00Z"
            ),
            NodeDto(
                id = "node_8",
                title = "余剰食品のリアルタイムマッチング",
                content = "店舗の余剰食品をリアルタイムで近隣のフードバンクやNPOに通知・マッチングするプラットフォーム。廃棄前に必要な人へ届ける。",
                type = "idea",
                authorId = "user_3",
                authorName = "鈴木一郎",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-19T20:00:00Z",
                updatedAt = "2026-01-19T20:00:00Z"
            ),
            // 派生アイデア（node_1 の通勤課題から派生）
            NodeDto(
                id = "node_9",
                title = "通勤中にできるマイクロタスクワーク",
                content = "node_1の課題に対する別アプローチ。通勤時間にスマホで完結する短いタスク（翻訳チェック、画像ラベリング等）をこなして副収入を得る仕組み。",
                type = "idea",
                authorId = "user_4",
                authorName = "高橋美咲",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-22T07:30:00Z",
                updatedAt = "2026-01-22T07:30:00Z"
            ),
            // 派生アイデア（node_2 の高齢者孤立から派生）
            NodeDto(
                id = "node_10",
                title = "オンライン茶話会プラットフォーム",
                content = "node_2の課題に対して。高齢者向けにワンタップでビデオ通話に参加できる超シンプルなアプリ。地域のボランティアが話し相手として参加。",
                type = "idea",
                authorId = "user_5",
                authorName = "山田健二",
                authorPicture = null,
                tags = emptyList(),
                likeCount = random.nextInt(31),
                isLiked = false,
                commentCount = random.nextInt(31),
                createdAt = "2026-01-21T18:00:00Z",
                updatedAt = "2026-01-21T18:00:00Z"
            )
        )
    }
}
