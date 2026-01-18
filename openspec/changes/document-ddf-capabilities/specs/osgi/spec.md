## ADDED Requirements

### Requirement: Blueprint Service Registration
The system MUST use Apache Aries Blueprint for declarative OSGi service registration in all bundles.

#### Scenario: Bean Definition
- **GIVEN** a blueprint.xml with a bean element
- **WHEN** the bundle starts
- **THEN** the bean MUST be instantiated with constructor injection (`<argument>`) and setter injection (`<property>`)

#### Scenario: Service Export
- **GIVEN** a blueprint.xml with `<service ref="beanId" interface="InterfaceName">`
- **WHEN** the bundle starts
- **THEN** the bean MUST be registered in the OSGi service registry
- **AND** service-properties MUST be available for filtering

#### Scenario: Service Reference (Single)
- **GIVEN** a blueprint.xml with `<reference id="refId" interface="InterfaceName">`
- **WHEN** the bundle resolves
- **THEN** the reference MUST block until a matching service is available
- **AND** optional filter attribute (`filter="(id=name)"`) MUST narrow selection

#### Scenario: Service Reference List (Dynamic)
- **GIVEN** a blueprint.xml with `<reference-list interface="InterfaceName" availability="optional">`
- **WHEN** services matching the interface are registered/unregistered
- **THEN** the list MUST dynamically update via bind/unbind callbacks
- **AND** SortedServiceList MUST maintain service ranking order

---

### Requirement: Plugin Discovery Pattern
The system MUST discover and rank plugins via reference-list with SortedServiceList.

#### Scenario: Plugin Binding
- **GIVEN** a reference-list with reference-listener pointing to SortedServiceList
- **WHEN** a plugin service is registered with service.ranking property
- **THEN** the plugin MUST be added to the list in ranked order (higher first)

#### Scenario: Plugin Unbinding
- **GIVEN** a plugin is registered in a reference-list
- **WHEN** the plugin bundle stops
- **THEN** unbindPlugin() MUST be called and plugin removed from list

#### Scenario: Optional Availability
- **GIVEN** a reference-list with `availability="optional"`
- **WHEN** no matching services exist
- **THEN** the bundle MUST still start with an empty list

---

### Requirement: Configuration Management
The system MUST support hot-reloadable configuration via OSGi Config Admin.

#### Scenario: Managed Properties
- **GIVEN** a bean with `<cm:managed-properties persistent-id="org.example.Service">`
- **WHEN** `$DDF_HOME/etc/org.example.Service.config` changes
- **THEN** properties MUST be injected into the bean without bundle restart

#### Scenario: Managed Service Factory
- **GIVEN** a `<cm:managed-service-factory factory-pid="org.example.Service">`
- **WHEN** a new config file matching the factory-pid is created
- **THEN** a new service instance MUST be created from that configuration

#### Scenario: Configuration Naming
- **GIVEN** a persistent-id "org.codice.ddf.catalog.plugin.MyPlugin"
- **WHEN** the bundle starts
- **THEN** configuration MUST be read from `etc/org.codice.ddf.catalog.plugin.MyPlugin.config`

---

### Requirement: Bean Lifecycle Management
The system MUST support init-method and destroy-method for bean lifecycle.

#### Scenario: Initialization
- **GIVEN** a bean with `init-method="init"`
- **WHEN** the bean is fully constructed and dependencies injected
- **THEN** the init() method MUST be called

#### Scenario: Destruction
- **GIVEN** a bean with `destroy-method="shutdown"`
- **WHEN** the bundle stops
- **THEN** the shutdown() method MUST be called for resource cleanup

---

### Requirement: Karaf Feature Installation
The system MUST support modular deployment through Karaf features defined in feature.xml.

#### Scenario: Feature Definition
- **GIVEN** a feature.xml with `<feature name="catalog-core-api">`
- **WHEN** `feature:install catalog-core-api` is executed
- **THEN** all bundles in the feature MUST be installed and started

#### Scenario: Feature Dependencies
- **GIVEN** a feature with `<feature>security-core-api</feature>` dependency
- **WHEN** the feature is installed
- **THEN** security-core-api MUST be installed first

#### Scenario: Bundle Start Level
- **GIVEN** a feature with `<bundle start-level="30">mvn:...</bundle>`
- **WHEN** the feature is installed
- **THEN** the bundle MUST start at level 30 (before default level 40)

#### Scenario: Feature Uninstall
- **GIVEN** an installed feature
- **WHEN** `feature:uninstall feature-name` is executed
- **THEN** feature bundles MUST stop unless shared by other installed features

---

### Requirement: Feature Repository Management
The system MUST support feature repositories for external feature discovery.

#### Scenario: Repository Registration
- **GIVEN** a feature.xml with `<repository>mvn:groupId/artifactId/version/xml/features</repository>`
- **WHEN** the feature is loaded
- **THEN** the referenced repository MUST be available for feature resolution

#### Scenario: Feature Version Resolution
- **GIVEN** multiple versions of a feature in repositories
- **WHEN** a feature is installed without version
- **THEN** the highest available version MUST be selected

---

### Requirement: Bundle Lifecycle
The system MUST manage bundle lifecycle according to OSGi specification.

#### Scenario: Bundle Resolution
- **GIVEN** a bundle with Import-Package declarations
- **WHEN** the bundle resolves
- **THEN** all imported packages MUST be satisfied by other bundles with matching version ranges

#### Scenario: Bundle Start
- **GIVEN** a bundle with Bundle-Blueprint header
- **WHEN** the bundle starts
- **THEN** blueprint.xml MUST be processed and services registered

#### Scenario: Bundle Stop
- **GIVEN** a running bundle with registered services
- **WHEN** the bundle stops
- **THEN** all services MUST be unregistered from OSGi registry

#### Scenario: Package Version Ranges
- **GIVEN** an Import-Package with version range `[2.30,3)`
- **WHEN** resolving packages
- **THEN** any version ≥2.30.0 and <3.0.0 MUST satisfy the import

---

### Requirement: Application Hierarchy
The system MUST organize features into user-installable applications following a three-tier model.

#### Scenario: Application Definition
- **GIVEN** an application feature (e.g., catalog-app) with install="auto"
- **WHEN** the KAR file is deployed
- **THEN** all component features MUST be automatically installed

#### Scenario: Application Dependencies
- **GIVEN** catalog-app depends on security-core-services and platform-app
- **WHEN** catalog-app is installed
- **THEN** dependencies MUST be installed in order: security → platform → catalog

#### Scenario: Boot Feature Chain
- **GIVEN** ddf-boot-features is configured
- **WHEN** DDF starts
- **THEN** features MUST install in order: kernel → ddf-core → security-core-services → platform-app → admin-app

---

### Requirement: JAX-RS Service Registration
The system MUST support CXF JAX-RS endpoint registration via Blueprint.

#### Scenario: REST Endpoint Registration
- **GIVEN** a blueprint.xml with `<jaxrs:server address="/catalog">`
- **WHEN** the bundle starts
- **THEN** JAX-RS resources MUST be available at the specified address

#### Scenario: Interceptor Chain
- **GIVEN** a jaxrs:server with inInterceptors/outInterceptors
- **WHEN** requests are processed
- **THEN** interceptors MUST execute in the defined order

---

### Requirement: Property Placeholder Substitution
The system MUST support property substitution in blueprint.xml via ext:property-placeholder.

#### Scenario: System Property Substitution
- **GIVEN** a blueprint with `${ddf.home}` placeholder
- **WHEN** the bean is constructed
- **THEN** the system property value MUST be substituted

#### Scenario: Environment Variable Substitution
- **GIVEN** a blueprint with `${env:HOME}` placeholder
- **WHEN** the bean is constructed
- **THEN** the environment variable MUST be substituted

---

## Design Notes

### Blueprint File Location
- Standard path: `src/main/resources/OSGI-INF/blueprint/blueprint.xml`
- Auto-activated via `Bundle-Blueprint` MANIFEST header
- One blueprint.xml per bundle

### Feature File Locations
- Feature modules: `src/main/feature/feature.xml`
- Application modules: `src/main/resources/features.xml`
- Schema: Karaf 1.3.0 (`xmlns="http://karaf.apache.org/xmlns/features/v1.3.0"`)

### Common Blueprint Namespaces
```xml
xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
xmlns:cm="http://aries.apache.org/blueprint/xmlns/blueprint-cm/v1.1.0"
xmlns:ext="http://aries.apache.org/blueprint/xmlns/blueprint-ext/v1.2.0"
xmlns:jaxrs="http://cxf.apache.org/blueprint/jaxrs"
```

### Service Ranking Convention
- Default ranking: 0
- Higher values execute first in plugin chains
- Set via service-properties: `<entry key="service.ranking" value="100"/>`

### DDF Feature Hierarchy
```
ddf-boot-features
├── ddf-core (kernel + CXF + Camel + branding)
├── security-core-services
├── oidc-auth, saml-auth
├── platform-app
└── admin-app

catalog-app (optional)
├── catalog-core-impl
│   ├── catalog-core-api
│   └── security-core-services
├── catalog-opensearch
└── catalog-plugin-*
```

### Key Files
- `/features/kernel/src/main/feature/feature.xml` - Base kernel features
- `/features/security/src/main/feature/feature.xml` - Security layer
- `/features/apps/src/main/feature/feature.xml` - Platform and admin apps
- `/catalog/catalog-app/src/main/resources/features.xml` - Catalog application
