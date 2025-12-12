# Platform Specification

## Purpose
Define requirements for the DDF platform infrastructure including build, CI/CD, and operational capabilities.

## Current State
- **Build System:** Maven 3.6.3+
- **CI/CD:** GitHub Actions (4 workflows)
- **Code Style:** google-java-format
- **Container:** Apache Karaf 4.4.8

---

## Requirements

### Requirement: Build Performance
The system MUST support fast development iteration.

#### Scenario: Fast Build
- GIVEN developer runs `mvn install -Dfast`
- WHEN build completes
- THEN elapsed time SHOULD be under 10 minutes for clean build

#### Scenario: Single Module Build
- GIVEN developer needs to rebuild one module
- WHEN running single module build
- THEN elapsed time SHOULD be under 60 seconds

---

### Requirement: Code Quality
The system MUST enforce consistent code style and quality standards.

#### Scenario: Format Validation
- GIVEN code is submitted for review
- WHEN format check runs
- THEN all code MUST conform to google-java-format

#### Scenario: Checkstyle Validation
- GIVEN code is submitted for review
- WHEN checkstyle runs
- THEN no violations MUST be present

---

### Requirement: CI Pipeline
The system MUST have automated build and test verification.

#### Scenario: Pull Request Validation
- GIVEN a pull request is opened
- WHEN CI pipeline runs
- THEN build, test, and quality checks MUST pass

#### Scenario: Nightly Build
- GIVEN scheduled nightly build time
- WHEN build runs
- THEN full test suite MUST execute with coverage reporting

---

### Requirement: Distribution Packaging
The system MUST produce deployable distribution artifacts.

#### Scenario: Distribution Build
- GIVEN full build completes
- WHEN distribution is assembled
- THEN `distribution/ddf/target/ddf-*` MUST contain runnable system

#### Scenario: Docker Support
- GIVEN Docker environment available
- WHEN SolrCloud is needed
- THEN `distribution/docker/solrcloud/` MUST provide working configuration

---

## CI Workflows

### build.yml (Main CI)
- Triggers: push to master, pull requests
- Steps: compile, test, quality checks
- Timeout: 60 minutes
- Exclusions: docs module (JRuby issues)

### build-sequential.yml (Full Sequential)
- Triggers: manual, nightly
- Steps: full build with single thread
- Timeout: 180 minutes
- Purpose: catch concurrency issues

### codeql.yml (Security Analysis)
- Triggers: push to master, weekly
- Steps: CodeQL security scanning
- Languages: java, javascript

### build-nightly.yml (Nightly)
- Triggers: cron schedule
- Steps: full build, coverage report
- Purpose: catch slow regressions

---

## Known CI Issues

| Issue | Description | Status |
|-------|-------------|--------|
| Docs Module | JRuby/Asciidoctor failures | Excluded from CI |
| Solr Download | Flaky Apache archive | Added retries |
| Spring Feature | Karaf 4.4+ Spring 6 only | Custom feature added |

---

## Build Profiles

| Profile | Command | Purpose |
|---------|---------|---------|
| Default | `mvn install` | Full build with tests |
| Fast | `mvn install -Dfast` | Skip tests/checks |
| Quick | `mvn compile -Dquick` | Compile only |
| CI | `mvn install -Dci` | CI validation mode |

---

## Dependency Management

### Version Properties (parent pom.xml)
All dependency versions centralized in parent POM properties.

### BOM Usage
- `ddf-bom`: Internal module versions
- Third-party BOMs: Karaf, Spring, CXF

### Vulnerability Scanning
- OWASP dependency-check plugin
- Run: `mvn org.owasp:dependency-check-maven:aggregate`
