import Shared

import SwiftUI

// MARK: - DetailReactionBar

struct DetailReactionBar: View {
    let node: Node
    let isAuthenticated: Bool
    let onLoginRequired: () -> Void
    let onToggleReaction: (ReactionType) -> Void
    @State private var showDerivedPost = false

    var body: some View {
        VStack(spacing: 12) {
            reactionButtons
            deriveButton
        }
    }

    // MARK: - Reaction Buttons

    private var reactionButtons: some View {
        HStack(spacing: 16) {
            reactionButton(
                emoji: "👍",
                label: "いいね",
                count: node.reactions.like.count,
                isReacted: node.reactions.like.isReacted
            ) {
                guard isAuthenticated else {
                    onLoginRequired()
                    return
                }
                onToggleReaction(.like)
            }

            reactionButton(
                emoji: "🔥",
                label: "気になる",
                count: node.reactions.interested.count,
                isReacted: node.reactions.interested.isReacted
            ) {
                guard isAuthenticated else {
                    onLoginRequired()
                    return
                }
                onToggleReaction(.interested)
            }

            reactionButton(
                emoji: "💪",
                label: "やってみたい",
                count: node.reactions.wantToTry.count,
                isReacted: node.reactions.wantToTry.isReacted
            ) {
                guard isAuthenticated else {
                    onLoginRequired()
                    return
                }
                onToggleReaction(.wantToTry)
            }
        }
        .padding(.vertical, 4)
    }

    private func reactionButton(
        emoji: String, label: String, count: Int32, isReacted: Bool, action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            VStack(spacing: 2) {
                Text(emoji)
                    .font(.title3)
                Text(count > 0 ? "\(label) \(count)" : label)
                    .font(.system(size: 10))
                    .foregroundColor(isReacted ? .blue : .secondary)
            }
            .frame(minWidth: 44, minHeight: 44)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel("\(label) \(count)件\(isReacted ? " リアクション済み" : "")")
    }

    // MARK: - Derive Button

    private var deriveButton: some View {
        Button(action: {
            guard isAuthenticated else {
                onLoginRequired()
                return
            }
            showDerivedPost = true
        }) {
            HStack {
                Image(systemName: "plus.bubble")
                Text("派生アイデアを投稿")
            }
            .font(.subheadline)
            .fontWeight(.semibold)
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(12)
            .background(Color.appSecondary)
            .cornerRadius(10)
        }
        .sheet(isPresented: $showDerivedPost) {
            DerivedPostView(parentNode: node)
        }
    }
}

#Preview("DetailReactionBar") {
    DetailReactionBar(
        node: PreviewData.sampleNode,
        isAuthenticated: true,
        onLoginRequired: {},
        onToggleReaction: { _ in }
    )
    .padding(16)
}

#Preview("DetailReactionBar - Not Authenticated") {
    DetailReactionBar(
        node: PreviewData.sampleIssueNode,
        isAuthenticated: false,
        onLoginRequired: {},
        onToggleReaction: { _ in }
    )
    .padding(16)
}
