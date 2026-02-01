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

                let newUser = self.viewModel.currentUser.value as? User
                if self.currentUser?.id != newUser?.id { self.currentUser = newUser }

                let newNodes = self.viewModel.myNodes.value as? [Node] ?? []
                if self.myNodes.count != newNodes.count || self.myNodes.map(\.id) != newNodes.map(\.id) {
                    self.myNodes = newNodes
                }

                let newIsLoading = self.viewModel.isLoading.value as! Bool
                if self.isLoading != newIsLoading { self.isLoading = newIsLoading }

                let newError = self.viewModel.error.value as? String
                if self.error != newError { self.error = newError }
            }
            .store(in: &cancellables)
    }

    func loadMyNodes() {
        viewModel.loadMyNodes()
    }
}
