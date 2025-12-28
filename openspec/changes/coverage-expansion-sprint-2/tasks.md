# Coverage Expansion Sprint 2 - Tasks

**Status:** In Progress
**Started:** 2025-12-27
**Target:** 80%+ coverage for 4 modules

---

## Module 1: security-rest-cxfwrapper (56% -> 72.9%)

**Path:** `platform/security/rest/security-rest-cxfwrapper`
**Source Files:** 12 | **Test Files:** 10 (was 6)

### 1.1 Existing Test Review
- [x] 1.1.1 Review SecureCxfClientFactoryTest coverage gaps
- [x] 1.1.2 Review OAuthOutInterceptorTest coverage gaps
- [x] 1.1.3 Review PaosInInterceptorTest and PaosOutInterceptorTest coverage gaps
- [x] 1.1.4 Run JaCoCo report: `mvn test jacoco:report -pl platform/security/rest/security-rest-cxfwrapper`

### 1.2 Missing Test Classes
- [x] 1.2.1 Create BodyWriterTest.java (50% coverage)
- [x] 1.2.2 Create ClientBuilderFactoryImplTest.java (100% coverage)
- [x] 1.2.3 Create ClientBuilderImplTest.java (100% coverage)
- [x] 1.2.4 Create ClientFactoryFactoryImplTest.java (95% coverage)
- [x] 1.2.5 Create SubjectRetrievalInterceptorTest.java (100% for main class)

### 1.3 Validation
- [x] 1.3.1 Run all tests: `mvn test -pl platform/security/rest/security-rest-cxfwrapper`
- [ ] 1.3.2 Verify 80%+ coverage in JaCoCo report (currently 72.9%, need +7.1%)
- [x] 1.3.3 Run `mvn fmt:format` to fix formatting

### 1.4 Remaining Gaps (to reach 80%)
- [ ] 1.4.1 SecureCxfClientFactoryImpl (56.2%) - needs more tests
- [ ] 1.4.2 OAuthSecurityImpl (77.2%) - close to target
- [ ] 1.4.3 PaosInInterceptor (72.5%) - needs edge case tests
- [ ] 1.4.4 BodyWriter (50.4%) - needs writeBody() path tests

---

## Module 2: platform-security-core-api (35.01% -> 99.4%) COMPLETE

**Path:** `platform/security/platform-security-core-api`
**Source Files:** 35 (mostly interfaces) | **Test Files:** 8 (was 6)

### 2.1 Analysis
Module is 90%+ interfaces. Only concrete classes are:
- ThreadContextProperties (99.2% - already covered)
- SecurityConstants (100% - already covered)
- SecurityServiceException (100%)
- SignatureException (100%)
- LogoutSecurityException (100%)
- Request/Response (100% - interface constants)

### 2.2 Tests Added
- [x] 2.2.1 Expanded SecurityServiceExceptionTest.java (all 4 constructors)
- [x] 2.2.2 Expanded LogoutSecurityExceptionTest.java (all 4 constructors)
- [x] 2.2.3 Expanded SignatureExceptionTest.java (all 4 constructors)
- [x] 2.2.4 Created RequestTest.java (interface constants)
- [x] 2.2.5 Created ResponseTest.java (interface constants)

### 2.3 Validation
- [x] 2.3.1 Run all tests: `mvn test -pl platform/security/platform-security-core-api`
- [x] 2.3.2 Coverage: **99.4%** (344/346 instructions)

---

## Module 3: catalog-core-commons (27.89% -> 73.3%)

**Path:** `catalog/core/catalog-core-commons`
**Source Files:** 21 | **Test Files:** 18 (was 16)

### 3.1 Analysis
- [x] 3.1.1 Run JaCoCo report
- [x] 3.1.2 Identify coverage gaps

### 3.2 Tests Added
- [x] 3.2.1 Create FuzzyFunctionFactoryTest.java (100% - was 0%)
- [x] 3.2.2 Create InetAddressUtilTest.java (100% - was 0%)
- [x] 3.2.3 Create CopyFilterDelegateTest.java (100% - was 0%, 766 instructions)

### 3.3 Remaining Gaps (require platform-specific setup)
- [ ] AbstractMetacardGroomerPlugin (0%) - abstract class, needs concrete subclass
- [ ] XSLTUtil (8%) - requires platform security whitelist
- [ ] NamespaceResolver (7.2%) - needs specific namespace setup
- [ ] TemporalFilter (42%) - partially covered
- [ ] Antimeridian (74%) - partially covered
- [ ] FilterToTextDelegate (66.6%) - partially covered
- [ ] XPathHelper (64.8%) - partially covered

### 3.4 Validation
- [x] 3.4.1 All tests pass
- [x] 3.4.2 Coverage improved from 27.89% to **73.3%** (+45.4%)

---

## Module 4: spatial-csw-common (23.31% -> 87%) COMPLETE

**Path:** `catalog/spatial/csw/spatial-csw-common`
**Source Files:** 24 | **Test Files:** 22 (was 20)

### 4.1 Coverage Analysis
- [x] 4.1.1 Run JaCoCo report
- [x] 4.1.2 List uncovered classes and methods
- [x] 4.1.3 Prioritize by complexity and usage

### 4.2 Expand Existing Tests
- [x] 4.2.1 Fixed BoundingBoxReaderTest.java (proper XML navigation simulation)
- [x] 4.2.2 Fixed CswSourceConfigurationTest.java (added encryption mock)
- [x] 4.2.3 Expanded CswExceptionTest.java (all 11 constructors + getters/setters)
- [x] 4.2.4 Expanded GetRecordsRequestTest.java (+30 tests for get202RecordsType)

### 4.3 Create Missing Tests
- [x] 4.3.1 Created ExtendedGeotoolsFunctionFactoryTest.java (was 0%)

### 4.4 Validation
- [x] 4.4.1 Run all tests: `mvn test -pl catalog/spatial/csw/spatial-csw-common`
- [x] 4.4.2 Coverage: **87%** (507 of 3,940 missed)
- [x] 4.4.3 Run `mvn fmt:format`

### 4.5 Coverage Details

| Class | Coverage | Notes |
|-------|----------|-------|
| CswConstants | **100%** | Already covered |
| CswRecordCollection | **100%** | Already covered |
| PropertyIsFuzzyFunction | **100%** | Already covered |
| DescribeRecordRequest | **100%** | Already covered |
| GetCapabilitiesRequest | **100%** | Already covered |
| GetRecordByIdRequest | **100%** | Already covered |
| GmdConstants | **100%** | Already covered |
| PropertyMapperVisitor | **100%** | Already covered |
| CswAxisOrder | **100%** | Already covered |
| CswRequest | **98%** | Already covered |
| ExtendedGeotoolsFunctionFactory | **100%** | NEW - was 0% |
| CswException | **100%** | Expanded - was 18% |
| GetRecordsRequest | **85%** | Expanded - was 30% |
| CswSourceConfiguration | **55%** | Partial |
| BoundingBoxReader | **68%** | Fixed tests |
| CswJAXBElementProvider | 0% | Static initialization only |

---

## Quick Reference

### Commands
```bash
# Run tests for single module
mvn test -pl platform/security/rest/security-rest-cxfwrapper

# Generate JaCoCo coverage report
mvn test jacoco:report -pl <module-path>

# View coverage report
open <module-path>/target/site/jacoco/index.html

# Format code
mvn fmt:format -pl <module-path>

# Run single test class
mvn test -Dtest=BodyWriterTest -pl <module-path>
```

### Test Patterns
```java
// Standard test class structure
@ExtendWith(MockitoExtension.class)
class MyClassTest {
    @Mock
    private Dependency mockDep;

    @InjectMocks
    private MyClass underTest;

    @Test
    void methodName_condition_expectedResult() {
        // Arrange
        when(mockDep.method()).thenReturn(value);

        // Act
        Result result = underTest.method();

        // Assert
        assertThat(result, is(expected));
    }
}
```

---

## Progress Summary

| Module | Start | Current | Target | Status |
|--------|-------|---------|--------|--------|
| security-rest-cxfwrapper | 56% | **72.9%** | 80% | In Progress (+16.9%) |
| platform-security-core-api | 35.01% | **99.4%** | 80% | COMPLETE (+64.4%) |
| catalog-core-commons | 27.89% | **73.3%** | 80% | Improved (+45.4%) |
| spatial-csw-common | 23.31% | **87%** | 80% | COMPLETE (+63.7%) |

## Coverage Details: security-rest-cxfwrapper

| Class | Coverage | Notes |
|-------|----------|-------|
| SubjectRetrievalInterceptor | **100%** | NEW - was 0% |
| SubjectRetrievalInterceptor.EventSecurityEndingInterceptor | **100%** | NEW - was 0% |
| ClientBuilderFactoryImpl | **100%** | NEW - was 0% |
| ClientBuilderImpl | **100%** | NEW - was 0% |
| ClientKeyInfo | **100%** | Already covered |
| ClientFactoryFactoryImpl | **94.8%** | NEW - was 0% |
| OAuthOutInterceptor | **100%** | Already covered |
| PaosOutInterceptor | **94.4%** | Already covered |
| OAuthSecurityImpl | 77.2% | Needs improvement |
| SubjectRetrievalInterceptor.ReceiverTrustDecider | 71.6% | Partial coverage |
| PaosInInterceptor | 72.5% | Needs improvement |
| SecureCxfClientFactoryImpl | 56.2% | Complex class |
| SecureCxfClientFactoryImpl.AliasSelectorKeyManager | 57.3% | Needs tests |
| BodyWriter | 50.4% | Needs writeBody tests |
