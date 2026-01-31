import SwiftUI
import Shared

struct HomeView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // ユーザー情報表示
                if let user = viewModel.currentUser {
                    VStack(spacing: 8) {
                        Image(systemName: "person.circle.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.blue)

                        Text(user.handle)
                            .font(.title2)
                            .fontWeight(.bold)

                        if let roleTag = user.roleTag {
                            Text(roleTag)
                                .font(.caption)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 4)
                                .background(Color.blue.opacity(0.1))
                                .cornerRadius(8)
                        }
                    }
                    .padding(.top, 40)
                }

                Spacer()

                // 仮の画面説明
                VStack(spacing: 16) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.green)

                    Text("ログイン成功！")
                        .font(.title)
                        .fontWeight(.bold)

                    Text("ホーム画面は今後実装予定です")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }

                Spacer()

                // ログアウトボタン
                Button(action: {
                    viewModel.logout()
                }) {
                    HStack {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                        Text("ログアウト")
                    }
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.red)
                    .cornerRadius(12)
                }
                .disabled(viewModel.isLoading)
                .padding(.horizontal, 32)

                if viewModel.isLoading {
                    ProgressView()
                        .padding(.top, 8)
                }
            }
            .navigationTitle("InspireHub")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
