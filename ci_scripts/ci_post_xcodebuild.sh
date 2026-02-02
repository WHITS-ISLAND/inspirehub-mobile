#!/bin/sh
set -e

echo "=== ci_post_xcodebuild.sh ==="

# archiveアクション時のみ実行
if [ "$CI_XCODEBUILD_ACTION" != "archive" ]; then
    echo "Skipping: not an archive action (action=$CI_XCODEBUILD_ACTION)"
    exit 0
fi

# --- Discord設定 ---
# Xcode Cloud の Environment Variables で以下を設定すること:
#   DISCORD_BOT_TOKEN: Discord BotのToken
#   DISCORD_CLIENT_ID: Discord BotのClient ID
#   DISCORD_CHANNEL_ID: 通知先のChannel ID

if [ -z "$DISCORD_BOT_TOKEN" ]; then
    echo "Error: DISCORD_BOT_TOKEN is not set. Set it in Xcode Cloud environment variables."
    exit 1
fi

if [ -z "$DISCORD_CHANNEL_ID" ]; then
    echo "Error: DISCORD_CHANNEL_ID is not set. Set it in Xcode Cloud environment variables."
    exit 1
fi

# --- ビルド結果を判定 ---
if [ "$CI_XCODEBUILD_EXIT_CODE" = "0" ]; then
    STATUS="✅ SUCCESS"
    COLOR=3066993
else
    STATUS="❌ FAILED (exit code: $CI_XCODEBUILD_EXIT_CODE)"
    COLOR=15158332
fi

# --- Discord Webhook (Bot API) で通知 ---
echo "Sending build notification to Discord (channel=$DISCORD_CHANNEL_ID)..."

PAYLOAD=$(cat <<ENDJSON
{
  "embeds": [{
    "title": "${STATUS} - InspireHub Mobile Build",
    "color": ${COLOR},
    "fields": [
      { "name": "Branch", "value": "${CI_BRANCH:-unknown}", "inline": true },
      { "name": "Build #", "value": "${CI_BUILD_NUMBER:-N/A}", "inline": true },
      { "name": "Commit", "value": "${CI_COMMIT:-unknown}", "inline": false },
      { "name": "Workflow", "value": "${CI_WORKFLOW:-default}", "inline": true },
      { "name": "Xcode", "value": "${CI_XCODE_VERSION:-unknown}", "inline": true }
    ],
    "footer": { "text": "Xcode Cloud" },
    "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  }]
}
ENDJSON
)

curl -f -X POST \
    "https://discord.com/api/v10/channels/${DISCORD_CHANNEL_ID}/messages" \
    -H "Authorization: Bot ${DISCORD_BOT_TOKEN}" \
    -H "Content-Type: application/json" \
    -d "$PAYLOAD"

echo ""
echo "=== Discord notification sent ==="
