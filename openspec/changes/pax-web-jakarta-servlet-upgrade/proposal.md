## Why

DDF's javax.servlet → jakarta.servlet migration is blocked by Pax Web 8.x (Jetty 9.x), which only supports the javax.servlet namespace. This leaves ~55 source files across 34 modules as the last major holdout in the Jakarta EE namespace migration (~20% of total javax imports). Upgrading to Pax Web 11 (Jetty 12, jakarta.servlet) unblocks the complete migration and aligns DDF with modern Jakarta EE standards.

**Why now:** DDF has already completed javax→jakarta migration for annotation, xml.bind, ws.rs, and validation namespaces. The servlet namespace is the final piece. Pax Web 11.1.0 is stable (released Nov 2025) and uses Jetty 12 with jakarta.servlet.

## What Changes

- **BREAKING**: Upgrade Pax Web 8.0.33 → 11.1.0 (Jetty 9.4.x → Jetty 12.0.x, javax.servlet → jakarta.servlet)
- **BREAKING**: Update `platform-paxweb-jettyconfig` Fragment-Host from `[8,9)` to `[11,12)` and adapt all custom Jetty code for Jetty 12 API changes
- Migrate 55 source files (34 modules) from javax.servlet to jakarta.servlet imports
- Update all security filter, handler, and servlet modules for jakarta.servlet API
- Update Pax Web feature repository references in Karaf feature.xml files
- Update `org.ops4j.pax.web.cfg` configuration if schema changes exist
- Remove javax.servlet-api 3.1.0 bundle from kernel feature (replaced by jakarta.servlet-api)
- Update Jetty version properties from 9.4.x to 12.0.x

**Not changing:**
- Karaf version stays at 4.4.9 (4.5.0 not yet released). Pax Web 11 features installed via custom feature repository override.
- pac4j stays on javaee (javax.servlet) adapter — will need a compatibility shim or separate upgrade

## Capabilities

### New Capabilities
- `pax-web-11-integration`: Integrating Pax Web 11.1.0 feature repository and bundles on Karaf 4.4.9, including feature.xml updates, version properties, and Pax Web configuration
- `jetty-12-adaptation`: Adapting platform-paxweb-jettyconfig custom code (authentication, session management, filters) for Jetty 12 API changes
- `javax-servlet-to-jakarta`: Migrating 55 source files across 34 modules from javax.servlet to jakarta.servlet namespace, updating POMs and OSGi Import-Package directives

### Modified Capabilities
<!-- No existing specs are changing at the requirement level -->

## Impact

**Critical module:** `platform/platform-paxweb-jettyconfig` — OSGi fragment bundle attached to pax-web-jetty. Contains custom authentication, session management, and filter chain code tightly coupled to Jetty 9 APIs. Fragment-Host version range must be updated and Jetty 12 API migration is required.

**Affected areas:**
- 34 modules with javax.servlet imports (security filters, handlers, servlets, admin UI, error pages, metrics)
- Feature.xml files: kernel, security, admin, utilities (pax-web feature references)
- Distribution configuration: `org.ops4j.pax.web.cfg`
- 9 custom Jetty integration classes in platform-paxweb-jettyconfig
- pac4j integration (security-handler-oidc, security-handler-oauth) — uses javax.servlet adapter

**Risk factors:**
- Karaf 4.4.9 does not natively include Pax Web 11; requires feature repository override (DDF already does this for CXF, Camel)
- Jetty 9 → 12 is a major version jump with significant API changes in authentication, session management, and handler APIs
- Custom Jetty session management (`AttributeSharingHashSessionIdManager`, `AttributeSharingSessionDataStoreFactory`) may need rewrite for Jetty 12 session architecture
- pac4j-javaee uses javax.servlet; may need pac4j-jakartaee adapter (reversing earlier workaround)
