# CI/CD Strategy: KMP + Xcode Cloud

> **Project**: InspireHub Mobile (KMP + SwiftUI)
> **Date**: 2026-02-02
> **Status**: 調査完了・推奨案あり

## 1. 問題の背景

Xcode CloudでKMP（Kotlin Multiplatform）プロジェクトをビルドする際、**JDK/Javaがプリインストールされていない**ため、GradleによるKotlinビルドが失敗する。

### 現在のプロジェクト構成

```
inspirehub-mobile/
├── shared/              # KMP共有コード（Kotlin）
│   └── build.gradle.kts # iosArm64 + iosSimulatorArm64 → "Shared" framework
├── iosApp/              # SwiftUI iOSアプリ
├── composeApp/          # Compose Multiplatform（Android）
├── build.gradle.kts     # ルート
└── settings.gradle.kts
```

- **shared module**: `binaries.framework { baseName = "Shared"; isStatic = true }` で iOS向けにスタティックフレームワークを生成
- **依存関係**: Ktor, Koin, KMP-ObservableViewModel, kotlinx-datetime, kotlinx-serialization
- **JVM Target**: JDK 11（Android向け）

### Xcode Cloudの制約

| 項目 | 状態 |
|------|------|
| JDK プリインストール | **なし** |
| Homebrew | プリインストール済み |
| sudo | **使用不可** |
| ci_scripts | `ci_post_clone.sh` でカスタムスクリプト実行可能 |
| 無料枠 | Apple Developer Program加入者に月25時間 |

## 2. 対応策の比較

### 案A: ci_post_clone.sh で JDK インストール（最もシンプル）

Xcode Cloudの `ci_scripts/ci_post_clone.sh` でHomebrewからJDKをインストールし、そのままGradleビルドを実行する。

```bash
#!/bin/sh
set -e

# JDK 17 インストール
brew install openjdk@17
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"

# Gradle キャッシュ（任意）
cd "$CI_PRIMARY_REPOSITORY_PATH"
./gradlew :shared:linkReleaseFrameworkIosArm64 \
          :shared:linkReleaseFrameworkIosSimulatorArm64
```

| 観点 | 評価 |
|------|------|
| **実装難易度** | ★☆☆ 低（1ファイル追加のみ） |
| **ビルド時間** | ★★★ 遅（毎回JDKインストール+Gradleフルビルドで10-15分追加） |
| **保守性** | ★★☆ 中（JDKバージョン管理がci_scriptsに分散） |
| **費用** | ★★☆ 中（Xcode Cloud無料枠を消費、25h/月） |
| **信頼性** | ★★☆ 中（Homebrew依存、ネットワーク依存） |

**メリット**: セットアップが最も簡単。追加サービス不要。
**デメリット**: 毎回のJDKダウンロードが遅い。Xcode Cloudの無料枠を大きく消費。Homebrew側の変更で壊れるリスク。

---

### 案B: GitHub Actions で XCFramework 事前ビルド + Xcode Cloud で iOS ビルド（推奨）

KMP共有コードのビルドをGitHub Actionsに分離し、XCFrameworkをアーティファクトとして配布。Xcode CloudはXCFrameworkを取得してiOSアプリのビルドのみ行う。

**GitHub Actions側**:
```yaml
# .github/workflows/build-xcframework.yml
name: Build Shared XCFramework
on:
  push:
    branches: [main, develop]
    paths: ['shared/**', 'gradle/**', '*.gradle.kts']

jobs:
  build:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Build XCFramework
        run: ./gradlew :shared:assembleSharedXCFramework
      - name: Upload XCFramework
        uses: actions/upload-artifact@v4
        with:
          name: SharedXCFramework
          path: shared/build/XCFrameworks/release/Shared.xcframework
```

**build.gradle.kts への追加**（shared/build.gradle.kts）:
```kotlin
kotlin {
    val xcf = XCFramework("Shared")
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            xcf.add(this)
        }
    }
}
```

**Xcode Cloud側（ci_post_clone.sh）**:
```bash
#!/bin/sh
set -e
# GitHub Releasesから事前ビルド済みXCFrameworkを取得
curl -L -o Shared.xcframework.zip \
  "https://github.com/{org}/{repo}/releases/latest/download/Shared.xcframework.zip"
unzip Shared.xcframework.zip -d "$CI_PRIMARY_REPOSITORY_PATH/iosApp/Frameworks/"
```

| 観点 | 評価 |
|------|------|
| **実装難易度** | ★★☆ 中（GitHub Actions workflow + build.gradle.kts変更 + ci_scripts） |
| **ビルド時間** | ★☆☆ 速（Xcode Cloud側はJDK不要、XCFramework DLのみ） |
| **保守性** | ★★☆ 中（2つのCIを管理する必要あり） |
| **費用** | ★☆☆ 安（GitHub Actions無料枠2000分/月 + Xcode Cloud最小利用） |
| **信頼性** | ★☆☆ 高（各CIが得意分野のみ担当） |

**メリット**: ビルド速度が最速。JDKはGitHub Actionsで標準サポート。Xcode Cloudの無料枠を温存。
**デメリット**: 2つのCIパイプラインの管理が必要。shared変更→iOSビルドの間にラグが生じる可能性。

---

### 案C: KMMBridge + SPM でバイナリ配布

Touchlab製のKMMBridgeを使い、KMP共有コードをSwift Package Manager経由でバイナリ配布する。

```kotlin
// shared/build.gradle.kts
plugins {
    id("co.touchlab.kmmbridge") version "1.3.0"
}

kmmbridge {
    githubReleaseArtifacts()
    githubReleaseVersions()
    versionPrefix.set("1.0")
    spm()
}
```

Xcode側でSPMパッケージとしてSharedを追加。Xcode CloudはSPMの依存解決だけでビルド可能になる。

| 観点 | 評価 |
|------|------|
| **実装難易度** | ★★★ 高（KMMBridge導入 + SPM移行 + Xcodeプロジェクト再構成） |
| **ビルド時間** | ★☆☆ 速（Xcode Cloud側はSPM解決のみ） |
| **保守性** | ★☆☆ 高（バージョン管理が自動化、SPMは標準ツール） |
| **費用** | ★☆☆ 安 |
| **信頼性** | ★★☆ 中（KMMBridgeへの依存が生じる） |

**メリット**: 長期的に最もクリーン。SPMはApple推奨。バージョン管理が自動化される。
**デメリット**: 初期導入コストが高い。Xcodeプロジェクトの再構成が必要。KMMBridge自体の学習コスト。

---

### 案D: Xcode Cloud をやめて GitHub Actions に統一

iOSビルド・TestFlight配布もGitHub Actionsで行う。

```yaml
# .github/workflows/ios-build.yml
jobs:
  build-ios:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: 'temurin', java-version: '17' }
      - name: Build Shared Framework
        run: ./gradlew :shared:linkReleaseFrameworkIosArm64
      - name: Build iOS App
        run: xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -sdk iphoneos build
      - name: Upload to TestFlight
        uses: apple-actions/upload-testflight-build@v1
        with:
          app-path: path/to/iosApp.ipa
          issuer-id: ${{ secrets.APPSTORE_ISSUER_ID }}
          api-key-id: ${{ secrets.APPSTORE_KEY_ID }}
          api-private-key: ${{ secrets.APPSTORE_PRIVATE_KEY }}
```

| 観点 | 評価 |
|------|------|
| **実装難易度** | ★★☆ 中（証明書・プロビジョニング管理をGitHub Secretsで行う必要あり） |
| **ビルド時間** | ★★☆ 中（1つのパイプラインで全て実行） |
| **保守性** | ★☆☆ 高（CI設定が1箇所に集約） |
| **費用** | ★★☆ 中（macOSランナーは10倍消費: 2000分→実質200分/月） |
| **信頼性** | ★★☆ 中（証明書管理が手動になる） |

**メリット**: CI管理が1箇所。JDK問題なし。全ステップが.yamlで定義される。
**デメリット**: macOSランナーの無料枠消費が激しい（10x倍率）。証明書管理がXcode Cloudより複雑。Apple APIキーの管理が必要。

## 3. 比較サマリ

| 案 | 実装難易度 | ビルド速度 | 費用 | 保守性 | 信頼性 |
|----|-----------|-----------|------|--------|--------|
| **A: ci_post_clone.sh** | ★低 | ★★★遅 | ★★中 | ★★中 | ★★中 |
| **B: GitHub Actions + Xcode Cloud** | ★★中 | ★速 | ★安 | ★★中 | ★高 |
| **C: KMMBridge + SPM** | ★★★高 | ★速 | ★安 | ★高 | ★★中 |
| **D: GitHub Actions統一** | ★★中 | ★★中 | ★★中 | ★高 | ★★中 |

## 4. 推奨案

### Phase 1（今すぐ）: 案A → 最小コストで動かす

まずは`ci_post_clone.sh`でJDKインストール方式を採用し、Xcode Cloudで動く状態を作る。Phase 1はモック中心なので高速ビルドは不要。

```
iosApp/ci_scripts/ci_post_clone.sh  ← 追加（1ファイルのみ）
```

### Phase 2（API連携後）: 案B → ハイブリッドCI/CDへ移行

本番運用が近づいたら、GitHub Actions + Xcode Cloudのハイブリッド構成に移行する。

- shared/ の変更 → GitHub ActionsでXCFrameworkビルド → GitHub Releasesに配布
- iosApp/ の変更 → Xcode CloudでXCFramework取得 → iOSビルド → TestFlight

### Phase 3（長期）: 案Cの検討

チーム拡大・他プロジェクトでのKMP採用時にKMMBridge + SPM方式を検討。

## 5. 既知の注意事項

- **Kotlin 2.2.21+必須**: Xcode 26 + Swift 6.2環境ではCInterop生成失敗の問題あり。Kotlin 2.2.21以降で修正済み
- **GitHub Actions料金改定**: 2026年3月以降、self-hosted runnerに$0.002/分の課金が追加予定。hosted runnerの無料枠で運用推奨
- **Xcode Cloud無料枠**: 月25時間。案Aでは1ビルド20-30分かかるため、月50-75回が上限目安
