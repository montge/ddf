## ADDED Requirements

### Requirement: Input Transformer Registry
The system MUST discover and register InputTransformers for file-to-metacard conversion.

#### Scenario: Transformer Discovery
- **GIVEN** an InputTransformer is registered as an OSGi service
- **WHEN** the service has `id` and `mime-type` properties
- **THEN** it MUST be available for transformation requests

#### Scenario: MIME Type Matching
- **GIVEN** a file with a specific MIME type
- **WHEN** transformation is requested
- **THEN** a transformer supporting that MIME type MUST be selected

---

### Requirement: Input Transformation
The system MUST convert various file formats to Metacard objects.

#### Scenario: XML Transformation
- **GIVEN** an XML file matching a supported schema
- **WHEN** the InputTransformer processes it
- **THEN** a Metacard MUST be created with extracted attributes

#### Scenario: Image Transformation
- **GIVEN** an image file (JPEG, PNG, TIFF)
- **WHEN** the InputTransformer processes it
- **THEN** a Metacard MUST include extracted EXIF/geolocation metadata

#### Scenario: Unknown Format
- **GIVEN** a file with unsupported MIME type
- **WHEN** transformation is attempted
- **THEN** either a fallback transformer MUST handle it or error returned

---

### Requirement: Metacard Transformer Registry
The system MUST discover and register MetacardTransformers for metacard-to-format conversion.

#### Scenario: Format Request
- **GIVEN** a Metacard and requested output format (JSON, XML, GeoJSON)
- **WHEN** transformation is requested
- **THEN** the appropriate MetacardTransformer MUST be invoked

#### Scenario: Default Formats
- **GIVEN** no specific format is requested
- **WHEN** metacard export is performed
- **THEN** JSON format MUST be used as default

---

### Requirement: Metacard Transformation
The system MUST convert Metacard objects to various output formats.

#### Scenario: JSON Output
- **GIVEN** a Metacard with attributes
- **WHEN** JSON transformation is requested
- **THEN** all attributes MUST be serialized to JSON with proper types

#### Scenario: XML Output
- **GIVEN** a Metacard with attributes
- **WHEN** XML transformation is requested
- **THEN** attributes MUST be serialized to valid XML

#### Scenario: GeoJSON Output
- **GIVEN** a Metacard with geometry attribute
- **WHEN** GeoJSON transformation is requested
- **THEN** geometry MUST be in GeoJSON format with properties

---

### Requirement: Query Response Transformation
The system MUST convert QueryResponse objects to various output formats.

#### Scenario: Batch Transformation
- **GIVEN** a QueryResponse with multiple Metacards
- **WHEN** transformation is requested
- **THEN** all Metacards MUST be transformed and combined

#### Scenario: CSV Export
- **GIVEN** a QueryResponse
- **WHEN** CSV transformation is requested
- **THEN** Metacards MUST be serialized with configurable columns

#### Scenario: Atom Feed
- **GIVEN** a QueryResponse
- **WHEN** Atom transformation is requested
- **THEN** results MUST be formatted as Atom/RSS feed
