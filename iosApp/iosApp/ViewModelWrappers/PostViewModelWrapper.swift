import SwiftUI
import Shared
import Combine

class PostViewModelWrapper: ObservableObject {
    private let viewModel: PostViewModel
    private var cancellables = Set<AnyCancellable>()

    @Published var title: String = ""
    @Published var content: String = ""
    @Published var tags: [String] = []
    @Published var parentNode: Node? = nil
    @Published var isSubmitting: Bool = false
    @Published var error: String? = nil
    @Published var isSuccess: Bool = false

    init() {
        self.viewModel = KoinHelper().getPostViewModel()
        observeViewModel()
    }

    private func observeViewModel() {
        Timer.publish(every: 0.1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                self.title = self.viewModel.title.value as! String
                self.content = self.viewModel.content.value as! String
                self.tags = self.viewModel.tags.value as! [String]
                self.parentNode = self.viewModel.parentNode.value as? Node
                self.isSubmitting = self.viewModel.isSubmitting.value as! Bool
                self.error = self.viewModel.error.value as? String
                self.isSuccess = self.viewModel.isSuccess.value as! Bool
            }
            .store(in: &cancellables)
    }

    func updateTitle(_ value: String) {
        viewModel.updateTitle(value: value)
    }

    func updateContent(_ value: String) {
        viewModel.updateContent(value: value)
    }

    func submitIssue() {
        viewModel.submitIssue()
    }

    func submitIdea() {
        viewModel.submitIdea()
    }

    func submitDerived() {
        viewModel.submitDerived()
    }

    func reset() {
        viewModel.reset()
    }
}
