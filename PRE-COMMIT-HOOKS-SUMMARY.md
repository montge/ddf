# Pre-commit Hooks Configuration Summary

## Quick Start

```bash
# 1. Install pre-commit framework
pip install pre-commit

# 2. Install hooks
pre-commit install
pre-commit install --hook-type pre-push

# 3. (Optional) Run on all files
pre-commit run --all-files
```

## Files Created

1. **`.pre-commit-config.yaml`** - Main configuration file
2. **`PRE-COMMIT-HOOKS-GUIDE.md`** - Comprehensive documentation

## Configuration Overview

### Hooks by Stage

#### Pre-commit Stage (5-15 seconds)
Fast checks that run on every `git commit`:

- **File Quality** (12 hooks)
  - trailing-whitespace, end-of-file-fixer, mixed-line-ending
  - check-yaml, check-xml, check-json
  - check-added-large-files, check-merge-conflict, check-case-conflict
  - detect-private-key, check-symlinks, destroyed-symlinks

- **Java Formatting** (1 hook)
  - `java-format`: Runs `mvn fmt:format` on changed Java files

- **Maven Validation** (1 hook)
  - `maven-validate`: Validates all pom.xml files

#### Pre-push Stage (30-60 seconds)
Moderate checks that run on every `git push`:

- **Static Analysis** (1 hook)
  - `maven-checkstyle`: Runs checkstyle:check (same as CI)

- **Compilation Check** (1 hook)
  - `maven-compile-all`: Compiles main + test sources

#### Manual Stage (2-10 minutes)
Slow checks run only when explicitly requested:

- **Full Test Suite** (1 hook)
  - `maven-test-all`: Runs entire test suite

- **Format Check** (1 hook)
  - `java-format-check`: Verifies formatting without changes

**Total:** 18 hooks across 3 stages

## Performance Expectations

| Stage | Command | Time (typical) | Time (large) |
|-------|---------|----------------|--------------|
| Pre-commit | `git commit` | 5-15 sec | 15-30 sec |
| Pre-push | `git push` | 30-60 sec | 60-120 sec |
| Manual | `pre-commit run --hook-stage manual` | 2-5 min | 5-10 min |

**First run:** Expect 3-5 minutes for environment setup and caching.

## What Gets Checked

### Java Files (.java)
- ✅ Google Java Format (auto-applied)
- ✅ Checkstyle validation (on push)
- ✅ Compilation check (on push)
- ✅ File quality (whitespace, line endings)

### XML Files (*.xml, pom.xml)
- ✅ XML syntax validation
- ✅ Maven POM validation
- ✅ File quality (whitespace, line endings)

### YAML Files (*.yaml, *.yml)
- ✅ YAML syntax validation
- ✅ File quality (whitespace, line endings)

### JSON Files (*.json)
- ✅ JSON syntax validation
- ✅ File quality (whitespace, line endings)

### All Files
- ✅ Trailing whitespace removed
- ✅ End-of-file newlines fixed
- ✅ Line endings normalized (LF)
- ✅ Large files blocked (>1MB)
- ✅ Merge conflicts detected
- ✅ Private keys detected

## Integration Points

### With Maven Build Tools

The pre-commit hooks use **the same Maven plugins** configured in `pom.xml`:

| Hook | Maven Plugin | Goal |
|------|--------------|------|
| java-format | fmt-maven-plugin (v2.9.1) | format |
| maven-checkstyle | maven-checkstyle-plugin | checkstyle:check |
| maven-compile-all | maven-compiler-plugin | test-compile |

**Result:** Local checks match CI/CD exactly - no surprises!

### With CI/CD Pipeline

| CI/CD Check | Pre-commit Hook | When |
|-------------|-----------------|------|
| `mvn fmt:check` | java-format | commit |
| `mvn checkstyle:check` | maven-checkstyle | push |
| `mvn test-compile` | maven-compile-all | push |
| `mvn test` | maven-test-all | manual |

**Benefit:** Catch CI failures locally before pushing.

### With Claude Code Auto-fix

The configuration is optimized for Claude Code workflows:

```
1. Claude makes changes to Java files
   ↓
2. Developer runs: git commit
   ↓
3. Pre-commit auto-formats Java code (fmt:format)
   ↓
4. If errors occur, Claude sees them and fixes
   ↓
5. Developer runs: git commit (retry)
   ↓
6. Commit succeeds with properly formatted code
```

**Key Features:**
- ✅ Automatic formatting (no manual `mvn fmt:format` needed)
- ✅ Immediate error feedback (seconds, not minutes)
- ✅ Same tools as CI (consistent results)
- ✅ Claude can see and fix hook failures

## Common Usage

### Normal Development Workflow
```bash
# Make changes
vim src/main/java/MyClass.java

# Commit (hooks run automatically)
git commit -m "feat: add new feature"
# → Runs: file checks, java-format, maven-validate (5-15 sec)

# Push (hooks run automatically)
git push origin feature-branch
# → Runs: checkstyle, compilation (30-60 sec)
```

### Running Hooks Manually
```bash
# Run all pre-commit hooks
pre-commit run --all-files

# Run specific hook
pre-commit run java-format --all-files
pre-commit run maven-checkstyle --all-files

# Run pre-push hooks
pre-commit run --hook-stage push --all-files

# Run full test suite
pre-commit run --hook-stage manual maven-test-all
```

### Bypassing Hooks (Emergency Use Only)
```bash
# Skip all hooks for one commit
git commit --no-verify -m "Emergency fix"

# Skip specific hook
SKIP=maven-checkstyle git commit -m "Fix compilation"
```

**Warning:** Hooks will still run in CI/CD, so bypassing locally just defers checks.

## Troubleshooting Quick Reference

| Problem | Solution |
|---------|----------|
| "pre-commit: command not found" | `pip install pre-commit` |
| Hooks are slow (first run) | Normal - environment setup takes 3-5 min |
| Hooks are slow (every run) | Run on smaller changesets |
| "Maven not found" | Ensure Maven is in PATH |
| "JAVA_HOME not set" | `export JAVA_HOME=/path/to/jdk-11` |
| Checkstyle fails | Run `mvn fmt:format` first |
| fmt-maven-plugin not found | Run `mvn compile` to download plugins |
| YAML parse error | Check for tabs (must use spaces) |

## Configuration Customization

To customize behavior, edit `.pre-commit-config.yaml`:

**Example: Change file size limit**
```yaml
- id: check-added-large-files
  args: [--maxkb=2000]  # Change from 1MB to 2MB
```

**Example: Exclude more directories**
```yaml
exclude: |
  (?x)^(
    .*/target/.*|
    my-custom-dir/.*
  )$
```

**Example: Run checkstyle on commit instead of push**
```yaml
- id: maven
  alias: maven-checkstyle
  stages: [commit]  # Change from [push]
```

## Best Practices

1. ✅ **Commit small, frequent changes** - hooks run faster
2. ✅ **Let hooks auto-fix formatting** - don't manually format
3. ✅ **Run hooks before creating PRs** - catch issues early
4. ✅ **Don't bypass hooks regularly** - defeats the purpose
5. ✅ **Update hooks monthly** - `pre-commit autoupdate`
6. ✅ **Share hook configurations** - keep team in sync

## Benefits Summary

### For Developers
- ⚡ **Faster feedback**: 5-60 seconds vs 5-10 minutes (CI/CD)
- 🔧 **Auto-formatting**: No manual `mvn fmt:format` needed
- 🐛 **Early bug detection**: Catch compilation errors before pushing
- 🎯 **Consistent style**: Same formatting as team/CI
- 💪 **Confidence**: Know your code will pass CI

### For the Team
- 📉 **Fewer CI failures**: Issues caught locally first
- 🔄 **Consistent codebase**: Everyone uses same formatting
- ⏱️ **Faster reviews**: PRs pre-validated for formatting
- 🛡️ **Better security**: Private keys detected before commit
- 📚 **Knowledge sharing**: Standardized tooling

### For Claude Code
- 🤖 **Seamless integration**: Auto-format after changes
- 👀 **Visible errors**: Claude sees and fixes hook failures
- ⚙️ **Same tools**: Local checks match CI/CD
- 🔁 **Fast iteration**: Fix errors in seconds, not minutes

## Next Steps

1. **Install pre-commit**: `pip install pre-commit`
2. **Install hooks**: `pre-commit install && pre-commit install --hook-type pre-push`
3. **Test on current files**: `pre-commit run --all-files`
4. **Fix any issues**: Use Claude Code or manual fixes
5. **Start committing**: Hooks run automatically from now on

For detailed documentation, see **`PRE-COMMIT-HOOKS-GUIDE.md`**.

## Support

If you encounter issues:

1. Check `PRE-COMMIT-HOOKS-GUIDE.md` troubleshooting section
2. Run with verbose output: `pre-commit run --verbose --all-files`
3. Check Maven directly: `mvn -X <goal>`
4. Review pre-commit docs: https://pre-commit.com
5. Consult DDF build docs: `CLAUDE.md`

---

**Configuration Version:** 1.0
**Created:** 2025-10-28
**DDF Version:** 2.29.0-SNAPSHOT
**Pre-commit Framework:** https://pre-commit.com
**Maven Hooks:** https://github.com/ejba/pre-commit-maven
