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

                let newNodes: [Node]
                if let arr = self.viewModel.nodes.value as? NSArray {
                    newNodes = arr.compactMap { $0 as? Node }
                } else {
                    newNodes = self.viewModel.nodes.value as? [Node] ?? []
                }
                if self.nodes.count != newNodes.count || self.nodes.map(\.id) != newNodes.map(\.id) {
                    self.nodes = newNodes
                }

                let newIsLoading = self.viewModel.isLoading.value as! Bool
                if self.isLoading != newIsLoading { self.isLoading = newIsLoading }

                let newError = self.viewModel.error.value as? String
                if self.error != newError { self.error = newError }
            }
            .store(in: &cancellables)
    }

    func loadNodes() {
        viewModel.loadNodes()
    }

    func getNodeTree() -> [NodeTreeItem] {
        return viewModel.getNodeTree() as? [NodeTreeItem] ?? []
    }
}
