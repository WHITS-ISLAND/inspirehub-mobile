import SwiftUI
import Shared
import Combine

class MapViewModelWrapper: ObservableObject {
    private let viewModel: MapViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var nodes: [Node] = []
    @Published var isLoading: Bool = false
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper().getMapViewModel()
        observeViewModel()
    }

    private func observeViewModel() {
        Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.nodes = self.viewModel.nodes.value as? [Node] ?? []
                self.isLoading = self.viewModel.isLoading.value as! Bool
                self.error = self.viewModel.error.value as? String
            }
            .store(in: &cancellables)
    }

    func loadNodes() {
        viewModel.loadNodes()
    }
}
