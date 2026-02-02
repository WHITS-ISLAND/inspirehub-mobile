import SwiftUI
import Shared
import KMPObservableViewModelSwiftUI

struct RootView: View {
    @StateViewModel var viewModel = KoinHelper().getAuthViewModel()

    var body: some View {
        Group {
            if viewModel.isAuthenticated {
                MainTabView()
            } else {
                LoginView(viewModel: viewModel)
            }
        }
    }
}

// MARK: - Preview

#Preview("RootView") {
    RootView()
}
