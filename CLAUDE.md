<!-- OPENSPEC:START -->
# OpenSpec Instructions

These instructions are for AI assistants working in this project.

Always open `@/openspec/AGENTS.md` when the request:
- Mentions planning or proposals (words like proposal, spec, change, plan)
- Introduces new capabilities, breaking changes, architecture shifts, or big performance/security work
- Sounds ambiguous and you need the authoritative spec before coding

Use `@/openspec/AGENTS.md` to learn:
- How to create and apply change proposals
- Spec format and conventions
- Project structure and guidelines

Keep this managed block so 'openspec update' can refresh the instructions.

<!-- OPENSPEC:END -->

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Distributed Data Framework (DDF) is an open source, modular integration framework built on Apache Karaf (OSGi container). It provides a federated metadata catalog with geospatial search capabilities, extensive security features (SAML, OAuth, X.509), and pluggable architecture for extensibility.

**Key Technologies:** Apache Karaf 4.3.7, Apache Solr 9.0.0, Apache CXF 3.5.3, Apache Camel 3.18.0, GeoTools 24.6, Spring 5.3.14

## Build Commands

> **⚡ Fast Build Guide:** See [BUILDING-FAST.md](BUILDING-FAST.md) for strategies to speed up development iteration by 40-90%.

### Quick Reference
| Use Case | Command |
|----------|---------|
| **Fast development build** | `mvn install -Dfast` |
| **Quick compile check** | `mvn compile -Dquick` |
| **Build single module** | `./build-scripts/build-module.sh <module>` |
| **CI-like validation** | `mvn install -Dci` |
| **Format code** | `mvn fmt:format` |

### Full Build
```bash
mvn install
```
Compiles all modules, runs tests, and creates distribution under `distribution/ddf/target/`.
**Note:** This can take 30-60 minutes. Use `-Dfast` for faster iteration.

### Fast Development Build (Recommended)
```bash
mvn install -Dfast
```
Skips tests, checkstyle, and other time-consuming checks for rapid iteration (~40-60% faster).

### Build Without Tests
```bash
mvn install -DskipTests
```

### Build Specific Module
```bash
# Using helper script (recommended)
./build-scripts/build-module.sh catalog/core/catalog-core-standardframework

# Or navigate and build directly
cd catalog/core/catalog-core-standardframework
mvn install -Dfast
```

### Code Formatting
```bash
mvn fmt:format
```
Required if build fails with formatting violations. Uses google-java-format.

### Clean Build
```bash
mvn clean install -Dfast
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Tests for Specific Module
```bash
cd platform/security/security-core-impl
mvn test
```

### Run Single Test Class
```bash
mvn test -Dtest=CatalogFrameworkImplTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=CatalogFrameworkImplTest#testQueryWithSecurityFiltering
```

**Test Framework:** JUnit with Mockito for mocking, Hamcrest for assertions. Tests use `@RunWith(MockitoJUnitRunner.class)` pattern.

## Running DDF

### Prerequisites
1. Start SolrCloud: See `distribution/docker/solrcloud/README.md`
2. Build distribution: `mvn install`

### Start DDF
```bash
cd distribution/ddf/target/ddf-<version>
bin/ddf      # Linux/Mac
bin\ddf.bat  # Windows
```

Access Admin Console at `https://localhost:8993/admin`

## Architecture

### Module Structure

DDF is organized into five main top-level modules:

1. **`/platform`** - Foundational services
   - `security/` - Security framework (SAML, OAuth, OIDC, STS, XACML PDP)
   - `osgi/` - OSGi utilities (config admin, conditions, boot flags)
   - `solr/` - Platform-level Solr integration
   - `admin/` - Admin UI and configuration management
   - `persistence/` - Data persistence layer
   - `metrics/` - Micrometer-based metrics collection
   - `action/`, `email/`, `parser/`, `mime/`, `io/` - Supporting utilities

2. **`/catalog`** - Core catalog functionality
   - `core/catalog-core-standardframework/` - `CatalogFrameworkImpl` (main orchestrator)
   - `core/catalog-core-api/` - All public catalog interfaces
   - `solr/` - Solr-backed catalog provider
   - `plugin/` - Federation, validation, security audit plugins
   - `transformer/` - 30+ format transformers (JSON, XML, CSV, PDF, etc.)
   - `rest/` - REST API endpoints
   - `opensearch/`, `spatial/` - Search protocol implementations

3. **`/features`** - Apache Karaf feature definitions for modular deployment
   - Defines installable feature sets (kernel, security, solr, admin, apps)

4. **`/distribution`** - Packaged distributions
   - `ddf/` - Main DDF distribution assembly
   - `kernel/` - Karaf kernel base
   - `docker/` - Docker deployments (including SolrCloud)

5. **`/libs`** - Shared libraries and utilities

### OSGi Plugin System

DDF uses OSGi Blueprint (Apache Aries) for declarative service registration. Components register services via `OSGI-INF/blueprint/blueprint.xml` in each bundle.

**Service Registration Pattern:**
```xml
<!-- In blueprint.xml -->
<bean id="myPlugin" class="org.example.MyPluginImpl"/>
<service ref="myPlugin" interface="ddf.catalog.plugin.PreIngestPlugin"/>
```

**Service Consumption Pattern:**
```xml
<!-- Dynamic service list -->
<reference-list id="preIngestPlugins"
                interface="ddf.catalog.plugin.PreIngestPlugin"
                availability="optional"/>
```

**Features, Bundles, Applications:**
- **Bundle**: Single JAR with OSGi manifest (one Maven module)
- **Feature**: Named collection of bundles in `feature.xml` (e.g., `catalog-core`, `security-core-services`)
- **Application**: User-facing feature grouping (e.g., `admin-app`, `catalog-app`)

Install features via Karaf console:
```bash
karaf> feature:install catalog-app
```

### Catalog Architecture

The catalog uses a **hub-and-spoke federation model**:

```
REST/UI → CatalogFramework (orchestrator)
  ├─ QueryOperations → FederationStrategy → [Sources]
  ├─ CreateOperations (Pre/Post Plugins)
  ├─ UpdateOperations (Pre/Post Plugins)
  ├─ DeleteOperations (Pre/Post Plugins)
  └─ ResourceOperations (content retrieval)
```

**Source Hierarchy:**
- `CatalogProvider`: Local storage (CRUD capable) - typically SolrCatalogProvider
- `ConnectedSource`: Always queried (local or remote, read-only)
- `FederatedSource`: Optionally included in queries (remote, read-only)

**Query Flow:**
1. REST endpoint receives query
2. Pre-Query Plugins transform query (optional)
3. `FederationStrategy.federate()` distributes to sources in parallel
4. Results aggregated and sorted
5. Post-Query Plugins filter/transform results
6. Response returned

**Plugin Integration Points:**
- `PreIngestPlugin` / `PostIngestPlugin`: Create/update/delete processing
- `PreQueryPlugin` / `PostQueryPlugin`: Query transformation and filtering
- `PreAuthorizationPlugin` / `PolicyPlugin` / `AccessPlugin`: Security decisions
- `PreResourcePlugin` / `PostResourcePlugin`: Content retrieval processing

Plugins can throw `StopProcessingException` to halt the chain.

### Security Architecture

**Authentication Methods:**
- SAML 2.0 (Web SSO, ECP) via OpenSAML 3.4.6
- OAuth 2.0 / OIDC
- X.509 client certificates
- LDAP (embedded OpenDJ server)
- Basic authentication
- Guest access

**Security Flow:**
```
Request → WebSSOFilter → LoginFilter → AuthorizationFilter
  → CXF Interceptors → Security Token Service (STS)
  → Policy Decision Point (XACML 3.0) → Endpoint
```

**Authorization:**
- Attribute-based access control via `PolicyPlugin` and `AccessPlugin`
- Results filtered based on user roles/attributes
- XACML 3.0 support for complex policies
- Subject stored in Apache Shiro `Subject` (thread-local)

**Configuration:** Security settings in `$DDF_HOME/etc/` with hot-reload support.

### Integration Points

1. **Apache Camel** (`/catalog/core/catalog-core-camelcomponent`)
   - Event-driven processing with `catalog://` endpoints
   - Routes: `catalog://query`, `catalog://create`, `catalog://update`, `catalog://delete`
   - Used for directory monitoring, scheduled operations

2. **Apache CXF**
   - JAX-RS REST endpoints in `/catalog/rest/`
   - SOAP services for CSW, WFS, WMS
   - Custom interceptors for security (OAuth, SAML, audit logging)

3. **Apache Solr**
   - `SolrCatalogProvider` implements `CatalogProvider`
   - WKT indexing for spatial queries
   - Full-text search with custom analyzers
   - Dynamic schema via `DynamicSchemaResolver`
   - Filter conversion via `GeotoolsFilterAdapterImpl` (OGC → Solr)

## Key Patterns

### Operation Pattern
`CatalogFramework` delegates to operation classes (`QueryOperations`, `CreateOperations`, etc.). Each follows:
1. Pre-processing plugins
2. Core operation
3. Post-processing plugins
4. Error handling

### Filter Adapter Pattern
Converts OGC filters to provider-specific queries:
- Input: `ddf.catalog.filter.Filter` (OGC standard)
- Adapter: `GeotoolsFilterAdapterImpl`
- Output: Provider query (e.g., Solr `SolrQuery`)

### Transformer Pattern
Pluggable format conversion:
- `InputTransformer`: File → Metacard (uses Apache Tika 1.28.4)
- `MetacardTransformer`: Metacard → Format (JSON, XML, etc.)
- `QueryResponseTransformer`: Results → Format
- Registry-based discovery via OSGi services

### Configuration Management
Properties files in `$DDF_HOME/etc/`:
- Naming: `org.codice.ddf.service.class.config`
- Hot-reloadable via OSGi Config Admin
- Blueprint integration via `<cm:managed-properties>`

## Data Model

**Metacard** (core metadata container):
- `id`: Unique identifier
- `type`: MetacardType reference (defines valid attributes)
- `attributes`: Key-value pairs (title, location WKT, created, modified, resource-uri, etc.)
- `securityAttributes`: Role-based access control
- `sourceId`: Originating source

**MetacardType**: Defines attribute schema for metacard classes. Registered as OSGi services for dynamic discovery.

**Attributes**: Typed values (STRING, INTEGER, GEOMETRY, DATE, XML, BINARY, etc.)

## Development Notes

### Requirements
- OpenJDK 17 LTS (set `JAVA_HOME`) - targeting Java 21 LTS when Karaf 4.5.x is released
- Maven 3.6.3+ (set `MAVEN_OPTS` with appropriate memory: `-Xmx1024m`)

### Code Style
- Uses google-java-format
- Run `mvn fmt:format` to auto-format
- IntelliJ/Eclipse plugins available (see README)

### Common File Locations
- Blueprint XML: `src/main/resources/OSGI-INF/blueprint/blueprint.xml`
- Feature definitions: `features/*/feature.xml`
- Tests: `src/test/java/**/*Test.java`
- Configuration: `src/main/resources/` (packaged into `$DDF_HOME/etc/`)

### Module Dependencies
Follow hierarchical feature dependencies:
```
kernel → utilities → security-core-api → ddf-core
  → security-core-services → platform-core → platform-app
  → admin-app → [catalog-app, spatial-app, etc.]
```

When adding dependencies, ensure proper feature ordering and avoid circular dependencies.

### Testing Patterns
- Use `@RunWith(MockitoJUnitRunner.class)` for unit tests
- Mock OSGi services with Mockito
- Hamcrest matchers for assertions: `assertThat(result, is(expected))`
- Integration tests use Pax Exam for OSGi container testing

### REST API Structure
Base path: `/services/catalog/`
- `/query` - Execute queries
- `/sources` - List/query specific sources
- `/metacards/{id}` - CRUD operations
- `/validate` - Validate metacards
- `/transforms` - Available transformers

### Common Tasks
- Adding a new source: Implement `FederatedSource` or `CatalogProvider`, register via Blueprint
- Adding a plugin: Implement plugin interface (e.g., `PreIngestPlugin`), register as OSGi service
- Adding a transformer: Implement `InputTransformer`/`MetacardTransformer`, register with `id` property
- Modifying security: Edit filters in `/platform/security/`, update `security-filter-chain`

## Important Conventions

1. **Never skip OSGi registration**: All catalog components must be registered as OSGi services
2. **Plugin ordering**: Plugins execute in service ranking order (default 0, higher = earlier)
3. **Thread safety**: Assume concurrent access; use immutable objects where possible
4. **Null safety**: Validate inputs; catalog API may pass null values
5. **Exception handling**: Use `StopProcessingException` to halt plugin chains; log at appropriate levels
6. **Resource cleanup**: Close `InputStream` and `BinaryContent` resources in finally blocks
7. **Security context**: Use `ddf.security.SecurityConstants.SECURITY_SUBJECT` from request context
8. **Spatial queries**: Use WKT (Well-Known Text) for geometry representation

## Useful References

- DDF Documentation: http://codice.org/ddf/Documentation-versions.html
- Release Notes: https://codice.atlassian.net/wiki/spaces/DDF/pages/71275152/Release+Notes
- Building DDF: https://codice.atlassian.net/wiki/spaces/DDF/pages/70986756
- Issue Tracker: https://github.com/codice/ddf/issues
