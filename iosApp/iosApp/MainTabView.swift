import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0
    @State private var showPostTypeSheet = false

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tabItem {
                        Image(systemName: "house.fill")
                        Text("ホーム")
                    }
                    .tag(0)

                MapPlaceholderView()
                    .tabItem {
                        Image(systemName: "map.fill")
                        Text("マップ")
                    }
                    .tag(1)

                MyPagePlaceholderView()
                    .tabItem {
                        Image(systemName: "person.fill")
                        Text("マイページ")
                    }
                    .tag(2)
            }

            // FAB
            Button(action: {
                showPostTypeSheet = true
            }) {
                Image(systemName: "plus")
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .frame(width: 56, height: 56)
                    .background(Color.blue)
                    .clipShape(Circle())
                    .shadow(color: .black.opacity(0.2), radius: 4, x: 0, y: 2)
            }
            .padding(.trailing, 20)
            .padding(.bottom, 80)
        }
        .sheet(isPresented: $showPostTypeSheet) {
            PostTypeSelectionSheet()
        }
    }
}

// MARK: - Placeholder Views

struct MapPlaceholderView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Image(systemName: "map")
                    .font(.system(size: 60))
                    .foregroundColor(.secondary)
                Text("マップ機能は今後実装予定")
                    .foregroundColor(.secondary)
            }
            .navigationTitle("マップ")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct MyPagePlaceholderView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 16) {
                Image(systemName: "person.circle")
                    .font(.system(size: 60))
                    .foregroundColor(.secondary)
                Text("マイページは今後実装予定")
                    .foregroundColor(.secondary)
            }
            .navigationTitle("マイページ")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct PostTypeSelectionSheet: View {
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationView {
            List {
                Button(action: {
                    dismiss()
                }) {
                    HStack {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .foregroundColor(.orange)
                        Text("課題を投稿")
                    }
                }

                Button(action: {
                    dismiss()
                }) {
                    HStack {
                        Image(systemName: "lightbulb.fill")
                            .foregroundColor(.yellow)
                        Text("アイデアを投稿")
                    }
                }
            }
            .navigationTitle("投稿タイプ")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("キャンセル") {
                        dismiss()
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
