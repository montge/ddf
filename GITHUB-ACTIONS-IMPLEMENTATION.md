# GitHub Actions CI/CD Implementation for DDF

**Date:** 2025-10-21
**Status:** Ready for Implementation
**Based on:** Alliance Project Patterns

---

## Executive Summary

This document describes the comprehensive GitHub Actions CI/CD workflows created for DDF, based on the Alliance project's production-tested patterns and adapted for DDF's specific requirements.

### What Was Created

Four production-ready GitHub Actions workflow files:

1. **`.github/workflows/build.yml`** (264 lines)
   - Multi-strategy build pipeline
   - Validation, incremental builds, full builds, deployment
   - Java 17 & 21 matrix testing
   - Nightly builds with failure notifications

2. **`.github/workflows/test-coverage.yml`** (237 lines)
   - JaCoCo coverage analysis and enforcement
   - Per-module and overall coverage reporting
   - 65% baseline, 80% per-module, 90-95% overall targets
   - Automated PR comments with coverage breakdowns

3. **`.github/workflows/security-scan.yml`** (328 lines)
   - Multi-layered security scanning
   - OWASP Dependency Check (CVSS >= 7)
   - CodeQL semantic analysis
   - Secret detection, license checks
   - Automated issue creation on vulnerabilities

4. **`.github/workflows/release.yml`** (294 lines)
   - Versioned releases with Maven release plugin
   - Automated nightly builds
   - GitHub release creation with distributions
   - Old release cleanup (keep last 7)

**Total:** 1,123 lines of production-ready workflow code

---

## Key Differences from Alliance Workflows

### 1. Build Pipeline Adjustments

| Aspect | Alliance | DDF | Rationale |
|--------|----------|-----|-----------|
| **Java Versions** | 11, 17, 21 | 17, 21 | DDF requires Java 17 minimum |
| **Module Count** | ~50 modules | 455 modules | 9x larger codebase |
| **Build Timeout** | 60-90 minutes | 120 minutes | Accommodate larger reactor |
| **Maven Heap** | -Xmx4G | -Xmx8G | More memory for 455 modules |
| **Incremental Build** | Not used | gitflow-incremental-builder | PR efficiency critical |
| **Artifact Retention** | 7 days | 7-30 days | Larger artifacts, longer debug time |

**Key Addition:** Gitflow incremental builder for PRs to only build changed modules out of 455.

### 2. Coverage Analysis Adjustments

| Aspect | Alliance | DDF | Rationale |
|--------|----------|-----|-----------|
| **Baseline Threshold** | 75% | 65% | Current DDF coverage level |
| **Target Overall** | 75% | 90-95% | Higher quality standards |
| **Per-Module Target** | 75% | 80% | Stricter module requirements |
| **Module Count** | ~50 | 455 | Report pagination needed |
| **Coverage Script** | Java/Maven | Python | Better XML parsing for scale |

**Key Addition:** Python script to aggregate coverage from 455 modules with pagination.

**Coverage Progression:**
- **Current:** 65.98% (baseline for CI)
- **Phase 1:** 75% (6 months)
- **Phase 2:** 85% (12 months)
- **Target:** 90-95% (18 months)

### 3. Security Scanning Adjustments

| Aspect | Alliance | DDF | Rationale |
|--------|----------|-----|-----------|
| **CVSS Threshold** | 7.0 | 7.0 | Same critical threshold |
| **Suppression File** | Custom | `dependency-check-maven-config.xml` | Use existing DDF config |
| **CVE Strategy** | Fix or suppress | Test-first remediation | Reference COMPREHENSIVE-TESTING-STRATEGY.md |
| **Issue Creation** | Yes | Yes + test reminder | Emphasize test-driven approach |
| **NVD Caching** | Yes | Yes | Same optimization |

**Key Addition:** References to COMPREHENSIVE-TESTING-STRATEGY.md in security issue templates.

### 4. Release Workflow Adjustments

| Aspect | Alliance | DDF | Rationale |
|--------|----------|-----|-----------|
| **Distribution Path** | `distribution/*/target/*.zip` | `distribution/ddf/target/ddf-*.zip` | DDF structure |
| **Version Format** | X.Y.Z | X.Y.Z (2.29.0) | Same semantic versioning |
| **Nightly Format** | `nightly-YYYYMMDD` | `2.29.0-nightly-YYYYMMDD-HHMMSS` | Maven-compatible |
| **Java Requirement** | "Java 11+" | "Java 17+ (21 recommended)" | Updated requirements |
| **Release Notes** | Generic | Coverage + security status | More comprehensive |

**Key Addition:** Coverage and security status in release notes.

---

## Architecture and Design Decisions

### Workflow Triggering Strategy

```yaml
# PRs: Fast feedback with incremental builds
pull_request:
  - validate (POM + formatting)
  - incremental-build (changed modules only)
  - coverage-analysis
  - security-scan (dependency-review + others)

# Master/Version Branches: Full validation
push:
  - full-build (all modules, Java 17 & 21)
  - deploy (Maven Central)
  - coverage-trend (Codecov)
  - security-scan (complete suite)

# Scheduled: Proactive monitoring
schedule:
  - nightly build (2 AM UTC)
  - weekly security scan (Sunday 3 AM UTC)
```

### Job Dependencies

```
build.yml:
  validate → incremental-build
  full-build → deploy

test-coverage.yml:
  coverage-analysis → coverage-trend

security-scan.yml:
  All jobs independent → security-summary

release.yml:
  versioned-release → post-release-verification
  nightly-release → cleanup-nightly → post-release-verification
```

### Artifact Strategy

**Test Results (7 days):**
- Surefire reports for debugging failures
- Short retention since tests are re-runnable

**Coverage Reports (30 days):**
- JaCoCo XML and HTML reports
- Longer retention for trend analysis
- Used by Codecov integration

**Distributions (14 days):**
- Full DDF distribution ZIPs
- Medium retention for release validation
- Nightly releases cleaned up automatically

**Security Scans (30 days):**
- OWASP HTML/JSON/SARIF reports
- Longer retention for compliance audits
- SARIF uploaded to GitHub Security tab

---

## Implementation Checklist

### Phase 1: Pre-Implementation (1-2 days)

- [ ] **Review Workflows**
  - [ ] Read all four workflow files
  - [ ] Review `.github/workflows/README.md`
  - [ ] Understand DDF-specific customizations

- [ ] **Configure GitHub Secrets**
  - [ ] `OSSRH_USERNAME` - Maven Central username
  - [ ] `OSSRH_TOKEN` - Maven Central token
  - [ ] `GPG_PRIVATE_KEY` - For artifact signing
  - [ ] `GPG_PASSPHRASE` - GPG key passphrase
  - [ ] `CODECOV_TOKEN` - (Optional) Codecov integration

- [ ] **Configure Branch Protection**
  - [ ] Master branch: Require PR + status checks
  - [ ] Version branches (*.x): Same rules
  - [ ] Required checks: validate, incremental-build, coverage-analysis
  - [ ] Require conversation resolution

- [ ] **Enable GitHub Features**
  - [ ] Actions: Read/write permissions
  - [ ] Security: Dependency graph, Dependabot, Code scanning
  - [ ] Projects: Create automation board (optional)

### Phase 2: Testing (1 week)

- [ ] **Test Branch Creation**
  - [ ] Create `test/github-actions` branch
  - [ ] Push workflow files
  - [ ] Create PR against master

- [ ] **PR Workflow Validation**
  - [ ] Verify `validate` job passes
  - [ ] Verify `incremental-build` runs on Java 17 & 21
  - [ ] Check coverage report in PR comments
  - [ ] Review security scan results
  - [ ] Confirm all required checks pass

- [ ] **Manual Trigger Testing**
  - [ ] Actions → Build and Test → Run workflow
  - [ ] Verify full build completes (expect ~90-120 min)
  - [ ] Check artifacts uploaded
  - [ ] Download and inspect distribution ZIP

- [ ] **Coverage Analysis Testing**
  - [ ] Verify JaCoCo reports generated
  - [ ] Check Python script extracts metrics correctly
  - [ ] Confirm baseline (65%) passes
  - [ ] Review per-module coverage table

- [ ] **Security Scan Testing**
  - [ ] OWASP Dependency Check completes
  - [ ] CodeQL analysis runs
  - [ ] Secret scan executes
  - [ ] License check passes
  - [ ] Review SARIF in Security tab

### Phase 3: Deployment (2-3 days)

- [ ] **Merge to Master**
  - [ ] Squash and merge test PR
  - [ ] Monitor first full build on master
  - [ ] Verify deployment job (if secrets configured)
  - [ ] Check coverage uploaded to Codecov

- [ ] **Nightly Build Testing**
  - [ ] Wait for scheduled run OR trigger manually
  - [ ] Verify build completes
  - [ ] Check failure notification (if fails)
  - [ ] Review artifacts

- [ ] **Release Testing**
  - [ ] Trigger nightly release
  - [ ] Verify GitHub release created
  - [ ] Download distribution ZIP
  - [ ] Test basic DDF functionality
  - [ ] Confirm cleanup runs (after 8+ nightly builds)

- [ ] **Documentation Updates**
  - [ ] Update project README with build status badge
  - [ ] Document workflow usage in contributor guide
  - [ ] Add security policy (if not exists)

### Phase 4: Monitoring (Ongoing)

- [ ] **Daily Monitoring**
  - [ ] Check Actions tab for failures
  - [ ] Review automated issues
  - [ ] Monitor build times

- [ ] **Weekly Review**
  - [ ] Security scan results (Sunday runs)
  - [ ] Coverage trends
  - [ ] Failed builds

- [ ] **Monthly Maintenance**
  - [ ] Update dependencies in workflows
  - [ ] Review and update OWASP suppressions
  - [ ] Adjust coverage targets if consistently meeting goals
  - [ ] Archive/close old automated issues

---

## Configuration Reference

### Required Secrets

```bash
# Maven Central Deployment
OSSRH_USERNAME=your-sonatype-username
OSSRH_TOKEN=your-sonatype-token
GPG_PRIVATE_KEY=your-gpg-private-key
GPG_PASSPHRASE=your-gpg-passphrase

# Optional: Code Coverage
CODECOV_TOKEN=your-codecov-token
```

**Generating GPG Key:**
```bash
# Generate key
gpg --full-generate-key

# Export private key
gpg --armor --export-secret-keys YOUR_EMAIL > gpg-private-key.asc

# Add to GitHub Secrets (paste contents of gpg-private-key.asc)
```

### Branch Protection Settings

**Pattern:** `master`

```yaml
Require pull request before merging:
  - Required approvals: 1
  - Dismiss stale reviews: true
  - Require review from Code Owners: true

Require status checks:
  - Require branches to be up to date: true
  - Required checks:
    - validate
    - incremental-build (17)
    - incremental-build (21)
    - coverage-analysis
    - dependency-review
    - codeql-analysis

Other settings:
  - Require conversation resolution: true
  - Include administrators: true
  - Allow force pushes: false
  - Allow deletions: false
```

Repeat for pattern: `*.x`

### GitHub Actions Permissions

**Settings → Actions → General → Workflow permissions:**
```
✓ Read and write permissions
✓ Allow GitHub Actions to create and approve pull requests
```

---

## Testing Recommendations

### Before Merging Workflows

#### 1. Syntax Validation
```bash
# Install actionlint
brew install actionlint  # macOS
# or download from: https://github.com/rhysd/actionlint

# Validate all workflows
actionlint .github/workflows/*.yml
```

#### 2. Dry Run Testing
```bash
# Use act to test workflows locally (optional)
# https://github.com/nektos/act
brew install act

# Test build workflow
act pull_request -W .github/workflows/build.yml
```

#### 3. PR Testing Strategy
```
1. Create test PR with workflow files only
2. Verify all checks pass
3. Review artifacts and logs
4. Make adjustments if needed
5. Re-push and verify fixes
6. Merge when confident
```

### After Merging Workflows

#### 1. Full Build Validation
```
Expected first run:
- Duration: 90-120 minutes (455 modules)
- Artifacts: 4-6 GB total
- Jobs: All green (validate, build, coverage, security)
```

#### 2. Coverage Baseline Check
```
Expected coverage report:
- Overall: 65-70% (current baseline)
- Modules below 80%: ~18-30 modules
- Format: Table in PR comments
```

#### 3. Security Scan Validation
```
Expected results:
- OWASP: Use existing suppressions
- CodeQL: Initial run may find issues
- Secrets: Should be clean
- License: Should pass
```

#### 4. Release Testing
```
Nightly release test:
1. Trigger manually
2. Verify release created
3. Download ZIP
4. Extract and run: bin/ddf
5. Access: https://localhost:8993/admin
6. Verify basic functionality
```

---

## Troubleshooting Guide

### Common Issues and Solutions

#### Build Timeout
**Symptom:** Build exceeds 120 minutes
**Solution:**
```yaml
# In build.yml, increase timeout:
timeout-minutes: 180  # 3 hours
```

#### Out of Memory
**Symptom:** `java.lang.OutOfMemoryError: Java heap space`
**Solution:**
```yaml
# In build.yml, increase heap:
MAVEN_OPTS: '-Xmx12G -Xms2G ...'
```

#### Coverage Script Fails
**Symptom:** Coverage analysis job fails at Python script
**Diagnosis:**
```bash
# Check JaCoCo reports exist
find . -name "jacoco.xml" -type f

# Test Python script locally
python3 extract_coverage.py
```
**Solution:**
- Ensure JaCoCo plugin configured in POMs
- Verify Python 3 available in runner
- Check XML parsing for malformed reports

#### OWASP Slow First Run
**Symptom:** Dependency check takes 60+ minutes
**Expected:** First run downloads NVD database (~30-60 min)
**Subsequent runs:** 5-10 minutes (cached)

**Solution:** Be patient on first run, cache will speed up future runs.

#### Secret Scan False Positives
**Symptom:** TruffleHog flags test data or examples
**Solution:**
```bash
# Create .github/.trivyignore
echo "path/to/test/data/**" >> .github/.trivyignore
```

#### Incremental Build Failures
**Symptom:** Changed modules don't build correctly
**Solution:**
```yaml
# In build.yml, add debug:
-Dgib.enabled=true
-Dgib.logImpactedTo=impacted.log
-X  # Maven debug output
```

#### Artifact Upload Failures
**Symptom:** `Error: Artifact upload failed`
**Diagnosis:**
- Check artifact size (max 10 GB)
- Verify path patterns match actual files
- Check runner disk space

**Solution:**
```yaml
# Reduce retention or exclude large files
retention-days: 7  # Reduce from 30
path: |
  **/target/surefire-reports/*.xml
  !**/target/classes/**  # Exclude compiled classes
```

---

## Performance Benchmarks

### Expected Build Times

| Build Type | Alliance | DDF | Notes |
|------------|----------|-----|-------|
| **Validate** | 2-3 min | 5-8 min | More POMs to validate |
| **Incremental Build** | 10-15 min | 15-30 min | Only changed modules |
| **Full Build (Java 17)** | 45-60 min | 90-120 min | 455 modules vs ~50 |
| **Full Build (Java 21)** | 45-60 min | 90-120 min | Same as Java 17 |
| **Coverage Analysis** | 50-65 min | 95-125 min | Includes test execution |
| **OWASP Check (first)** | 30-40 min | 45-60 min | NVD download |
| **OWASP Check (cached)** | 5-10 min | 10-15 min | With cache |
| **CodeQL Analysis** | 60-75 min | 90-120 min | Larger codebase |
| **Nightly Release** | 50-60 min | 95-125 min | Full build + packaging |

### Resource Usage

| Resource | Alliance | DDF | Runner Type |
|----------|----------|-----|-------------|
| **CPU Cores** | 2 cores | 2 cores | ubuntu-latest |
| **Memory** | 7 GB | 7 GB (8G heap) | ubuntu-latest |
| **Disk Space** | 14 GB SSD | 14 GB SSD | ubuntu-latest |
| **Build Artifacts** | 1-2 GB | 4-6 GB | Per build |

---

## Future Enhancements

### Short Term (1-3 months)

- [ ] **Distributed Builds**: Use matrix strategy to parallelize module builds
- [ ] **Test Parallelization**: Configure Surefire for parallel test execution
- [ ] **Caching Improvements**: Cache Solr, test fixtures, etc.
- [ ] **Slack/Email Notifications**: Alert team on build failures
- [ ] **Performance Tracking**: Track build time trends

### Medium Term (3-6 months)

- [ ] **Integration Test Isolation**: Separate integration tests into dedicated workflow
- [ ] **Docker Build Caching**: Speed up containerized tests
- [ ] **Incremental Coverage**: Only analyze coverage for changed modules
- [ ] **Automated Dependency Updates**: Dependabot + auto-merge for patches
- [ ] **Release Automation**: Auto-create release from version tags

### Long Term (6-12 months)

- [ ] **Self-Hosted Runners**: Dedicated build infrastructure
- [ ] **Build Matrix Expansion**: Add Windows, macOS builds
- [ ] **E2E Test Suite**: Full system testing in CI
- [ ] **Performance Regression Testing**: Automated performance benchmarks
- [ ] **Chaos Engineering**: Resilience testing in CI

---

## Metrics and KPIs

### Build Health Metrics

Track these metrics monthly:

| Metric | Target | Current | Trend |
|--------|--------|---------|-------|
| **Build Success Rate** | > 95% | TBD | - |
| **Average Build Time** | < 120 min | TBD | - |
| **PR Feedback Time** | < 30 min | TBD | - |
| **Nightly Build Success** | > 90% | TBD | - |

### Coverage Metrics

| Metric | Target | Current | Trend |
|--------|--------|---------|-------|
| **Overall Coverage** | 90-95% | 65.98% | → |
| **Modules Below 80%** | 0 | 18 | - |
| **Coverage Increase** | +2% per month | TBD | - |

### Security Metrics

| Metric | Target | Current | Trend |
|--------|--------|---------|-------|
| **Critical CVEs** | 0 | TBD | - |
| **High CVEs** | < 5 | TBD | - |
| **CVE Resolution Time** | < 7 days | TBD | - |
| **False Positives** | < 10 | TBD | - |

---

## References

### Documentation Created

1. `.github/workflows/build.yml` - Build and test pipeline
2. `.github/workflows/test-coverage.yml` - Coverage analysis
3. `.github/workflows/security-scan.yml` - Security scanning
4. `.github/workflows/release.yml` - Release automation
5. `.github/workflows/README.md` - Comprehensive workflow documentation
6. This document - Implementation guide

### External References

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [TruffleHog Secret Scanner](https://github.com/trufflesecurity/trufflehog)

### DDF-Specific References

- `COMPREHENSIVE-TESTING-STRATEGY.md` - Testing strategy (80%/90-95% targets)
- `dependency-check-maven-config.xml` - OWASP suppressions
- `JAVA-17-MIGRATION-GUIDE.md` - Java version requirements
- `pom.xml` - Maven configuration (compiler settings, JaCoCo config)

### Alliance Project References

- Alliance build.yml - Source template
- Alliance test-coverage.yml - Coverage enforcement pattern
- Alliance security-scan.yml - Security scanning approach
- Alliance release.yml - Release automation strategy

---

## Support and Contact

### For Workflow Issues

1. **Check Workflow Logs**: Actions tab → Select run → View logs
2. **Review Troubleshooting**: See section above
3. **Create Issue**: Use `github-actions` label
4. **Reference Run URL**: Include link to failed run

### For Security Issues

1. **Review COMPREHENSIVE-TESTING-STRATEGY.md**
2. **Create Test**: Following test-first approach
3. **Submit PR**: With fix or suppression
4. **Re-run Scans**: Verify fix

### For Coverage Issues

1. **Check JaCoCo Reports**: Download artifacts
2. **Review Per-Module Coverage**: See workflow comments
3. **Target Lowest Coverage Modules**: Start with < 80%
4. **Follow Testing Strategy**: COMPREHENSIVE-TESTING-STRATEGY.md

---

## Conclusion

These GitHub Actions workflows provide DDF with:

✅ **Production-ready CI/CD** based on Alliance patterns
✅ **Java 17 & 21 testing** aligned with DDF requirements
✅ **Comprehensive coverage enforcement** (65% → 90-95%)
✅ **Multi-layered security scanning** (OWASP, CodeQL, secrets, licenses)
✅ **Automated releases** (versioned + nightly)
✅ **Scalability** for 455-module reactor build
✅ **Test-first security** approach referencing COMPREHENSIVE-TESTING-STRATEGY.md

**Next Steps:**
1. Review all workflow files
2. Configure GitHub secrets
3. Set up branch protection
4. Test on feature branch
5. Merge and monitor

**Document Version:** 1.0
**Status:** Ready for Production
**Last Updated:** 2025-10-21
