import SwiftUI
import Shared
import Combine

class MyPageViewModelWrapper: ObservableObject {
    private let viewModel: MyPageViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var currentUser: User? = nil
    @Published var myNodes: [Node] = []
    @Published var isLoading: Bool = false
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper().getMyPageViewModel()
        observeViewModel()
    }

    private func observeViewModel() {
        Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.currentUser = self.viewModel.currentUser.value as? User
                self.myNodes = self.viewModel.myNodes.value as? [Node] ?? []
                self.isLoading = self.viewModel.isLoading.value as! Bool
                self.error = self.viewModel.error.value as? String
            }
            .store(in: &cancellables)
    }

    func loadMyNodes() {
        viewModel.loadMyNodes()
    }
}
