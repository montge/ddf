## ADDED Requirements

### Requirement: WFS Federation Support
The system MUST support querying Web Feature Service (WFS) endpoints as federated sources.

#### Scenario: WFS 1.0 Source
- **GIVEN** a WFS 1.0.0 endpoint is configured
- **WHEN** a spatial query is executed
- **THEN** the query MUST be translated to WFS GetFeature request

#### Scenario: WFS 1.1 Source
- **GIVEN** a WFS 1.1.0 endpoint is configured
- **WHEN** a spatial query is executed
- **THEN** the query MUST use WFS 1.1 filter encoding

#### Scenario: WFS 2.0 Source
- **GIVEN** a WFS 2.0.0 endpoint is configured
- **WHEN** a spatial query is executed
- **THEN** the query MUST use FES 2.0 filter encoding

---

### Requirement: CSW Federation Support
The system MUST support querying Catalog Service for the Web (CSW) endpoints.

#### Scenario: CSW 2.0.2 Source
- **GIVEN** a CSW 2.0.2 endpoint is configured
- **WHEN** a catalog query is executed
- **THEN** the query MUST be translated to CSW GetRecords request

#### Scenario: CSW Transaction
- **GIVEN** a CSW endpoint supporting transactions
- **WHEN** a create/update/delete is executed
- **THEN** the operation MUST use CSW Transaction request

---

### Requirement: Spatial Query Support
The system MUST support OGC spatial predicates in queries.

#### Scenario: INTERSECTS Query
- **GIVEN** a query with INTERSECTS filter on a WKT geometry
- **WHEN** the query is executed
- **THEN** Metacards with intersecting geometry MUST be returned

#### Scenario: WITHIN Query
- **GIVEN** a query with WITHIN filter
- **WHEN** the query is executed
- **THEN** only Metacards fully within the geometry MUST be returned

#### Scenario: DWITHIN Query
- **GIVEN** a query with DWITHIN filter (distance within)
- **WHEN** the query is executed
- **THEN** Metacards within specified distance MUST be returned

#### Scenario: BBOX Query
- **GIVEN** a query with bounding box coordinates
- **WHEN** the query is executed
- **THEN** Metacards intersecting the bbox MUST be returned

---

### Requirement: Filter Adapter
The system MUST convert OGC filters to provider-specific query formats.

#### Scenario: Solr Filter Conversion
- **GIVEN** an OGC filter with spatial and attribute predicates
- **WHEN** converting for Solr provider
- **THEN** filter MUST be translated to Solr query syntax

#### Scenario: WFS Filter Conversion
- **GIVEN** an OGC filter
- **WHEN** converting for WFS source
- **THEN** filter MUST be translated to OGC Filter Encoding XML

---

### Requirement: Geometry Handling
The system MUST use WKT (Well-Known Text) for geometry representation.

#### Scenario: WKT Storage
- **GIVEN** a Metacard with location attribute
- **WHEN** geometry is stored
- **THEN** it MUST be in WKT format

#### Scenario: GeoJSON Conversion
- **GIVEN** a geometry in WKT format
- **WHEN** GeoJSON output is requested
- **THEN** geometry MUST be converted to GeoJSON coordinates

#### Scenario: Coordinate Reference System
- **GIVEN** geometry data
- **WHEN** no CRS is specified
- **THEN** WGS84 (EPSG:4326) MUST be assumed
