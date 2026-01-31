import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Koin初期化
        KoinInitializerKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}