# Modernization Specification

## Purpose
Define requirements for upgrading DDF to modern Java and Jakarta EE standards while maintaining backward compatibility during transition.

## Current State
- **Java:** 17 ✅ LTS until 2029 (targeting 21 LTS)
- **Namespace:** javax.* (targeting jakarta.*)
- **Spring:** 5.3.39 (targeting 6.x)
- **CXF:** 3.6.8 (targeting 4.x)
- **Karaf:** 4.4.8 (partial Java 21 support - needs 4.5.x for full support)

---

## Requirements

### Requirement: Jakarta EE Migration
The system MUST migrate from javax.* to jakarta.* namespace for Jakarta EE 9+ compatibility.

#### Scenario: Servlet Migration
- GIVEN code using javax.servlet classes
- WHEN migration is complete
- THEN all code MUST use jakarta.servlet equivalents

#### Scenario: JAX-RS Migration
- GIVEN code using javax.ws.rs classes
- WHEN migration is complete
- THEN all code MUST use jakarta.ws.rs equivalents

#### Scenario: XML Binding Migration
- GIVEN code using javax.xml.bind classes
- WHEN migration is complete
- THEN all code MUST use jakarta.xml.bind equivalents

---

### Requirement: Spring Framework Upgrade
The system MUST upgrade from Spring 5.3.x to Spring 6.x.

#### Scenario: Spring Core Migration
- GIVEN Spring 5.3.x beans and configuration
- WHEN Spring 6.x is integrated
- THEN all beans MUST be compatible with Spring 6.x API

#### Scenario: Spring Security Migration
- GIVEN Spring Security 5.x configuration
- WHEN Spring Security 6.x is integrated
- THEN all security filters MUST use new API patterns

---

### Requirement: Apache CXF Upgrade
The system MUST upgrade from CXF 3.x to CXF 4.x.

#### Scenario: REST Service Migration
- GIVEN JAX-RS services using CXF 3.x
- WHEN CXF 4.x is integrated
- THEN all services MUST use jakarta.ws.rs annotations

#### Scenario: SOAP Service Migration
- GIVEN JAX-WS services using CXF 3.x
- WHEN CXF 4.x is integrated
- THEN all services MUST use jakarta.xml.ws annotations

---

### Requirement: Java Version Support
The system MUST support Java 17 LTS and SHOULD support Java 21 LTS.

#### Scenario: Java 17 Compatibility ✅
- GIVEN code compiled with Java 17
- WHEN Java 17 runtime is used
- THEN all code MUST execute without errors
- **Status:** Complete - maven.compiler.source/target set to 17

#### Scenario: Java 21 Compatibility
- GIVEN code compiled with Java 17
- WHEN Java 21 runtime is used
- THEN all code SHOULD execute without errors
- **Blocker:** Karaf 4.4.8 has partial Java 21 support; needs Karaf 4.5.x
- **Blocker:** Apache Aries Blueprint compatibility not confirmed

---

### Requirement: Logging Modernization
The system MUST upgrade logging infrastructure to Logback 1.4.x.

#### Scenario: Logback Configuration
- GIVEN Logback 1.2.x configuration
- WHEN Logback 1.4.x is integrated
- THEN all configuration files MUST use new schema

---

## Migration Phases

### Phase 1: Foundation (Current)
- [x] Karaf 4.3.7 -> 4.4.8
- [x] Pax Web 7.x -> 8.0.33
- [x] OpenSAML 3.x -> 4.x
- [x] Java 17 compilation target ✅
- [ ] Spring feature compatibility fix

### Phase 1.5: Java 21 Preparation
- [ ] Upgrade Karaf 4.4.8 -> 4.5.x (when released)
- [ ] Validate Apache Aries Blueprint on Java 21
- [ ] Test OSGi runtime on Java 21
- [ ] Update maven.compiler.source/target to 21
- [ ] Update CI to test both Java 17 and 21

### Phase 2: Transformation Tooling
- [ ] Eclipse Transformer integration
- [ ] Bytecode transformation for javax->jakarta
- [ ] Build-time transformation pipeline

### Phase 3: Core Upgrades
- [ ] Spring 5.3 -> 6.x
- [ ] CXF 3.6 -> 4.x
- [ ] Logback 1.2 -> 1.4.x
- [ ] Camel 3.18 -> 3.22+

### Phase 4: Cleanup
- [ ] Remove transformation shims
- [ ] Native jakarta.* source code
- [ ] Documentation updates

---

## Blocked Dependencies

| Dependency | Current | Target | Blocker |
|------------|---------|--------|---------|
| Apache Camel | 3.18.8 | 3.22+ | Requires jakarta.* |
| Spring | 5.3.39 | 6.x | Requires jakarta.* |
| CXF | 3.6.8 | 4.x | Requires jakarta.* |
| Logback | 1.2.x | 1.4.x | Requires SLF4J 2.x |

---

## GitHub Issues Tracking

| Issue | Title | Status |
|-------|-------|--------|
| #60 | Jakarta EE 9+ modernization | Open |
| #62 | Pax Web 10.x upgrade | Partial |
| #63 | Eclipse Transformer integration | Open |
| #64 | javax.* to jakarta.* migration | Open |
| #65 | Spring 5.3 to 6.x upgrade | Open |
| #66 | CXF 3.x to 4.x upgrade | Open |
| #67 | Logback 1.2 to 1.4.x upgrade | Open |
