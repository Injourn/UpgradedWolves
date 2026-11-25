#!/usr/bin/env bash
# Run the local Minecraft data generator (runData launch config, working dir ./run-data),
# wait for it to finish, print last ~100 lines of output.
# Should be run after code changes (Java) or build changes; the Gradle runData task
# recompiles changed sources before launching the data generator.

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../../.." && pwd)"
RUN_DIR="$PROJECT_ROOT/run-data"
LOG_DIR="${TMPDIR:-/tmp}/opencode"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/run-data-$(date +%Y%m%d-%H%M%S).log"

# Run the data generator via the runData task (workingDirectory is configured as ./run-data
# in build.gradle for the runData task and in .vscode/launch.json).
"$PROJECT_ROOT/gradlew" runData >"$LOG_FILE" 2>&1

echo "--- last 100 lines of $LOG_FILE ---"
tail -n 100 "$LOG_FILE"

echo "LOG_FILE=$LOG_FILE"
