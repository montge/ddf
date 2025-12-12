# DDF Project Conventions

## Overview
Distributed Data Framework (DDF) is an open source, modular integration framework built on Apache Karaf (OSGi container). It provides a federated metadata catalog with geospatial search capabilities, extensive security features (SAML, OAuth, X.509), and pluggable architecture for extensibility.

## Tech Stack

### Runtime
- **Java:** OpenJDK 17 ✅ LTS (targeting 21 LTS when Karaf 4.5.x released)
- **OSGi:** Apache Karaf 4.4.8
- **Web:** Pax Web 8.0.33 (Jetty 9.4.58)
- **Security:** OpenSAML 4.x, pac4j 5.x

### Core Libraries
- **Apache Solr:** 9.0.0 (metadata storage/search)
- **Apache CXF:** 3.6.8 (REST/SOAP services)
- **Apache Camel:** 3.18.8 (integration routes)
- **GeoTools:** 33.1 (geospatial processing)
- **Spring Framework:** 5.3.39 (DI, targeting 6.x)

### Build
- **Maven:** 3.6.3+
- **Code Style:** google-java-format
- **CI/CD:** GitHub Actions

## Architecture Patterns

### OSGi Services
- All components registered via Blueprint XML (`OSGI-INF/blueprint/blueprint.xml`)
- Service ranking controls plugin execution order
- Features group bundles for modular deployment

### Catalog Pattern
```
REST/UI -> CatalogFramework (orchestrator)
  -> Pre* Plugins -> Core Operation -> Post* Plugins
  -> FederationStrategy -> [Sources]
```

### Plugin Chain
- `PreIngestPlugin` / `PostIngestPlugin` - Create/update/delete
- `PreQueryPlugin` / `PostQueryPlugin` - Query transformation
- `PolicyPlugin` / `AccessPlugin` - Authorization

## Coding Conventions

### Java
- Use Hamcrest matchers for assertions: `assertThat(result, is(expected))`
- Tests use `@RunWith(MockitoJUnitRunner.class)` pattern (JUnit 4 for now)
- Null safety: validate inputs at boundaries
- Thread safety: assume concurrent access, use immutables
- Exception handling: `StopProcessingException` halts plugin chains

### Maven
- Fast build: `mvn install -Dfast`
- Format code: `mvn fmt:format`
- Single module: `./build-scripts/build-module.sh <module>`

### OSGi
- Never skip service registration
- Close `InputStream` and `BinaryContent` in finally blocks
- Use WKT for geometry representation

## Current State

### Completed Migrations
- OpenSAML 3.x -> 4.x
- Karaf 4.3.7 -> 4.4.8
- Pax Web 7.x -> 8.0.33
- GeoTools 24.6 -> 31.6

### In Progress
- Spring 5.3.x -> 6.x (requires jakarta.* migration)
- CXF 3.6.x -> 4.x
- javax.* -> jakarta.* namespace

### Security Status
- 126 active vulnerabilities (7 CRITICAL, 40 HIGH)
- 70-80+ CVEs fixed in recent sessions
- Blocked upgrades: Camel (requires Jakarta EE)

## Testing Standards
- **Target:** 90%+ line coverage for core modules
- **Unit Tests:** Mockito-based isolation
- **Integration Tests:** Pax Exam for OSGi container
- **Patterns:** AAA (Arrange-Act-Assert)
