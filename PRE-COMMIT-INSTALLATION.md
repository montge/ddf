# Pre-commit Hooks - Quick Installation Guide

This is a quick-start guide for installing and using pre-commit hooks in the DDF project.

## Prerequisites

- Python 3.7+ (check with: `python3 --version`)
- Maven 3.6.3+ (check with: `mvn --version`)
- Java 11 (check with: `java -version`)

## Installation (5 minutes)

### Step 1: Install pre-commit framework

```bash
pip install pre-commit
```

Or using pip3:
```bash
pip3 install pre-commit
```

Or using Homebrew (macOS):
```bash
brew install pre-commit
```

Verify:
```bash
pre-commit --version
# Should output: pre-commit 3.x.x or higher
```

### Step 2: Install git hooks

Navigate to DDF repository root:
```bash
cd /path/to/ddf
```

Install the hooks:
```bash
pre-commit install
pre-commit install --hook-type pre-push
```

You should see:
```
pre-commit installed at .git/hooks/pre-commit
pre-commit installed at .git/hooks/pre-push
```

### Step 3: (Optional) Run on existing files

```bash
pre-commit run --all-files
```

**Note:** First run takes 3-5 minutes to set up environments. Subsequent runs are much faster.

## Verification

Test that hooks work:

```bash
# Create a test commit
touch test-file.txt
git add test-file.txt
git commit -m "test: verify pre-commit hooks"
```

You should see hooks running:
```
Trim trailing whitespace.........................................Passed
Fix end of files.................................................Passed
Fix mixed line endings...........................................Passed
...
```

If hooks ran, you're all set! You can now remove the test file:
```bash
git reset HEAD~1
rm test-file.txt
```

## What Happens Now

### On every `git commit`:
- File quality checks (whitespace, line endings)
- Java code formatting (Google Java Format)
- YAML/XML/JSON validation
- Maven POM validation
- **Time:** 5-15 seconds

### On every `git push`:
- Checkstyle validation
- Compilation check (main + test sources)
- **Time:** 30-60 seconds

### Manual runs (when you want):
- Full test suite: `pre-commit run --hook-stage manual maven-test-all`
- **Time:** 2-10 minutes

## Usage Examples

### Normal workflow (hooks run automatically)
```bash
# Make changes
vim src/main/java/MyClass.java

# Commit (hooks run)
git commit -m "feat: new feature"

# Push (hooks run)
git push origin my-branch
```

### Run hooks manually
```bash
# Run all hooks on all files
pre-commit run --all-files

# Run specific hook
pre-commit run java-format --all-files

# Run push hooks without pushing
pre-commit run --hook-stage push --all-files
```

### Bypass hooks (emergency only)
```bash
# Skip all hooks
git commit --no-verify -m "emergency fix"

# Skip specific hook
SKIP=maven-checkstyle git commit -m "fix"
```

## Updating Hooks

Keep hooks up to date:
```bash
pre-commit autoupdate
```

## Uninstalling

To remove hooks (but keep configuration):
```bash
pre-commit uninstall
pre-commit uninstall --hook-type pre-push
```

To completely remove:
```bash
pre-commit uninstall
pre-commit uninstall --hook-type pre-push
pip uninstall pre-commit
```

## Troubleshooting

### Issue: "pre-commit: command not found"
**Solution:** Install pre-commit: `pip install pre-commit`

### Issue: Hooks are very slow
**Solution:** First run is slow (3-5 min). Subsequent runs are faster. If still slow, commit smaller changes.

### Issue: "Maven not found"
**Solution:** Ensure Maven is in PATH: `mvn --version`

### Issue: "JAVA_HOME not set"
**Solution:** Set JAVA_HOME: `export JAVA_HOME=/path/to/jdk-11`

### Issue: Checkstyle fails
**Solution:** Format code first: `mvn fmt:format`, then retry

## Documentation

For complete documentation, see:

- **PRE-COMMIT-HOOKS-SUMMARY.md** - Quick reference
- **PRE-COMMIT-HOOKS-GUIDE.md** - Comprehensive guide
- **CLAUDE.md** - DDF project overview

## Help

If you have questions or issues:

1. Read the troubleshooting section in `PRE-COMMIT-HOOKS-GUIDE.md`
2. Run with verbose output: `pre-commit run --verbose --all-files`
3. Check pre-commit docs: https://pre-commit.com

---

That's it! Pre-commit hooks are now protecting your commits and helping maintain code quality automatically.
