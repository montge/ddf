# Coverage Expansion Sprint 2

**Status:** Proposed
**Created:** 2025-12-27
**Author:** AI Agent

## Summary

Expand test coverage for low-coverage modules identified in the DDF 2.29 modernization effort. This sprint focuses on modules with existing tests that are below the 80% target threshold.

## Rationale

The DDF 2.29 security modernization (Phase 1.3) successfully brought all security modules to 80%+ coverage. However, several core modules still have coverage well below target:

| Module | Current Coverage | Target | Gap |
|--------|-----------------|--------|-----|
| catalog-spatial-csw-common | 23.31% | 80% | 56.69% |
| catalog-core-commons | 27.89% | 80% | 52.11% |
| platform-security-core-api | 35.01% | 80% | 44.99% |
| security-rest-cxfwrapper | 40.31% | 80% | 39.69% |

Note: `catalog-core-api` (7.58%) is excluded as it's primarily interfaces (147/184 files) which cannot be directly tested.

## Goals

1. **Increase coverage** for the 4 priority modules to 80%+ line coverage
2. **Maintain test quality** following established patterns (AAA, Mockito, Hamcrest)
3. **Enable safe refactoring** for upcoming Jakarta EE migration
4. **Document test patterns** for each module to guide future contributors

## Scope

### In Scope
- Unit tests for concrete classes in target modules
- Integration points with mocked dependencies
- Edge cases and error handling paths
- Security-sensitive code paths (priority)

### Out of Scope
- Integration tests requiring OSGi container (Pax Exam)
- UI/E2E tests (covered in Phase 6)
- Performance tests
- Interface-only modules

## Approach

### Priority Order
1. **security-rest-cxfwrapper** (40.31%) - Closest to target, security-critical
2. **platform-security-core-api** (35.01%) - Security foundation, many concrete utility classes
3. **catalog-core-commons** (27.89%) - Core utilities used throughout codebase
4. **catalog-spatial-csw-common** (23.31%) - CSW protocol implementation

### Test Strategy
- Analyze each module's production code structure
- Identify untested concrete classes
- Prioritize by: (1) security impact, (2) usage frequency, (3) complexity
- Write focused unit tests with clear assertions
- Run `mvn test -Dtest=*Test` to verify
- Check JaCoCo reports for coverage gaps

## Success Criteria

- [ ] All 4 target modules reach 80%+ line coverage
- [ ] All new tests pass in CI
- [ ] No regressions in existing tests
- [ ] Test code follows DDF conventions (Mockito, Hamcrest, AAA pattern)

## Dependencies

- JaCoCo coverage reports (already configured)
- Mockito 5.x for mocking
- Hamcrest for assertions
- JUnit 5 test framework

## Risks

| Risk | Mitigation |
|------|------------|
| Complex mocking requirements | Use `@ExtendWith(MockitoExtension.class)` and focused test design |
| OSGi-specific code hard to test | Mock Blueprint services, test logic in isolation |
| Time-consuming for large gaps | Prioritize security-critical paths first |

## Related Work

- Previous: `ddf-2.29-security-modernization` (Phase 1.3 security test coverage)
- Spec: `openspec/specs/coverage/spec.md`
- Blocked: Jakarta EE migration waiting on CXF 4.x/cxf-karaf
