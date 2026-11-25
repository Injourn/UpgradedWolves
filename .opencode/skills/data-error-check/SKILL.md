---
name: Data Error Check
description: Run the local Minecraft data generator via the runData VS Code launch config, wait for it to finish, and check the last ~100 lines of output for errors
---

# Data Error Check

Run the local Minecraft data generator (the `runData` configuration from
`.vscode/launch.json`), wait for it to finish, and check the last ~100 lines of
its output for errors.

## Workflow

1. Run the data generator via the `runData` launch configuration, which runs
   the data generator with its working directory set to the `./run-data` folder:

   ```sh
   ./.opencode/skills/data-error-check/scripts/run-data.sh
   ```

2. The script runs the data generator (via `./gradlew runData`, working directory
   `./run-data`) with output captured to a temp log file, waits for the process to
   finish, and prints the last 100 lines of the captured output.

   Note that the data generator is not like the client: it runs to completion on
   its own, so no process needs to be killed afterwards.

3. Run this check after any code changes (Java sources) or build changes. The
   underlying Gradle `runData` task automatically recompiles changed Java
   sources and launches the updated build, so each run reflects the current
   code state.

4. Review the printed output and check for errors in the last ~100 lines.
   The data generator runs with DEBUG logging enabled, so first restrict the
   check to the last 100 lines and then filter out DEBUG-level noise before
   looking for real error indicators:

   ```sh
   tail -n 100 "$LOG_FILE" | grep -niE 'error|err[: ]|panic|fatal|crash|failed|✗|✘' | grep -vi 'DEBUG' | tail -n 30
   ```

   A clean run should show no output here. Real failures (e.g. a thrown
   exception, a validation problem, or a `BUILD FAILED` from Gradle) will still
   surface.

5. Report the outcome.

## Cleanup

Remove the temporary data generator log file:

```sh
rm -f /tmp/opencode/run-data-*.log
```
