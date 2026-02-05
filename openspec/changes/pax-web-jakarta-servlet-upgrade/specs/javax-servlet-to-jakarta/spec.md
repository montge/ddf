## ADDED Requirements

### Requirement: javax.servlet namespace migration
All DDF source files using javax.servlet imports must be migrated to jakarta.servlet.

#### Scenario: Source files use jakarta.servlet
- **WHEN** any DDF source file imports servlet APIs
- **THEN** the import uses `jakarta.servlet` namespace (not `javax.servlet`)
- **AND** the module POM declares `jakarta.servlet-api` dependency (not `javax.servlet-api`)

#### Scenario: Security filter modules compile with jakarta.servlet
- **WHEN** security filter modules are compiled (security-filter-authorization, security-filter-csrf, security-filter-login, security-filter-web-sso)
- **THEN** they compile successfully using jakarta.servlet.Filter, jakarta.servlet.http.HttpServletRequest, etc.

#### Scenario: Security handler modules compile with jakarta.servlet
- **WHEN** security handler modules are compiled (security-handler-api, security-handler-basic, security-handler-oauth, security-handler-oidc, security-handler-pki, security-handler-saml)
- **THEN** they compile successfully using jakarta.servlet APIs

#### Scenario: Security servlet modules compile with jakarta.servlet
- **WHEN** security servlet modules are compiled (logout, session-expiry, whoami, web-socket-api)
- **THEN** they compile successfully using jakarta.servlet.http.HttpServlet, etc.

### Requirement: OSGi Import-Package consistency
OSGi bundles must import `jakarta.servlet` packages instead of `javax.servlet`.

#### Scenario: Bundle manifests reference jakarta.servlet
- **WHEN** an OSGi bundle is built that uses servlet APIs
- **THEN** its MANIFEST.MF contains `Import-Package: jakarta.servlet` (not `javax.servlet`)
- **AND** the bundle resolves at runtime against the jakarta.servlet-api bundle

### Requirement: pac4j servlet adapter compatibility
The pac4j security integration must work with jakarta.servlet.

#### Scenario: pac4j uses jakarta.servlet adapter
- **WHEN** security-handler-oidc or security-handler-oauth modules use pac4j
- **THEN** they use `pac4j-jakartaee` adapter (not `pac4j-javaee`)
- **AND** pac4j filter/callback operations work with jakarta.servlet.http.HttpServletRequest

### Requirement: Test files updated
Test files using javax.servlet must also be migrated to jakarta.servlet.

#### Scenario: Test compilation with jakarta.servlet
- **WHEN** test files that mock or use servlet APIs are compiled
- **THEN** they use jakarta.servlet imports
- **AND** all tests pass with the new namespace
