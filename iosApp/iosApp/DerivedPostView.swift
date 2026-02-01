import SwiftUI
import Shared

struct DerivedPostView: View {
    let parentNode: Node
    @StateObject private var viewModel = PostViewModelWrapper()
    @Environment(\.dismiss) private var dismiss

    @State private var tagInput: String = ""

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("派生元")) {
                    HStack {
                        Image(systemName: parentNode.type == .issue
                              ? "exclamationmark.triangle.fill"
                              : "lightbulb.fill")
                            .foregroundColor(parentNode.type == .issue ? .orange : .yellow)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(parentNode.title)
                                .font(.subheadline)
                                .fontWeight(.semibold)
                            Text(parentNode.content)
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .lineLimit(2)
                        }
                    }
                    .padding(.vertical, 4)
                }

                Section(header: Text("タイトル")) {
                    TextField("派生アイデアのタイトルを入力", text: Binding(
                        get: { viewModel.title },
                        set: { viewModel.updateTitle($0) }
                    ))
                }

                Section(header: Text("本文")) {
                    TextEditor(text: Binding(
                        get: { viewModel.content },
                        set: { viewModel.updateContent($0) }
                    ))
                    .frame(minHeight: 150)
                }

                Section(header: Text("タグ")) {
                    HStack {
                        TextField("タグを入力", text: $tagInput)
                            .onSubmit {
                                addTag()
                            }
                        Button(action: addTag) {
                            Image(systemName: "plus.circle.fill")
                                .foregroundColor(.blue)
                        }
                        .disabled(tagInput.trimmingCharacters(in: .whitespaces).isEmpty)
                    }

                    if !viewModel.tags.isEmpty {
                        FlowLayout(tags: viewModel.tags) { tag in
                            TagChip(text: tag)
                        }
                    }
                }

                if let error = viewModel.error {
                    Section {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                }
            }
            .navigationTitle("派生アイデアを投稿")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("キャンセル") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("投稿") {
                        viewModel.submitDerived()
                    }
                    .disabled(viewModel.title.trimmingCharacters(in: .whitespaces).isEmpty || viewModel.isSubmitting)
                }
            }
            .overlay {
                if viewModel.isSubmitting {
                    ProgressView("投稿中...")
                        .padding()
                        .background(Color(.systemBackground).opacity(0.9))
                        .cornerRadius(12)
                }
            }
            .onAppear {
                viewModel.parentNode = parentNode
            }
            .onChange(of: viewModel.isSuccess) { newValue in
                if newValue {
                    dismiss()
                }
            }
        }
    }

    private func addTag() {
        let trimmed = tagInput.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return }
        viewModel.tags.append(trimmed)
        tagInput = ""
    }
}
