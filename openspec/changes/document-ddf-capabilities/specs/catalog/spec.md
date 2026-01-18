## ADDED Requirements

### Requirement: Catalog Framework Operations
The system MUST provide CRUD operations for metacards through a centralized CatalogFramework that delegates to specialized operation classes.

#### Scenario: Create Metacard
- **WHEN** a client submits a CreateRequest with valid metacards
- **THEN** CatalogFrameworkImpl MUST delegate to CreateOperations
- **AND** execute the plugin chain: AttributeInjector → DefaultValues → PreAuthorizationPlugin → PolicyPlugin → AccessPlugin → PreIngestPlugin → CatalogProvider.create() → PostIngestPlugin
- **AND** return a CreateResponse with created metacard IDs

#### Scenario: Query Metacards
- **WHEN** a client submits a QueryRequest with a filter
- **THEN** CatalogFrameworkImpl MUST delegate to QueryOperations
- **AND** execute: PreAuthorizationPlugin → PolicyPlugin → AccessPlugin → PreQueryPlugin → FederationStrategy.federate() → PostQueryPlugin
- **AND** return a QueryResponse with matching results

#### Scenario: Update Metacard
- **WHEN** a client submits an UpdateRequest with modified metacards
- **THEN** CatalogFrameworkImpl MUST delegate to UpdateOperations
- **AND** validate existence, execute the plugin chain, and persist changes

#### Scenario: Delete Metacard
- **WHEN** a client submits a DeleteRequest with metacard IDs
- **THEN** CatalogFrameworkImpl MUST delegate to DeleteOperations
- **AND** execute plugins and remove metacards from CatalogProvider

#### Scenario: Resource Retrieval
- **WHEN** a client submits a ResourceRequest
- **THEN** CatalogFrameworkImpl MUST delegate to ResourceOperations
- **AND** execute PreResourcePlugin → resource fetch → PostResourcePlugin

---

### Requirement: Plugin Chain Execution
The system MUST execute plugins in a defined order based on OSGi service ranking (higher ranking executes first, default is 0).

#### Scenario: Ingest Plugin Order
- **GIVEN** PreIngestPlugins registered with service rankings 100, 50, and 0
- **WHEN** a create operation is processed
- **THEN** plugins MUST execute in order: ranking 100 → 50 → 0

#### Scenario: Plugin Stop Processing
- **GIVEN** a plugin throws StopProcessingException
- **WHEN** the exception is thrown during chain execution
- **THEN** the plugin chain MUST halt immediately
- **AND** return error to client without executing remaining plugins

#### Scenario: Plugin Request Modification
- **GIVEN** a PreIngestPlugin modifies the CreateRequest
- **WHEN** the plugin returns the modified request
- **THEN** subsequent plugins MUST receive the modified request

---

### Requirement: Security Plugin Chain
The system MUST process security decisions through PreAuthorizationPlugin → PolicyPlugin → AccessPlugin for all operations.

#### Scenario: Pre-Authorization Processing
- **GIVEN** a catalog operation request
- **WHEN** PreAuthorizationPlugin.processPreCreate/Query/Delete() executes
- **THEN** initial security preprocessing MUST occur before policy evaluation

#### Scenario: Policy Building
- **GIVEN** a request passes pre-authorization
- **WHEN** PolicyPlugin.processPreCreate/Query/etc() executes
- **THEN** a policy map (Map<String, Set<String>>) MUST be built for access decisions

#### Scenario: Access Decision
- **GIVEN** a policy map is built
- **WHEN** AccessPlugin.processPreCreate/Query/etc() executes
- **THEN** an allow/deny decision MUST be made based on user attributes

#### Scenario: Post-Query Filtering
- **GIVEN** query results are returned from sources
- **WHEN** AccessPlugin.processPostQuery() executes
- **THEN** results MUST be filtered based on user's security attributes

---

### Requirement: Federation Strategy
The system MUST support federated queries across multiple sources via pluggable FederationStrategy.

#### Scenario: Parallel Federation
- **GIVEN** SortedFederationStrategy is configured with multiple sources
- **WHEN** a query is federated
- **THEN** queries MUST execute in parallel via ExecutorService
- **AND** results MUST be aggregated and sorted

#### Scenario: Source Timeout
- **GIVEN** a source does not respond within timeout
- **WHEN** federation completes
- **THEN** partial results from responding sources MUST be returned
- **AND** source failure MUST be indicated in response

#### Scenario: Query Sorting
- **GIVEN** a QueryRequest with sort criteria (relevance, temporal, distance)
- **WHEN** SortedFederationStrategy aggregates results
- **THEN** results MUST be sorted according to specified criteria and order (ASC/DESC)

#### Scenario: Pagination Limits
- **GIVEN** SortedFederationStrategy with maxStartIndex=50000 (default)
- **WHEN** a query requests startIndex > maxStartIndex
- **THEN** the query MUST be rejected or limited

---

### Requirement: Source Hierarchy
The system MUST support three source types with distinct capabilities and query inclusion behavior.

#### Scenario: CatalogProvider (Local Storage)
- **GIVEN** a CatalogProvider is registered (typically SolrCatalogProvider)
- **WHEN** create/update/delete operations are performed
- **THEN** the CatalogProvider MUST persist changes locally
- **AND** the CatalogProvider MUST be included in ALL queries

#### Scenario: FederatedSource (Remote, Optional)
- **GIVEN** FederatedSource instances are registered
- **WHEN** a query explicitly requests those source IDs
- **THEN** the specified FederatedSources MUST be queried
- **AND** FederatedSources MUST NOT be queried unless explicitly requested

#### Scenario: ConnectedSource (Remote, Always Queried)
- **GIVEN** ConnectedSource instances are registered
- **WHEN** any query is executed (local or enterprise)
- **THEN** all ConnectedSources MUST always be included in federation
- **AND** ConnectedSources MUST NOT be queryable by name

#### Scenario: Source Availability Check
- **GIVEN** a Source with isAvailable() method
- **WHEN** federation selects sources
- **THEN** only available sources MUST be included in query distribution

---

### Requirement: Metacard Data Model
The system MUST use Metacard as the core data container with typed attributes defined by MetacardType.

#### Scenario: Metacard Structure
- **GIVEN** a Metacard instance
- **WHEN** attributes are accessed
- **THEN** it MUST provide: getId(), getMetacardType(), getAttribute(name), setAttribute(attribute)
- **AND** convenience methods: getTitle(), getLocation(), getCreatedDate(), getModifiedDate(), getMetadata()

#### Scenario: MetacardType Schema
- **GIVEN** a MetacardType (e.g., BASIC_METACARD)
- **WHEN** getAttributeDescriptors() is called
- **THEN** it MUST return the Set of valid AttributeDescriptor for that type

#### Scenario: AttributeDescriptor Definition
- **GIVEN** an AttributeDescriptor
- **WHEN** its properties are queried
- **THEN** it MUST specify: name, type (AttributeType), isIndexed, isStored, isTokenized, isMultiValued

#### Scenario: Attribute Types
- **GIVEN** an AttributeType with AttributeFormat
- **WHEN** the format is queried
- **THEN** it MUST be one of: STRING, DATE, BOOLEAN, LONG, INTEGER, SHORT, FLOAT, DOUBLE, GEOMETRY, BINARY, XML, OBJECT

---

### Requirement: Reserved Metacard Attributes
The system MUST support standard reserved attributes on all Metacards.

#### Scenario: Core Attributes
- **GIVEN** a Metacard
- **THEN** it MUST support attributes: ID, TITLE, METADATA, GEOGRAPHY (location), TAGS, SECURITY, DESCRIPTION

#### Scenario: Temporal Attributes
- **GIVEN** a Metacard
- **THEN** it MUST support temporal attributes: CREATED, MODIFIED, EFFECTIVE, EXPIRATION

#### Scenario: Resource Attributes
- **GIVEN** a Metacard with associated resource
- **THEN** it MUST support: RESOURCE_URI, RESOURCE_SIZE, RESOURCE_DOWNLOAD_URL, THUMBNAIL, CHECKSUM

#### Scenario: Geometry Storage
- **GIVEN** a Metacard with location
- **WHEN** getLocation() is called
- **THEN** geometry MUST be returned in WKT (Well-Known Text) format

---

### Requirement: Attribute Injection
The system MUST support pluggable AttributeInjector for adding derived attributes during ingest.

#### Scenario: Injection Before Plugins
- **GIVEN** AttributeInjector instances are registered
- **WHEN** a create operation begins
- **THEN** injectors MUST execute before PreIngestPlugin chain

#### Scenario: Default Value Setting
- **GIVEN** a MetacardType with default values defined
- **WHEN** a Metacard is created without those attributes
- **THEN** default values MUST be applied via setDefaultValues()

---

### Requirement: Fanout Proxy Mode
The system MUST optionally support fanout mode that virtualizes all sources as a single source.

#### Scenario: Fanout Enabled
- **GIVEN** fanoutEnabled=true in configuration
- **WHEN** queries are executed
- **THEN** all federated sources MUST appear as a single virtual source
- **AND** source IDs in results MUST be rewritten to DDF site name

#### Scenario: Fanout Blacklist
- **GIVEN** fanout mode with blacklisted tags
- **WHEN** create operations include blacklisted tags
- **THEN** remote creation MUST be blocked for those metacards

---

### Requirement: Operation Transaction Context
The system MUST track operations via OperationTransaction for rollback and auditing.

#### Scenario: Transaction Creation
- **GIVEN** a create/update/delete operation begins
- **WHEN** OperationTransaction is created
- **THEN** it MUST record: operation type (CREATE/UPDATE/DELETE), original metacards list

#### Scenario: Rollback Support
- **GIVEN** an operation fails after partial completion
- **WHEN** rollback is triggered
- **THEN** OperationTransaction MUST provide original state for restoration

---

## Design Notes

### Key Classes
- `CatalogFrameworkImpl` - Main orchestrator (catalog-core-standardframework)
- `FrameworkProperties` - Central registry for plugins, sources, strategies
- `CreateOperations`, `UpdateOperations`, `DeleteOperations`, `QueryOperations`, `ResourceOperations` - Delegated operation handlers
- `SortedFederationStrategy` - Default federation with parallel queries and sorting

### Plugin Interfaces (catalog-core-api)
- `PreIngestPlugin` / `PostIngestPlugin` - Ingest processing
- `PreQueryPlugin` / `PostQueryPlugin` - Query processing
- `PreResourcePlugin` / `PostResourcePlugin` - Resource retrieval
- `PreAuthorizationPlugin` - Security pre-processing
- `PolicyPlugin` - Policy building
- `AccessPlugin` - Access decisions

### Source Interfaces (catalog-core-api)
- `Source` - Base interface with query() and isAvailable()
- `CatalogProvider` - Local writable storage (extends Source)
- `FederatedSource` - Remote optional (extends RemoteSource)
- `ConnectedSource` - Remote always-queried (extends RemoteSource)

### Data Model (catalog-core-api)
- `Metacard` - Core container with getId(), getAttribute(), getMetacardType()
- `MetacardType` - Schema defining valid AttributeDescriptors
- `AttributeDescriptor` - Attribute metadata (indexed, stored, tokenized, multivalued)
- `AttributeType` - Type with AttributeFormat enum and Java Class binding
- `Attribute` - Actual value instance with name and value(s)
