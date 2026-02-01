import SwiftUI
import Shared
import Combine

class DetailViewModelWrapper: ObservableObject {
    private let viewModel: DetailViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var selectedNode: Node? = nil
    @Published var comments: [Comment] = []
    @Published var childNodes: [Node] = []
    @Published var isLoading: Bool = false
    @Published var error: String? = nil
    @Published var commentText: String = ""
    @Published var isCommentSubmitting: Bool = false

    init() {
        self.viewModel = KoinHelper().getDetailViewModel()
        observeViewModel()
    }

    private func observeViewModel() {
        Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.selectedNode = self.viewModel.selectedNode.value as? Node
                self.comments = self.viewModel.comments.value as? [Comment] ?? []
                self.childNodes = self.viewModel.childNodes.value as? [Node] ?? []
                self.isLoading = self.viewModel.isLoading.value as! Bool
                self.error = self.viewModel.error.value as? String
                self.commentText = self.viewModel.commentText.value as? String ?? ""
                self.isCommentSubmitting = self.viewModel.isCommentSubmitting.value as! Bool
            }
            .store(in: &cancellables)
    }

    func loadDetail(nodeId: String) {
        viewModel.loadDetail(nodeId: nodeId)
    }

    func toggleLike() {
        viewModel.toggleLike()
    }

    func updateCommentText(_ text: String) {
        viewModel.updateCommentText(text: text)
    }

    func submitComment() {
        viewModel.submitComment()
    }

    func selectNode(node: Node) {
        viewModel.selectNode(node: node)
    }
}
