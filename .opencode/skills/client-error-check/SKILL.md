---
name: Client Error Check
description: Run the local Minecraft client via the runClient VS Code launch config, wait around 20 seconds, kill the process, and check the last ~100 lines of output for errors
---

# Client Error Check

Run the local Minecraft client (the `runClient` configuration from
`.vscode/launch.json`), wait around 20 seconds, kill the process, and check the
last ~100 lines of its output for errors.

## Workflow

1. Run the client locally via the `runClient` launch configuration, which runs
   the client with its working directory set to the `./run` folder:

   ```sh
   ./.opencode/skills/client-error-check/scripts/run-client.sh
   ```

2. The script runs the client (via `./gradlew runClient`, working directory
   `./run`) with output captured to a temp log file, waits ~20 seconds, kills the
   client process (SIGTERM, then SIGKILL), and prints the last 100 lines of the
   captured output.

3. Run this check after any code changes (Java sources) or build changes. The
   underlying Gradle `runClient` task automatically recompiles changed Java
   sources and launches the updated build, so each run reflects the current
   code state.

4. Review the printed output and check for errors in the last ~100 lines:

   ```sh
   grep -niE 'error|err[: ]|panic|fatal|crash|failed|✗|✘' "$LOG_FILE" | tail -n 30
   ```

5. Report the outcome.

## Cleanup

Remove the temporary client log and pid files:

```sh
rm -f /tmp/opencode/run-client.pid /tmp/opencode/run-client-*.log
```
