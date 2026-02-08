## ADDED Requirements

### Requirement: Pax Web 11 feature repository integration
DDF MUST load Pax Web 11.1.0 feature repository and install Pax Web 11 features instead of Pax Web 8 features on Karaf 4.4.9.

#### Scenario: Kernel boot with Pax Web 11 features
- **WHEN** DDF kernel starts on Karaf 4.4.9
- **THEN** Pax Web 11.1.0 features are resolved and installed (pax-web-jetty, pax-web-http-whiteboard, pax-web-karaf)
- **AND** Jetty 12.0.x bundles are active (not Jetty 9.4.x)
- **AND** jakarta.servlet-api bundle is active (not javax.servlet-api 3.1.0)

#### Scenario: Pax Web configuration compatibility
- **WHEN** `org.ops4j.pax.web.cfg` is loaded by Pax Web 11
- **THEN** all existing configuration properties (SSL, threadpool, ports) are applied correctly
- **AND** HTTPS connector starts with configured keystore/truststore

### Requirement: Version property updates
Root POM version properties MUST be updated to reflect Pax Web 11 and Jetty 12 versions.

#### Scenario: Consistent version management
- **WHEN** `pax.web.version` property is referenced in feature.xml or POM files
- **THEN** it resolves to `11.1.0`
- **AND** `jetty.version` resolves to the Jetty 12 version used by Pax Web 11.1.0

### Requirement: Feature.xml updates
All Karaf feature.xml files referencing Pax Web features MUST be updated to use Pax Web 11 feature repository.

#### Scenario: Kernel feature installs Pax Web 11
- **WHEN** kernel feature.xml is processed
- **THEN** Pax Web feature repository URL uses version `11.1.0`
- **AND** `jakarta-servlet-api` feature is used (javax.servlet-api bundle removed)
- **AND** pax-web-http-war, pax-web-karaf features resolve from Pax Web 11 repository

#### Scenario: Security feature installs Pax Web 11 components
- **WHEN** security feature.xml is processed
- **THEN** pax-web-http-whiteboard, pax-web-jetty, pax-web-http-jetty resolve from Pax Web 11
- **AND** pax-web-jsp bundle version matches Pax Web 11.1.0

### Requirement: Pax Web 8 feature override
Since Karaf 4.4.9 ships Pax Web 8 by default, the DDF distribution MUST override Karaf's default Pax Web features.

#### Scenario: No Pax Web 8 bundles active
- **WHEN** DDF is fully started
- **THEN** no Pax Web 8.x bundles are installed
- **AND** no Jetty 9.4.x bundles are installed (except if needed by other components)
- **AND** only Pax Web 11.x bundles are active for web container functionality
