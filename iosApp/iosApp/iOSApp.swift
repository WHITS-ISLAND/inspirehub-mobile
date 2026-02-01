import SwiftUI
import Shared
import GoogleSignIn

@main
struct iOSApp: App {
    init() {
        // Koin初期化
        KoinInitializerKt.doInitKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .onOpenURL { url in
                    // Google Sign-In のコールバックをハンドル
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}