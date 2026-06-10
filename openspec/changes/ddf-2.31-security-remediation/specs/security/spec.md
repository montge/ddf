## ADDED Requirements

### Requirement: Post-2.31-Sync Dependency CVE Remediation
After syncing to 2.31.0-SNAPSHOT (JDK 21, Karaf 4.4.10), the system MUST remediate reachable critical and high severity dependency vulnerabilities, applying fixes as direct root-pom version-property bumps and moving lockstep dependency families together.

#### Scenario: Critical dependency bump
- GIVEN a dependency with a critical CVE reachable at runtime (e.g. mina-core, tomcat-embed-core)
- WHEN the fix version is identified
- THEN the controlling root-pom property MUST be bumped to a version that fixes ALL of that package's CVEs
- AND the build MUST remain green under `mvn install -Dfast`

#### Scenario: Lockstep family move
- GIVEN a dependency family controlled by one property (BouncyCastle `${bouncy.version}`, Netty `${netty.version}`, Solr `${solr.version}`)
- WHEN one member is bumped
- THEN all members MUST move to the same version with no split-version classpath (verified via `mvn dependency:tree`)

#### Scenario: Redundant Dependabot PR closure
- GIVEN a version bump applied directly to the root pom
- WHEN it supersedes an open Dependabot PR
- THEN that PR MUST be closed to avoid duplicate/conflicting changes

### Requirement: Transitive Vulnerability Neutralization
The system MUST neutralize transitive vulnerabilities that cannot be fixed by a direct version property, using dependencyManagement pins, exclusions, or removal of dead dependencies.

#### Scenario: Legacy artifact exclusion
- GIVEN a legacy/EOL artifact pulled transitively (e.g. bouncycastle `*-jdk15on`)
- WHEN it is excluded and forced to the maintained artifact (`*-jdk18on`)
- THEN the legacy artifact MUST NOT appear on any module classpath

#### Scenario: Dead dependency removal
- GIVEN a vulnerable dependency with zero first-party source consumers (e.g. hibernate-core, commons-httpclient)
- WHEN no other bundle imports its packages
- THEN the dependency declaration MUST be removed rather than upgraded

#### Scenario: Build-time-only suppression
- GIVEN a vulnerability in a build-time-only artifact not shipped in the distribution (e.g. jgit via git hooks)
- WHEN upgrading would break the tooling
- THEN the finding MUST be suppressed with documented justification rather than force-upgraded

### Requirement: Documented Residual Exposure for Blocked Fixes
The system MUST record an explicit decision for any vulnerability whose runtime fix is blocked by packaging or major-version constraints.

#### Scenario: Missing OSGi wrapper bundle
- GIVEN a fixed library version exists but no compatible ServiceMix/OSGi bundle is published (e.g. Spring 6.2.11)
- WHEN the deployed Karaf bundle cannot be advanced
- THEN the residual runtime exposure MUST be documented and the source-level bump applied without pointing feature/bundle versions at a nonexistent coordinate

#### Scenario: Deferred to dedicated effort
- GIVEN a fix requires a major migration owned by another change (Jetty 9.4→12 on the Pax Web branch; pac4j 5→6)
- WHEN the vulnerability cannot be safely fixed in this change
- THEN it MUST be explicitly deferred to the owning change rather than partially patched
