#!/usr/bin/env bash
# Run the local Minecraft client (runClient launch config, working dir ./run),
# wait ~20s, kill it, print last ~100 lines of output.
# Should be run after code changes (Java) or build changes; the Gradle runClient
# task recompiles changed sources before launching the client.

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
RUN_DIR="$PROJECT_ROOT/run"
LOG_DIR="${TMPDIR:-/tmp}/opencode"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/run-client-$(date +%Y%m%d-%H%M%S).log"
PID_FILE="$LOG_DIR/run-client.pid"

# Run the client via the runClient task (workingDirectory is configured as ./run
# in build.gradle for the runClient task and in .vscode/launch.json).
"$PROJECT_ROOT/gradlew" runClient >"$LOG_FILE" 2>&1 &
CLIENT_PID=$!
echo "$CLIENT_PID" >"$PID_FILE"

sleep 20

kill "$CLIENT_PID" 2>/dev/null || true
sleep 3
kill -9 "$CLIENT_PID" 2>/dev/null || true
pkill -f ForgeBootstrap 2>/dev/null || true

echo "--- last 100 lines of $LOG_FILE ---"
tail -n 100 "$LOG_FILE"

echo "CLIENT_PID=$CLIENT_PID"
echo "LOG_FILE=$LOG_FILE"
echo "PID_FILE=$PID_FILE"
