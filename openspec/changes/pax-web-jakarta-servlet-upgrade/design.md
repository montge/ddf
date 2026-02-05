## Context

DDF runs on Karaf 4.4.9 with Pax Web 8.0.33 (Jetty 9.4.x, javax.servlet). The javax.servlet namespace is the last major blocker in the Jakarta EE migration (~55 source files, 34 modules). Pax Web 11.1.0 (Jetty 12 EE10, jakarta.servlet) is the target to complete the migration.

**Key constraint:** Karaf 4.5.0 (which natively ships Pax Web 11) is not yet released. DDF must override Karaf 4.4.9's default Pax Web 8 features with Pax Web 11, similar to how it already overrides CXF (using camel-cxf-all) and other framework components.

**Critical module:** `platform-paxweb-jettyconfig` is an OSGi fragment that attaches to `pax-web-jetty` and provides custom authentication, session management, filter chain, and request logging integration. It has 12+ Java classes tightly coupled to Jetty 9 APIs that require significant adaptation for Jetty 12.

## Goals / Non-Goals

**Goals:**
- Upgrade Pax Web 8.0.33 → 11.1.0 (Jetty 12 EE10 mode, jakarta.servlet)
- Migrate all 55 javax.servlet source files to jakarta.servlet
- Adapt platform-paxweb-jettyconfig custom code for Jetty 12 APIs
- Maintain all existing security functionality (authentication, session management, filter chains)
- Maintain OSGi bundle resolution for all DDF features

**Non-Goals:**
- Karaf 4.4.9 → 4.5.x upgrade (not available yet; defer until released)
- Jetty WebSocket migration (separate concern, already using Jetty 9 websocket-api)
- Migrating third-party dependencies that still use javax.servlet internally (handled by Pax Web 11's EE8 compatibility if needed)
- Performance optimization of new Jetty 12 async handler model

## Decisions

### D1: Target Pax Web 11 directly (skip 9 and 10)

**Decision:** Go straight to Pax Web 11.1.0 (Jetty 12 EE10, jakarta.servlet).

**Rationale:**
- Pax Web 9 (Jetty 10, javax.servlet) is EOL — pointless intermediate step
- Pax Web 10 (Jetty 12 EE8, javax.servlet) modernizes Jetty but doesn't unblock the namespace migration
- Pax Web 11 (Jetty 12 EE10, jakarta.servlet) is what we need to complete Jakarta EE migration
- Jetty 12's architecture is the same for EE8 and EE10 — the Jetty code migration effort is identical regardless

**Trade-off:** Bigger single jump (Jetty 9→12) vs. doing the migration once instead of twice.

### D2: Override Pax Web features on Karaf 4.4.9

**Decision:** Override Karaf 4.4.9's Pax Web 8 feature repository with Pax Web 11.1.0 repository in DDF's feature.xml files.

**Approach:**
1. In kernel feature.xml: replace `mvn:org.ops4j.pax.web/pax-web-features/${pax.web.version}/xml/features` with version 11.1.0
2. Exclude Karaf's default pax-web features from startup.properties/etc
3. Install Pax Web 11 features via DDF's own feature definitions

**Precedent:** DDF already overrides Karaf's default CXF (uses camel-cxf-all instead), framework (Felix instead of Equinox), and logging (Pax Logging customizations).

### D3: Adapt custom Jetty code to Jetty 12 core APIs

**Decision:** Rewrite platform-paxweb-jettyconfig classes against Jetty 12's EE10 APIs.

**Key API changes requiring adaptation:**

| Component | Jetty 9 API | Jetty 12 API | Impact |
|-----------|-------------|--------------|--------|
| Handler chain | `HandlerWrapper.handle(target, baseRequest, request, response)` | `Handler.Wrapper.handle(Request, Response, Callback)` | **HIGH** — async model, complete rewrite |
| Session packages | `org.eclipse.jetty.server.session.*` | `org.eclipse.jetty.session.*` | MEDIUM — package relocation |
| Security classes | `org.eclipse.jetty.server.Authentication` | `org.eclipse.jetty.security.AuthenticationState` (EE10) | MEDIUM — interface changes |
| UserIdentity | `org.eclipse.jetty.server.UserIdentity` | `org.eclipse.jetty.security.UserIdentity` | LOW — package move |
| RequestLogHandler | `o.e.j.server.handler.RequestLogHandler` | **REMOVED** — use `Server.setRequestLog()` | HIGH — jetty.xml restructure |
| Request/Response | Classes (cast from servlet) | Core interfaces + EE10 servlet wrappers | MEDIUM |

**Files requiring changes (by severity):**

**High impact (significant rewrite):**
- `DelegatingHttpFilterHandler.java` — Handler.Wrapper with new async handle() signature
- `ProxyHttpFilterChain.java` — Handler invocation model change
- `jetty.xml` — RequestLogHandler removed, session/handler structure changes

**Medium impact (package moves + interface changes):**
- `JettyAuthenticator.java` — LoginAuthenticator API changes, security package moves
- `JettyAuthenticatedUser.java` — Authentication → AuthenticationState, package moves
- `AttributeSharingSessionDataStore.java` — `o.e.j.server.session` → `o.e.j.session`
- `AttributeSharingHashSessionIdManager.java` — same package relocation
- `AttributeSharingSessionDataStoreFactory.java` — same package relocation

**Low impact (package moves only):**
- `JettyUserIdentity.java` — UserIdentity package move
- `JettyIdentityService.java` — IdentityService package verification
- `AccessRequestLog.java` — logback-access-jetty12 artifact, minor API updates
- `SecurityAuthService.java` — Authenticator package verification

### D4: Reverse pac4j adapter to jakartaee

**Decision:** Switch pac4j from `pac4j-javaee` back to `pac4j-jakartaee` adapter.

**Context:** DDF previously switched from pac4j-jakartaee → pac4j-javaee (task 2.2.9) because Pax Web 8.x required javax.servlet. With Pax Web 11 and jakarta.servlet, we reverse this.

**Modules affected:** security-handler-oidc, security-handler-oauth, security-handler-oidc-bundle

### D5: Use OpenRewrite for bulk servlet namespace migration

**Decision:** Use OpenRewrite `javax.servlet.toJakartaServlet` recipe for the 55-file migration, followed by manual verification.

**Rationale:** OpenRewrite handles import rewriting, method signature updates, and constant references more reliably than sed for servlet APIs (which have complex hierarchies like HttpServletRequest → ServletRequest).

**Alternative considered:** Manual sed replacement — rejected due to complexity of servlet API class hierarchy and risk of missed references in method signatures, type casts, and exception handlers.

## Risks / Trade-offs

### R1: Karaf 4.4.9 + Pax Web 11 compatibility (HIGH)
Karaf 4.4.9 was not designed for Pax Web 11. Feature resolution, startup ordering, and bundle wiring may have unexpected issues.

**Mitigation:** Incremental testing — boot kernel with Pax Web 11 first, then add features one by one. DDF's `ddf-boot-features` already handles complex startup ordering.

### R2: Jetty 12 async Handler model (HIGH)
The DelegatingHttpFilterHandler currently uses blocking handler semantics. Jetty 12's async Callback model may require careful adaptation to avoid breaking the filter chain.

**Mitigation:** Study Pax Web 11's own EE10 handler implementation as reference. The EE10 layer wraps async handlers with servlet compatibility, so DDF's custom code may be able to leverage this wrapping.

### R3: Custom session management on Jetty 12 (MEDIUM)
AttributeSharingHashSessionIdManager and AttributeSharingSessionDataStore use internal Jetty session APIs that may have behavioral changes.

**Mitigation:** Verify against Jetty 12 session documentation. The core session architecture is similar; main changes are package relocation.

### R4: Third-party library compatibility (MEDIUM)
Some bundled dependencies may still import javax.servlet. Pax Web 11 doesn't export javax.servlet packages.

**Mitigation:** Identify any third-party bundles still importing javax.servlet. For critical ones, use Eclipse Transformer at build time. For others, consider providing javax.servlet-api as a compatibility bundle (not recommended long-term).

### R5: logback-access Jetty 12 support (LOW)
The AccessRequestLog extends logback-access's RequestLogImpl. Need logback-access-jetty12 artifact.

**Mitigation:** Logback-access already provides a Jetty 12 artifact. Update POM dependency.
