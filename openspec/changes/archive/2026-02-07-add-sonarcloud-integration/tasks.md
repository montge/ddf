## 1. Fix Branch References

- [x] 1.1 Update `sonarcloud.yml` branch triggers from `master` to `main`
- [x] 1.2 Update `test-coverage.yml` branch triggers and `refs/heads/master` condition to `main`
- [x] 1.3 Update `ci.yml` branch triggers and all `refs/heads/master` conditions to `main`
- [x] 1.4 Update `build-parallel.yml` branch triggers from `master` to `main`
- [x] 1.5 Update `build-sequential.yml` branch triggers from `master` to `main`
- [x] 1.6 Update `build.yml` branch triggers from `master` to `main`
- [x] 1.7 Update `security-scan.yml` branch triggers from `master` to `main`
- [x] 1.8 Update `playwright.yml` branch triggers and comments from `master` to `main`
- [x] 1.9 Update `README.md`, `VALIDATION-CHECKLIST.md`, and `PARALLEL-BUILD-STRATEGY.md` references from `master` to `main`

## 2. Maven Sonar Configuration

- [x] 2.1 Add `<sonar.projectKey>montge_ddf</sonar.projectKey>` to root `pom.xml` Sonar properties section

## 3. SonarCloud Workflow Enhancement

- [x] 3.1 Split `sonarcloud.yml` into two jobs: `analysis` (push/PR, compile-only) and `analysis-with-coverage` (nightly/manual, runs tests + JaCoCo + Sonar)
- [x] 3.2 Add `mvn test jacoco:report` step to the nightly job before `sonar:sonar`
- [x] 3.3 Verify `sonar.coverage.jacoco.xmlReportPaths` POM property matches actual JaCoCo output paths

## 4. Quality Gate Configuration

- [x] 4.1 Verify SonarCloud project exists at `montge_ddf` and is linked to the GitHub repo
- [x] 4.2 Enable SonarCloud PR decoration (quality gate status check on PRs) — auto-enabled via GITHUB_TOKEN
- [x] 4.3 Confirm default quality gate conditions are appropriate — using "Sonar way" default gate

## 5. Validation

- [x] 5.1 Push changes and verify `sonarcloud.yml` triggers on push to `main` (post-merge) — verified: workflow triggers on push events
- [x] 5.2 Verify compile-only analysis completes successfully on a PR (post-merge) — fixed: Java 17→21 for Karaf 4.4.9 plugin, added cxf-core-all artifact size override
- [ ] 5.3 Trigger a manual workflow run with full analysis + coverage and confirm SonarCloud receives coverage data (post-merge)
- [ ] 5.4 Confirm quality gate status appears on a test PR (post-merge)
