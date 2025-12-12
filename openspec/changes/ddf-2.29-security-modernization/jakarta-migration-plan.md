# Jakarta EE Migration Plan for DDF

**Created:** 2025-12-12
**Status:** Ready for Implementation (Blocked on CXF 4.x)

## CRITICAL PREREQUISITE: CXF 4.x Upgrade

**Finding from pilot test (2025-12-12):**

OpenRewrite successfully transformed source code from javax to jakarta namespace, but the build fails because:
- CXF 3.5.x provides javax.ws.rs APIs
- Jakarta migration requires CXF 4.x which provides jakarta.ws.rs APIs

**Required Order:**
1. **FIRST:** Upgrade CXF from 3.5.x to 4.x
2. **THEN:** Run OpenRewrite on modules in phases below

**OpenRewrite Command (verified working):**
```bash
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JakartaEE10
```

OpenRewrite automatically:
- Transforms javax.* imports to jakarta.* in source code
- Updates pom.xml dependencies to jakarta equivalents

---

## Executive Summary

This document outlines the migration strategy for converting DDF's ~1,043 javax imports to Jakarta EE namespace (jakarta.*). The migration is organized into 8 phases, starting with proof-of-concept modules and progressing to the most critical security components.

### Migration Scope

| Package Type | Occurrences | Files | Migration Target |
|--------------|-------------|-------|------------------|
| javax.servlet | 334 | 127 | jakarta.servlet |
| javax.ws.rs | 421 | 129 | jakarta.ws.rs |
| javax.xml.bind | 195 | 89 | jakarta.xml.bind |
| javax.annotation | 82 | 77 | jakarta.annotation |
| javax.inject | 11 | 11 | jakarta.inject |
| **Total** | **~1,043** | **~200** | - |

**Note:** javax.xml.parsers, javax.xml.stream, javax.security.auth, javax.management are Java SE APIs and do NOT require migration.

---

## Phase 1: Proof of Concept

**Goal:** Validate migration approach and tooling on smallest modules

### 1.1 catalog-rest-api (EASIEST)
- **Path:** `catalog/rest/catalog-rest-api`
- **Files:** 1 file (CatalogService.java)
- **Imports:** javax.servlet.http, javax.ws.rs
- **Effort:** 1-2 hours

### 1.2 catalog-rest-endpoint
- **Path:** `catalog/rest/catalog-rest-endpoint`
- **Files:** 4 files
- **Imports:** javax.ws.rs (20+), javax.servlet.http (2), javax.inject (1)
- **Effort:** 2-3 hours

### 1.3 platform-admin/ui
- **Path:** `platform/admin/ui`
- **Files:** 1 file (Configuration.java)
- **Imports:** javax.activation, javax.servlet.http, javax.ws.rs
- **Effort:** 1-2 hours

**Phase 1 Outcome:** 6 files migrated, REST endpoint pattern validated

---

## Phase 2: Low-Risk Common Libraries

**Goal:** Migrate utility modules used by others

### 2.1 catalog-core-commons
- **Path:** `catalog/core/catalog-core-commons`
- **Files:** 8 files (XPathHelper.java, XPathCache.java, XSLTUtil.java)
- **Imports:** javax.xml.* (stdlib, no change), javax.annotation
- **Effort:** 3-4 hours

### 2.2 platform-security/encryption
- **Path:** `platform/security/encryption`
- **Files:** 1 file
- **Imports:** javax.annotation.Nullable only
- **Effort:** 30 minutes

### 2.3 platform-security/pdp
- **Path:** `platform/security/pdp/security-pdp-authzrealm`
- **Files:** 2 files
- **Imports:** javax.xml.bind, javax.xml.transform
- **Effort:** 2-3 hours

**Phase 2 Outcome:** 11 additional files, XML and annotation patterns established

---

## Phase 3: Security Module Core Components

**Goal:** Migrate authentication and security infrastructure APIs

### 3.1 platform-security/session-management-api
- **Path:** `platform/security/session-management-api`
- **Files:** 2 files
- **Imports:** javax.servlet.http only
- **Effort:** 1 hour

### 3.2 platform-security/session-management-impl
- **Path:** `platform/security/session-management-impl`
- **Files:** 2 files
- **Imports:** javax.servlet.http
- **Effort:** 1 hour

### 3.3 platform-security/servlet (Multiple sub-modules)
- security-servlet-logout (7 files)
- security-servlet-logout-endpoint (3 files)
- security-servlet-whoami (2 files)
- security-servlet-session-expiry (2 files)
- security-servlet-web-socket-api (1 file)
- **Total:** 15 files
- **Effort:** 1-2 days

### 3.4 platform-security/handler/security-handler-api
- **Path:** `platform/security/handler/security-handler-api`
- **Files:** 2 files
- **Imports:** javax.servlet.ServletRequest/Response
- **Effort:** 1 hour

**Phase 3 Outcome:** Core session and handler APIs migrated

---

## Phase 4: Authentication Handlers

**Goal:** Migrate protocol-specific authentication handlers

| Handler | Files | Complexity | Effort |
|---------|-------|------------|--------|
| security-handler-basic | 5 | MEDIUM | 4-6 hours |
| security-handler-pki | 4 | MEDIUM | 4-6 hours |
| security-handler-oauth | 3 | LOW | 2-3 hours |
| security-handler-oidc | 6+ | MEDIUM | 6-8 hours |
| security-handler-saml | 8+ | HIGH | 1-2 days |

**Key Challenge:** SAML handler uses javax.xml.soap, javax.xml.ws which require careful migration.

**Phase 4 Outcome:** All authentication handlers updated

---

## Phase 5: Critical Security Components

**Goal:** Migrate security policies, filters, and core services

### 5.1 platform-security/filter (HIGH PRIORITY)
- security-filter-web-sso (9 files)
- security-filter-login (9 files)
- security-filter-authorization (6 files)
- security-filter-csrf (5 files)
- **Total:** 29 files, 60+ javax imports
- **Effort:** 2-3 days

### 5.2 platform-security/core/security-core-services (VERY HIGH)
- **Files:** 24 files
- **Key Classes:** LogoutMessageImpl, SubjectUtils, SecurityLoggerImpl, SecurityAssertionSaml
- **Imports:** Complex mix of servlet, ws.rs, xml.stream, security.auth
- **Effort:** 2-3 days

### 5.3 platform-security/core/security-core-impl (VERY HIGH)
- **Files:** 12+ files
- **Key Classes:** SamlProtocol, SamlValidator, SimpleSign, SecurityAssertionSaml
- **Imports:** SOAP, XMLStream, security.auth
- **Effort:** 2-3 days

### 5.4 platform-security/claims (MEDIUM-HIGH)
- security-claims-attributequerycommon (5 files)
- security-claims-property (4 files)
- security-claims-ldap (5 files)
- **Effort:** 1-2 days

**Phase 5 Outcome:** DDF security layer fully Jakarta-compliant

---

## Phase 6: Remaining Security Components

| Module | Files | Effort |
|--------|-------|--------|
| platform-security/realm | 5 | 4-6 hours |
| platform-security/pep | 2 | 2-3 hours |
| platform-security/interceptor | 1 | 1 hour |
| platform-security/rest | 8+ | 1 day |
| platform-security/certificate | 6+ | 4-6 hours |
| platform-security/security-jaas-ldap | 6 | 4-6 hours |
| platform-security/saml | 2 | 2 hours |
| platform-security-core-api | 6+ | 4-6 hours |

**Phase 6 Outcome:** Platform security module 100% migrated

---

## Phase 7: Catalog Core Components

### Priority Order:
1. **catalog-core-api** (4 files) - API definitions
2. **catalog-core-api-impl** (10 files)
3. **catalog-core-standardframework** (20 files) - Main orchestrator
4. **catalog-core-impl** (15+ files) - pubsub, filters
5. **catalog-core-camelcomponent** (6 files)
6. **catalog-core-directorymonitor** (6 files)
7. **catalog-core-urlresourcereader** (4 files)
8. **catalog-core-localstorageprovider** (1 file)
9. **catalog-core-downloadaction** (5 files)
10. **catalog-core-commands** (3 files)
11. **catalog-core-versioning** (2 files)
12. **catalog-core-attachment** (1 file)

**Phase 7 Outcome:** Catalog core framework fully migrated

---

## Phase 8: Remaining Modules

| Module | Files | Effort |
|--------|-------|--------|
| catalog-rest-impl | 2 | 2-3 hours |
| catalog-rest-service | 4 | 3-4 hours |
| platform-admin/core | 10 | 1 day |
| platform-admin/modules | 2 | 1 hour |
| platform-admin/configurator | 2 | 2 hours |
| platform-admin/admin-configuration-configupdater | 1 | 30 min |

**Phase 8 Outcome:** Complete DDF Jakarta EE migration

---

## Jakarta EE Package Mapping

| javax Package | Jakarta Equivalent | Notes |
|---------------|-------------------|-------|
| javax.servlet | jakarta.servlet | Full migration required |
| javax.servlet.http | jakarta.servlet.http | Full migration required |
| javax.ws.rs | jakarta.ws.rs | Full migration required |
| javax.ws.rs.core | jakarta.ws.rs.core | Full migration required |
| javax.annotation | jakarta.annotation | @Nullable, @Nonnull |
| javax.inject | jakarta.inject | DI annotations |
| javax.activation | jakarta.activation | MimeType handling |
| javax.xml.bind | jakarta.xml.bind | JAXB |
| javax.xml.ws | jakarta.xml.ws | JAX-WS SOAP |
| javax.xml.soap | jakarta.xml.soap | SOAP APIs |
| javax.validation | jakarta.validation | Bean Validation |

### NO Migration Required (Java SE APIs):
- javax.xml.parsers (Java stdlib)
- javax.xml.stream (Java stdlib)
- javax.xml.transform (Java stdlib)
- javax.xml.namespace (Java stdlib)
- javax.xml.xpath (Java stdlib)
- javax.security.auth (Java stdlib)
- javax.net.ssl (Java stdlib)
- javax.management (Java stdlib)

---

## Tooling Strategy

### Primary: OpenRewrite (Recommended for OSGi)
```bash
# Run Jakarta migration recipe
mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:LATEST \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.jakarta.JakartaEE10
```

### Secondary: Eclipse Transformer (for third-party deps)
```bash
# Build with bytecode transformation
mvn install -Djakarta
```

### Validation
```bash
# Find any remaining javax imports after migration
grep -r "import javax\.\(servlet\|ws\.rs\|annotation\|inject\|activation\|xml\.bind\)" --include="*.java" .
```

---

## Risk Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| OSGi Import-Package headers | HIGH | Use OpenRewrite for source changes |
| SOAP/WS-* compatibility | HIGH | Test SAML thoroughly |
| Third-party dependency conflicts | MEDIUM | Use Jakarta BOM |
| Runtime classloading issues | MEDIUM | Integration test each phase |
| Transitive dependency issues | LOW | Check dependency:tree |

---

## Success Criteria

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] OSGi bundles resolve correctly
- [ ] REST endpoints respond normally
- [ ] SAML authentication works
- [ ] OIDC authentication works
- [ ] Catalog CRUD operations work
- [ ] No javax.* imports in Jakarta-migrated packages
- [ ] No runtime ClassNotFoundExceptions

---

## Next Steps

1. **Immediate:** Test OpenRewrite on catalog-rest-api (simplest module)
2. **Week 1:** Complete Phase 1 (3 modules)
3. **Week 2:** Complete Phase 2 (3 modules)
4. **Ongoing:** Progress through phases with testing at each checkpoint
