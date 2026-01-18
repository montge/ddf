## ADDED Requirements

### Requirement: Blueprint Service Registration
The system MUST use Apache Aries Blueprint for declarative OSGi service registration.

#### Scenario: Service Registration
- **GIVEN** a blueprint.xml defining a bean and service
- **WHEN** the bundle starts
- **THEN** the service MUST be registered in the OSGi service registry

#### Scenario: Service Reference
- **GIVEN** a blueprint.xml with a reference to another service
- **WHEN** the referenced service becomes available
- **THEN** the bean MUST be injected with the service proxy

#### Scenario: Reference List
- **GIVEN** a blueprint.xml with a reference-list
- **WHEN** multiple services matching the interface exist
- **THEN** all services MUST be available as a dynamic list

---

### Requirement: Karaf Feature Installation
The system MUST support modular deployment through Karaf features.

#### Scenario: Feature Install
- **GIVEN** a feature is defined in features.xml
- **WHEN** `feature:install <name>` is executed
- **THEN** all bundles in the feature MUST be installed and started

#### Scenario: Feature Dependencies
- **GIVEN** a feature depends on other features
- **WHEN** the feature is installed
- **THEN** dependent features MUST be installed first

#### Scenario: Feature Uninstall
- **GIVEN** a feature is installed
- **WHEN** `feature:uninstall <name>` is executed
- **THEN** feature bundles MUST stop (unless shared by other features)

---

### Requirement: Bundle Lifecycle
The system MUST manage bundle lifecycle according to OSGi specification.

#### Scenario: Bundle Start
- **GIVEN** a bundle JAR with valid MANIFEST.MF
- **WHEN** the bundle is started
- **THEN** BundleActivator.start() MUST be called (if present)

#### Scenario: Bundle Stop
- **GIVEN** a running bundle
- **WHEN** the bundle is stopped
- **THEN** BundleActivator.stop() MUST be called and services unregistered

#### Scenario: Package Resolution
- **GIVEN** a bundle with Import-Package declarations
- **WHEN** the bundle resolves
- **THEN** all imported packages MUST be satisfied by other bundles

---

### Requirement: Application Hierarchy
The system MUST organize features into user-installable applications.

#### Scenario: Application Definition
- **GIVEN** an application feature (e.g., catalog-app)
- **WHEN** the application is installed
- **THEN** all component features MUST be installed

#### Scenario: Application Status
- **GIVEN** an application is partially installed
- **WHEN** status is queried
- **THEN** missing/failed features MUST be reported
