---
description: Rules for editing Swift/SwiftUI files in the iOS app
globs: ["iosApp/**/*.swift"]
---

# iOS Swift 開発ルール

## ターゲット: iOS 18+（可能な限りiOS 26）

## 禁止API（使ったら即修正）
- `NavigationView` → `NavigationStack` を使え
- `@StateObject` (KMP ViewModel用) → `@StateViewModel` を使え
- `@ObservedObject` (KMP ViewModel用) → `@ObservedViewModel` を使え
- `.onChange(of:) { newValue in }` → `.onChange(of:) { oldValue, newValue in }` を使え

## ViewModelWrapper禁止
- ObservableObject Wrapperクラスを作るな
- KMP-ObservableViewModelでKotlin VMを直接使え
- `import KMPObservableViewModelSwiftUI` を使え
- Timer.publishによるポーリング監視は禁止

## ナビゲーション
- `NavigationStack` を使え（`NavigationView` は非推奨）
- `NavigationLink(destination:)` より `NavigationLink(value:)` + `.navigationDestination` を推奨

## コードレビュー時のチェックリスト
1. NavigationView が使われていないか
2. ViewModelWrapper/Timer.publish が使われていないか
3. @StateObject が KMP ViewModel に使われていないか（@StateViewModel を使うべき）
4. iOS 16以降の非推奨Warning がないか
5. SwiftUI Preview が壊れていないか
