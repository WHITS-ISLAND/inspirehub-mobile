package io.github.witsisland.inspirehub.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.witsisland.inspirehub.domain.model.Comment
import io.github.witsisland.inspirehub.domain.model.Node
import io.github.witsisland.inspirehub.domain.model.NodeType
import io.github.witsisland.inspirehub.presentation.viewmodel.DetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    nodeId: String,
    onNodeClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val selectedNode by viewModel.selectedNode.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val childNodes by viewModel.childNodes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val isCommentSubmitting by viewModel.isCommentSubmitting.collectAsState()

    LaunchedEffect(nodeId) {
        viewModel.loadDetail(nodeId = nodeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("詳細") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                isLoading && selectedNode == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null && selectedNode == null -> {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                selectedNode != null -> {
                    val node = selectedNode!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Header
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (node.type == NodeType.ISSUE) Icons.Default.Warning else Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = if (node.type == NodeType.ISSUE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (node.type == NodeType.ISSUE) "課題" else "アイデア",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (node.type == NodeType.ISSUE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = node.title, style = MaterialTheme.typography.headlineMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = node.content, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = node.authorId,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Divider(modifier = Modifier.padding(top = 16.dp))
                            }
                        }

                        // Child nodes
                        if (childNodes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "派生ノード",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                            items(childNodes, key = { it.id }) { child ->
                                ChildNodeRow(node = child, onClick = { onNodeClick(child.id) })
                            }
                            item { Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
                        }

                        // Comments header
                        item {
                            Text(
                                text = "コメント",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }

                        // Comment input
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { viewModel.updateCommentText(text = it) },
                                    placeholder = { Text("コメントを入力...") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                )
                                IconButton(
                                    onClick = { viewModel.submitComment() },
                                    enabled = commentText.isNotBlank() && !isCommentSubmitting,
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "送信")
                                }
                            }
                        }

                        // Comment list
                        if (comments.isEmpty()) {
                            item {
                                Text(
                                    text = "まだコメントはありません",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                        } else {
                            items(comments, key = { it.id }) { comment ->
                                CommentRow(comment = comment)
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildNodeRow(node: Node, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = node.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = comment.authorId,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
    }
}
