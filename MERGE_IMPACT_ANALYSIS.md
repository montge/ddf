# MERGE IMPACT ANALYSIS REPORT

**Date:** 2025-10-26
**Branch:** master
**Commits Analyzed:** HEAD~15..HEAD (10 Dependabot merge commits)

## Executive Summary

**STATUS: CRITICAL BREAKING CHANGES - BUILD BROKEN**

The merged Dependabot PRs introduce **MULTIPLE MAJOR VERSION UPGRADES** that have immediately broken the build. The codebase cannot compile in its current state.

**Immediate Blocker:** Jetty 9.4 → 11.0 upgrade breaks WebSocket API usage.

---

## What Was Merged

### 10 Dependabot Pull Requests (8 files changed, 76 line changes)

#### Maven Dependency Changes:

| Dependency | Old Version | New Version | Risk Level | Breaking |
|------------|-------------|-------------|------------|----------|
| Jetty | 9.4.58.v20250814 | 11.0.26 | **HIGH** | ✗ YES |
| OpenSAML | 3.4.6 | 4.0.1 | **HIGH** | ✗ YES |
| Netty | 4.1.125.Final | 4.2.7.Final | **HIGH** | ✗ YES |
| GML (jvnet.ogc) | 1.1.0 | 2.6.1 | **MEDIUM** | ✗ YES |
| gmavenplus-plugin | 3.0.2 | 4.2.1 | **MEDIUM** | ✗ YES |
| Maven Surefire | 3.2.5 | 3.5.4 | LOW | ✓ No |
| Byte Buddy | 1.14.11 | 1.17.8 | LOW | ✓ No |
| ddf.support | 2.3.16 | 2.3.17 | LOW | ✓ No |

#### GitHub Actions Updates (Infrastructure only - Non-breaking):
- actions/checkout: v4 → v5
- actions/setup-java: v4 → v5
- actions/github-script: v7 → v8
- github/codeql-action: v3 → v4
- actions/download-artifact: v5 → v6

---

## Immediate Build Failures

### FAILURE #1: Jetty WebSocket API Breaking Change

**Error:**
```
[ERROR] 'dependencies.dependency.version' for org.eclipse.jetty.websocket:websocket-common:jar is missing.
@ /platform/security/servlet/security-servlet-web-socket-api/pom.xml line 48, column 21
```

**Root Cause:**
Jetty 11.x completely restructured the WebSocket API. The artifact `org.eclipse.jetty.websocket:websocket-common` no longer exists.

**Impact:**
- ❌ Build cannot complete: `mvn clean compile -DskipTests` FAILS immediately at POM processing
- ❌ Blocks ALL downstream compilation, testing, and validation
- ❌ Affects: `/platform/security/servlet/security-servlet-web-socket-api/`

**Jetty 9.4 → 11.0 Breaking Changes:**
1. Package restructure: `org.eclipse.jetty.websocket.*` split into multiple new modules
2. Artifact `websocket-common` removed/merged into other artifacts
3. WebSocket API migrated to Jakarta EE 9+ (javax → jakarta namespace)
4. `WebSocketServlet` → `JettyWebSocketServlet` (class rename)
5. `WebSocketServletFactory` → `JettyWebSocketServletFactory` (class rename)
6. Annotation-based WebSocket support changed significantly

---

## Conflict Analysis with Our Recent Fixes

### Our Recent Work (Commits before the merge):
```
4f1851f Fix Mockito double-initialization errors (76+ test failures)
cdf0a16 Fix test failures from dependency upgrades
61b1db6 Fix P0 compilation errors from dependency upgrades
afd04fb Fix test infrastructure with Bouncy Castle
6bd0976 Fix GitHub Actions: Java 17+ compatibility
```

### Compatibility Assessment:

✅ **No Direct Conflicts** - Our fixes addressed orthogonal issues:
- Mockito 4.x migration patterns (void method stubs)
- Test infrastructure repairs (JUnit runners, assertions)
- Bouncy Castle security provider initialization
- GitHub Actions workflow fixes (Java module exports)
- Code formatting (google-java-format)

❌ **But New Problems Introduced by Merge:**
- Jetty 11 upgrade creates new compilation blocker
- OpenSAML 4.x may reintroduce security layer issues we haven't seen yet
- Netty 4.2.x may affect HTTP client code (52 references)
- These new issues overshadow the stability we achieved

**Conclusion:** Our fixes remain valid and necessary, but are now blocked by new dependency breakage.

---

## Dependency Risk Assessment

### HIGH RISK UPGRADES (Require Extensive Code Migration):

#### 1. Jetty 9.4 → 11.0 (IMMEDIATE BLOCKER)

**Scope:** 50+ pom.xml references, core HTTP/servlet/websocket infrastructure

**Breaking Changes:**
- WebSocket API completely restructured
- Servlet API: `javax.servlet.*` → `jakarta.servlet.*` (Jakarta EE 9)
- HTTP client API changes
- Session management changes

**Migration Effort:** 2-3 developer days

**Files Requiring Immediate Fixes:**
- `/platform/security/servlet/security-servlet-web-socket-api/pom.xml`
- `/platform/security/servlet/security-servlet-web-socket-api/src/main/java/org/codice/ddf/security/servlet/web/socket/SecureWebSocketServlet.java`
- `/platform/security/servlet/security-servlet-web-socket-api/src/main/java/org/codice/ddf/security/servlet/web/socket/WebSocket.java`
- `/platform/security/servlet/security-servlet-web-socket-api/src/main/java/org/codice/ddf/security/servlet/web/socket/SessionPlugin.java`

**Additional Files Potentially Affected:**
- All Jetty HTTP client usage across the codebase
- Karaf servlet integration
- OSGi bundle manifests (Import-Package for websocket classes)

---

#### 2. OpenSAML 3.4.6 → 4.0.1

**Scope:** 37 pom.xml references, entire SAML 2.0 security stack

**Breaking Changes:**
- Core SAML object model API changes
- XML signature/encryption API changes
- Bootstrap initialization changes
- Velocity template engine removed (replaced with different templating)
- Dependency updates (Xerces, XMLSec, etc.)

**Migration Effort:** 3-5 developer days

**Files Potentially Affected:**
- `/platform/security/core/security-core-impl/` - SAML SSO handlers
- `/platform/security/idp/` - Identity Provider implementation
- `/platform/security/sts/` - Security Token Service
- All SAML metadata handling
- SAML authentication filters

**Risk:** This is the authentication backbone of DDF. Breaking SAML breaks most enterprise deployments.

---

#### 3. Netty 4.1 → 4.2

**Scope:** 52 pom.xml references

**Breaking Changes:**
- Channel API refinements
- Buffer management changes
- Event loop API updates
- Bootstrap configuration changes

**Migration Effort:** 1-2 developer days

**Files Potentially Affected:**
- All async HTTP client implementations
- Streaming data handlers
- WebSocket implementations (if using Netty WebSocket)

---

### MEDIUM RISK UPGRADES:

#### 4. GML (org.jvnet.ogc) 1.1.0 → 2.6.1

**Scope:** `/catalog/spatial/wfs/2.0.0/spatial-wfs-v2_0_0-common/` only

**Breaking Changes:**
- Geography Markup Language schema parsing changes
- Potential namespace changes

**Migration Effort:** 1 developer day

**Files Affected:**
- WFS 2.0 spatial query implementations
- GML geometry parsing

---

#### 5. gmavenplus-plugin 3.0.2 → 4.2.1

**Scope:** Build process (Groovy script compilation)

**Breaking Changes:**
- Plugin configuration schema may have changed
- Groovy version compatibility requirements

**Migration Effort:** 0.5 developer day (mostly testing)

---

### LOW RISK UPGRADES (Likely Backward Compatible):

✓ **Maven Surefire 3.2.5 → 3.5.4** - Minor version, should be backward compatible
✓ **Byte Buddy 1.14.11 → 1.17.8** - Test instrumentation agent, likely compatible
✓ **ddf.support 2.3.16 → 2.3.17** - Patch version bump, safe

---

## Impact on Our Work

### Before Merge (Our Achievements):
- ✅ Fixed 100+ Mockito 4.x compilation errors
- ✅ Fixed 76 test failures from Mockito migration
- ✅ Fixed GitHub Actions workflows (Java 17 module exports, formatting)
- ✅ Fixed test infrastructure (Bouncy Castle, JUnit runners)
- ✅ Achieved: Clean compilation for 95% of modules

### After Merge (Current State):
- ❌ **Cannot compile at all** - POM validation fails before any code compilation
- ❌ Unknown OpenSAML 4.x issues (will be discovered after Jetty fix)
- ❌ Unknown Netty 4.2.x issues (will be discovered after Jetty fix)
- ⚠️ Our existing fixes remain valid but are overshadowed

### Work Now Blocked:
1. ❌ Cannot run tests (build doesn't complete)
2. ❌ Cannot validate checkstyle/formatting (build doesn't complete)
3. ❌ Cannot run security scans (build doesn't complete)
4. ❌ Cannot validate GitHub Actions CI/CD (build breaks immediately)
5. ❌ Cannot continue with P1/P2 issue fixes

---

## Recommendations

### PRIORITY 0: RESTORE BUILD (Choose One Approach)

#### Option A: REVERT BREAKING CHANGES (Fastest - 1 hour)

**Strategy:** Revert the major version upgrades, keep safe updates

```bash
# Revert major breaking changes
git revert --no-commit 97c135667e  # Jetty 11.0.26 → back to 9.4.58
git revert --no-commit daafc4a96c  # GML 2.6.1 → back to 1.1.0

# Keep these (check if they compile):
# - OpenSAML 4.0.1 (test separately)
# - Netty 4.2.7 (test separately)
# - Surefire 3.5.4 (safe)
# - Byte Buddy 1.17.8 (safe)
# - ddf.support 2.3.17 (safe)

# Test compilation
mvn clean compile -DskipTests
```

**Pros:**
- ✅ Immediate build restoration
- ✅ Can continue with P1/P2 GitHub Actions fixes
- ✅ Allows time to plan proper Jetty/OpenSAML migration sprints
- ✅ Keeps safe dependency updates

**Cons:**
- ❌ Security updates delayed for Jetty 9.4 (check CVE status)
- ❌ Dependabot will re-propose these updates
- ❌ Need to configure Dependabot to ignore major version updates

**Risk:** LOW - Just restores previous working state

---

#### Option B: FIX JETTY MIGRATION NOW (Recommended if time allows - 2-3 days)

**Strategy:** Properly migrate to Jetty 11, defer OpenSAML/Netty

**Day 1: Fix Jetty WebSocket**
1. Update `/platform/security/servlet/security-servlet-web-socket-api/pom.xml`:
   ```xml
   <!-- Remove -->
   <dependency>
       <groupId>org.eclipse.jetty.websocket</groupId>
       <artifactId>websocket-common</artifactId>
   </dependency>

   <!-- Update -->
   <dependency>
       <groupId>org.eclipse.jetty.websocket</groupId>
       <artifactId>websocket-servlet</artifactId>
   </dependency>

   <!-- Add -->
   <dependency>
       <groupId>org.eclipse.jetty.websocket</groupId>
       <artifactId>websocket-jetty-server</artifactId>
       <version>${jetty.version}</version>
   </dependency>
   ```

2. Update Java source files:
   - `SecureWebSocketServlet.java`: Extend `JettyWebSocketServlet` instead of `WebSocketServlet`
   - Update imports: `org.eclipse.jetty.websocket.server.*`
   - Update `configure()` method to use `JettyWebSocketServletFactory`

3. Update OSGi bundle imports in `maven-bundle-plugin` configuration

**Day 2: Test and Fix Jakarta Servlet Migration**
- Search for `javax.servlet` imports → replace with `jakarta.servlet`
- Update OSGi bundle Import-Package directives
- Test HTTP/HTTPS functionality
- Test WebSocket connections

**Day 3: Address OpenSAML 4.x (if breaking)**
- Run full build: `mvn clean install`
- Fix any OpenSAML API incompatibilities
- Update SAML authentication tests

**Pros:**
- ✅ Gets security updates from Jetty 11
- ✅ Modernizes to Jakarta EE (long-term requirement)
- ✅ Aligns with Karaf roadmap

**Cons:**
- ❌ 2-3 day investment before resuming other fixes
- ❌ High complexity (servlet namespace migration)
- ❌ May uncover more issues in OSGi/Karaf integration

**Risk:** MEDIUM - Well-documented migration path exists

---

#### Option C: STAGED APPROACH (Most Conservative - 1 week)

**Strategy:** Revert all, then upgrade incrementally on separate branches

**Phase 1: Revert and Stabilize (Day 1)**
```bash
git revert --no-commit 97c135667e  # Jetty
git revert --no-commit daafc4a96c  # GML
git revert --no-commit 2eeceed8c7  # Byte Buddy (test)
git revert --no-commit 1971be00d3  # ddf.support (test)

# Cherry-pick only GitHub Actions updates (infrastructure)
# Keep workflow file changes
```

**Phase 2: Test Safe Updates (Day 2)**
- Create branch: `dependabot/safe-updates`
- Re-apply: ddf.support 2.3.17, Maven Surefire 3.5.4, Byte Buddy 1.17.8
- Test: `mvn clean install`
- Merge if green

**Phase 3: Jetty Migration (Days 3-4)**
- Create branch: `feature/jetty-11-migration`
- Follow Option B steps
- Full CI/CD validation
- Merge when stable

**Phase 4: OpenSAML Migration (Days 5-6)**
- Create branch: `feature/opensaml-4-migration`
- Update SAML stack
- Full security testing
- Merge when validated

**Phase 5: Netty/GML (Day 7)**
- Create branches for remaining upgrades
- Test independently

**Pros:**
- ✅ Maximum safety through isolation
- ✅ Each upgrade independently validated
- ✅ Easy to roll back individual changes
- ✅ Clear audit trail

**Cons:**
- ❌ Most time-intensive (1 week)
- ❌ Multiple merge operations
- ❌ Requires extensive CI/CD runs

**Risk:** VERY LOW - But highest time investment

---

## My Recommendation

**Choose Option A (Revert) IF:**
- Security vulnerabilities in Jetty 9.4/OpenSAML 3.4.6 are not critical
- Need to continue P1/P2 GitHub Actions fixes quickly
- Want to plan proper migration sprints

**Choose Option B (Fix Now) IF:**
- Critical CVEs exist in Jetty 9.4.58 or OpenSAML 3.4.6
- Have 2-3 days available for migration work
- Want to tackle Jakarta EE migration proactively

**Choose Option C (Staged) IF:**
- This is a production-critical codebase
- Need maximum stability and testing
- Have 1 week for comprehensive migration

**My Specific Recommendation:** **Option A (Revert)** based on:
1. Build is completely broken - need quick restoration
2. Our P0/P1 fixes are ready to continue
3. Major migrations deserve dedicated sprint planning
4. Can investigate CVE urgency while build is restored

---

## Technical Migration Guide: Jetty 11 WebSocket

### Current Code (Jetty 9.4):

**pom.xml:**
```xml
<dependency>
    <groupId>org.eclipse.jetty.websocket</groupId>
    <artifactId>websocket-servlet</artifactId>
</dependency>
<dependency>
    <groupId>org.eclipse.jetty.websocket</groupId>
    <artifactId>websocket-common</artifactId>
</dependency>
```

**SecureWebSocketServlet.java:**
```java
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.websocket.api.Session;

public class SecureWebSocketServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> new WebSocketImpl());
    }
}
```

---

### Required Changes (Jetty 11.0):

**pom.xml:**
```xml
<dependency>
    <groupId>org.eclipse.jetty.websocket</groupId>
    <artifactId>websocket-jetty-server</artifactId>
    <version>${jetty.version}</version>
</dependency>
<!-- websocket-servlet is now included in websocket-jetty-server -->
```

**SecureWebSocketServlet.java:**
```java
import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.eclipse.jetty.websocket.api.Session;

public class SecureWebSocketServlet extends JettyWebSocketServlet {
    @Override
    protected void configure(JettyWebSocketServletFactory factory) {
        factory.setCreator((req, resp) -> new WebSocketImpl());
    }
}
```

**Key Changes:**
1. `WebSocketServlet` → `JettyWebSocketServlet`
2. `WebSocketServletFactory` → `JettyWebSocketServletFactory`
3. `configure()` changes from `public` to `protected`
4. Import package: `org.eclipse.jetty.websocket.servlet.*` → `org.eclipse.jetty.websocket.server.*`

**OSGi Bundle Manifest (if explicit):**
```
Import-Package:
  org.eclipse.jetty.websocket.server;version="[11.0,12)",
  org.eclipse.jetty.websocket.api;version="[11.0,12)"
```

---

## Additional Considerations

### Servlet API Namespace Migration (javax → jakarta)

Jetty 11 requires Jakarta EE 9+, which means:
```java
// OLD
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

// NEW
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
```

**Impact:** Every servlet, filter, and listener in the codebase needs updating.

**OSGi/Karaf Implications:**
- Karaf 4.3 may not have Jakarta Servlet bundles
- May require Karaf 4.4+ upgrade (another dependency cascade)
- Check DDF's Karaf version: Currently 4.3.10 (from CLAUDE.md)

**This is a BLOCKER:** Karaf 4.3.x uses javax.servlet. Jetty 11 uses jakarta.servlet. **These are incompatible.**

**Revised Assessment:** Jetty 11 upgrade requires **Karaf upgrade to 4.4+**, which is a much larger migration effort (weeks, not days).

---

## Critical Discovery: Karaf Compatibility Issue

**BREAKING INSIGHT:** I just realized the full scope:

1. Jetty 11 requires Jakarta EE 9 (jakarta.servlet.*)
2. DDF runs on Karaf 4.3.10 (per CLAUDE.md)
3. **Karaf 4.3.x provides javax.servlet.* bundles (JavaEE 8)**
4. **Karaf 4.4+ provides jakarta.servlet.* bundles (Jakarta EE 9)**

**Conclusion:** Upgrading Jetty to 11.0.26 is NOT possible without upgrading Karaf to 4.4+.

**This means Dependabot's Jetty PR is fundamentally incompatible with the current platform.**

---

## Revised Recommendation

**IMMEDIATE ACTION: REVERT JETTY UPGRADE (Option A)**

The Jetty 11 upgrade is not just a code migration - it requires a platform upgrade (Karaf 4.3 → 4.4+), which cascades to:
- All OSGi bundles need Jakarta EE migration
- All features need revalidation
- Entire DDF distribution rebuild
- **Estimated effort: 2-3 weeks, not 2-3 days**

**Commands:**
```bash
# Revert Jetty 11
git revert 97c135667e

# Revert other potentially breaking changes
git revert daafc4a96c  # GML (less critical)

# Test compilation
mvn clean compile -DskipTests

# If successful, commit
git commit -m "Revert Jetty 11 and GML upgrades: incompatible with Karaf 4.3

Jetty 11 requires Jakarta EE 9 (jakarta.servlet.*), but DDF runs on
Karaf 4.3.10 which provides JavaEE 8 (javax.servlet.*). Upgrading Jetty
would require upgrading to Karaf 4.4+, which is a multi-week platform
migration effort.

Keeping Jetty 9.4.58 until Karaf upgrade is planned.

Refs: Dependabot PR #10, Karaf 4.4 migration tracking"
```

**Next Steps After Revert:**
1. ✅ Verify build works: `mvn clean compile -DskipTests`
2. ✅ Continue with P1/P2 GitHub Actions fixes
3. ✅ Check OpenSAML 4.x compatibility (may need revert too)
4. ✅ Check Netty 4.2.x compatibility (may be OK)
5. ✅ Configure Dependabot to ignore major version updates for Jetty until Karaf 4.4 migration

---

## Files Requiring Attention (Post-Revert)

### After Jetty Revert:
- None immediately (reverts to working state)

### Still Need Investigation:
1. **OpenSAML 3.4.6 → 4.0.1:** May also be breaking, test after Jetty revert
2. **Netty 4.1 → 4.2:** May be OK, test after Jetty revert
3. **GML 1.1.0 → 2.6.1:** Lower priority, can test independently

---

## Conclusion

**STATUS: CRITICAL - BUILD BROKEN - REQUIRES IMMEDIATE REVERT**

The Dependabot merges introduced a **platform-incompatible upgrade** (Jetty 11 on Karaf 4.3). This is not a simple dependency update - it requires a multi-week Jakarta EE migration across the entire OSGi platform.

**IMMEDIATE ACTION REQUIRED:**
1. Revert Jetty 11 upgrade (and likely GML upgrade)
2. Test OpenSAML 4.x and Netty 4.2.x separately
3. Configure Dependabot to block major version updates until platform readiness
4. Plan proper Karaf 4.4 + Jakarta EE migration as a dedicated epic

**After Revert:**
- Build restored
- Can continue with P1/P2 fixes
- Can properly plan major migrations

**DO NOT attempt Jetty 11 migration without Karaf 4.4 upgrade first.**

---

## Next Steps

1. **Immediate (1 hour):** Revert Jetty and GML upgrades
2. **P0 (2 hours):** Verify OpenSAML 4.x and Netty 4.2.x compatibility
3. **P1 (1 day):** Continue with GitHub Actions P1/P2 fixes from previous work
4. **P2 (1 week):** Plan Karaf 4.4 + Jakarta EE migration epic
5. **P3 (ongoing):** Configure Dependabot policies for safer updates

---

**Analysis Completed:** 2025-10-26
**Recommendation:** Revert breaking changes, resume P1/P2 work, plan major migrations properly
