import SwiftUI
import Shared
import Combine

enum HomeTabUI: String, CaseIterable {
    case latest = "新着"
    case issue = "課題"
    case idea = "アイデア"
    case mine = "自分"

    var kotlinTab: HomeTab {
        switch self {
        case .latest: return .recent
        case .issue: return .issues
        case .idea: return .ideas
        case .mine: return .mine
        }
    }
}

enum SortOrderUI: String, CaseIterable {
    case newest = "新しい順"
    case popular = "人気順"

    var kotlinOrder: Shared.SortOrder {
        switch self {
        case .newest: return .recent
        case .popular: return .popular
        }
    }
}

class HomeViewModelWrapper: ObservableObject {
    private let viewModel: HomeViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var nodes: [Node] = []
    @Published var isLoading: Bool = false
    @Published var currentTab: HomeTabUI = .latest
    @Published var sortOrder: SortOrderUI = .newest
    @Published var error: String? = nil

    init() {
        self.viewModel = KoinHelper().getHomeViewModel()
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

                if let kotlinTab = self.viewModel.currentTab.value as? HomeTab {
                    switch kotlinTab {
                    case .recent: self.currentTab = .latest
                    case .issues: self.currentTab = .issue
                    case .ideas: self.currentTab = .idea
                    case .mine: self.currentTab = .mine
                    default: break
                    }
                }

                if let kotlinOrder = self.viewModel.sortOrder.value as? Shared.SortOrder {
                    switch kotlinOrder {
                    case .recent: self.sortOrder = .newest
                    case .popular: self.sortOrder = .popular
                    default: break
                    }
                }
            }
            .store(in: &cancellables)
    }

    func loadNodes() {
        viewModel.loadNodes(forceRefresh: false)
    }

    func refresh() {
        viewModel.refresh()
    }

    func setTab(_ tab: HomeTabUI) {
        currentTab = tab
        viewModel.setTab(tab: tab.kotlinTab)
    }

    func setSortOrder(_ order: SortOrderUI) {
        sortOrder = order
        viewModel.setSortOrder(order: order.kotlinOrder)
    }

    func toggleLike(nodeId: String) {
        viewModel.toggleLike(nodeId: nodeId)
    }
}
