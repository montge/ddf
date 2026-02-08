# DDF Project Conventions

## Overview
Distributed Data Framework (DDF) is an open source, modular integration framework built on Apache Karaf (OSGi container). It provides a federated metadata catalog with geospatial search capabilities, extensive security features (SAML, OAuth, X.509), and pluggable architecture for extensibility.

## Tech Stack

### Runtime
- **Java:** OpenJDK 17 ✅ LTS (targeting 21 LTS when Karaf 4.5.x released)
- **OSGi:** Apache Karaf 4.4.9
- **Web:** Pax Web 8.0.33 (Jetty 9.4.58) - upgrading to Pax Web 11 + Jetty 12
- **Security:** OpenSAML 4.x, pac4j 5.x

### Core Libraries
- **Apache Solr:** 9.0.0 (metadata storage/search)
- **Apache CXF:** 4.1.1 (REST/SOAP services) via ddf-cxf-karaf shaded bundle
- **Apache Camel:** 4.10.7 (integration routes) via camel-karaf
- **GeoTools:** 34.1 (geospatial processing) - Java 17+ version, includes all CVE fixes
- **Spring Framework:** 6.2.8 via ServiceMix OSGi bundles

### Build
- **Maven:** 3.6.3+
- **Code Style:** google-java-format
- **CI/CD:** GitHub Actions
- **Code Quality:** SonarCloud (montge_ddf)

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
- Karaf 4.3.7 -> 4.4.9
- Pax Web 7.x -> 8.0.33
- GeoTools 24.6 -> 34.1 (Java 17+)
- Spring 5.3.x -> 6.2.8
- Jackson 2.18 -> 2.19.4
- Commons Validator 1.6 -> 1.10.0
- CXF 3.5.x -> 4.1.1 (via ddf-cxf-karaf shaded bundle)
- Camel 3.22.4 -> 4.10.7 (via camel-karaf)
- SLF4J 1.7.36 -> 2.0.17, Logback 1.2.13 -> 1.5.21
- javax.ws.rs -> jakarta.ws.rs
- javax.xml.bind -> jakarta.xml.bind
- javax.annotation -> jakarta.annotation
- javax.validation -> jakarta.validation
- javax.servlet -> jakarta.servlet (113 Java + 4 Groovy + 45 POMs)

### In Progress
- Pax Web 8.x -> 11.x (Jetty 9 -> 12, jakarta.servlet runtime)
- Boot testing with full feature stack
- pac4j 5.x -> 6.x (for jakartaee adapter)

### Security Status
- 65 active vulnerabilities (0 critical, 24 high) - down from 912 (-93%)
- Major reductions: Hazelcast removal, Jackson/Commons/GeoTools/Logback upgrades

## Capability Spec Relationships

```
                    ┌─────────────┐
                    │   admin     │
                    │  (console)  │
                    └──────┬──────┘
                           │ manages
              ┌────────────┼────────────┐
              ▼            ▼            ▼
       ┌────────────┐ ┌────────┐ ┌──────────┐
       │    osgi    │ │catalog │ │ content  │
       │(blueprint, │ │(CRUD,  │ │(storage, │
       │ features)  │ │plugins,│ │ resource │
       └──────┬─────┘ │federate│ │retrieval)│
              │       └───┬────┘ └────┬─────┘
              │ registers │ queries    │ stores
              ▼           ▼           ▼
       ┌──────────────────────────────────┐
       │          transformers            │
       │  (GeoJSON, XML, CSV, Atom, PDF)  │
       └───────────────┬──────────────────┘
                       │ converts
                       ▼
              ┌──────────────┐
              │   spatial    │
              │ (WFS, CSW,   │
              │  OGC filters)│
              └──────────────┘
```

## Documented Capabilities

| Capability | Spec | Requirements | Scenarios |
|------------|------|-------------|-----------|
| Catalog Framework | catalog | 10 | 36 |
| OSGi/Blueprint | osgi | 11 | 40 |
| Transformers | transformers | 11 | 27 |
| Spatial/Geospatial | spatial | 8 | 30 |
| Content/Storage | content | 8 | 25 |
| Admin Console | admin | 10 | 37 |
| **Total** | | **58** | **195** |

## Testing Standards
- **Target:** 90%+ line coverage for core modules
- **Unit Tests:** Mockito-based isolation
- **Integration Tests:** Pax Exam for OSGi container
- **Patterns:** AAA (Arrange-Act-Assert)
