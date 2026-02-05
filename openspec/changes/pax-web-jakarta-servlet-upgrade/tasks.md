## 1. Pax Web 11 Integration

- [ ] 1.1 Update `pax.web.version` from `8.0.33` to `11.1.0` in root pom.xml
- [ ] 1.2 Update `jetty.version` from `9.4.57.v20241219` to Jetty 12 version used by Pax Web 11.1.0
- [ ] 1.3 Update kernel feature.xml: replace Pax Web 8 feature repository with Pax Web 11 repository URL
- [ ] 1.4 Update kernel feature.xml: remove `javax.servlet-api` bundle, keep `jakarta-servlet-api` feature
- [ ] 1.5 Update security feature.xml: verify pax-web-http-whiteboard, pax-web-jetty, pax-web-http-jetty references resolve with Pax Web 11
- [ ] 1.6 Update pax-web-jsp bundle version in security feature.xml to match Pax Web 11.1.0
- [ ] 1.7 Verify `org.ops4j.pax.web.cfg` compatibility with Pax Web 11 configuration schema
- [ ] 1.8 Boot test: start Karaf kernel with Pax Web 11 features and verify web container starts

## 2. Jetty 12 Custom Code Adaptation

### 2.1 Session Management (package relocation)
- [ ] 2.1.1 Update `AttributeSharingSessionDataStore.java`: `o.e.j.server.session` → `o.e.j.session`
- [ ] 2.1.2 Update `AttributeSharingHashSessionIdManager.java`: same package relocation
- [ ] 2.1.3 Update `AttributeSharingSessionDataStoreFactory.java`: same package relocation

### 2.2 Security Integration (package moves + interface changes)
- [ ] 2.2.1 Update `JettyAuthenticator.java`: adapt to Jetty 12 LoginAuthenticator API, update security package imports
- [ ] 2.2.2 Update `JettyAuthenticatedUser.java`: adapt Authentication → AuthenticationState, update packages
- [ ] 2.2.3 Update `JettyUserIdentity.java`: move from `o.e.j.server.UserIdentity` to `o.e.j.security.UserIdentity`
- [ ] 2.2.4 Update `JettyIdentityService.java`: verify/update IdentityService interface for Jetty 12
- [ ] 2.2.5 Update `SecurityAuthService.java`: verify Authenticator interface package for Jetty 12

### 2.3 Handler Chain (significant rewrite)
- [ ] 2.3.1 Rewrite `DelegatingHttpFilterHandler.java`: HandlerWrapper → Handler.Wrapper, adapt to async handle(Request, Response, Callback) signature
- [ ] 2.3.2 Rewrite `ProxyHttpFilterChain.java`: adapt handler invocation to Jetty 12 model
- [ ] 2.3.3 Update `SecurityFilterChain.java`: adapt to new handler/filter model if needed

### 2.4 Filters and Logging
- [ ] 2.4.1 Update `ClientInfoFilter.java`, `DoPrivilegedFilter.java`, `TraceContextFilter.java`, `ResponseFilter.java`: verify compatibility with Jetty 12 + jakarta.servlet
- [ ] 2.4.2 Update `AccessRequestLog.java`: switch to logback-access-jetty12 artifact
- [ ] 2.4.3 Update `jetty.xml`: remove RequestLogHandler (use Server.setRequestLog()), update session package references, verify GzipHandler/ErrorHandler compatibility

### 2.5 Fragment-Host and POM
- [ ] 2.5.1 Update `platform-paxweb-jettyconfig/pom.xml`: change Fragment-Host from `[8,9)` to `[11,12)`, update Jetty dependency versions
- [ ] 2.5.2 Add `logback-access-jetty12` dependency, remove old logback-access dependency
- [ ] 2.5.3 Update Pax Web API dependency version to 11.1.0

## 3. javax.servlet → jakarta.servlet Migration

- [ ] 3.1 Run OpenRewrite `javax.servlet.toJakartaServlet` recipe on all 55 source files (main + test)
- [ ] 3.2 Update security filter modules: security-filter-authorization, security-filter-csrf, security-filter-login, security-filter-web-sso
- [ ] 3.3 Update security handler modules: security-handler-api, security-handler-basic, security-handler-oauth, security-handler-oidc, security-handler-pki, security-handler-saml
- [ ] 3.4 Update security servlet modules: security-servlet-logout, security-servlet-session-expiry, security-servlet-whoami, security-servlet-web-socket-api
- [ ] 3.5 Update platform modules: platform-error-api, platform-error-impl, platform-error-servlet, landing-page, admin UI, metrics endpoints
- [ ] 3.6 Update catalog module: search-ui/search-redirect
- [ ] 3.7 Update API modules: http-filter-api, security-filter-api, session-management-api, security-servlet-logout-api
- [ ] 3.8 Update module POMs: replace `javax.servlet-api` dependency with `jakarta.servlet-api` in all affected modules
- [ ] 3.9 Verify no javax.servlet imports remain in main source files (excluding third-party)

## 4. pac4j Adapter Migration

- [ ] 4.1 Switch security-handler-oidc from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.2 Switch security-handler-oauth from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.3 Switch security-handler-oidc-bundle from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.4 Verify pac4j jakarta adapter works with DDF's OIDC/OAuth flows

## 5. Build and Integration Validation

- [ ] 5.1 Compile: `mvn compile -Dquick` passes with all changes
- [ ] 5.2 Unit tests: `mvn test` passes for platform-paxweb-jettyconfig
- [ ] 5.3 Unit tests: `mvn test` passes for all security filter/handler/servlet modules
- [ ] 5.4 Distribution build: `mvn install -Dfast` produces distribution artifact
- [ ] 5.5 Boot test: kernel starts with Pax Web 11 features, Jetty 12 active
- [ ] 5.6 Boot test: security features install, custom Jetty authentication working
- [ ] 5.7 Boot test: web applications accessible via HTTPS
- [ ] 5.8 Commit changes, update ddf-2.29-security-modernization tasks.md, push feature branch
