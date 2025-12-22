# Security Specification

## Purpose
Define security requirements for DDF including authentication, authorization, vulnerability management, and secure coding practices.

## Current State (Updated 2025-12-22)
- **Active Vulnerabilities:** Reduced - major dependencies upgraded
- **Key Upgrades Completed:**
  - Hazelcast 3.12.10 → 5.3.5
  - Camel 3.18.8 → 3.22.4
  - Zookeeper 3.9.2 → 3.9.4
  - GeoTools 31.6 → 34.1
  - Netty 4.1.92 → 4.1.128.Final
  - Apache Batik → 1.18 (via Tika 3.2.3)
  - Tika 1.28 → 3.2.3
- **Authentication:** SAML 2.0, OAuth 2.0/OIDC, X.509, LDAP, Basic, Guest
- **Authorization:** XACML 3.0, attribute-based access control

---

## Requirements

### Requirement: Vulnerability Management
The system MUST maintain zero CRITICAL and zero HIGH severity vulnerabilities in production dependencies.

#### Scenario: Dependency Scanning
- GIVEN a build is triggered
- WHEN OWASP dependency-check runs
- THEN all CRITICAL and HIGH vulnerabilities MUST be either fixed or suppressed with documented rationale

#### Scenario: New Vulnerability Discovery
- GIVEN a new CVE is published affecting a DDF dependency
- WHEN the CVE severity is CRITICAL
- THEN a fix MUST be applied within 14 days

#### Scenario: High Severity Response
- GIVEN a new CVE is published affecting a DDF dependency
- WHEN the CVE severity is HIGH
- THEN a fix MUST be applied within 30 days

---

### Requirement: Authentication Handlers
The system MUST support multiple authentication mechanisms through pluggable handlers.

#### Scenario: SAML Authentication
- GIVEN a user accesses a protected resource
- WHEN the user has a valid SAML assertion from a trusted IdP
- THEN access MUST be granted with appropriate subject attributes

#### Scenario: OIDC Authentication
- GIVEN a user accesses a protected resource
- WHEN the user has a valid OIDC token
- THEN access MUST be granted with claims mapped to subject attributes

#### Scenario: X.509 Certificate Authentication
- GIVEN a user presents a client certificate
- WHEN the certificate is valid and issued by a trusted CA
- THEN access MUST be granted with DN attributes extracted

---

### Requirement: Authorization Framework
The system MUST enforce attribute-based access control on all catalog operations.

#### Scenario: Query Filtering
- GIVEN a user queries the catalog
- WHEN results contain metacards with security markings
- THEN only metacards matching user attributes MUST be returned

#### Scenario: Ingest Authorization
- GIVEN a user attempts to create a metacard
- WHEN the user lacks required permissions
- THEN the operation MUST be rejected with appropriate error

---

### Requirement: Security Plugin Chain
The system MUST process security decisions through a defined plugin chain.

#### Scenario: Pre-Authorization Plugin
- GIVEN an operation request arrives
- WHEN the PreAuthorizationPlugin processes it
- THEN subject attributes MUST be validated before proceeding

#### Scenario: Policy Plugin
- GIVEN an operation passes pre-authorization
- WHEN the PolicyPlugin evaluates it
- THEN applicable policies MUST be determined

#### Scenario: Access Plugin
- GIVEN policies have been determined
- WHEN the AccessPlugin processes the operation
- THEN the final allow/deny decision MUST be made

---

### Requirement: Secure Session Management
The system MUST maintain secure sessions with proper expiration and invalidation.

#### Scenario: Session Timeout
- GIVEN a user has an active session
- WHEN the session exceeds the configured idle timeout
- THEN the session MUST be invalidated

#### Scenario: Session Logout
- GIVEN a user initiates logout
- WHEN the logout request is processed
- THEN all session tokens MUST be invalidated

---

### Requirement: CSRF Protection
The system MUST protect against Cross-Site Request Forgery attacks.

#### Scenario: CSRF Token Validation
- GIVEN a state-changing request arrives
- WHEN the CSRF token is missing or invalid
- THEN the request MUST be rejected

---

### Requirement: Security Logging
The system MUST log security-relevant events for audit purposes.

#### Scenario: Authentication Events
- GIVEN an authentication attempt occurs
- WHEN the attempt succeeds or fails
- THEN the event MUST be logged with user identity and outcome

#### Scenario: Authorization Events
- GIVEN an authorization decision is made
- WHEN the decision denies access
- THEN the event MUST be logged with user identity and resource

---

## Critical Dependencies (Status as of 2025-12-22)

| Dependency | Current | Target | Status |
|------------|---------|--------|--------|
| Hazelcast | 5.3.5 | 5.x | ✅ DONE |
| Apache Camel | 3.22.4 | 3.22+ | ✅ DONE |
| Zookeeper | 3.9.4 | 3.9.3+ | ✅ DONE |
| GeoTools | 34.1 | 32.x+ | ✅ DONE |
| Netty | 4.1.128.Final | 4.1.114+ | ✅ DONE |
| Apache Batik | 1.18 | 1.17+ | ✅ DONE |
| Apache Tika | 3.2.3 | 2.x+ | ✅ DONE |
| Xalan | 2.7.3 | 2.7.3 | ✅ DONE (CVE-2022-34169) |
| Commons BeanUtils | 1.11.0 | 1.11.0 | ✅ DONE (CVE-2025-48734) |
| OWASP HTML Sanitizer | 20240325.1 | - | ⚠️ LOW RISK (no patch avail) |
| Groovy | 4.0.23 | 4.x | ✅ DONE (test-only 2.4.21 mitigated) |
| JDOM2 | 2.0.6.1_1 | 2.0.6.1 | ✅ DONE (CVE-2021-33813) |
| Jetty | 9.4.58 | 9.4.57+ | ✅ DONE (CVE-2024-13009) |
| Logback | 1.5.21 | 1.3.12+ | ✅ DONE (CVE-2023-6378) |

### Known False Positives (Not in Runtime Distribution)

| Package | CVE | Location | Reason |
|---------|-----|----------|--------|
| jgit 3.2.0 | CVE-2014-9390 | gitsetup/ | Build-time git hooks only |
| slf4j-ext 1.7.1 | CVE-2018-8088 | gitsetup/ | Build-time git hooks only |
| groovy-all 2.4.x | CVE-2016-6814 | test scope | Test-only, version override to 2.4.21 |

---

## Test Coverage Requirements

| Module | Current | Target |
|--------|---------|--------|
| security-core-impl | 46.88% | 80% |
| security-core-services | 45.77% | 80% |
| security-handler-impl | 13.84% | 80% |
| platform-security-core-api | 35.01% | 80% |
| security-filter-csrf | 0% | 80% |
| catalog-security-logging | 0% | 80% |
