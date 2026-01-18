## ADDED Requirements

### Requirement: WFS Federation Support
The system MUST support querying Web Feature Service (WFS) endpoints as federated sources.

#### Scenario: WFS 1.1.0 Source
- **GIVEN** a WFS 1.1.0 endpoint configured as FederatedSource
- **WHEN** a spatial query is executed
- **THEN** the query MUST be translated to OGC Filter 1.1.0 XML
- **AND** geometry MUST be encoded in GML 3.1.1

#### Scenario: WFS 2.0.0 Source
- **GIVEN** a WFS 2.0.0 endpoint configured as FederatedSource
- **WHEN** a spatial query is executed
- **THEN** the query MUST be translated to OGC Filter 2.0.0 (FES) XML
- **AND** geometry MUST be encoded in GML 3.2.1

#### Scenario: Property Mapping
- **GIVEN** a WFS source with MetacardMapper configured
- **WHEN** sorting or filtering by Metacard attribute
- **THEN** attribute names MUST be mapped to WFS feature property names

#### Scenario: SSL/TLS Authentication
- **GIVEN** a WFS source with X.509 certificate configured
- **WHEN** connecting to the endpoint
- **THEN** certificate-based authentication MUST be used

---

### Requirement: CSW Federation Support
The system MUST support querying Catalog Service for the Web (CSW) endpoints.

#### Scenario: CSW 2.0.2 GetRecords
- **GIVEN** a CSW 2.0.2 endpoint configured as source
- **WHEN** a catalog query is executed
- **THEN** the query MUST be translated to CSW GetRecords request
- **AND** filter MUST use OGC Filter 1.1.0 encoding

#### Scenario: Dublin Core Response
- **GIVEN** a CSW GetRecords response with Dublin Core records
- **WHEN** results are processed
- **THEN** Dublin Core elements MUST be mapped to Metacard attributes

#### Scenario: OAuth Authentication
- **GIVEN** a CSW source with OAuth 2.0 configured
- **WHEN** connecting to the endpoint
- **THEN** OAuth token MUST be obtained and used for authentication

---

### Requirement: Spatial Query Predicates
The system MUST support OGC spatial predicates in catalog queries.

#### Scenario: INTERSECTS Query
- **GIVEN** a query with INTERSECTS(location, WKT_POLYGON)
- **WHEN** the query is executed against sources
- **THEN** Metacards with geometry intersecting the polygon MUST be returned

#### Scenario: WITHIN Query
- **GIVEN** a query with WITHIN(location, WKT_POLYGON)
- **WHEN** the query is executed
- **THEN** only Metacards with geometry fully inside the polygon MUST be returned

#### Scenario: DWITHIN Query (Distance Within)
- **GIVEN** a query with DWITHIN(location, WKT_POINT, 10, kilometers)
- **WHEN** the query is executed
- **THEN** Metacards within 10 kilometers of the point MUST be returned

#### Scenario: BEYOND Query
- **GIVEN** a query with BEYOND(location, WKT_POINT, 50, miles)
- **WHEN** the query is executed
- **THEN** Metacards beyond 50 miles from the point MUST be returned

#### Scenario: BBOX Query
- **GIVEN** a query with bounding box (minX, minY, maxX, maxY)
- **WHEN** the query is executed
- **THEN** Metacards intersecting the envelope MUST be returned

#### Scenario: CONTAINS Query
- **GIVEN** a query with CONTAINS(location, WKT_POINT)
- **WHEN** the query is executed
- **THEN** Metacards whose geometry contains the point MUST be returned

#### Scenario: DISJOINT Query
- **GIVEN** a query with DISJOINT(location, WKT_POLYGON)
- **WHEN** the query is executed
- **THEN** Metacards NOT intersecting the polygon MUST be returned

---

### Requirement: Filter Adapter Architecture
The system MUST convert OGC filters to provider-specific query formats via FilterAdapter.

#### Scenario: Filter Visitor Pattern
- **GIVEN** an OGC Filter object (e.g., Intersects)
- **WHEN** FilterAdapter.adapt(Filter, FilterDelegate) is called
- **THEN** GeotoolsFilterAdapterImpl MUST visit the filter tree
- **AND** delegate to FilterDelegate for format-specific output

#### Scenario: Solr Filter Conversion
- **GIVEN** a spatial filter for Solr provider
- **WHEN** SolrFilterDelegate processes it
- **THEN** output MUST be Solr query syntax with spatial predicates

#### Scenario: WFS Filter Conversion
- **GIVEN** a spatial filter for WFS source
- **WHEN** WfsFilterDelegate processes it
- **THEN** output MUST be OGC Filter XML (JAXBElement)

#### Scenario: CSW Filter Conversion
- **GIVEN** a spatial filter for CSW source
- **WHEN** CswFilterDelegate processes it
- **THEN** output MUST be OGC Filter 1.1.0 XML

---

### Requirement: Distance Unit Normalization
The system MUST normalize distance units to meters for spatial queries.

#### Scenario: Kilometers to Meters
- **GIVEN** a DWITHIN query with distance in kilometers
- **WHEN** filter is processed
- **THEN** distance MUST be converted to meters

#### Scenario: Miles to Meters
- **GIVEN** a DWITHIN query with distance in statute miles
- **WHEN** filter is processed
- **THEN** distance MUST be converted to meters (1 mile = 1609.344 m)

#### Scenario: Nautical Miles to Meters
- **GIVEN** a DWITHIN query with distance in nautical miles
- **WHEN** filter is processed
- **THEN** distance MUST be converted to meters (1 nm = 1852 m)

---

### Requirement: Spatial Operator Fallbacks
The system MUST implement fallback strategies for unsupported spatial operators.

#### Scenario: INTERSECTS Fallback to BBOX
- **GIVEN** a source that doesn't support INTERSECTS
- **WHEN** an INTERSECTS query is submitted
- **THEN** it MUST fall back to BBOX with geometry bounding box

#### Scenario: DWITHIN Fallback to Buffered INTERSECTS
- **GIVEN** a source that doesn't support DWITHIN
- **WHEN** a DWITHIN query is submitted
- **THEN** geometry MUST be buffered by distance and INTERSECTS used

#### Scenario: CONTAINS Fallback to NOT WITHIN
- **GIVEN** a source that doesn't support CONTAINS
- **WHEN** a CONTAINS query is submitted
- **THEN** it MUST be converted to negated WITHIN with swapped operands

#### Scenario: DISJOINT Fallback to NOT INTERSECTS
- **GIVEN** a source that doesn't support DISJOINT
- **WHEN** a DISJOINT query is submitted
- **THEN** it MUST be converted to NOT INTERSECTS

---

### Requirement: Geometry Format Handling
The system MUST support multiple geometry formats with WKT as canonical format.

#### Scenario: WKT Storage
- **GIVEN** a Metacard with location attribute
- **WHEN** geometry is stored
- **THEN** it MUST be in WKT (Well-Known Text) format

#### Scenario: GML Encoding for WFS
- **GIVEN** a WKT geometry in a WFS query
- **WHEN** filter is serialized
- **THEN** geometry MUST be encoded in GML (3.1.1 or 3.2.1)

#### Scenario: JTS Internal Representation
- **GIVEN** geometry operations are needed
- **WHEN** filter processing occurs
- **THEN** JTS (Java Topology Suite) MUST be used internally

#### Scenario: Coordinate Reference System
- **GIVEN** geometry without explicit CRS
- **WHEN** spatial operations are performed
- **THEN** WGS84 (EPSG:4326) MUST be assumed as default

---

### Requirement: Temporal Query Support
The system MUST support temporal predicates for WFS 2.0 sources.

#### Scenario: DURING Query
- **GIVEN** a WFS 2.0 source with temporal support
- **WHEN** a DURING(timestamp, start, end) query is submitted
- **THEN** the query MUST use FES 2.0 temporal operators

#### Scenario: Relative Temporal Query
- **GIVEN** a query with relative temporal (e.g., last 30 days)
- **WHEN** the filter is processed
- **THEN** relative duration MUST be converted to absolute date range

---

## Design Notes

### Source Classes
| Class | Location | Purpose |
|-------|----------|---------|
| WfsSource (1.1) | catalog/spatial/wfs/1.1.0/.../WfsSource.java | WFS 1.1.0 federation |
| WfsSource (2.0) | catalog/spatial/wfs/2.0.0/.../WfsSource.java | WFS 2.0.0 federation |
| AbstractWfsSource | catalog/spatial/wfs/spatial-wfs-common/... | Common WFS functionality |
| CswSourceImpl | catalog/spatial/csw/spatial-csw-source/... | CSW 2.0.2 source |
| AbstractCswSource | catalog/spatial/csw/spatial-csw-source-common/... | Common CSW functionality |

### Filter Delegates
| Class | Output Format | Used By |
|-------|---------------|---------|
| SolrFilterDelegate | Solr query syntax | SolrCatalogProvider |
| WfsFilterDelegate (1.1) | OGC Filter 1.1.0 XML | WFS 1.1 sources |
| WfsFilterDelegate (2.0) | OGC Filter 2.0.0 XML | WFS 2.0 sources |
| CswFilterDelegate | OGC Filter 1.1.0 XML | CSW sources |

### Supported Spatial Operators
| Operator | Description | Fallback |
|----------|-------------|----------|
| INTERSECTS | Geometries overlap | BBOX |
| WITHIN | Geometry inside boundary | - |
| CONTAINS | Geometry contains query | NOT WITHIN |
| DWITHIN | Within distance | Buffered INTERSECTS |
| BEYOND | Beyond distance | NOT DWITHIN |
| BBOX | Bounding box intersection | - |
| DISJOINT | No intersection | NOT INTERSECTS |
| CROSSES | Line crosses polygon | - |
| TOUCHES | Boundaries touch | - |
| OVERLAPS | Polygons overlap | - |

### Geometry Types
- Point, MultiPoint
- LineString, MultiLineString
- Polygon, MultiPolygon
- GeometryCollection
- Envelope (Bounding Box)

### Key Interfaces
```java
// Filter conversion
public interface FilterAdapter {
  <T> T adapt(Filter filter, FilterDelegate<T> delegate);
}

// Provider-specific filter building
public abstract class FilterDelegate<T> {
  T intersects(String propertyName, String wkt);
  T within(String propertyName, String wkt);
  T dwithin(String propertyName, String wkt, double distance);
  // ... other spatial methods
}
```
