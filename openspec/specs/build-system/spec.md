# Build System Modernization Specification

## Purpose
Analyze and improve the DDF build system to enable parallel builds, reduce build times, and improve modularity through dependency analysis.

## Current State (2025-12-20)
- **Modules:** 455 Maven modules
- **Build Time:** 45-90 minutes (full build)
- **CI Issues:**
  - StackOverflowError in Karaf feature resolution (required 32MB stack)
  - Deep recursive dependency resolution
  - Sequential dependency builds
- **Tooling:** Maven 3.9.x, Karaf 4.4.8 tooling

---

## Requirements

### Requirement: Dependency Structure Analysis
The system MUST use Design Structure Matrix (DSM) analysis to identify build optimization opportunities.

#### Scenario: Module Dependency Mapping
- GIVEN the 455 Maven modules
- WHEN DSM analysis is performed
- THEN module dependencies, cycles, and layers MUST be identified

#### Scenario: Cycle Detection
- GIVEN the module dependency graph
- WHEN cycles are identified
- THEN recommendations for breaking cycles MUST be provided

#### Scenario: Layer Identification
- GIVEN the dependency graph
- WHEN layers are identified
- THEN modules at the same layer CAN be built in parallel

---

### Requirement: Parallel Build Optimization
The system MUST support parallel module builds where dependencies allow.

#### Scenario: Independent Module Groups
- GIVEN modules with no inter-dependencies
- WHEN parallel build is enabled
- THEN these modules SHOULD build concurrently

#### Scenario: CI Parallelization
- GIVEN the GitHub Actions CI pipeline
- WHEN module groups are identified
- THEN separate jobs CAN run in parallel

---

### Requirement: Build Time Reduction
The system MUST reduce full build time by at least 40%.

#### Scenario: Incremental Builds
- GIVEN a code change in a single module
- WHEN incremental build runs
- THEN only affected modules SHOULD rebuild

#### Scenario: Cached Dependencies
- GIVEN repeated builds
- WHEN dependency caching is enabled
- THEN dependency resolution time SHOULD be minimized

---

## Analysis Tasks

### Task 1: Generate Dependency Matrix
Extract Maven dependency graph and convert to DSM format.

```bash
# Generate dependency tree for all modules
mvn dependency:tree -DoutputType=dot -DoutputFile=deps.dot

# Or use a DSM tool
mvn org.apache.maven.plugins:maven-dependency-plugin:analyze-report
```

### Task 2: Identify Problematic Patterns

#### Deep Dependency Chains
- Symptom: StackOverflowError in Karaf feature resolution
- Current: Required 32MB stack (abnormally high)
- Analysis: Feature dependency graph too deep

#### Circular Dependencies
- Detection: `mvn validate -Denforcer.skip=false`
- Impact: Prevents parallel builds

#### Bottleneck Modules
- Modules depended upon by many others
- Changes to these trigger large rebuild graphs

### Task 3: Propose Modularization Strategy

#### Option A: Feature-Based Splitting
Split large feature files into smaller, independent features:
```
kernel-feature.xml
  ├── kernel-base-feature.xml
  ├── kernel-security-feature.xml
  └── kernel-admin-feature.xml
```

#### Option B: Domain-Based Modules
Reorganize modules by domain:
```
ddf/
  ├── catalog-domain/
  ├── security-domain/
  ├── spatial-domain/
  └── admin-domain/
```

#### Option C: Build Reactor Optimization
Use Maven's `-T` flag and module ordering:
```bash
mvn install -T 4C  # 4 threads per core
```

---

## Tools for DSM Analysis

### Maven Dependency Tools
- `maven-dependency-plugin`
- `degraph` - Visualize dependencies
- `jdeps` - JDK dependency analyzer

### DSM Tools
- Lattix - Commercial DSM tool
- Structure101 - Architecture analysis
- Custom Python script with NetworkX

### Visualization
- Graphviz (dot format)
- D3.js force-directed graphs
- PlantUML component diagrams

---

## Metrics to Track

| Metric | Current | Target |
|--------|---------|--------|
| Full Build Time | 45-90 min | <30 min |
| Incremental Build | ~10 min | <5 min |
| CI Parallel Jobs | 2 | 6+ |
| Max Dependency Depth | >1000 | <100 |
| Circular Dependencies | Unknown | 0 |
| Thread Stack Size | 32MB | 4MB |

---

## Next Steps

1. [ ] Run dependency analysis on current codebase
2. [ ] Generate DSM visualization
3. [ ] Identify top 10 dependency bottlenecks
4. [ ] Propose refactoring for Karaf feature files
5. [ ] Test parallel build configurations
6. [ ] Measure build time improvements
