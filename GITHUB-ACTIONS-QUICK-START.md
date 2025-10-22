# GitHub Actions Quick Start Guide

**5-Minute Setup Guide for DDF CI/CD Workflows**

---

## What You Get

✅ Automated builds on every PR and commit to master
✅ Code coverage enforcement (65% baseline → 90-95% target)
✅ Security scanning (OWASP, CodeQL, secrets, licenses)
✅ Automated releases (versioned + nightly)
✅ Java 17 & 21 testing matrix

---

## Immediate Actions Required

### 1. Configure Secrets (5 minutes)

Go to: **Settings → Secrets and variables → Actions → New repository secret**

Add these secrets:

```bash
# For Maven Central Deployment
OSSRH_USERNAME       # Your Sonatype username
OSSRH_TOKEN          # Your Sonatype token
GPG_PRIVATE_KEY      # GPG private key (see below)
GPG_PASSPHRASE       # GPG key passphrase

# Optional: For coverage tracking
CODECOV_TOKEN        # From codecov.io
```

**Generate GPG Key:**
```bash
gpg --full-generate-key
gpg --armor --export-secret-keys YOUR_EMAIL > key.asc
# Copy contents of key.asc to GPG_PRIVATE_KEY secret
```

### 2. Enable Branch Protection (3 minutes)

**Settings → Branches → Add rule**

Pattern: `master`

Required settings:
- ✅ Require pull request before merging (1 approval)
- ✅ Require status checks to pass:
  - `validate`
  - `incremental-build (17)`
  - `incremental-build (21)`
  - `coverage-analysis`
- ✅ Require conversation resolution

Click "Save changes"

Repeat for pattern: `*.x` (version branches)

### 3. Enable Actions Permissions (1 minute)

**Settings → Actions → General**

Workflow permissions:
- ✅ Read and write permissions
- ✅ Allow GitHub Actions to create and approve pull requests

Click "Save"

---

## Testing the Workflows

### Option A: Test on Feature Branch (Recommended)

```bash
# Create test branch
git checkout -b test/github-actions

# Push workflows
git add .github/workflows/ GITHUB-ACTIONS-*.md
git commit -m "Add GitHub Actions CI/CD workflows"
git push -u origin test/github-actions

# Create PR and watch the magic happen
```

**What to expect:**
- `validate` job: 5-8 minutes (POM validation, formatting check)
- `incremental-build`: 15-30 minutes (only changed modules)
- `coverage-analysis`: 95-125 minutes (full test suite)
- `security-scan`: 45-90 minutes (first run downloads NVD database)

### Option B: Manual Trigger

```
1. Go to Actions tab
2. Select "Build and Test"
3. Click "Run workflow"
4. Select branch: master
5. Click "Run workflow"
```

---

## What Happens Automatically

### On Every Pull Request
- ✅ Validates POM files
- ✅ Checks code formatting (`mvn fmt:check`)
- ✅ Builds only changed modules (fast feedback)
- ✅ Runs tests with coverage analysis
- ✅ Posts coverage report as PR comment
- ✅ Scans for security vulnerabilities
- ✅ Reviews new dependencies for CVEs

### On Every Commit to Master
- ✅ Full build with all 455 modules
- ✅ Tests on Java 17 and Java 21
- ✅ Uploads distribution ZIPs
- ✅ Deploys to Maven Central (if secrets configured)
- ✅ Tracks coverage trends
- ✅ Complete security scan suite

### Nightly (2 AM UTC)
- ✅ Full build from scratch
- ✅ Creates issue if build fails
- ✅ (Optional) Creates nightly release

### Weekly (Sunday 3 AM UTC)
- ✅ Complete security scan
- ✅ Updates vulnerability database
- ✅ Creates issues for new vulnerabilities

---

## Common Commands

### Trigger Workflows Manually

```bash
# In GitHub UI: Actions tab → Select workflow → Run workflow
```

### Check Build Status

```bash
# View all runs
https://github.com/codice/ddf/actions

# View specific workflow
https://github.com/codice/ddf/actions/workflows/build.yml
```

### Download Artifacts

```bash
# Go to: Actions → Select run → Artifacts section → Download
```

### Create Release

```bash
# Go to: Actions → Release → Run workflow
# Choose:
#   - Release Type: versioned
#   - Version: 2.29.0
# Click: Run workflow
```

---

## Troubleshooting

### Build Taking Too Long?
**Expected:** 90-120 minutes for full build (455 modules)
**Action:** This is normal. Incremental builds are much faster (~15-30 min).

### Out of Memory Error?
**Solution:** Edit `.github/workflows/build.yml`, increase:
```yaml
MAVEN_OPTS: '-Xmx12G -Xms2G ...'
```

### Coverage Below Baseline?
**Current Baseline:** 65%
**Target:** 90-95%
**Action:** See `COMPREHENSIVE-TESTING-STRATEGY.md` for improvement plan.

### Security Scan Failing?
**Action:**
1. Review vulnerability report in workflow artifacts
2. For each CVE, create test (see COMPREHENSIVE-TESTING-STRATEGY.md)
3. Fix or add suppression to `dependency-check-maven-config.xml`

### First OWASP Scan Very Slow?
**Expected:** 45-60 minutes (downloads NVD database)
**Subsequent runs:** 10-15 minutes (cached)

---

## Quick Reference

### Workflow Files
- `build.yml` - Main build and test pipeline
- `test-coverage.yml` - Coverage analysis and enforcement
- `security-scan.yml` - Security scanning suite
- `release.yml` - Release automation

### Documentation
- `.github/workflows/README.md` - Detailed workflow documentation
- `GITHUB-ACTIONS-IMPLEMENTATION.md` - Implementation guide
- This file - Quick start guide

### Build Status Badge

Add to README.md:
```markdown
[![Build Status](https://github.com/codice/ddf/actions/workflows/build.yml/badge.svg)](https://github.com/codice/ddf/actions/workflows/build.yml)
```

---

## Next Steps After Setup

1. ✅ Merge workflows to master
2. ✅ Monitor first full build (90-120 min)
3. ✅ Review coverage report
4. ✅ Check security scan results
5. ✅ Test nightly release workflow
6. ✅ Set up Codecov integration (optional)
7. ✅ Create GitHub Project board for automated issues (optional)

---

## Key Metrics to Watch

### Build Health
- Build success rate: Target > 95%
- Average build time: Target < 120 min
- PR feedback time: Target < 30 min (incremental build)

### Code Coverage
- Current: 65.98%
- Phase 1 (6 months): 75%
- Phase 2 (12 months): 85%
- Target (18 months): 90-95%

### Security
- Critical CVEs (CVSS >= 9): 0
- High CVEs (CVSS >= 7): < 5
- Resolution time: < 7 days

---

## Support

**Questions?** Read the full documentation:
- `.github/workflows/README.md` - Comprehensive workflow guide
- `GITHUB-ACTIONS-IMPLEMENTATION.md` - Implementation details

**Issues?** Create GitHub issue with:
- `github-actions` label
- Link to failed workflow run
- Error message and logs

**Security?** Follow test-first approach:
- Read `COMPREHENSIVE-TESTING-STRATEGY.md`
- Create test reproducing vulnerability
- Submit PR with fix or suppression

---

## Success Checklist

After setup, verify:

- [ ] All secrets configured
- [ ] Branch protection enabled
- [ ] Actions permissions set
- [ ] Test PR created and passed
- [ ] Full build completed on master
- [ ] Coverage report visible
- [ ] Security scans passed
- [ ] Distribution ZIP downloadable
- [ ] Nightly release tested
- [ ] Team notified of new workflows

---

**Time to fully operational:** ~2-3 days
**Time to first successful build:** ~2-3 hours
**Time to configure secrets:** ~5-10 minutes

**Status:** Ready for production use
**Last Updated:** 2025-10-21
