## ADDED Requirements

### Requirement: Fragment-Host version update
The platform-paxweb-jettyconfig OSGi fragment must attach to Pax Web 11's pax-web-jetty bundle.

#### Scenario: Fragment attaches to Pax Web 11 jetty bundle
- **WHEN** pax-web-jetty bundle version 11.x is installed
- **THEN** platform-paxweb-jettyconfig fragment attaches successfully
- **AND** custom Jetty configuration is loaded by Pax Web

### Requirement: Custom authentication adapts to Jetty 12 Security API
JettyAuthenticator, JettyIdentityService, JettyUserIdentity, and JettyAuthenticatedUser must work with Jetty 12's security module.

#### Scenario: DDF authentication integrates with Jetty 12
- **WHEN** an HTTP request arrives at Jetty 12
- **THEN** JettyAuthenticator validates the request using DDF's SecurityAuthService
- **AND** authenticated user identity is available as a Jetty 12 UserIdentity
- **AND** the security principal is propagated to downstream handlers

#### Scenario: Jetty 12 security API compatibility
- **WHEN** platform-paxweb-jettyconfig is compiled against Jetty 12
- **THEN** Authenticator, UserIdentity, IdentityService interfaces use Jetty 12 API signatures
- **AND** ServerAuthException handling follows Jetty 12 patterns

### Requirement: Custom filter chain adapts to Jetty 12 Handler API
DelegatingHttpFilterHandler, ProxyHttpFilterChain, SecurityFilterChain, and all Filter implementations must work with Jetty 12's Handler architecture.

#### Scenario: Filter chain operates on Jetty 12
- **WHEN** HTTP requests pass through DDF's custom filter chain
- **THEN** DelegatingHttpFilterHandler dispatches to registered filters
- **AND** ProxyHttpFilterChain invokes filters in correct order
- **AND** SecurityFilterChain applies security decisions before request reaches servlet

#### Scenario: Individual filters work with Jetty 12
- **WHEN** ClientInfoFilter, DoPrivilegedFilter, TraceContextFilter, or ResponseFilter is invoked
- **THEN** it receives Jetty 12 Request/Response objects (or adapted jakarta.servlet wrappers)
- **AND** filter processing completes without Jetty API errors

### Requirement: Custom session management adapts to Jetty 12 Session API
Session-related customizations (AttributeSharingHashSessionIdManager, AttributeSharingSessionDataStoreFactory) must work with Jetty 12's session architecture.

#### Scenario: Custom session management on Jetty 12
- **WHEN** Jetty 12 server initializes with custom jetty.xml
- **THEN** DDF's custom session ID manager is loaded
- **AND** DDF's custom session data store factory is used
- **AND** session attributes are shared across web contexts as before

### Requirement: jetty.xml configuration compatibility
The custom jetty.xml must be updated for Jetty 12 class names and configuration structure.

#### Scenario: jetty.xml loads on Jetty 12
- **WHEN** Pax Web 11 processes the custom jetty.xml
- **THEN** all configured components instantiate successfully (GzipHandler, error handler, request logging)
- **AND** server starts with correct thread pool, connector, and handler configuration

### Requirement: Access logging works on Jetty 12
AccessRequestLog must work with Jetty 12's request logging mechanism.

#### Scenario: HTTP access logging
- **WHEN** HTTP requests are processed by Jetty 12
- **THEN** AccessRequestLog records request details
- **AND** log format matches existing output format
