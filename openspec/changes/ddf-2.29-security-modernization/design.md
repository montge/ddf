# Design Decisions

## Hazelcast Strategy

### Decision: REMOVED ✅ (2025-12-12)

Hazelcast was completely removed from DDF.

### Analysis Performed
- Hazelcast 5.3.5 was only used for local file-based persistence
- No distributed clustering features were used
- Only 2 Java files imported Hazelcast (`FileSystemPersistenceProvider` in 2 modules)
- A `PersistenceStore` interface already existed as replacement

### Implementation
1. Removed Hazelcast imports from `FileSystemPersistenceProvider` (2 modules)
2. Changed implementation to use `PersistenceStore` interface
3. Removed Hazelcast dependency from 4 pom.xml files:
   - catalog-core-directorymonitor
   - catalog-core-camelcomponent
   - catalog-core-resourcestatusplugin (unused dependency)
   - catalog-core-resourcesizeplugin (unused dependency)

### Impact
- **Vulnerabilities reduced:** 1798 → 1177 (621 fewer)
- **Critical CVEs reduced:** 6 → 4
- **Functionality:** No change - local persistence still works
- **Code removed:** ~30 lines, no behavioral changes

---

## Jakarta Migration Strategy

### Approach: Bytecode Transformation
Use Eclipse Transformer to convert javax.* bytecode to jakarta.* at build time.

**Advantages:**
- No source code changes initially
- Gradual migration possible
- Works with third-party javax.* dependencies

**Process:**
1. Add Eclipse Transformer Maven plugin
2. Configure transformation rules
3. Transform artifacts post-compile
4. Validate in OSGi container
5. Eventually migrate source code

### Module Order
1. `platform/security/*` - Most isolated, good test case
2. `platform/admin/*` - Simple services
3. `catalog/core/*` - Core framework
4. `catalog/rest/*` - JAX-RS heavy
5. `distribution/*` - Final assembly

---

## Spring Migration Strategy

### Spring 5.3 -> 6.0 Changes
- Requires Java 17+ (we have)
- Requires jakarta.* (transformer handles)
- Deprecated API removals
- Security filter chain changes

### Blueprint Compatibility
- Spring beans in Blueprint should continue working
- May need to update bean class references
- Test service injection carefully

---

## CXF Migration Strategy

### CXF 3.6 -> 4.x Changes
- javax.ws.rs -> jakarta.ws.rs annotations
- javax.xml.ws -> jakarta.xml.ws annotations
- Service interface changes

### REST Service Migration
```java
// Before (CXF 3.x)
import javax.ws.rs.GET;
import javax.ws.rs.Path;

// After (CXF 4.x)
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
```

### Testing Strategy
- Test each REST endpoint after migration
- Verify SOAP services still resolve WSDL
- Check security interceptors attach properly

---

## Test Coverage Strategy

### Priority: Security First
Security modules must be tested before any refactoring:
1. Prevents introducing vulnerabilities during migration
2. Ensures auth flows continue working
3. Documents expected behavior

### Test Template
```java
@RunWith(MockitoJUnitRunner.class)
public class SecurityHandlerTest {

    @Mock
    private AuthenticationProvider mockProvider;

    @InjectMocks
    private SecurityHandlerImpl handler;

    @Test
    public void testAuthenticate_ValidCredentials_ReturnsSubject() {
        // Arrange
        Credentials creds = createValidCredentials();
        when(mockProvider.authenticate(any()))
            .thenReturn(createSubject());

        // Act
        Subject result = handler.authenticate(creds);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getPrincipal(), is("testuser"));
    }
}
```

---

## CI Stabilization

### Current Issues
1. Docs module fails (JRuby/Asciidoctor)
2. Solr download flaky
3. Spring feature mismatch

### Resolutions Applied
1. Exclude docs module: `-pl '!distribution/docs'`
2. Add download retries: `retries=3, timeout=120s`
3. Custom Spring 5.3 feature in kernel

### Outstanding
- Consider dedicated docs workflow
- Cache Solr download in CI
- Monitor for new issues

---

## Dependency Version Coordination

### Version Properties (parent pom.xml)
```xml
<spring.version>6.0.x</spring.version>
<cxf.version>4.0.x</cxf.version>
<camel.version>3.22.x</camel.version>
<logback.version>1.4.x</logback.version>
<jakarta.servlet.version>6.0.0</jakarta.servlet.version>
```

### BOM Alignment
All version changes must be coordinated across:
- Parent POM properties
- Karaf features
- Blueprint imports
- Test dependencies
