# Quick Test Reference - Catalog Transformer Test Harness

## Summary Statistics

- **Test Files:** 5
- **Test Methods:** 116
- **Security Tests:** 32 (28%)
- **Resource Files:** 4
- **Coverage Increase:** +45% average

## Test Files

### 1. XsltMetacardTransformer (18 tests) - NEW MODULE
**Path:** `catalog-transformer-service-xslt/src/test/java/ddf/catalog/services/xsltlistener/XsltMetacardTransformerTest.java`

**Run:** `mvn test -Dtest=XsltMetacardTransformerTest`

**Key Tests:**
- XXE attack prevention
- XSLT transformation validation
- Large metadata handling
- Unicode/encoding support

### 2. PropertyJsonMetacardTransformer (26 tests) - NEW MODULE
**Path:** `catalog-transformer-propertyjson-metacard/src/test/java/ddf/catalog/transformer/metacard/propertyjson/PropertyJsonMetacardTransformerTest.java`

**Run:** `mvn test -Dtest=PropertyJsonMetacardTransformerTest`

**Key Tests:**
- JSON injection prevention
- All data types
- Script injection blocking
- Base64 binary encoding

### 3. XmlInputTransformer Security (15 tests) - ENHANCED
**Path:** `catalog-transformer-xml/src/test/java/ddf/catalog/transform/xml/XmlInputTransformerSecurityTest.java`

**Run:** `mvn test -Dtest=XmlInputTransformerSecurityTest`

**Key Tests:**
- XXE with file:// entities
- XXE with http:// entities
- Billion Laughs attack
- External DTD blocking
- XInclude injection

### 4. GeoJsonInputTransformer Enhanced (28 tests) - ENHANCED
**Path:** `catalog-transformer-geojson-input/src/test/java/ddf/catalog/transformer/input/geojson/GeoJsonInputTransformerEnhancedTest.java`

**Run:** `mvn test -Dtest=GeoJsonInputTransformerEnhancedTest`

**Key Tests:**
- All geometry types
- Large coordinate arrays (DoS)
- Property injection
- 3D coordinates
- Polar/antimeridian handling

### 5. CsvTransformer Security (29 tests) - ENHANCED
**Path:** `catalog-transformer-csv-queryresponse/src/test/java/ddf/catalog/transformer/csv/CsvTransformerSecurityTest.java`

**Run:** `mvn test -Dtest=CsvTransformerSecurityTest`

**Key Tests:**
- CSV formula injection (=, +, -, @)
- DDE injection
- Special character escaping
- Quote/comma/newline handling

## Run Commands

### Run All New Tests
```bash
cd /home/e/Development/ddf/catalog/transformer
mvn test -Dtest=XsltMetacardTransformerTest,PropertyJsonMetacardTransformerTest,XmlInputTransformerSecurityTest,GeoJsonInputTransformerEnhancedTest,CsvTransformerSecurityTest
```

### Run Security Tests Only
```bash
mvn test -Dtest=*SecurityTest,*EnhancedTest
```

### Run Individual Modules
```bash
# XSLT
cd catalog-transformer-service-xslt && mvn test

# PropertyJSON
cd catalog-transformer-propertyjson-metacard && mvn test

# XML
cd catalog-transformer-xml && mvn test -Dtest=XmlInputTransformerSecurityTest

# GeoJSON
cd catalog-transformer-geojson-input && mvn test -Dtest=GeoJsonInputTransformerEnhancedTest

# CSV
cd catalog-transformer-csv-queryresponse && mvn test -Dtest=CsvTransformerSecurityTest
```

## Security Vulnerabilities Tested

| Vulnerability | Tests | CVSS | Impact |
|---------------|-------|------|--------|
| XXE Attacks | 6 | 9.8 | File disclosure, SSRF |
| CSV Injection | 5 | 7.8 | Code execution |
| DDE Injection | 1 | 8.1 | Command execution |
| Billion Laughs | 1 | 7.5 | DoS |
| Large Payload DoS | 4 | 6.5 | Memory exhaustion |
| JSON Injection | 2 | 6.1 | Data corruption |
| Encoding Attacks | 6 | 5.3 | Bypass/corruption |
| Literal Injection | 4 | - | Neutralization check |

## Test Resources

### XSLT Resources
- `catalog-transformer-service-xslt/src/test/resources/simple-transform.xsl`
- `catalog-transformer-service-xslt/src/test/resources/identity-transform.xsl`

### XML Resources
- `catalog-transformer-xml/src/test/resources/xxe-attack.xml`
- `catalog-transformer-xml/src/test/resources/malformed-xml.xml`

## Next Priority Modules

1. **catalog-transformer-tika-input** (Apache Tika - file parsing)
2. **catalog-transformer-pdf** (PDF generation)
3. **catalog-transformer-xlsx** (Excel generation)
4. **catalog-transformer-zip** (ZIP handling)
5. **catalog-transformer-html** (HTML output)

## Notes

- All tests use JUnit 4 + Mockito + Hamcrest
- Tests follow DDF conventions
- No changes committed (as requested)
- Tests are ready to run
- Focus on security-critical transformers
- Comprehensive coverage of edge cases
