import SwiftUI
import Shared

struct RootView: View {
    @StateObject private var viewModel = AuthViewModelWrapper()

    var body: some View {
        Group {
            if viewModel.isAuthenticated {
                HomeView()
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    }
}

// AuthViewModel をSwiftUIで使えるようにラップ
class AuthViewModelWrapper: ObservableObject {
    private let viewModel: AuthViewModel

    @Published var currentUser: User? = nil
    @Published var isAuthenticated: Bool = false
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    @Published var authUrl: String? = nil

    init() {
        // Koinから取得
        self.viewModel = KoinHelperKt.getAuthViewModel()

        // StateFlowを監視
        observeCurrentUser()
        observeIsAuthenticated()
        observeIsLoading()
        observeError()
        observeAuthUrl()
    }

    private func observeCurrentUser() {
        Task {
            for await value in viewModel.currentUser {
                await MainActor.run {
                    self.currentUser = value
                }
            }
        }
    }

    private func observeIsAuthenticated() {
        Task {
            for await value in viewModel.isAuthenticated {
                await MainActor.run {
                    self.isAuthenticated = value.boolValue
                }
            }
        }
    }

    private func observeIsLoading() {
        Task {
            for await value in viewModel.isLoading {
                await MainActor.run {
                    self.isLoading = value.boolValue
                }
            }
        }
    }

    private func observeError() {
        Task {
            for await value in viewModel.error {
                await MainActor.run {
                    self.error = value as? String
                }
            }
        }
    }

    private func observeAuthUrl() {
        Task {
            for await value in viewModel.authUrl {
                await MainActor.run {
                    self.authUrl = value as? String
                }
            }
        }
    }

    func getGoogleAuthUrl() {
        viewModel.getGoogleAuthUrl()
    }

    func loginWithAuthCode(code: String) {
        viewModel.loginWithAuthCode(code: code)
    }

    func logout() {
        viewModel.logout()
    }

    func clearError() {
        viewModel.clearError()
    }
}
