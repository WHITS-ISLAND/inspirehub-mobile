#!/bin/sh
set -e

echo "=== ci_post_xcodebuild.sh ==="

# archiveアクション時のみDeployGateにアップロード
if [ "$CI_XCODEBUILD_ACTION" != "archive" ]; then
    echo "Skipping: not an archive action (action=$CI_XCODEBUILD_ACTION)"
    exit 0
fi

# --- DeployGate設定 ---
# Xcode Cloud の Environment Variables で以下を設定すること:
#   DEPLOYGATE_API_TOKEN: DeployGateのAPIトークン
#   DEPLOYGATE_OWNER_NAME: DeployGateのオーナー名（ユーザー名 or 組織名）
DEPLOYGATE_DISTRIBUTION_KEY="1468009525184561284"

# --- IPAファイルを探す ---
# Xcode Cloudが提供する署名済みアプリパスから検索
# 優先順: Ad Hoc → Development → App Store
IPA_PATH=""
for SIGNED_PATH in "$CI_AD_HOC_SIGNED_APP_PATH" "$CI_DEVELOPMENT_SIGNED_APP_PATH" "$CI_APP_STORE_SIGNED_APP_PATH"; do
    if [ -n "$SIGNED_PATH" ] && [ -e "$SIGNED_PATH" ]; then
        IPA_PATH="$SIGNED_PATH"
        break
    fi
done

if [ -z "$IPA_PATH" ]; then
    echo "Error: No signed app found"
    echo "  CI_AD_HOC_SIGNED_APP_PATH=$CI_AD_HOC_SIGNED_APP_PATH"
    echo "  CI_DEVELOPMENT_SIGNED_APP_PATH=$CI_DEVELOPMENT_SIGNED_APP_PATH"
    echo "  CI_APP_STORE_SIGNED_APP_PATH=$CI_APP_STORE_SIGNED_APP_PATH"
    exit 1
fi

echo "Found signed app: $IPA_PATH"

# --- DeployGateにアップロード ---
if [ -z "$DEPLOYGATE_API_TOKEN" ]; then
    echo "Error: DEPLOYGATE_API_TOKEN is not set. Set it in Xcode Cloud environment variables."
    exit 1
fi

if [ -z "$DEPLOYGATE_OWNER_NAME" ]; then
    echo "Error: DEPLOYGATE_OWNER_NAME is not set. Set it in Xcode Cloud environment variables."
    exit 1
fi

echo "Uploading to DeployGate (owner=$DEPLOYGATE_OWNER_NAME, distribution=$DEPLOYGATE_DISTRIBUTION_KEY)..."

curl -f \
    -F "token=${DEPLOYGATE_API_TOKEN}" \
    -F "file=@${IPA_PATH}" \
    -F "message=Xcode Cloud Build #${CI_BUILD_NUMBER} (${CI_COMMIT} on ${CI_BRANCH})" \
    -F "distribution_key=${DEPLOYGATE_DISTRIBUTION_KEY}" \
    -F "release_note=Branch: ${CI_BRANCH}, Commit: ${CI_COMMIT}" \
    "https://deploygate.com/api/users/${DEPLOYGATE_OWNER_NAME}/apps"

echo ""
echo "=== DeployGate upload complete ==="
