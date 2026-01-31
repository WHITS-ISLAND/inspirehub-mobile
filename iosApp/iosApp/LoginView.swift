import SwiftUI
import SafariServices

struct LoginView: View {
    @ObservedObject var viewModel: AuthViewModelWrapper
    @State private var showingSafari = false

    var body: some View {
        VStack(spacing: 32) {
            Spacer()

            // ロゴ・タイトル
            VStack(spacing: 16) {
                Image(systemName: "lightbulb.fill")
                    .font(.system(size: 80))
                    .foregroundColor(.orange)

                Text("InspireHub")
                    .font(.largeTitle)
                    .fontWeight(.bold)

                Text("社内ハッカソンをもっと楽しく")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()

            // ログインボタン
            Button(action: {
                viewModel.getGoogleAuthUrl()
            }) {
                HStack {
                    Image(systemName: "g.circle.fill")
                    Text("Googleでログイン")
                }
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .cornerRadius(12)
            }
            .disabled(viewModel.isLoading)
            .padding(.horizontal, 32)

            // ローディング表示
            if viewModel.isLoading {
                ProgressView()
                    .padding(.top, 8)
            }

            // エラー表示
            if let error = viewModel.error {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
                    .padding(.horizontal, 32)
                    .padding(.top, 8)
            }

            Spacer()
        }
        .onChange(of: viewModel.authUrl) { newValue in
            if let urlString = newValue, let url = URL(string: urlString) {
                showingSafari = true
            }
        }
        .sheet(isPresented: $showingSafari) {
            if let urlString = viewModel.authUrl, let url = URL(string: urlString) {
                SafariView(url: url) { result in
                    handleAuthCallback(result: result)
                }
            }
        }
    }

    private func handleAuthCallback(result: Result<URL, Error>) {
        showingSafari = false

        switch result {
        case .success(let callbackURL):
            // URLから認可コードを抽出
            if let code = extractAuthCode(from: callbackURL) {
                viewModel.loginWithAuthCode(code: code)
            }
        case .failure(let error):
            print("Safari error: \(error)")
        }
    }

    private func extractAuthCode(from url: URL) -> String? {
        // URL例: yourapp://auth/callback?code=XXXXX
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
              let queryItems = components.queryItems else {
            return nil
        }

        return queryItems.first(where: { $0.name == "code" })?.value
    }
}

// SafariViewControllerをSwiftUIで使うためのラッパー
struct SafariView: UIViewControllerRepresentable {
    let url: URL
    let onDismiss: (Result<URL, Error>) -> Void

    func makeUIViewController(context: Context) -> SFSafariViewController {
        let safari = SFSafariViewController(url: url)
        safari.delegate = context.coordinator
        return safari
    }

    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onDismiss: onDismiss)
    }

    class Coordinator: NSObject, SFSafariViewControllerDelegate {
        let onDismiss: (Result<URL, Error>) -> Void

        init(onDismiss: @escaping (Result<URL, Error>) -> Void) {
            self.onDismiss = onDismiss
        }

        func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
            // ユーザーがキャンセルした場合
            onDismiss(.failure(NSError(domain: "SafariView", code: -1, userInfo: [NSLocalizedDescriptionKey: "User cancelled"])))
        }
    }
}
