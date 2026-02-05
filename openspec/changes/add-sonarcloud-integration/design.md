## Context

DDF already has partial SonarCloud integration in place:
- Root `pom.xml` defines Sonar properties (`sonar.organization`, `sonar.host.url`, `sonar.coverage.jacoco.xmlReportPaths`, exclusions)
- `.github/workflows/sonarcloud.yml` runs `mvn install -DskipTests` then `mvn sonar:sonar` on push/PR/nightly
- `.github/workflows/test-coverage.yml` runs JaCoCo coverage with PR commenting and trend tracking
- `SONAR_TOKEN` secret is configured in the repository
- JaCoCo plugin is configured in the root POM with `prepare-agent` and `report` executions

However, the integration has not yet completed a successful analysis run, and several issues need resolution before quality gates can be enforced.

**Current issues:**
1. Both `sonarcloud.yml` and `test-coverage.yml` reference `master` branch — now renamed to `main`
2. The sonarcloud workflow does a compile-only build (`-DskipTests`) so Sonar receives no coverage data from that run
3. Coverage data from `test-coverage.yml` is not fed into the SonarCloud analysis
4. No `sonar.projectKey` is set in `pom.xml` — it's only passed via CLI, making local analysis harder
5. Quality gate configuration on SonarCloud has not been validated

## Goals / Non-Goals

**Goals:**
- Fix branch references (`master` → `main`) in all affected workflows
- Achieve a successful SonarCloud analysis run with coverage data
- Configure quality gate status checks on PRs
- Consolidate coverage reporting so SonarCloud receives JaCoCo data
- Enable developers to run Sonar analysis locally

**Non-Goals:**
- Achieving specific coverage thresholds (that's the `coverage-expansion-sprint-2` change)
- Replacing Codecov with SonarCloud for coverage trending (keep both)
- Adding SonarLint IDE configuration
- Configuring custom quality profiles (use SonarCloud defaults initially)

## Decisions

### 1. Two-tier workflow strategy (keep existing approach)

**Decision:** Keep the split between fast push/PR analysis and nightly full analysis.

**Rationale:** DDF's full `mvn test` takes 60-90+ minutes. Running tests on every push would burn CI minutes and slow feedback. The compile-only analysis still catches code smells, bugs, security issues, and duplication. Nightly runs add coverage data.

**Alternatives considered:**
- Single workflow with tests on every push — too slow, would frequently timeout
- Only nightly analysis — no PR feedback, defeats the purpose of quality gates

### 2. Feed JaCoCo reports into SonarCloud via nightly workflow

**Decision:** Add a `sonar:sonar` step to the nightly schedule path in `sonarcloud.yml` that runs after tests, or merge the nightly logic so `sonarcloud.yml` handles both compile-only (push/PR) and full-with-coverage (nightly/manual).

**Rationale:** The existing `test-coverage.yml` already generates JaCoCo XML reports. Rather than duplicating test execution, the sonarcloud nightly job should run tests + JaCoCo, then feed reports to Sonar. The `sonar.coverage.jacoco.xmlReportPaths` property in the root POM already points to the right location.

**Alternatives considered:**
- Chaining `test-coverage.yml` → `sonarcloud.yml` via `workflow_run` trigger — adds complexity, artifact passing is fragile across workflows
- Single mega-workflow for everything — harder to maintain and debug

### 3. Add `sonar.projectKey` to root POM

**Decision:** Add `<sonar.projectKey>montge_ddf</sonar.projectKey>` to the root POM properties alongside the existing Sonar properties.

**Rationale:** Centralizes configuration. Developers can run `mvn sonar:sonar` locally without remembering CLI flags. The workflow can still override via `-D` if needed.

### 4. Use SonarCloud automatic analysis for PR decoration

**Decision:** Enable SonarCloud's GitHub integration for PR status checks and inline annotations.

**Rationale:** SonarCloud provides native GitHub PR decoration when the `GITHUB_TOKEN` is passed (already done in the workflow). This shows quality gate pass/fail as a PR check and adds inline code annotations for new issues.

### 5. Update all workflow branch references to `main`

**Decision:** Update `sonarcloud.yml`, `test-coverage.yml`, and any other workflows still referencing `master` to use `main`.

**Rationale:** The `master` → `main` rename means these workflows won't trigger on push/PR to the default branch until updated.

## Risks / Trade-offs

**Nightly coverage delay** — Coverage data in SonarCloud will be up to 24 hours stale for PR reviews.
  Mitigation: The `test-coverage.yml` PR comment provides immediate coverage feedback. SonarCloud shows code quality issues (bugs, smells, security) in real-time on push/PR.

**CI minute consumption** — Nightly full builds with tests + Sonar analysis will use ~180 minutes of runner time per run.
  Mitigation: Only runs once daily. Push/PR analysis is compile-only (~30-45 min). Can reduce frequency if costs become a concern.

**Sonar analysis timeout** — The 180-minute timeout may not be enough for full test + analysis on the entire DDF codebase.
  Mitigation: Monitor first few runs. Can exclude slow integration test modules from nightly if needed via `-pl '!...'`.

**Branch mismatch window** — Until workflows are updated, push/PR triggers on `main` won't fire.
  Mitigation: This is the first task to implement — fix branch refs before anything else.
