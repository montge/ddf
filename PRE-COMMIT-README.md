# Pre-commit Hooks for DDF

Automated code quality checks for the DDF (Distributed Data Framework) project.

## Quick Start

```bash
# 1. Install pre-commit
pip install pre-commit

# 2. Install hooks
pre-commit install
pre-commit install --hook-type pre-push

# 3. Done! Hooks now run automatically
```

## What You Get

- ✅ **Automatic Java formatting** (Google Java Format)
- ✅ **Checkstyle validation** (same as CI/CD)
- ✅ **Compilation checks** (catch errors before pushing)
- ✅ **File quality** (whitespace, line endings, etc.)
- ✅ **Security checks** (private keys, large files)
- ✅ **YAML/XML/JSON validation**

## Performance

- **Pre-commit:** 5-15 seconds (every commit)
- **Pre-push:** 30-60 seconds (every push)
- **Manual tests:** 2-10 minutes (on-demand)

## Documentation

| Document | Purpose |
|----------|---------|
| **[PRE-COMMIT-INSTALLATION.md](PRE-COMMIT-INSTALLATION.md)** | Step-by-step installation |
| **[PRE-COMMIT-HOOKS-SUMMARY.md](PRE-COMMIT-HOOKS-SUMMARY.md)** | Quick reference |
| **[PRE-COMMIT-HOOKS-GUIDE.md](PRE-COMMIT-HOOKS-GUIDE.md)** | Comprehensive guide |
| **[PRE-COMMIT-IMPLEMENTATION-REPORT.md](PRE-COMMIT-IMPLEMENTATION-REPORT.md)** | Technical details |

## Basic Usage

**Normal workflow (automatic):**
```bash
git add .
git commit -m "feat: new feature"  # Hooks run automatically
git push                            # More hooks run
```

**Run manually:**
```bash
pre-commit run --all-files          # All hooks
pre-commit run java-format          # Specific hook
```

**Bypass (emergency only):**
```bash
git commit --no-verify              # Skip all hooks
SKIP=maven-checkstyle git commit    # Skip specific hook
```

## Hooks Overview

### Pre-commit (Fast)
- File quality (12 hooks)
- Java formatting (1 hook)
- Maven validation (1 hook)

### Pre-push (Moderate)
- Checkstyle (1 hook)
- Compilation (1 hook)

### Manual (Slow)
- Full test suite (1 hook)
- Format verification (1 hook)

**Total: 18 hooks**

## Benefits

### For Developers
- ⚡ Faster feedback (seconds vs minutes)
- 🔧 Auto-formatting (no manual steps)
- 🐛 Early error detection
- 💪 Confidence in PR quality

### For the Team
- 📉 Fewer CI failures
- 🔄 Consistent code style
- ⏱️ Faster code reviews
- 🛡️ Better security

### For Claude Code
- 🤖 Seamless integration
- 👀 Visible error feedback
- ⚙️ Same tools as CI
- 🔁 Fast iteration

## Integration

**With Maven:**
- Uses existing fmt-maven-plugin
- Uses existing checkstyle configuration
- Leverages Maven cache

**With CI/CD:**
- Same tools and configs
- Catches issues before CI
- Reduces CI roundtrip time

**With Claude Code:**
- Auto-formats after edits
- Shows errors immediately
- Enables iterative fixing

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Command not found | `pip install pre-commit` |
| Hooks are slow | First run takes 3-5 min (caching) |
| Maven not found | Ensure Maven is in PATH |
| Checkstyle fails | Run `mvn fmt:format` first |

See [PRE-COMMIT-HOOKS-GUIDE.md](PRE-COMMIT-HOOKS-GUIDE.md) for detailed troubleshooting.

## Configuration

Edit `.pre-commit-config.yaml` to customize:
- Hook behavior
- File exclusions
- Stage assignments
- Performance tuning

## Support

- **Installation:** [PRE-COMMIT-INSTALLATION.md](PRE-COMMIT-INSTALLATION.md)
- **Quick Reference:** [PRE-COMMIT-HOOKS-SUMMARY.md](PRE-COMMIT-HOOKS-SUMMARY.md)
- **Full Guide:** [PRE-COMMIT-HOOKS-GUIDE.md](PRE-COMMIT-HOOKS-GUIDE.md)
- **Implementation:** [PRE-COMMIT-IMPLEMENTATION-REPORT.md](PRE-COMMIT-IMPLEMENTATION-REPORT.md)
- **Pre-commit Docs:** https://pre-commit.com
- **DDF Project:** [CLAUDE.md](CLAUDE.md)

## Summary

This pre-commit configuration provides:
- ✅ 18 hooks across 3 stages
- ✅ Automatic Java formatting
- ✅ CI/CD alignment
- ✅ Claude Code optimization
- ✅ 1,900+ lines of documentation
- ✅ Production-ready

**Status:** Ready to use immediately!

---

**Version:** 1.0
**Created:** 2025-10-28
**Framework:** https://pre-commit.com
**License:** Same as DDF project
