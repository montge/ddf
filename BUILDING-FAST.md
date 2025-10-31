# Fast Development Build Guide

This guide provides strategies for faster iteration during DDF development. The full build can take a long time, so use these techniques to speed up your workflow.

## Quick Reference

| Use Case | Command | Time Saved |
|----------|---------|------------|
| Fast full build | `mvn install -Dfast` | ~40-60% faster |
| Quick compile check | `mvn compile -Dquick` | ~70% faster |
| Build single module | `./build-scripts/build-module.sh <module>` | ~90% faster |
| CI-like build | `mvn install -Dci` | Full validation |
| Format code only | `mvn fmt:format` | Very fast |

## Maven Profiles

### Fast Profile (`-Dfast`)
Skips time-consuming checks for rapid iteration:
```bash
mvn install -Dfast
```

**What it skips:**
- Tests (use `-DskipTests=false` to run them)
- Checkstyle validation
- Dependency vulnerability checks
- Enforcer rules
- Javadoc generation
- Source JAR generation
- Code formatting checks

**When to use:** Development iterations when you need to build quickly

### Quick Profile (`-Dquick`)
Even faster - compiles code but doesn't install to local Maven repo:
```bash
mvn compile -Dquick
```

**When to use:** Just checking if code compiles

### CI Profile (`-Dci`)
Runs all checks like GitHub Actions:
```bash
mvn install -Dci
```

**When to use:** Before pushing to verify CI will pass

## Modular Builds

### Build Single Module
Use the helper script to build just one module:

```bash
# Build a specific module (fast by default)
./build-scripts/build-module.sh platform/security

# Build with tests
./build-scripts/build-module.sh catalog/core/catalog-core-api -DskipTests=false

# Clean and install
./build-scripts/build-module.sh libs clean install
```

### Build from Module Directory
Navigate to any module and build it directly:

```bash
cd catalog/core/catalog-core-standardframework
mvn install -Dfast
```

### Common Module Paths

**Core APIs:**
- `libs` - Shared libraries
- `platform/security/security-core-api` - Security APIs
- `catalog/core/catalog-core-api` - Catalog APIs

**Security:**
- `platform/security` - All security modules
- `platform/security/handler/security-handler-saml` - SAML handler
- `platform/security/handler/security-handler-oidc` - OIDC handler

**Catalog:**
- `catalog/core` - Core catalog functionality
- `catalog/core/catalog-core-standardframework` - Main catalog framework
- `catalog/solr` - Solr integration
- `catalog/rest` - REST endpoints

**Admin UI:**
- `platform/admin` - Admin console

**Distribution:**
- `distribution/ddf` - Full DDF distribution

## Workflow Strategies

### Strategy 1: Module-First Development
When working on a specific feature:

1. **Identify the module** you're working in
2. **Build just that module** during development:
   ```bash
   ./build-scripts/build-module.sh catalog/core/catalog-core-api
   ```
3. **Run pre-commit hooks** before committing:
   ```bash
   pre-commit run --files <changed-files>
   ```
4. **Build dependent modules** if needed
5. **Full fast build** before creating PR:
   ```bash
   mvn install -Dfast
   ```

### Strategy 2: Incremental Testing
Test incrementally to catch issues early:

1. **Quick compile** after changes:
   ```bash
   mvn compile -Dquick
   ```
2. **Fast build** when compile passes:
   ```bash
   mvn install -Dfast
   ```
3. **Run specific tests** for your module:
   ```bash
   cd your-module
   mvn test -Dtest=YourTestClass
   ```
4. **CI build** before pushing:
   ```bash
   mvn install -Dci
   ```

### Strategy 3: Parallel Development
Speed up multi-module work:

```bash
# Build multiple modules in parallel (Maven 3.x)
mvn -T 4 install -Dfast  # Uses 4 threads

# Or use all available cores
mvn -T 1C install -Dfast  # 1 thread per CPU core
```

## Pre-commit and Pre-push Hooks

The repository uses pre-commit hooks to enforce quality:

### Pre-commit Hooks (Run on `git commit`)
- File formatting (trailing whitespace, line endings)
- YAML/XML/JSON validation
- Java code formatting

### Pre-push Hooks (Run on `git push`)
- Checkstyle validation

### Manual Hook Execution
```bash
# Run all pre-commit hooks
pre-commit run --all-files

# Run specific hook
pre-commit run check-yaml --all-files

# Run on specific files
pre-commit run --files catalog/core/pom.xml

# Skip hooks (emergency only!)
git commit --no-verify
git push --no-verify
```

## Troubleshooting

### Build Fails Due to Test Dependency
Some modules depend on test artifacts from other modules. If you get missing test-jar errors:

```bash
# Build the dependency module first
./build-scripts/build-module.sh <dependency-module> -DskipTests=false

# Then build your module
./build-scripts/build-module.sh <your-module>
```

### Clean Build After Major Changes
If you encounter weird errors after pulling latest changes:

```bash
# Clean everything and rebuild
mvn clean install -Dfast

# Or for specific module
cd your-module
mvn clean install -Dfast
```

### Format Errors in CI
If GitHub Actions fails on formatting:

```bash
# Format all code
mvn fmt:format

# Check formatting without fixing
mvn fmt:check
```

### Checkstyle Errors
Fix checkstyle issues:

```bash
# Check what would fail
mvn checkstyle:check

# Auto-format fixes most issues
mvn fmt:format

# Some issues require manual fixes
```

## GitHub Actions Workflow

GitHub Actions uses a parallel test strategy with 7 jobs:
1. **Build** - Compile all modules without tests (~30-40 min)
2. **Test Libs + Platform Core** - Test foundational libraries (~45-70 min)
3. **Test Catalog Core** - Test core catalog (~50-80 min)
4. **Test Features + Transformers + Plugins** - Test plugins (~35-50 min)
5. **Test Admin + Solr + Spatial** - Test admin/search (~40-60 min)
6. **Integration Tests** - OSGi container tests (~60-120 min)
7. **Aggregate Results** - Collect and report results (~10-20 min)

### Simulate CI Build Locally

Run the same build as CI Job 1 (Build All Modules):

```bash
./build-scripts/ci-build-local.sh
```

This runs:
```bash
mvn --batch-mode --errors --fail-at-end --show-version -T 1C clean install -DskipTests
```

### Run Specific Test Groups

Test specific groups like CI does (after building):

```bash
# Test libs and platform core (CI Job 2)
./build-scripts/ci-test-group.sh libs-platform-core

# Test catalog core (CI Job 3)
./build-scripts/ci-test-group.sh catalog-core

# Test features, transformers, plugins (CI Job 4)
./build-scripts/ci-test-group.sh features-transformers-plugins

# Test admin, Solr, spatial (CI Job 5)
./build-scripts/ci-test-group.sh admin-solr-spatial

# Run integration tests (CI Job 6)
./build-scripts/ci-test-group.sh integration
```

### Pre-Push Checklist

To ensure your changes will pass CI:

1. **Format code:**
   ```bash
   mvn fmt:format
   ```

2. **Run pre-commit hooks:**
   ```bash
   pre-commit run --all-files
   ```

3. **Run pre-push hooks:**
   ```bash
   pre-commit run --hook-stage pre-push --all-files
   ```

4. **Build like CI (recommended):**
   ```bash
   ./build-scripts/ci-build-local.sh
   ```

   Or if you prefer traditional Maven:
   ```bash
   mvn clean install -T 1C -DskipTests
   ```

5. **Optionally test specific groups:**
   ```bash
   ./build-scripts/ci-test-group.sh <group-name>
   ```

6. **Commit and push:**
   ```bash
   git add .
   git commit -m "Your message"
   git push
   ```

## Performance Tips

### Maven Settings
Add to `~/.m2/settings.xml` for better performance:

```xml
<settings>
  <localRepository>${user.home}/.m2/repository</localRepository>
  <profiles>
    <profile>
      <id>fast-build</id>
      <properties>
        <!-- Parallel downloads -->
        <maven.artifact.threads>10</maven.artifact.threads>
        <!-- Skip unnecessary resolution -->
        <dependency.resolution>runtime</dependency.resolution>
      </properties>
    </profile>
  </profiles>
</settings>
```

### JVM Options
Set in `.mvn/jvm.config` (already configured):
```
-Xmx4g -Xms1g
-XX:+UseParallelGC
-XX:ReservedCodeCacheSize=512m
```

### Disk I/O
- Use SSD for Maven repository (~/.m2/repository)
- Exclude target/ directories from antivirus scanning
- On Windows: Enable Developer Mode for faster file operations

## Advanced: Reactor Build Customization

Build only specific modules and their dependencies:

```bash
# Build from a specific module onward
mvn install -Dfast -pl catalog/core/catalog-core-api -am

# Build specific modules without dependencies
mvn install -Dfast -pl catalog/core/catalog-core-api

# Build everything except tests
mvn install -Dfast -pl '!distribution/test'
```

Flags:
- `-pl` (--projects): Select specific modules
- `-am` (--also-make): Build dependencies too
- `-amd` (--also-make-dependents): Build dependent modules too
- `-rf` (--resume-from): Resume from specific module after failure

## Examples

### Example 1: Working on SAML Handler
```bash
# Make changes to SAML handler
cd platform/security/handler/security-handler-saml

# Quick compile check
mvn compile -Dquick

# Build this module
mvn install -Dfast

# Run tests for this module only
mvn test

# Format before committing
mvn fmt:format

# Commit
git add .
git commit -m "Fix SAML assertion validation"
```

### Example 2: Working on Catalog API
```bash
# Build the API module
./build-scripts/build-module.sh catalog/core/catalog-core-api

# Build modules that depend on it
./build-scripts/build-module.sh catalog/core/catalog-core-standardframework

# Run pre-commit before committing
pre-commit run --all-files

# Push and let CI do full validation
git push
```

### Example 3: Preparing for PR
```bash
# Format all code
mvn fmt:format

# Run pre-commit hooks
pre-commit run --all-files

# Full fast build
mvn clean install -Dfast

# If that passes, run CI-like build
mvn clean install -Dci

# Create commit and push
git add .
git commit -m "Add feature X"
git push
```

## Summary

**For fastest iteration:**
1. Use `-Dfast` for regular builds
2. Build only the module you're working on
3. Use pre-commit hooks to catch issues early
4. Run full CI build before creating PRs

**Remember:** The build tools are here to help you iterate faster while maintaining quality. Don't skip validation entirely - just defer it to the right times in your workflow.
