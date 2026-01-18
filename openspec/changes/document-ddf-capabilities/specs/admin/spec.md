## ADDED Requirements

### Requirement: Admin Console
The system MUST provide a web-based administration console.

#### Scenario: Console Access
- **GIVEN** a user with admin role
- **WHEN** accessing https://localhost:8993/admin
- **THEN** the admin console MUST be displayed

#### Scenario: Authentication Required
- **GIVEN** an unauthenticated user
- **WHEN** accessing admin console
- **THEN** user MUST be redirected to login

---

### Requirement: Configuration Management
The system MUST support viewing and modifying OSGi configurations.

#### Scenario: View Configuration
- **GIVEN** a Managed Service is registered
- **WHEN** its configuration is requested
- **THEN** all properties MUST be displayed

#### Scenario: Modify Configuration
- **GIVEN** a configuration with editable properties
- **WHEN** properties are modified and saved
- **THEN** changes MUST be persisted to etc/ and service notified

#### Scenario: Factory Configuration
- **GIVEN** a Managed Service Factory
- **WHEN** new instance is created
- **THEN** new configuration file MUST be created with unique PID

---

### Requirement: Application Management
The system MUST support installing and managing applications.

#### Scenario: List Applications
- **GIVEN** applications are defined
- **WHEN** application list is requested
- **THEN** all applications with status MUST be returned

#### Scenario: Install Application
- **GIVEN** an uninstalled application
- **WHEN** install is requested
- **THEN** all application features MUST be installed

#### Scenario: Uninstall Application
- **GIVEN** an installed application
- **WHEN** uninstall is requested
- **THEN** application features MUST be uninstalled

---

### Requirement: System Installer
The system MUST provide a guided setup wizard for initial configuration.

#### Scenario: First Boot
- **GIVEN** DDF is started for the first time
- **WHEN** admin console is accessed
- **THEN** installer wizard MUST be displayed

#### Scenario: Profile Selection
- **GIVEN** installer is running
- **WHEN** user selects installation profile
- **THEN** corresponding applications MUST be queued for installation

#### Scenario: System Configuration
- **GIVEN** installer is running
- **WHEN** system properties are configured (hostname, ports)
- **THEN** configuration MUST be applied to system.properties

---

### Requirement: Source Management
The system MUST allow adding and configuring catalog sources.

#### Scenario: Add Source
- **GIVEN** a source type (WFS, CSW, OpenSearch)
- **WHEN** source configuration is submitted
- **THEN** new source MUST be created and registered

#### Scenario: Edit Source
- **GIVEN** an existing source
- **WHEN** configuration is modified
- **THEN** source MUST be updated with new settings

#### Scenario: Delete Source
- **GIVEN** an existing source
- **WHEN** delete is requested
- **THEN** source configuration MUST be removed

#### Scenario: Test Source
- **GIVEN** a configured source
- **WHEN** connectivity test is requested
- **THEN** source availability MUST be verified and reported

---

### Requirement: System Status
The system MUST provide health and status information.

#### Scenario: Bundle Status
- **GIVEN** admin console is accessed
- **WHEN** system status is requested
- **THEN** bundle states (Active, Resolved, Installed) MUST be shown

#### Scenario: Feature Status
- **GIVEN** admin console is accessed
- **WHEN** feature status is requested
- **THEN** installed/uninstalled features MUST be listed

#### Scenario: Logs Access
- **GIVEN** admin with log access
- **WHEN** logs are requested
- **THEN** recent log entries MUST be displayed
