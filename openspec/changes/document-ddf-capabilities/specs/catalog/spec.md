## ADDED Requirements

### Requirement: Catalog Framework Operations
The system MUST provide CRUD operations for metacards through a centralized CatalogFramework.

#### Scenario: Create Metacard
- **WHEN** a client submits a CreateRequest with valid metacards
- **THEN** the system MUST execute PreIngestPlugins, persist to CatalogProvider, execute PostIngestPlugins
- **AND** return a CreateResponse with created metacard IDs

#### Scenario: Query Metacards
- **WHEN** a client submits a QueryRequest with a filter
- **THEN** the system MUST execute PreQueryPlugins, federate to sources, aggregate results
- **AND** execute PostQueryPlugins and return a QueryResponse

#### Scenario: Update Metacard
- **WHEN** a client submits an UpdateRequest with modified metacards
- **THEN** the system MUST validate existence, execute plugins, and persist changes

#### Scenario: Delete Metacard
- **WHEN** a client submits a DeleteRequest with metacard IDs
- **THEN** the system MUST execute plugins and remove metacards from storage

---

### Requirement: Plugin Chain Execution
The system MUST execute plugins in a defined order based on service ranking.

#### Scenario: Pre-Ingest Plugin Chain
- **GIVEN** plugins registered with different service rankings
- **WHEN** a create operation is processed
- **THEN** PreIngestPlugins MUST execute in descending ranking order

#### Scenario: Plugin Stop Processing
- **GIVEN** a plugin throws StopProcessingException
- **WHEN** the exception is thrown
- **THEN** the plugin chain MUST halt and return error to client

---

### Requirement: Federation Strategy
The system MUST support federated queries across multiple sources.

#### Scenario: Parallel Federation
- **GIVEN** multiple federated sources are configured
- **WHEN** a query includes those sources
- **THEN** queries MUST execute in parallel with configurable timeout

#### Scenario: Source Unavailable
- **GIVEN** a federated source is unreachable
- **WHEN** a query includes that source
- **THEN** partial results from available sources MUST be returned

---

### Requirement: Source Hierarchy
The system MUST support three source types with distinct capabilities.

#### Scenario: CatalogProvider (Local)
- **GIVEN** a CatalogProvider is registered
- **WHEN** create/update/delete operations are performed
- **THEN** the CatalogProvider MUST persist changes locally

#### Scenario: FederatedSource (Remote, Optional)
- **GIVEN** a FederatedSource is registered
- **WHEN** a query specifies that source
- **THEN** the source MUST be queried for results

#### Scenario: ConnectedSource (Remote, Always Queried)
- **GIVEN** a ConnectedSource is registered
- **WHEN** any query is executed
- **THEN** the ConnectedSource MUST always be included

---

### Requirement: Metacard Data Model
The system MUST use Metacard as the core data container with typed attributes.

#### Scenario: Metacard Creation
- **GIVEN** a MetacardType defining valid attributes
- **WHEN** a Metacard is created
- **THEN** it MUST have id, type reference, and attribute map

#### Scenario: Attribute Types
- **GIVEN** an Attribute is added to a Metacard
- **WHEN** the value is set
- **THEN** it MUST match the AttributeType (STRING, DATE, GEOMETRY, etc.)
