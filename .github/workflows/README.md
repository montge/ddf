# DDF GitHub Actions CI/CD Workflows

This directory contains comprehensive GitHub Actions workflows for DDF continuous integration and deployment, based on the Alliance project patterns and adapted for DDF's specific requirements.

## Workflows Overview

### 1. **build.yml** - Build and Test Pipeline

**Purpose:** Multi-strategy build pipeline with validation, incremental builds, and full builds.

**Triggers:**
- Pull requests (incremental build)
- Push to master and version branches (full build)
- Manual trigger (workflow_dispatch)
- Nightly at 2 AM UTC (scheduled)

**Jobs:**
- `validate` - POM validation and code formatting check (PRs only)
- `incremental-build` - Build only changed modules for fast feedback (PRs only)
- `full-build` - Complete build with all tests on Java 17 & 21 matrix
- `deploy` - Deploy artifacts to Maven Central (master/version branches)
- `nightly-failure-notification` - Create issue on nightly build failure

**Key Features:**
- ✅ Java 17 and 21 testing matrix (removed Java 11 per DDF requirements)
- ✅ Gitflow incremental builder for PR efficiency
- ✅ 120-minute timeout (DDF has 455 modules vs Alliance's ~50)
- ✅ Artifact retention: distributions (14 days), tests (7 days), coverage (30 days)
- ✅ MAVEN_OPTS: `-Xmx8G -Xms1G` for large reactor build

### 2. **test-coverage.yml** - Coverage Analysis and Enforcement

**Purpose:** JaCoCo coverage analysis with per-module and overall thresholds.

**Triggers:**
- Pull requests
- Push to master and version branches
- Manual trigger

**Jobs:**
- `coverage-analysis` - Run JaCoCo, extract metrics, enforce thresholds
- `coverage-trend` - Track coverage over time with Codecov (master only)

**Coverage Targets:**
- **Baseline:** 65% (minimum to pass CI)
- **Per-Module Target:** 80%
- **Overall Target:** 90-95%

**Key Features:**
- ✅ Automated coverage report extraction from all 455 modules
- ✅ Per-module coverage table in PR comments
- ✅ Lists modules below 80% threshold
- ✅ Python script to parse JaCoCo XML reports
- ✅ Fails build if coverage drops below 65% baseline
- ✅ Codecov integration for trend tracking

### 3. **security-scan.yml** - Multi-Layered Security Scanning

**Purpose:** Comprehensive security scanning with OWASP, CodeQL, secret detection, and license checks.

**Triggers:**
- Pull requests
- Push to master and version branches
- Manual trigger
- Weekly on Sundays at 3 AM UTC (scheduled)

**Jobs:**
- `dependency-check` - OWASP Dependency Check for CVEs (CVSS >= 7)
- `codeql-analysis` - Semantic code analysis with security-extended queries
- `dependency-review` - Block PRs introducing vulnerable dependencies
- `secret-scan` - TruffleHog credential detection
- `license-check` - License compliance validation
- `security-summary` - Aggregate results and create issues on failure

**Key Features:**
- ✅ Uses existing `dependency-check-maven-config.xml` suppression file
- ✅ OWASP NVD database caching for faster scans
- ✅ Automatic issue creation on vulnerability detection
- ✅ PR comments with security scan summaries
- ✅ References COMPREHENSIVE-TESTING-STRATEGY.md for remediation
- ✅ SARIF upload to GitHub Security tab

### 4. **release.yml** - Versioned and Nightly Releases

**Purpose:** Create versioned releases and automated nightly builds.

**Triggers:**
- Manual trigger with release type selection
- Optional: Scheduled nightly (commented out by default)

**Release Types:**

**Versioned Release:**
- Uses Maven release plugin
- Requires version input (e.g., `2.29.0`)
- Creates GitHub release with distribution ZIP
- Deploys to Maven Central
- Tags: `ddf-2.29.0`

**Nightly Release:**
- Timestamp-based versions: `2.29.0-nightly-20251021-040000`
- Marked as pre-release
- Automatic cleanup (keeps last 7)
- Tags: `nightly-20251021-040000`

**Jobs:**
- `versioned-release` - Official numbered releases
- `nightly-release` - Automated snapshot builds
- `cleanup-nightly` - Delete old nightly releases (keep last 7)
- `post-release-verification` - Verify release artifacts

**Key Features:**
- ✅ Maven release plugin integration
- ✅ GPG signing for Maven Central
- ✅ Distribution ZIP attached to GitHub release
- ✅ Automatic version validation
- ✅ Nightly cleanup to prevent bloat

---

## DDF-Specific Customizations

### Differences from Alliance Workflows

| Aspect | Alliance | DDF | Reason |
|--------|----------|-----|--------|
| **Java Versions** | 11, 17, 21 | 17, 21 | DDF requires Java 17+ |
| **Module Count** | ~50 | 455 | Much larger codebase |
| **Timeout** | 60-90 min | 120 min | Accommodate larger build |
| **MAVEN_OPTS** | -Xmx4G | -Xmx8G | More heap for 455 modules |
| **Coverage Baseline** | 75% | 65% → 90-95% | Progressive improvement |
| **Coverage Target** | 75% | 80% per-module, 90-95% overall | Higher standards |
| **Suppression File** | None | `dependency-check-maven-config.xml` | Existing DDF config |
| **Distribution Path** | `distribution/*/target/*.zip` | `distribution/ddf/target/ddf-*.zip` | DDF structure |
| **Version Format** | X.Y.Z | X.Y.Z (e.g., 2.29.0) | Same format |

### Key Adaptations

1. **Incremental Builder:** Added gitflow-incremental-builder for PR efficiency with 455 modules
2. **Coverage Reporting:** Python script handles large number of modules (455 vs ~50)
3. **Security Scans:** References DDF's COMPREHENSIVE-TESTING-STRATEGY.md for test-first remediation
4. **Nightly Builds:** Adapted to DDF's version scheme (2.29.0-SNAPSHOT → 2.29.0-nightly-...)
5. **Artifact Paths:** Updated for DDF directory structure

---

## Initial Setup

### Required GitHub Secrets

Configure these secrets in repository settings (`Settings > Secrets and variables > Actions`):

#### Maven Central Deployment
```
OSSRH_USERNAME       - Sonatype OSS username
OSSRH_TOKEN          - Sonatype OSS token
GPG_PRIVATE_KEY      - GPG private key for artifact signing
GPG_PASSPHRASE       - GPG key passphrase
```

#### Code Coverage (Optional)
```
CODECOV_TOKEN        - Codecov.io token for coverage tracking
```

### Required GitHub Settings

#### Branch Protection Rules

Configure for `master` and `*.x` branches:

**Settings > Branches > Add rule**

```yaml
Branch name pattern: master
```

**Protection rules:**
- ✅ Require a pull request before merging
  - ✅ Require approvals: 1
  - ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ Require status checks to pass before merging
  - ✅ Require branches to be up to date before merging
  - **Required checks:**
    - `validate` (Validate POM and Code Formatting)
    - `incremental-build` (Java 17)
    - `incremental-build` (Java 21)
    - `coverage-analysis` (Coverage Analysis and Enforcement)
    - `dependency-review` (Dependency Review)
    - `codeql-analysis` (CodeQL Security Analysis)
- ✅ Require conversation resolution before merging
- ✅ Do not allow bypassing the above settings

Repeat for pattern: `*.x` (version branches)

#### Actions Permissions

**Settings > Actions > General**

```yaml
Workflow permissions:
  - Read and write permissions
  - Allow GitHub Actions to create and approve pull requests
```

#### Security Features

**Settings > Code security and analysis**

- ✅ Dependency graph: Enabled
- ✅ Dependabot alerts: Enabled
- ✅ Dependabot security updates: Enabled
- ✅ Code scanning: Enabled (via CodeQL workflow)
- ✅ Secret scanning: Enabled

---

## Testing the Workflows

### Before Merging to Master

1. **Create a test branch:**
   ```bash
   git checkout -b test/github-actions
   ```

2. **Push the workflows:**
   ```bash
   git add .github/workflows/
   git commit -m "Add GitHub Actions CI/CD workflows"
   git push -u origin test/github-actions
   ```

3. **Create a Pull Request:**
   - Open PR against `master`
   - Verify `validate` job runs and passes
   - Verify `incremental-build` runs on Java 17 and 21
   - Check coverage report in PR comments
   - Review security scan results

4. **Test Manual Triggers:**
   - Go to `Actions` tab
   - Select `Build and Test` workflow
   - Click `Run workflow`
   - Verify full build completes

5. **Verify Artifacts:**
   - Check that test results are uploaded
   - Verify coverage reports are available
   - Confirm distribution ZIPs are created

### After Merging to Master

1. **Monitor First Full Build:**
   - Watch the full build on master
   - Verify all 455 modules compile
   - Check that artifacts are deployed (if configured)

2. **Test Nightly Build:**
   - Wait for scheduled nightly run at 2 AM UTC, OR
   - Manually trigger: `Actions > Build and Test > Run workflow`

3. **Test Release Workflow:**
   ```
   Actions > Release > Run workflow
   Release Type: nightly
   ```
   - Verify nightly release is created
   - Download and test distribution ZIP
   - Confirm cleanup job removes old nightlies

4. **Monitor Coverage Trends:**
   - Check Codecov dashboard (if configured)
   - Review coverage trend over several builds

### Troubleshooting

#### Build Timeout
```yaml
# If build takes > 120 minutes, increase timeout in build.yml:
timeout-minutes: 180  # Increase to 3 hours
```

#### Out of Memory
```yaml
# If builds run out of heap, increase in build.yml:
MAVEN_OPTS: '-Xmx12G -Xms2G ...'  # Increase heap
```

#### Coverage Extraction Fails
```yaml
# If Python script fails, check:
1. JaCoCo reports exist: ls **/target/site/jacoco/jacoco.xml
2. Python 3 is available: python3 --version
3. XML parsing works: python3 extract_coverage.py
```

#### OWASP NVD Download Slow
```yaml
# NVD database cache should help, but first run may be slow (30-60 min)
# Monitor: Actions > dependency-check job > View logs
```

#### Secret Scanning False Positives
```yaml
# Add to .github/.trivyignore or configure TruffleHog excludes
# See: https://github.com/trufflesecurity/trufflehog
```

---

## Maintenance

### Regular Tasks

**Weekly:**
- Review security scan results from Sunday scheduled run
- Check for failed nightly builds (automated issues will be created)

**Monthly:**
- Review code coverage trends
- Update coverage targets if consistently meeting 90%+
- Review and update OWASP suppressions if needed

**Per Release:**
- Run versioned release workflow
- Verify Maven Central deployment
- Test distribution ZIP
- Update documentation

### Updating Workflows

**To modify workflows:**

1. Create feature branch
2. Edit workflow files in `.github/workflows/`
3. Test on PR before merging
4. Document changes in PR description

**Common updates:**

- **Add/remove Java versions:** Edit `matrix.java` in build.yml
- **Adjust coverage thresholds:** Edit threshold checks in test-coverage.yml
- **Update CVSS threshold:** Edit `-DfailBuildOnCVSS=X` in security-scan.yml
- **Change nightly schedule:** Edit `cron` in workflow triggers

---

## Workflow Artifacts

### Artifact Retention Policies

| Artifact Type | Retention | Workflows |
|---------------|-----------|-----------|
| Test Results | 7 days | build.yml |
| Coverage Reports | 30 days | build.yml, test-coverage.yml |
| Distribution ZIPs | 14 days | build.yml, release.yml |
| Security Scans | 30 days | security-scan.yml |
| Build Logs | 7 days | build.yml (on failure) |

### Downloading Artifacts

1. Go to `Actions` tab
2. Select workflow run
3. Scroll to `Artifacts` section
4. Click to download

---

## Performance Optimization

### Build Performance

**Current optimizations:**
- Maven dependency caching
- Incremental builds for PRs (only changed modules)
- Parallel test execution (Maven default)

**Future optimizations:**
- Distributed builds (multiple runners)
- Test parallelization (Surefire parallel)
- Docker layer caching for integration tests

### Security Scan Performance

**Current optimizations:**
- OWASP NVD database caching (30-60 min → 5-10 min)
- Parallel security jobs (run independently)
- Skip tests during CodeQL build

**Future optimizations:**
- Incremental dependency analysis
- Scheduled scans off-hours only

---

## Monitoring and Alerts

### Automated Notifications

**Issues created automatically:**
- Nightly build failures
- Security vulnerabilities detected
- Secrets detected in commits

**PR comments automatically posted:**
- Coverage reports with module breakdown
- Security scan summaries
- Dependency review results

### Dashboard Recommendations

**GitHub Projects:** Create project board with automation:
- New issue → `build-failure` label → Auto-add to board
- New issue → `security` label → Auto-add to board
- Issue closed → Move to Done

**External Monitoring:**
- Codecov: Coverage trends
- Sonar: Code quality metrics
- Dependabot: Dependency updates

---

## Resources

### Documentation
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [CodeQL](https://codeql.github.com/docs/)
- [JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)

### DDF-Specific
- DDF COMPREHENSIVE-TESTING-STRATEGY.md
- DDF dependency-check-maven-config.xml
- DDF JAVA-17-MIGRATION-GUIDE.md

### Alliance Reference
- Alliance build patterns (source of these workflows)
- Alliance security scanning approach
- Alliance release automation

---

## Support

For issues with workflows:

1. Check workflow logs in Actions tab
2. Review this README for troubleshooting
3. Create issue with `github-actions` label
4. Reference workflow run URL in issue

For security vulnerabilities:

1. Review COMPREHENSIVE-TESTING-STRATEGY.md
2. Create test reproducing vulnerability
3. Fix vulnerability or document suppression
4. Re-run security scans

---

**Document Version:** 1.0
**Last Updated:** 2025-10-21
**Status:** Ready for Production
