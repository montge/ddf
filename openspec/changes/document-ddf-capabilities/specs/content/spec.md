## ADDED Requirements

### Requirement: Content Storage Framework
The system MUST provide content storage and retrieval through a pluggable ContentStore.

#### Scenario: Content Create
- **GIVEN** a file to be stored with associated Metacard
- **WHEN** CreateStorageRequest is submitted
- **THEN** content MUST be stored and content URI returned

#### Scenario: Content Update
- **GIVEN** existing stored content
- **WHEN** UpdateStorageRequest is submitted
- **THEN** content MUST be replaced and version tracked

#### Scenario: Content Delete
- **GIVEN** existing stored content
- **WHEN** DeleteStorageRequest is submitted
- **THEN** content MUST be removed from storage

---

### Requirement: Resource Retrieval
The system MUST retrieve resources (files) associated with Metacards.

#### Scenario: Local Resource
- **GIVEN** a Metacard with local resource-uri
- **WHEN** ResourceRequest is submitted
- **THEN** resource MUST be retrieved from content store

#### Scenario: Remote Resource
- **GIVEN** a Metacard with remote resource-uri (http/https)
- **WHEN** ResourceRequest is submitted
- **THEN** resource MUST be fetched from remote location

#### Scenario: Cached Resource
- **GIVEN** a previously retrieved remote resource
- **WHEN** ResourceRequest is submitted again
- **THEN** cached version SHOULD be returned if still valid

---

### Requirement: Content Plugin Chain
The system MUST execute content plugins during storage operations.

#### Scenario: Pre-Create Plugin
- **GIVEN** PreCreateStoragePlugins are registered
- **WHEN** content create is requested
- **THEN** plugins MUST execute before storage

#### Scenario: Post-Create Plugin
- **GIVEN** PostCreateStoragePlugins are registered
- **WHEN** content is stored
- **THEN** plugins MUST execute after storage completes

---

### Requirement: MIME Type Detection
The system MUST detect MIME types for stored content.

#### Scenario: Extension-Based Detection
- **GIVEN** a file with known extension (.pdf, .xml, .json)
- **WHEN** MIME type is detected
- **THEN** appropriate MIME type MUST be assigned

#### Scenario: Content-Based Detection
- **GIVEN** a file without extension or with generic extension
- **WHEN** MIME type is detected
- **THEN** content analysis (magic bytes) MUST determine type

#### Scenario: Custom MIME Types
- **GIVEN** custom MimeTypeResolver registered
- **WHEN** MIME type detection is needed
- **THEN** custom resolver MUST be consulted

---

### Requirement: Content Item Model
The system MUST represent stored content as ContentItem objects.

#### Scenario: ContentItem Creation
- **GIVEN** content to be stored
- **WHEN** ContentItem is created
- **THEN** it MUST include id, MIME type, size, and input stream

#### Scenario: Derived Content
- **GIVEN** a ContentItem with derived content (thumbnail, preview)
- **WHEN** derived content is requested
- **THEN** it MUST be retrievable by qualifier
