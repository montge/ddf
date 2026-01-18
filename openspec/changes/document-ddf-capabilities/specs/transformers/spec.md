## ADDED Requirements

### Requirement: Input Transformer Registry
The system MUST discover and register InputTransformers for file-to-metacard conversion via OSGi service registry.

#### Scenario: Transformer Registration
- **GIVEN** an InputTransformer registered as OSGi service
- **WHEN** the service has `id` and `mime-type` properties
- **THEN** it MUST be discoverable via MimeTypeToTransformerMapper

#### Scenario: MIME Type Matching
- **GIVEN** a file with MIME type "application/json"
- **WHEN** transformation is requested
- **THEN** transformers with matching `mime-type` property MUST be candidates
- **AND** base type matching (e.g., "application/*") MUST be supported

#### Scenario: ID-Based Selection
- **GIVEN** a request specifying transformer ID "geojson"
- **WHEN** transformers are queried
- **THEN** only transformers with matching `id` property MUST be returned

#### Scenario: Service Ranking
- **GIVEN** multiple transformers matching the same MIME type
- **WHEN** transformers are returned
- **THEN** they MUST be ordered by OSGi service.ranking (higher first)

---

### Requirement: InputTransformer Interface
The system MUST support InputTransformer interface for converting input streams to Metacards.

#### Scenario: Stream Transformation
- **GIVEN** an InputTransformer implementation
- **WHEN** transform(InputStream input) is called
- **THEN** a Metacard MUST be created with extracted attributes

#### Scenario: ID-Aware Transformation
- **GIVEN** an InputTransformer implementation
- **WHEN** transform(InputStream input, String id) is called
- **THEN** a Metacard MUST be created with the specified ID

#### Scenario: Transformation Error
- **GIVEN** an invalid or unsupported input stream
- **WHEN** transformation fails
- **THEN** CatalogTransformerException MUST be thrown

---

### Requirement: MetacardTransformer Interface
The system MUST support MetacardTransformer interface for converting Metacards to output formats.

#### Scenario: Format Transformation
- **GIVEN** a MetacardTransformer (e.g., GeoJsonMetacardTransformer)
- **WHEN** transform(Metacard, Map<String, Serializable>) is called
- **THEN** BinaryContent MUST be returned with formatted data

#### Scenario: Arguments Processing
- **GIVEN** transform arguments map with format options
- **WHEN** transformation is performed
- **THEN** arguments MUST influence output format (e.g., pretty-print, fields)

---

### Requirement: QueryResponseTransformer Interface
The system MUST support QueryResponseTransformer interface for converting query results to output formats.

#### Scenario: Batch Transformation
- **GIVEN** a QueryResponseTransformer (e.g., GeoJsonQueryResponseTransformer)
- **WHEN** transform(SourceResponse, Map) is called
- **THEN** all Metacards in the response MUST be transformed and combined

#### Scenario: Result Metadata
- **GIVEN** a query response with relevance scores and distances
- **WHEN** transformation is performed
- **THEN** result metadata MUST be included in output

---

### Requirement: GeoJSON Transformers
The system MUST provide GeoJSON transformers for geospatial data exchange.

#### Scenario: GeoJSON Input
- **GIVEN** a GeoJSON Feature input stream
- **WHEN** GeoJsonInputTransformer processes it
- **THEN** a Metacard MUST be created with geometry in WKT format
- **AND** Feature properties MUST map to Metacard attributes

#### Scenario: GeoJSON Metacard Output
- **GIVEN** a Metacard with location geometry
- **WHEN** GeoJsonMetacardTransformer transforms it
- **THEN** output MUST be valid GeoJSON Feature
- **AND** geometry MUST be in GeoJSON coordinates format
- **AND** attributes MUST be in properties object

#### Scenario: GeoJSON Query Response
- **GIVEN** a QueryResponse with multiple results
- **WHEN** GeoJsonQueryResponseTransformer transforms it
- **THEN** output MUST be GeoJSON FeatureCollection
- **AND** each result MUST be a Feature with scores in properties

---

### Requirement: XML Transformers
The system MUST provide XML transformers for metadata exchange.

#### Scenario: XML Input
- **GIVEN** an XML input stream with metacard schema
- **WHEN** XmlInputTransformer processes it
- **THEN** a Metacard MUST be created via Parser unmarshalling

#### Scenario: XML Output
- **GIVEN** a Metacard
- **WHEN** XmlMetacardTransformer transforms it
- **THEN** output MUST be valid XML via MetacardMarshaller
- **AND** MIME type MUST be "text/xml"

---

### Requirement: Atom Feed Transformer
The system MUST provide Atom/RSS transformer for web syndication.

#### Scenario: Atom Query Response
- **GIVEN** a QueryResponse
- **WHEN** AtomTransformer transforms it
- **THEN** output MUST be Atom feed (application/atom+xml)
- **AND** each result MUST be an Atom entry with GeoRSS extensions

---

### Requirement: CSV Transformers
The system MUST provide CSV transformers for tabular data export.

#### Scenario: CSV Query Response
- **GIVEN** a QueryResponse with configurable column list
- **WHEN** CsvQueryResponseTransformer transforms it
- **THEN** output MUST be CSV with header row
- **AND** only specified columns MUST be included

#### Scenario: CSV Metacard Output
- **GIVEN** a Metacard
- **WHEN** CsvMetacardTransformer transforms it
- **THEN** output MUST be single-row CSV with all attributes

---

### Requirement: Document Input Transformers
The system MUST provide InputTransformers for common document formats using Apache Tika.

#### Scenario: PDF Input
- **GIVEN** a PDF file input stream
- **WHEN** PdfInputTransformer processes it
- **THEN** document metadata MUST be extracted to Metacard attributes

#### Scenario: Office Document Input
- **GIVEN** a PowerPoint/Word/Excel file
- **WHEN** the appropriate InputTransformer processes it
- **THEN** document metadata and text MUST be extracted

---

### Requirement: Transformer Discovery Service
The system MUST provide MimeTypeToTransformerMapper for transformer lookup.

#### Scenario: Find by MIME Type
- **GIVEN** MimeTypeToTransformerMapper service
- **WHEN** findMatches(Class<T>, MimeType) is called
- **THEN** all transformers supporting that MIME type MUST be returned

#### Scenario: Filter by ID
- **GIVEN** a MimeType with id parameter
- **WHEN** findMatches is called
- **THEN** only transformers with matching id MUST be returned

---

## Design Notes

### Transformer Interfaces (catalog-core-api)
```java
// File → Metacard
public interface InputTransformer {
  Metacard transform(InputStream input) throws CatalogTransformerException;
  Metacard transform(InputStream input, String id) throws CatalogTransformerException;
}

// Metacard → Format
public interface MetacardTransformer {
  BinaryContent transform(Metacard metacard, Map<String, Serializable> arguments);
}

// QueryResponse → Format
public interface QueryResponseTransformer {
  BinaryContent transform(SourceResponse upstreamResponse, Map<String, Serializable> arguments);
}
```

### Service Properties
| Property | Type | Purpose |
|----------|------|---------|
| `id` | String | Unique transformer identifier |
| `mime-type` | String/List | MIME types supported |
| `displayName` | String | Human-readable name |
| `service.ranking` | Integer | Priority (higher = first) |

### Key Transformer Implementations
| Module | ID | MIME Type | Type |
|--------|-----|-----------|------|
| catalog-transformer-geojson-input | geojson | application/json | Input |
| catalog-transformer-geojson-metacard | geojson | application/json | Metacard |
| catalog-transformer-queryresponse-geojson | geojson | application/json | QueryResponse |
| catalog-transformer-xml | xml | text/xml | Input/Metacard |
| catalog-transformer-service-atom | atom | application/atom+xml | QueryResponse |
| catalog-transformer-csv-queryresponse | csv | text/csv | QueryResponse |
| catalog-transformer-html | html | text/html | Metacard/QueryResponse |
| catalog-transformer-pdf | pdf | application/pdf | Input |

### Blueprint Registration Pattern
```xml
<bean id="transformer" class="...GeoJsonInputTransformer"/>
<service ref="transformer" interface="ddf.catalog.transform.InputTransformer">
  <service-properties>
    <entry key="id" value="geojson"/>
    <entry key="mime-type">
      <list><value>application/json</value></list>
    </entry>
  </service-properties>
</service>
```
