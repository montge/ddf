# Pre-commit Hooks Guide for DDF

This guide explains the pre-commit hook configuration for the DDF (Distributed Data Framework) project.

## Table of Contents
- [Overview](#overview)
- [Installation](#installation)
- [What Each Hook Does](#what-each-hook-does)
- [Performance Expectations](#performance-expectations)
- [Usage Examples](#usage-examples)
- [Integration with Claude Code](#integration-with-claude-code)
- [CI/CD Integration](#cicd-integration)
- [Troubleshooting](#troubleshooting)

## Overview

Pre-commit hooks help maintain code quality by automatically checking and formatting code before it's committed or pushed. The DDF configuration uses a **staged approach**:

1. **Pre-commit stage (fast)**: Runs on every `git commit` - file quality checks and formatting
2. **Pre-push stage (moderate)**: Runs on every `git push` - checkstyle and compilation
3. **Manual stage (slow)**: Run explicitly for full test suite

This design ensures fast feedback during development while catching issues before they reach CI/CD.

## Installation

### Prerequisites

1. **Python 3.7 or higher** (required by pre-commit framework)
   ```bash
   python3 --version  # Should be 3.7+
   ```

2. **Maven 3.6.3 or higher** (already required by DDF)
   ```bash
   mvn --version
   ```

3. **Java 11** (already required by DDF)
   ```bash
   java -version
   ```

### Step 1: Install pre-commit framework

Choose one of the following methods:

**Using pip (recommended):**
```bash
pip install pre-commit
```

**Using pip3 explicitly:**
```bash
pip3 install pre-commit
```

**Using Homebrew (macOS):**
```bash
brew install pre-commit
```

**Using conda:**
```bash
conda install -c conda-forge pre-commit
```

Verify installation:
```bash
pre-commit --version
```

### Step 2: Install the git hooks

Navigate to the DDF repository root and run:

```bash
cd /path/to/ddf
pre-commit install
pre-commit install --hook-type pre-push
```

This creates hooks in `.git/hooks/` that will run automatically.

### Step 3: (Optional) Run on all files initially

To check all existing files (useful for first-time setup):

```bash
pre-commit run --all-files
```

**Note:** This may take several minutes on first run as it sets up environments and caches.

## What Each Hook Does

### Stage 1: Pre-commit (Fast File Checks)

These hooks run on every commit and take 5-15 seconds:

#### **trailing-whitespace**
- **What it does**: Removes trailing whitespace from end of lines
- **Why**: Prevents unnecessary diffs and keeps code clean
- **Files**: All text files except `.patch` files
- **Auto-fix**: Yes

#### **end-of-file-fixer**
- **What it does**: Ensures files end with exactly one newline
- **Why**: POSIX compliance and consistent diffs
- **Files**: All text files
- **Auto-fix**: Yes

#### **mixed-line-ending**
- **What it does**: Converts all line endings to LF (Unix style)
- **Why**: Consistent line endings across platforms
- **Files**: All except `.bat` and `.cmd` (Windows scripts)
- **Auto-fix**: Yes

#### **check-yaml**
- **What it does**: Validates YAML syntax
- **Why**: Catches YAML errors before they break builds
- **Files**: `*.yaml`, `*.yml`
- **Auto-fix**: No (reports errors)

#### **check-xml**
- **What it does**: Validates XML syntax including `pom.xml`
- **Why**: Catches malformed XML before Maven errors
- **Files**: `*.xml` (including all POMs)
- **Auto-fix**: No (reports errors)

#### **check-json**
- **What it does**: Validates JSON syntax
- **Why**: Catches JSON errors in configuration files
- **Files**: `*.json`
- **Auto-fix**: No (reports errors)

#### **check-added-large-files**
- **What it does**: Warns if files > 1MB are added
- **Why**: Prevents bloating the git repository
- **Files**: All except build artifacts
- **Auto-fix**: No (blocks commit)

#### **check-merge-conflict**
- **What it does**: Detects merge conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)
- **Why**: Prevents committing unresolved conflicts
- **Files**: All text files
- **Auto-fix**: No (blocks commit)

#### **detect-private-key**
- **What it does**: Scans for private key files (RSA, DSA, EC, etc.)
- **Why**: Prevents accidentally committing secrets
- **Files**: All except test resources
- **Auto-fix**: No (blocks commit)

#### **java-format (fmt:format)**
- **What it does**: Formats Java code using Google Java Format
- **Why**: Enforces consistent code style automatically
- **Files**: `*.java` (except generated sources)
- **Auto-fix**: Yes
- **Maven goal**: `com.spotify.fmt:fmt-maven-plugin:format`
- **Note**: Uses the **same** fmt-maven-plugin configured in `pom.xml`

### Stage 2: Pre-commit (Maven Validation)

#### **maven-validate**
- **What it does**: Validates all `pom.xml` files
- **Why**: Catches Maven configuration errors early
- **Files**: `pom.xml`
- **Auto-fix**: No (reports errors)
- **Maven goal**: `mvn validate -q`

### Stage 3: Pre-push (Checkstyle)

These hooks run on `git push` and take 20-40 seconds:

#### **maven-checkstyle**
- **What it does**: Runs Checkstyle static analysis
- **Why**: Enforces code quality rules (same as CI/CD)
- **Files**: `*.java`
- **Auto-fix**: No (reports violations)
- **Maven goal**: `mvn checkstyle:check -DskipTests=true`
- **Note**: Uses the **same** Checkstyle config from `support-checkstyle` dependency

### Stage 4: Pre-push (Compilation)

#### **maven-compile-all**
- **What it does**: Compiles main and test sources
- **Why**: Catches compilation errors and missing imports before pushing
- **Files**: `*.java`, `*.xml`
- **Auto-fix**: No (reports compilation errors)
- **Maven goal**: `mvn test-compile -DskipTests=true -DskipStatic=true`
- **Impact**: Prevents pushing code that won't compile in CI

### Stage 5: Manual (Full Tests)

These hooks run only when explicitly requested:

#### **maven-test-all**
- **What it does**: Runs the full test suite
- **Why**: Comprehensive validation before major pushes
- **Files**: All
- **Auto-fix**: No (reports test failures)
- **Maven goal**: `mvn test`
- **Usage**: `pre-commit run --hook-stage manual maven-test-all`

#### **java-format-check**
- **What it does**: Checks formatting without modifying files
- **Why**: Verify formatting in CI/CD pipelines
- **Files**: `*.java`
- **Auto-fix**: No (reports violations)
- **Maven goal**: `mvn com.spotify.fmt:fmt-maven-plugin:check`

## Performance Expectations

### Typical Timing (on a modern laptop)

| Stage | Hooks | Time (small change) | Time (large change) |
|-------|-------|---------------------|---------------------|
| **Pre-commit** | File checks + Java format | 5-15 seconds | 15-30 seconds |
| **Pre-push** | Checkstyle + Compilation | 30-60 seconds | 60-120 seconds |
| **Manual** | Full test suite | 2-5 minutes | 5-10 minutes |

### First Run Performance

The **first time** you run pre-commit hooks, expect:
- **3-5 minutes** for environment setup and caching
- Subsequent runs are much faster due to caching

### Optimization Tips

1. **Commit frequently with small changes** - hooks run faster
2. **Use `--no-verify` sparingly** - only for emergencies
3. **Run manual tests explicitly** - don't make them automatic
4. **Pre-push hooks run in parallel with git push** - minimal impact

## Usage Examples

### Basic Workflow

```bash
# Make changes to Java files
vim catalog/core/catalog-core-api/src/main/java/ddf/catalog/CatalogFramework.java

# Stage changes
git add .

# Commit (pre-commit hooks run automatically)
git commit -m "Add new method to CatalogFramework"
# → Runs: file checks, java-format, maven-validate (5-15 sec)

# Push (pre-push hooks run automatically)
git push origin feature-branch
# → Runs: checkstyle, compilation (30-60 sec)
```

### Running Hooks Manually

**Run all pre-commit hooks on all files:**
```bash
pre-commit run --all-files
```

**Run all pre-commit hooks on staged files only:**
```bash
pre-commit run
```

**Run specific hook:**
```bash
pre-commit run java-format --all-files
pre-commit run maven-checkstyle --all-files
```

**Run pre-push hooks:**
```bash
pre-commit run --hook-stage push --all-files
```

**Run manual-only hooks (full test suite):**
```bash
pre-commit run --hook-stage manual maven-test-all
```

**Run on specific files:**
```bash
pre-commit run --files catalog/core/catalog-core-api/src/main/java/ddf/catalog/*.java
```

### Bypassing Hooks (Use Sparingly!)

**Skip all hooks for a single commit:**
```bash
git commit --no-verify -m "Emergency hotfix"
```

**Skip specific hook:**
```bash
SKIP=maven-checkstyle git commit -m "Fix compilation error"
```

**Skip multiple hooks:**
```bash
SKIP=maven-checkstyle,java-format git commit -m "WIP: refactoring"
```

**Note:** Hooks will still run in CI/CD, so bypassing locally just defers the checks.

### Updating Hook Versions

Update to latest versions of all hooks:

```bash
pre-commit autoupdate
```

This updates the `rev:` field in `.pre-commit-config.yaml`.

### Uninstalling Hooks

To remove pre-commit hooks (but keep `.pre-commit-config.yaml`):

```bash
pre-commit uninstall
pre-commit uninstall --hook-type pre-push
```

## Integration with Claude Code

The pre-commit configuration is designed to work seamlessly with Claude Code's auto-fix workflows:

### Automatic Formatting

```
Claude Code makes changes → pre-commit formats Java automatically → ready to commit
```

**Example:**
1. Claude Code edits `CatalogFrameworkImpl.java`
2. You run: `git add . && git commit -m "Update query logic"`
3. Pre-commit runs `fmt:format` automatically
4. Code is formatted according to Google Java Style
5. Commit succeeds with properly formatted code

### Error Detection and Fixing

**Scenario 1: Missing Import**
```
Claude adds code → commit triggers → compilation fails → Claude sees error → adds import
```

**Scenario 2: Checkstyle Violation**
```
Claude adds code → push triggers → checkstyle fails → Claude sees violation → fixes it
```

**Scenario 3: Test Compilation Error**
```
Claude modifies test → push triggers → test compilation fails → Claude fixes imports/API usage
```

### Benefits for Claude Code Workflows

1. **Immediate Feedback**: Errors caught in seconds, not minutes (CI/CD)
2. **Same Tools as CI**: No "works locally, fails in CI" surprises
3. **Auto-fix Capable**: Java formatting happens automatically
4. **Iterative Fixing**: Claude can see hook errors and fix them immediately
5. **No Manual Steps**: Hooks run automatically, no need to remember `mvn fmt:format`

### Claude Code Commands

```bash
# After Claude makes changes
git add .
git commit -m "feat: implement feature X"
# → Pre-commit runs automatically, Claude sees any errors

# If hooks fail, Claude can fix and retry
git add .
git commit -m "feat: implement feature X"
# → Hooks pass, commit succeeds
```

## CI/CD Integration

The pre-commit configuration **mirrors** the checks run in CI/CD pipelines:

### Local vs CI Alignment

| Check | Pre-commit Hook | CI/CD Equivalent | Stage |
|-------|----------------|------------------|-------|
| Java formatting | `java-format` | `mvn fmt:check` | commit |
| Checkstyle | `maven-checkstyle` | `mvn checkstyle:check` | push |
| Compilation | `maven-compile-all` | `mvn test-compile` | push |
| Tests | `maven-test-all` (manual) | `mvn test` | CI |
| XML validation | `check-xml` | Maven build | commit |
| YAML validation | `check-yaml` | CI linting | commit |

### Running CI Checks Locally

Replicate CI/CD checks before pushing:

```bash
# Format code
mvn fmt:format

# Run checkstyle
mvn checkstyle:check

# Compile everything
mvn test-compile -DskipTests

# Run tests
mvn test

# OR: Run all via pre-commit
pre-commit run --all-files
pre-commit run --hook-stage push --all-files
pre-commit run --hook-stage manual maven-test-all
```

### Benefits of Local-CI Alignment

1. **Faster feedback loop**: Catch issues in seconds locally vs minutes in CI
2. **Reduced CI failures**: Most issues caught before pushing
3. **Consistent environments**: Same Maven goals and configurations
4. **Developer confidence**: Know your PR will pass CI before creating it

## Troubleshooting

### Common Issues and Solutions

#### Issue: "pre-commit: command not found"

**Solution:**
```bash
# Install pre-commit
pip install pre-commit
# OR
pip3 install pre-commit

# Verify
pre-commit --version
```

#### Issue: "Maven not found" or "JAVA_HOME not set"

**Solution:**
```bash
# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk-11

# Verify Maven
mvn --version
```

#### Issue: Hooks are very slow

**Solutions:**
1. **First run is always slow** (3-5 min) due to environment setup
2. **Run on fewer files**: `pre-commit run --files path/to/file.java`
3. **Skip slow hooks temporarily**: `SKIP=maven-checkstyle git commit`
4. **Check if Maven repo is cached**: Ensure `~/.m2/repository` exists

#### Issue: "fmt-maven-plugin not found"

**Cause:** Maven hasn't downloaded the plugin yet.

**Solution:**
```bash
# Download plugin manually
mvn com.spotify.fmt:fmt-maven-plugin:format -Dfmt.skip=true

# Then retry pre-commit
pre-commit run java-format --all-files
```

#### Issue: Checkstyle fails with many violations

**Solution:**
```bash
# Auto-fix formatting first
mvn fmt:format

# Then check what's left
mvn checkstyle:check

# Fix remaining violations manually or with Claude Code
```

#### Issue: Hooks fail but CI passes (or vice versa)

**Cause:** Versions or configurations differ.

**Solution:**
```bash
# Update hooks to latest versions
pre-commit autoupdate

# Ensure Maven is same version as CI
mvn --version  # Should match CI version

# Check fmt-maven-plugin version in pom.xml matches
```

#### Issue: Hook fails with "pass_filenames" error

**Cause:** Maven hooks don't accept individual filenames.

**Solution:** This is expected behavior. Maven hooks run on the entire project, not individual files. The `pass_filenames: false` setting in `.pre-commit-config.yaml` handles this.

#### Issue: "Unable to parse .pre-commit-config.yaml"

**Solution:**
```bash
# Validate YAML syntax
python3 -c "import yaml; yaml.safe_load(open('.pre-commit-config.yaml'))"

# Check for tabs (must use spaces)
grep -P '\t' .pre-commit-config.yaml
```

### Getting Help

If you encounter issues not covered here:

1. **Check pre-commit logs**: `cat .git/hooks/pre-commit`
2. **Run with verbose output**: `pre-commit run --verbose --all-files`
3. **Check Maven output**: `mvn -X <goal>` (debug mode)
4. **Review pre-commit docs**: https://pre-commit.com
5. **Check DDF build docs**: `CLAUDE.md` in repository root

## Advanced Configuration

### Customizing Hook Behavior

Edit `.pre-commit-config.yaml` to customize:

**Change max file size:**
```yaml
- id: check-added-large-files
  args: [--maxkb=2000]  # Increase to 2MB
```

**Exclude more directories:**
```yaml
exclude: |
  (?x)^(
    .*/target/.*|
    .*/my-custom-dir/.*
  )$
```

**Add hook to different stage:**
```yaml
- id: maven-checkstyle
  stages: [commit]  # Run on commit instead of push
```

**Disable a hook:**
```yaml
- id: maven-checkstyle
  stages: [manual]  # Never run automatically
```

### Adding New Hooks

To add custom hooks, see the pre-commit documentation:
- https://pre-commit.com/#adding-pre-commit-plugins-to-your-project
- https://pre-commit.com/hooks.html

## Best Practices

1. **Commit frequently**: Smaller commits = faster hooks
2. **Let hooks auto-fix**: Don't manually fix what hooks can fix
3. **Run hooks before creating PR**: Catch issues early
4. **Don't bypass hooks regularly**: They're there for a reason
5. **Keep hooks updated**: Run `pre-commit autoupdate` monthly
6. **Test hooks on feature branches**: Don't experiment on main/master
7. **Share hook failures**: Help teammates avoid the same issues

## Summary

The DDF pre-commit hook configuration provides:

- **Fast feedback** on code quality (5-60 seconds vs 5-10 minutes in CI)
- **Automatic formatting** with Google Java Format
- **Early error detection** for compilation and imports
- **CI/CD alignment** using the same Maven plugins and configs
- **Claude Code integration** for seamless auto-fix workflows
- **Flexible staging** (commit/push/manual) to balance speed and thoroughness

For most developers, hooks will "just work" and catch issues before they reach CI/CD. Happy coding!
