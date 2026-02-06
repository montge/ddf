## 1. Pax Web 11 Integration

- [x] 1.1 Update `pax.web.version` from `8.0.33` to `11.1.0` in root pom.xml
- [x] 1.2 Update `jetty.version` from `9.4.57.v20241219` to `12.1.4` (Jetty 12 used by Pax Web 11.1.0)
- [x] 1.3 Update kernel feature.xml: Pax Web 11 repository URL (auto via pax.web.version property)
- [x] 1.4 Update kernel feature.xml: remove `javax.servlet-api` bundle, keep `jakarta-servlet-api` feature
- [ ] 1.5 Update security feature.xml: verify pax-web-http-whiteboard, pax-web-jetty, pax-web-http-jetty references resolve with Pax Web 11
- [ ] 1.6 Update pax-web-jsp bundle version in security feature.xml to match Pax Web 11.1.0
- [ ] 1.7 Verify `org.ops4j.pax.web.cfg` compatibility with Pax Web 11 configuration schema
- [ ] 1.8 Boot test: start Karaf kernel with Pax Web 11 features and verify web container starts

## 2. Jetty 12 Custom Code Adaptation

### 2.1 Session Management (package relocation)
- [x] 2.1.1 Update `AttributeSharingSessionDataStore.java`: `o.e.j.server.session` → `o.e.j.session`
- [x] 2.1.2 Update `AttributeSharingHashSessionIdManager.java`: same package relocation
- [x] 2.1.3 Update `AttributeSharingSessionDataStoreFactory.java`: same package relocation

### 2.2 Security Integration (package moves + interface changes)
- [x] 2.2.1 Update `JettyAuthenticator.java`: adapt to Jetty 12 LoginAuthenticator API — validateRequest(Request,Response,Callback), AuthenticationState, SecurityHandler, bridge to servlet via ServletContextRequest
- [x] 2.2.2 Update `JettyAuthenticatedUser.java`: Authentication.User → AuthenticationState.Succeeded, getAuthMethod→getAuthenticationType, isUserInRole simplified
- [x] 2.2.3 Update `JettyUserIdentity.java`: `o.e.j.server.UserIdentity` → `o.e.j.security.UserIdentity`, remove Scope parameter
- [x] 2.2.4 Update `JettyIdentityService.java`: associate/disassociate → Association pattern, add onLogout(Request), remove setRunAs/unsetRunAs
- [x] 2.2.5 Update `SecurityAuthService.java`: verified — Authenticator stays in same package, no changes needed

### 2.3 Handler Chain (significant rewrite)
- [x] 2.3.1 Rewrite `DelegatingHttpFilterHandler.java`: HandlerWrapper → Handler.Wrapper, boolean handle(Request, Response, Callback), bridge via ServletContextRequest
- [x] 2.3.2 Rewrite `ProxyHttpFilterChain.java`: Handler.Wrapper + Request/Response/Callback model, delegate to wrapped handler when filters exhausted
- [x] 2.3.3 `SecurityFilterChain.java`: no Jetty API changes needed — only uses javax.servlet (migrated in task 3)

### 2.4 Filters and Logging
- [x] 2.4.1 `ClientInfoFilter`, `DoPrivilegedFilter`, `TraceContextFilter`, `ResponseFilter`: verified — no Jetty API usage, only javax.servlet (migrated in task 3)
- [x] 2.4.2 Update logback-access: `ch.qos.logback:logback-access:1.2.13` → `ch.qos.logback.access:logback-access-jetty12:2.0.9` + `logback-access-common:2.0.9`
- [x] 2.4.3 Update `jetty.xml`: removed RequestLogHandler, use Server.setRequestLog() directly

### 2.5 Fragment-Host and POM
- [x] 2.5.1 Update `platform-paxweb-jettyconfig/pom.xml`: Fragment-Host `[8,9)` → `[11,12)`, add `jetty-ee10-servlet` dependency
- [x] 2.5.2 Replace `logback-access` with `logback-access-common` + `jetty12`, update Embed-Dependency
- [ ] 2.5.3 Update Pax Web API dependency version to 11.1.0 (managed by parent — verify resolves)

## 3. javax.servlet → jakarta.servlet Migration

- [x] 3.1 Bulk replace `javax.servlet` → `jakarta.servlet` in 113 Java source files (main + test)
- [x] 3.2 Update security filter modules: all 4 filter modules migrated
- [x] 3.3 Update security handler modules: all 6 handler modules migrated (note: pac4j imports still use org.pac4j.jee — see task 4)
- [x] 3.4 Update security servlet modules: all 4 servlet modules migrated
- [x] 3.5 Update platform modules: error-api/impl/servlet, landing-page, admin UI, metrics — all migrated
- [x] 3.6 Update catalog module: search-ui/search-redirect migrated
- [x] 3.7 Update API modules: http-filter-api, security-filter-api, session-management-api — all migrated
- [x] 3.8 Update 45+ module POMs: javax.servlet-api → jakarta.servlet-api (solr modules kept on javax for Solr 9.x compat)
- [x] 3.9 Updated blueprint.xml files (9 files), security feature.xml, catalog/ui pom.xml version override

## 4. pac4j Adapter Migration

**BLOCKED: pac4j-jakartaee only exists in pac4j 6.x. DDF uses pac4j 5.7.7.**
Upgrading pac4j from 5.x → 6.x is a major version upgrade with potential breaking API changes.
This should be a separate change/feature branch.

- [ ] 4.1 Upgrade pac4j.version from 5.7.7 to 6.x (separate change needed)
- [ ] 4.2 Switch security-handler-oidc from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.3 Switch security-handler-oauth from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.4 Switch security-oidc-bundle from `pac4j-javaee` → `pac4j-jakartaee`
- [ ] 4.5 Verify pac4j jakarta adapter works with DDF's OIDC/OAuth flows

## 5. Build and Integration Validation

- [x] 5.1 Compile: `mvn compile -Dquick` passes with all changes (pac4j modules not affected; only packaging-phase errors remain for solr-factory-impl and kernel which need `install` not `compile`)
- [x] 5.2 Unit tests: `mvn test` passes for platform-paxweb-jettyconfig (85 tests, 0 failures)
- [ ] 5.3 Unit tests: `mvn test` passes for all security filter/handler/servlet modules
- [ ] 5.4 Distribution build: `mvn install -Dfast` produces distribution artifact
- [ ] 5.5 Boot test: kernel starts with Pax Web 11 features, Jetty 12 active
- [ ] 5.6 Boot test: security features install, custom Jetty authentication working
- [ ] 5.7 Boot test: web applications accessible via HTTPS
- [ ] 5.8 Commit changes, update ddf-2.29-security-modernization tasks.md, push feature branch
