import KMPObservableViewModelSwiftUI

import Shared

import SwiftUI

// MARK: - ReactionUsersView

/// リアクションユーザー一覧画面
///
/// 指定されたノードのリアクション種別に対してリアクションしたユーザーの一覧を表示する。
/// カーソルベースのページネーションで無限スクロールをサポートする。
struct ReactionUsersView: View {
    /// 表示するノードのID
    let nodeId: String
    /// 表示するリアクション種別
    let reactionType: ReactionType
    /// 画面を閉じる
    @Environment(\.dismiss) private var dismiss
    /// リアクションユーザー一覧のViewModel
    @StateViewModel var viewModel = KoinHelper().getReactionUsersViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading == true && viewModel.users.isEmpty {
                    ProgressView("読み込み中...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let error = viewModel.error, viewModel.users.isEmpty {
                    errorView(message: error)
                } else if viewModel.users.isEmpty {
                    emptyView
                } else {
                    userList
                }
            }
            .navigationTitle(navigationTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("閉じる") {
                        dismiss()
                    }
                }
            }
        }
        .onAppear {
            viewModel.loadUsers(nodeId: nodeId, type: reactionType)
        }
    }

    // MARK: - Navigation Title

    private var navigationTitle: String {
        let emoji = reactionType.emoji
        let label = reactionType.label
        let total = viewModel.total
        return total > 0 ? "\(emoji) \(label) \(total)人" : "\(emoji) \(label)"
    }

    // MARK: - User List

    private var userList: some View {
        List {
            ForEach(viewModel.users, id: \.userId) { user in
                ReactedUserRow(user: user)
                    .onAppear {
                        if user.userId == viewModel.users.last?.userId {
                            viewModel.loadMore()
                        }
                    }
            }

            if viewModel.isLoadingMore == true {
                HStack {
                    Spacer()
                    ProgressView()
                    Spacer()
                }
                .listRowSeparator(.hidden)
            }
        }
        .listStyle(.plain)
    }

    // MARK: - Empty View

    private var emptyView: some View {
        VStack(spacing: 12) {
            Text(reactionType.emoji)
                .font(.system(size: 48))
            Text("まだリアクションがありません")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // MARK: - Error View

    private func errorView(message: String) -> some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 40))
                .foregroundColor(.secondary)
            Text(message)
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
            Button("再読み込み") {
                viewModel.loadUsers(nodeId: nodeId, type: reactionType)
            }
            .buttonStyle(.bordered)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - ReactedUserRow

/// リアクションユーザーの行コンポーネント
private struct ReactedUserRow: View {
    /// 表示するユーザー
    let user: ReactedUser

    var body: some View {
        HStack(spacing: 12) {
            UserAvatarView(pictureURL: user.userPicture, size: 40)

            VStack(alignment: .leading, spacing: 2) {
                Text(user.userName ?? "名前未設定")
                    .font(.subheadline)
                    .fontWeight(.medium)

                Text(formattedDate(user.reactedAt))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Spacer()
        }
        .padding(.vertical, 4)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(user.userName ?? "名前未設定")がリアクションしました")
    }

    private func formattedDate(_ isoString: String) -> String {
        let formatter = ISO8601DateFormatter()
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        if let date = formatter.date(from: isoString) {
            let display = RelativeDateTimeFormatter()
            display.locale = Locale(identifier: "ja_JP")
            return display.localizedString(for: date, relativeTo: Date())
        }
        // fractionalSecondsなしでも試す
        formatter.formatOptions = [.withInternetDateTime]
        if let date = formatter.date(from: isoString) {
            let display = RelativeDateTimeFormatter()
            display.locale = Locale(identifier: "ja_JP")
            return display.localizedString(for: date, relativeTo: Date())
        }
        return isoString
    }
}

// MARK: - ReactionType Extension

extension ReactionType {
    /// リアクション種別の表示ラベル
    var label: String {
        switch self {
        case .like: return "いいね"
        case .interested: return "気になる"
        case .wantToTry: return "やってみたい"
        default: return "リアクション"
        }
    }

    /// リアクション種別の絵文字
    var emoji: String {
        switch self {
        case .like: return "👍"
        case .interested: return "🔥"
        case .wantToTry: return "💪"
        default: return "👍"
        }
    }
}

// MARK: - Identifiable Wrapper

/// シート表示用のReactionTypeラッパー
struct ReactionTypeItem: Identifiable {
    /// ユニークID
    let id: String
    /// リアクション種別
    let reactionType: ReactionType
    /// 対象ノードID
    let nodeId: String

    init(reactionType: ReactionType, nodeId: String) {
        self.id = "\(nodeId)-\(reactionType)"
        self.reactionType = reactionType
        self.nodeId = nodeId
    }
}

// MARK: - Previews

#Preview("ReactionUsersView - いいね") {
    ReactionUsersView(
        nodeId: "preview-1",
        reactionType: .like
    )
}

#Preview("ReactionUsersView - 気になる") {
    ReactionUsersView(
        nodeId: "preview-1",
        reactionType: .interested
    )
}
