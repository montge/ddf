## Purpose

Specifies the admin console and configuration management capabilities, including the web-based administration UI, pluggable admin modules, OSGi configuration management via ConfigurationAdmin, application and feature lifecycle management, and the system installer wizard.

## Requirements

### Requirement: Admin Console
The system MUST provide a web-based administration console at `/admin`.

#### Scenario: Console Access
- **GIVEN** a user with admin role
- **WHEN** accessing https://localhost:8993/admin
- **THEN** the admin console MUST be displayed with available modules

#### Scenario: Authentication Required
- **GIVEN** an unauthenticated user
- **WHEN** accessing admin console
- **THEN** user MUST be redirected to authentication

#### Scenario: Module Discovery
- **GIVEN** AdminModule services are registered
- **WHEN** admin console loads
- **THEN** each module MUST appear as a tab with name from getName()
- **AND** JavaScript from getJSLocation() MUST be loaded

---

### Requirement: Admin Module Architecture
The system MUST support pluggable admin modules via AdminModule interface.

#### Scenario: Module Registration
- **GIVEN** an AdminModule implementation registered as OSGi service
- **THEN** it MUST provide: getName(), getId(), getJSLocation()
- **AND** optionally: getCSSLocation(), getIframeLocation()

#### Scenario: Configuration Module
- **GIVEN** the Configuration admin module
- **WHEN** loaded
- **THEN** it MUST display all configurable services with metatype schemas

#### Scenario: Installer Module
- **GIVEN** the Installer admin module (Setup tab)
- **WHEN** loaded
- **THEN** it MUST display available features for installation

#### Scenario: Application Module
- **GIVEN** the Application admin module
- **WHEN** loaded
- **THEN** it MUST display application status and lifecycle controls

---

### Requirement: Configuration Management
The system MUST support viewing and modifying OSGi configurations via ConfigurationAdmin.

#### Scenario: List Services
- **GIVEN** ManagedService and ManagedServiceFactory instances registered
- **WHEN** listServices() is called
- **THEN** all configurable services MUST be returned with metatype information

#### Scenario: View Configuration
- **GIVEN** a service with persistent-id
- **WHEN** getConfiguration(pid) is called
- **THEN** all properties MUST be returned
- **AND** password fields MUST be masked

#### Scenario: Update Configuration
- **GIVEN** a configuration with modified properties
- **WHEN** update(pid, configurationTable) is called
- **THEN** changes MUST be persisted to `$DDF_HOME/etc/`
- **AND** service MUST be notified of changes

#### Scenario: Factory Configuration Create
- **GIVEN** a ManagedServiceFactory (e.g., FederatedSource)
- **WHEN** createFactoryConfiguration(factoryPid) is called
- **THEN** a new configuration instance MUST be created with unique PID

---

### Requirement: Metatype-Driven Configuration UI
The system MUST render configuration forms dynamically from ObjectClassDefinition.

#### Scenario: Attribute Discovery
- **GIVEN** a service with metatype.xml or @Designate annotation
- **WHEN** configuration UI is rendered
- **THEN** AttributeDefinition MUST define field type, cardinality, validation

#### Scenario: Type Coercion
- **GIVEN** configuration values from UI
- **WHEN** update() is called
- **THEN** TypeParser MUST convert strings to appropriate types (Integer, Boolean, etc.)

#### Scenario: Cardinality Handling
- **GIVEN** an AttributeDefinition with cardinality > 1 or < 0
- **WHEN** configuration is updated
- **THEN** values MUST be transformed to array or Vector as appropriate

---

### Requirement: Configuration Enable/Disable
The system MUST support enabling and disabling service configurations.

#### Scenario: Disable Configuration
- **GIVEN** an active managed service factory configuration
- **WHEN** disableConfiguration(pid) is called
- **THEN** factory-pid MUST be renamed with "_disabled" suffix
- **AND** service instance MUST be stopped

#### Scenario: Enable Configuration
- **GIVEN** a disabled configuration (with _disabled suffix)
- **WHEN** enableConfiguration(pid) is called
- **THEN** "_disabled" suffix MUST be removed
- **AND** service instance MUST be started

---

### Requirement: Security-Filtered Configuration
The system MUST apply role-based access control to configuration visibility.

#### Scenario: Service Permission Check
- **GIVEN** a user with specific roles
- **WHEN** configuration services are listed
- **THEN** only services permitted by AdminConfigPolicy MUST be visible

#### Scenario: Per-PID Authorization
- **GIVEN** isPermittedToViewService(servicePid, subject) check
- **WHEN** evaluating service visibility
- **THEN** KeyValueCollectionPermission with action "view-service.pid" MUST be checked

---

### Requirement: Application Management
The system MUST support managing DDF applications via ApplicationService.

#### Scenario: List Applications
- **GIVEN** ApplicationService is available
- **WHEN** getApplications() is called
- **THEN** all registered applications MUST be returned with status

#### Scenario: Get Application
- **GIVEN** an application name
- **WHEN** getApplication(name) is called
- **THEN** Application details MUST be returned (case-insensitive lookup)

#### Scenario: Installation Profiles
- **GIVEN** installation profiles defined in features
- **WHEN** getInstallationProfiles() is called
- **THEN** available profiles (starter, full, etc.) MUST be listed

---

### Requirement: Feature Management
The system MUST support feature lifecycle management via FeatureActions.

#### Scenario: Start Feature
- **GIVEN** an uninstalled feature
- **WHEN** start(featureName) is called
- **THEN** the feature and dependencies MUST be installed

#### Scenario: Stop Feature
- **GIVEN** an installed feature
- **WHEN** stop(featureName) is called
- **THEN** the feature MUST be uninstalled (if not required by others)

#### Scenario: Feature Status Check
- **GIVEN** a feature name
- **WHEN** isFeatureStarted(featureName) is called
- **THEN** current installation status MUST be returned

---

### Requirement: Source Configuration
The system MUST support configuring catalog sources through the admin console.

#### Scenario: Add Source
- **GIVEN** a source factory PID (e.g., ddf.catalog.source.FederatedSource)
- **WHEN** createFactoryConfiguration() is called with source properties
- **THEN** a new source instance MUST be created and registered

#### Scenario: Edit Source
- **GIVEN** an existing source configuration
- **WHEN** properties are modified via update()
- **THEN** the source MUST be reconfigured with new settings

#### Scenario: Delete Source
- **GIVEN** an existing source configuration
- **WHEN** the configuration is deleted
- **THEN** the source MUST be unregistered from catalog framework

#### Scenario: Test Source Connectivity
- **GIVEN** a configured source
- **WHEN** availability check is performed
- **THEN** source.isAvailable() MUST be called and result displayed

---

### Requirement: System Installer Wizard
The system MUST provide a guided setup wizard for initial configuration.

#### Scenario: First Boot Detection
- **GIVEN** DDF is started for the first time
- **WHEN** admin console is accessed
- **THEN** Setup (Installer) tab MUST guide initial configuration

#### Scenario: Profile Selection
- **GIVEN** installer is running
- **WHEN** user selects installation profile
- **THEN** features for that profile MUST be queued for installation

#### Scenario: System Properties Configuration
- **GIVEN** installer system configuration step
- **WHEN** hostname, ports, certificates are configured
- **THEN** system.properties MUST be updated

---

### Requirement: AdminConsoleService MBean
The system MUST expose configuration management as JMX MBean.

#### Scenario: JMX Access
- **GIVEN** AdminConsoleService MBean registered
- **WHEN** JMX client connects
- **THEN** all configuration operations MUST be available via MBean interface

#### Scenario: SSO Configuration
- **GIVEN** OIDC or SAML SSO settings
- **WHEN** getSsoConfigurations() / setSsoConfigurations() is called
- **THEN** SSO provider settings MUST be managed

---

## Design Notes

### Admin Module Interface
```java
public interface AdminModule {
  String getName();           // Tab name in UI
  String getId();             // DOM element ID
  URI getJSLocation();        // JavaScript module URI
  URI getCSSLocation();       // Optional CSS
  URI getIframeLocation();    // Optional iframe
}
```

### ConfigurationAdmin Interface
```java
public interface ConfigurationAdmin {
  List<Service> listServices(filter, serviceFilter);
  Configuration getConfiguration(pid);
  ObjectClassDefinition getObjectClassDefinition(config);
  boolean isPermittedToViewService(pid, subject);
  void enableManagedServiceFactoryConfiguration(pid);
  void disableManagedServiceFactoryConfiguration(pid, factoryPid);
}
```

### Admin Modules
| Module | Tab Name | Purpose |
|--------|----------|---------|
| Configuration | System | Service configuration management |
| Installer | Setup | First-time installation wizard |
| Application | Applications | App lifecycle management |
| Security Certificate | Certificates | X.509 certificate management |
| Metrics | Metrics | Micrometer metrics display |
| Docs | Help | Documentation system |

### Feature Hierarchy
```
admin-core (ConfigurationAdmin, AppService)
├── admin-ui (REST endpoint at /admin)
├── admin-configurator (high-level orchestration)
├── admin-config-updater (etc/ sync)
└── admin-modules-*
    ├── admin-modules-configuration
    ├── admin-modules-installer
    ├── admin-modules-application
    └── admin-modules-docs
```

### Configuration Management Patterns

**Metatype-Driven UI:**
- ObjectClassDefinition defines configurable properties
- AttributeDefinition specifies type, cardinality, validation
- UI rendered dynamically (no hardcoded forms)

**Enable/Disable Pattern:**
```
Active:   org.example.Service-abc123.config
Disabled: org.example.Service_disabled-abc123.config
```

**Security Integration:**
- All access checked against Apache Shiro Subject
- Per-PID authorization via AdminConfigPolicy
- Filtered service list based on user roles

### Key Files
- `/platform/admin/core/admin-core-api/` - Interfaces
- `/platform/admin/core/admin-core-impl/` - Implementation
- `/platform/admin/ui/` - REST endpoint
- `/platform/admin/modules/` - UI modules
- `/features/admin/src/main/feature/feature.xml` - Features
