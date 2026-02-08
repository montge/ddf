## Purpose

Specifies the content storage and resource management subsystem, including the pluggable StorageProvider interface for binary content CRUD operations, the ContentItem data model, content-metacard linking, resource retrieval via scheme-based readers, and derived resource support.

## Requirements

### Requirement: Content Storage Provider
The system MUST provide a pluggable StorageProvider interface for binary content storage.

#### Scenario: Create Content
- **GIVEN** a CreateStorageRequest with ContentItems
- **WHEN** StorageProvider.create() is called
- **THEN** content MUST be stored with auto-generated GUIDs
- **AND** CreateStorageResponse MUST contain ContentItems with assigned URIs

#### Scenario: Read Content
- **GIVEN** a content URI (e.g., `content:<GUID>#<qualifier>`)
- **WHEN** StorageProvider.read() is called via ReadStorageRequest
- **THEN** ReadStorageResponse MUST return the ContentItem with InputStream

#### Scenario: Update Content
- **GIVEN** an UpdateStorageRequest with modified ContentItems
- **WHEN** StorageProvider.update() is called
- **THEN** existing content MUST be replaced
- **AND** UpdateStorageResponse MUST contain updated ContentItems

#### Scenario: Delete Content
- **GIVEN** a DeleteStorageRequest with Metacard list
- **WHEN** StorageProvider.delete() is called
- **THEN** all content associated with those Metacards MUST be removed
- **AND** DeleteStorageResponse MUST list deleted ContentItems

#### Scenario: Transactional Support
- **GIVEN** a storage operation in progress
- **WHEN** commit(StorageRequest) is called
- **THEN** changes MUST be persisted permanently
- **WHEN** rollback(StorageRequest) is called instead
- **THEN** changes MUST be reverted

---

### Requirement: ContentItem Data Model
The system MUST represent stored content as ContentItem objects with unique URIs.

#### Scenario: ContentItem Structure
- **GIVEN** a ContentItem is created
- **THEN** it MUST have: id (GUID), uri, filename, mimeType, size, inputStream, metacard reference

#### Scenario: Content URI Format
- **GIVEN** stored content
- **WHEN** the URI is generated
- **THEN** format MUST be `content:<GUID>#<qualifier>`
- **AND** null/blank qualifier indicates the primary product

#### Scenario: Qualified Content (Derivatives)
- **GIVEN** a ContentItem with qualifier "thumbnail"
- **WHEN** stored alongside the main product
- **THEN** it MUST have distinct URI: `content:<GUID>#thumbnail`
- **AND** enable retrieval of alternative formats

#### Scenario: Default MIME Type
- **GIVEN** a ContentItem without explicit MIME type
- **WHEN** stored
- **THEN** MIME type MUST default to "application/octet-stream"

---

### Requirement: Content Storage Plugin Chain
The system MUST execute storage plugins during create and update operations.

#### Scenario: Pre-Create Storage Plugin
- **GIVEN** PreCreateStoragePlugins are registered
- **WHEN** content creation begins
- **THEN** plugins MUST process CreateStorageRequest before storage
- **AND** plugins MAY transform content or modify metadata

#### Scenario: Post-Create Storage Plugin
- **GIVEN** PostCreateStoragePlugins are registered
- **WHEN** content is successfully stored
- **THEN** plugins MUST process CreateStorageResponse
- **AND** plugins MAY perform post-storage operations (indexing, notifications)

#### Scenario: Pre-Update Storage Plugin
- **GIVEN** PreUpdateStoragePlugins are registered
- **WHEN** content update begins
- **THEN** plugins MUST process UpdateStorageRequest before storage

#### Scenario: Post-Update Storage Plugin
- **GIVEN** PostUpdateStoragePlugins are registered
- **WHEN** content is successfully updated
- **THEN** plugins MUST process UpdateStorageResponse

---

### Requirement: Content-Metacard Linking
The system MUST link stored content to Metacards via resource attributes.

#### Scenario: Resource URI Population
- **GIVEN** content is stored with associated Metacard
- **WHEN** storage completes
- **THEN** Metacard.RESOURCE_URI MUST be set to content URI
- **AND** Metacard.RESOURCE_SIZE MUST be set to content size in bytes

#### Scenario: ContentItem-Metacard Association
- **GIVEN** a ContentItem
- **WHEN** getMetacard() is called
- **THEN** the associated Metacard reference MUST be returned

#### Scenario: ID Matching
- **GIVEN** a main product ContentItem (no qualifier)
- **WHEN** stored
- **THEN** ContentItem.getId() MUST match Metacard.getId()

---

### Requirement: Resource Retrieval
The system MUST retrieve resources via ResourceReader implementations.

#### Scenario: Local Resource Retrieval
- **GIVEN** a Metacard with local content URI (content:// scheme)
- **WHEN** ResourceRequest is submitted
- **THEN** LocalResourceRetriever MUST use appropriate ResourceReader
- **AND** return ResourceResponse with InputStream

#### Scenario: Remote Resource Retrieval
- **GIVEN** a Metacard from FederatedSource with remote resource
- **WHEN** ResourceRequest is submitted
- **THEN** RemoteResourceRetriever MUST fetch from the federated source

#### Scenario: Scheme-Based Reader Selection
- **GIVEN** ResourceReaders registered with different schemes (content, http, file)
- **WHEN** resource is requested
- **THEN** reader supporting the URI scheme MUST be selected

#### Scenario: Resource Plugins
- **GIVEN** PreResourcePlugin and PostResourcePlugin instances
- **WHEN** resource retrieval occurs
- **THEN** PreResourcePlugin MUST execute before retrieval
- **AND** PostResourcePlugin MUST execute after retrieval

---

### Requirement: Derived Resources
The system MUST support multiple content items per Metacard via qualifiers.

#### Scenario: Thumbnail Storage
- **GIVEN** a ContentItem with qualifier="thumbnail"
- **WHEN** stored alongside main product
- **THEN** it MUST be accessible via `content:<GUID>#thumbnail`

#### Scenario: Derived Resource Attributes
- **GIVEN** a Metacard with derived content
- **THEN** DERIVED_RESOURCE_URI attribute MUST contain alternative URIs
- **AND** DERIVED_RESOURCE_DOWNLOAD_URL MUST provide download URLs

#### Scenario: Multiple Qualifiers
- **GIVEN** multiple ContentItems with different qualifiers for same Metacard
- **WHEN** all are stored
- **THEN** each MUST have distinct URI and be independently retrievable

---

### Requirement: File System Storage Provider
The system MUST provide a file-based StorageProvider implementation as default.

#### Scenario: File Storage Location
- **GIVEN** FileSystemStorageProvider is configured
- **WHEN** content is stored
- **THEN** files MUST be written to configured directory (default: `$DDF_HOME/content`)

#### Scenario: Directory Structure
- **GIVEN** content with GUID "abc123"
- **WHEN** stored
- **THEN** file path MUST be based on GUID for distribution (e.g., `content/store/abc/123/...`)

---

## Design Notes

### Content Interfaces (catalog-core-api)
```java
public interface ContentItem {
  String getId();           // GUID
  URI getUri();             // content:<GUID>#<qualifier>
  String getFilename();
  String getMimeType();
  long getSize();
  InputStream getInputStream();
  Metacard getMetacard();
  String getQualifier();    // null for main, "thumbnail" for derived
}

public interface StorageProvider {
  CreateStorageResponse create(CreateStorageRequest request);
  ReadStorageResponse read(ReadStorageRequest request);
  UpdateStorageResponse update(UpdateStorageRequest request);
  DeleteStorageResponse delete(DeleteStorageRequest request);
  void commit(StorageRequest request);
  void rollback(StorageRequest request);
}
```

### Storage Plugin Interfaces
| Plugin | Processes | Purpose |
|--------|-----------|---------|
| PreCreateStoragePlugin | CreateStorageRequest | Transform before storage |
| PostCreateStoragePlugin | CreateStorageResponse | Post-storage processing |
| PreUpdateStoragePlugin | UpdateStorageRequest | Transform before update |
| PostUpdateStoragePlugin | UpdateStorageResponse | Post-update processing |

### Resource Retrieval Flow
```
ResourceRequest
    |
PreResourcePlugin.process()
    |
Metacard lookup (get RESOURCE_URI)
    |
ResourceReader selection (by URI scheme)
    |-- LocalResourceRetriever (content://, file://)
    +-- RemoteResourceRetriever (federated source)
    |
PolicyPlugin.processPostResource()
    |
AccessPlugin.processPostResource()
    |
PostResourcePlugin.process()
    |
ResourceResponse
```

### Metacard Resource Attributes
| Attribute | Type | Purpose |
|-----------|------|---------|
| RESOURCE_URI | URI | Primary content reference |
| RESOURCE_SIZE | Long | Size in bytes |
| RESOURCE_DOWNLOAD_URL | String | Download URL |
| DERIVED_RESOURCE_URI | List<URI> | Alternative format URIs |
| DERIVED_RESOURCE_DOWNLOAD_URL | List<String> | Alternative download URLs |

### Key Classes
- `ContentItemImpl` - Default ContentItem implementation
- `FileSystemStorageProvider` - File-based storage (catalog-core-localstorageprovider)
- `CreateOperations`, `UpdateOperations`, `DeleteOperations` - Storage orchestration
- `ResourceOperations` - Resource retrieval coordination
- `LocalResourceRetriever`, `RemoteResourceRetriever` - Retrieval strategies
