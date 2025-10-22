# Test Coverage Improvement Plan - Executive Summary

**Document:** TEST-COVERAGE-IMPROVEMENT-PLAN.md
**Date:** 2025-10-21
**Status:** Ready for Review

---

## Quick Reference

### Current State
- **Overall Coverage:** 63.54% (only 31 of 457 modules measured)
- **Modules Below 80%:** 13 identified + ~191 estimated
- **Critical Gap:** 426 modules unmeasured due to test infrastructure blockers

### Target State
- **Overall Coverage:** 95%
- **Per-Module:** 80% minimum
- **Timeline:** 16 weeks (4 phases)
- **Effort:** 1,500-2,300 hours

### Critical Path

**Week 1:** Fix test infrastructure blockers (BLOCKING)
- Upgrade gmavenplus-plugin to 3.0.2
- Fix resource-bundle-locator tests
- Generate complete baseline

**Weeks 2-4:** Security & Core Catalog (P0)
- platform/security/* modules → 80%
- catalog/core/* modules → 80%
- catalog/solr/* modules → 80%

**Weeks 5-8:** High-Value Modules (P1)
- REST APIs, Federation, Sources → 80%

**Weeks 9-12:** Medium-Priority (P2)
- Transformers, Plugins, Utilities → 80%

**Weeks 13-16:** Completion (P3)
- Configuration, Admin, Final verification → 95% overall

---

## Investment Breakdown

| Priority | Modules | Tests | Hours | Timeline |
|----------|---------|-------|-------|----------|
| P0 Critical | 35-40 | 319-442 | 533-777 | Weeks 1-4 |
| P1 High | 23-25 | 185-260 | 300-435 | Weeks 5-8 |
| P2 Medium | 71-75 | 285-425 | 425-660 | Weeks 9-12 |
| P3 Low | 75-80 | 175-260 | 250-395 | Weeks 13-16 |
| **TOTAL** | **204-220** | **964-1,387** | **1,508-2,267** | **16 weeks** |

---

## Top 10 Priority Modules

1. **platform/security/security-core-impl** (P0)
   - Estimated: 45-55% → 80%
   - Tests: 25-35
   - Hours: 40-60
   - Why: SAML, OAuth, security filters

2. **catalog/core/catalog-core-standardframework** (P0)
   - Estimated: 55-65% → 80%
   - Tests: 35-50
   - Hours: 60-90
   - Why: Main orchestrator, CatalogFrameworkImpl

3. **catalog/core/catalog-core-api-impl** (P0)
   - Estimated: 50-60% → 80%
   - Tests: 45-60
   - Hours: 75-105
   - Why: MetacardImpl, core data model

4. **catalog/solr/catalog-solr-provider** (P0)
   - Estimated: 60-70% → 80%
   - Tests: 20-30
   - Hours: 35-50
   - Why: Primary storage backend

5. **platform/security/filter/* (8 modules)** (P0)
   - Estimated: 40-60% → 80%
   - Tests: 60-80
   - Hours: 100-140
   - Why: Authentication filters, critical security path

6. **libs/gson-support** (P0)
   - Current: 1.19% → 80%
   - Tests: 15-20
   - Hours: 24-32
   - Why: JSON serialization, critical utility

7. **platform/resource-bundle-locator** (P0)
   - Current: 2.86% → 80%
   - Tests: 8-12
   - Hours: 16-24
   - Why: Test failure blocker, i18n support

8. **catalog/rest/catalog-rest-impl** (P1)
   - Estimated: 50-65% → 80%
   - Tests: 30-40
   - Hours: 50-70
   - Why: Public REST API

9. **platform/security/security-oidc-bundle** (P0)
   - Current: 39.97% → 80%
   - Tests: 10-15
   - Hours: 18-26
   - Why: OIDC authentication

10. **catalog/solr/catalog-solr-core** (P0)
    - Estimated: 55-65% → 80%
    - Tests: 25-35
    - Hours: 40-60
    - Why: Solr integration, filter adaptation

---

## Success Patterns from High-Coverage Modules

**100% Coverage Achievers:**
- catalog-validator-wkt
- libs-checksum
- libs-platform-configuration-impl
- platform-io-impl
- platform-parser-api

**Common Patterns:**
1. Clear API boundaries
2. Pure functions (deterministic)
3. Single responsibility
4. Comprehensive edge case testing
5. Proper mock infrastructure

---

## Testing Strategy

### Test Types Distribution
- **Unit Tests:** 80% of effort (mocking, isolated testing)
- **Integration Tests:** 15% of effort (OSGi, Solr)
- **Security Tests:** 5% of effort (authentication, authorization)

### Coverage Patterns
1. **Happy Path:** Basic functionality
2. **Error Handling:** Exceptions, null inputs
3. **Edge Cases:** Boundary values, empty collections
4. **Security:** Authentication, authorization scenarios
5. **Concurrency:** Thread safety (where applicable)

### Test-to-Source Ratio Targets
- Simple utilities: 0.5:1 (1 test for 2 source files)
- Medium services: 1:1 (1 test per source file)
- Complex orchestrators: 1.5:1 (multiple tests per source)

---

## Risk Mitigation

**Top Risks:**
1. ❌ **Test infrastructure failures** → Fix in Week 1 (CRITICAL)
2. ⚠️ **Scope creep** → Strict prioritization, time-boxing
3. ⚠️ **Team capacity** → Flexible timeline (can extend to 20 weeks)
4. ⚠️ **Legacy code complexity** → Refactor for testability
5. ⚠️ **Coverage vs quality** → Code review for all tests

**Green/Yellow/Red Indicators:**

🟢 **Green (On Track):**
- All P0 modules ≥80% by Week 4
- Overall ≥70% by Week 8
- Test execution <15 minutes

🟡 **Yellow (Monitor):**
- P0 modules delayed 1 week
- Overall 65-70% by Week 8
- Some flaky tests (1-3%)

🔴 **Red (Escalate):**
- P0 modules delayed >2 weeks
- Overall <65% by Week 8
- Flaky tests >5%
- Test execution >20 minutes

---

## Next Steps

### Immediate (This Week)
1. Review and approve this plan
2. Fix Groovy compilation error (1-2 hours)
3. Fix resource-bundle-locator tests (2-4 hours)
4. Generate complete coverage baseline (30 minutes)

### Week 1 Deliverables
- [ ] Test infrastructure stable
- [ ] Complete coverage baseline documented
- [ ] First P0 module tests written
- [ ] CI/CD coverage gates configured

### Week 4 Checkpoint
- [ ] All P0 modules ≥80%
- [ ] Overall coverage ≥70%
- [ ] Security & core catalog tested
- [ ] Test patterns documented

### Week 8 Checkpoint
- [ ] All P0 + P1 modules ≥80%
- [ ] Overall coverage ≥80%
- [ ] REST APIs, federation tested

### Week 16 Goal
- [ ] All modules ≥80%
- [ ] Overall coverage ≥95%
- [ ] Maintenance plan documented
- [ ] Team trained

---

## Resources Required

**Team Composition:**
- 3-4 developers dedicated to test development
- 1 architect for complex module guidance
- 1 security expert for security module testing

**Tools:**
- Maven 3.6.3+
- JUnit 4
- Mockito
- Hamcrest
- JaCoCo 0.8.8+
- CI/CD pipeline (GitLab CI or equivalent)

**Time Commitment:**
- **Full-time:** 16 weeks @ 120-160 hours/week
- **Part-time (50%):** 32 weeks
- **Mixed:** ~20 weeks with variable capacity

---

## Key Metrics Dashboard

Track weekly:
```
Overall Coverage:     XX.XX% (→ 95%)
Branch Coverage:      XX.XX% (→ 80%)
Modules ≥80%:         XXX/457 (→ 457)
Tests Added:          X,XXX
Hours Invested:       X,XXX
Test Execution Time:  XX min
Flaky Test Rate:      X.XX%
```

---

## Questions & Answers

**Q: Why 95% overall and not 100%?**
A: Some code is genuinely untestable (generated code, configuration files, packaging). 95% is industry best practice for enterprise software.

**Q: Why 80% per module minimum?**
A: Balances thoroughness with pragmatism. 80% catches most bugs while avoiding diminishing returns of 90-100%.

**Q: Can we accelerate the timeline?**
A: Yes, with more developers. 6 developers could complete in 10-12 weeks. But quality should not be sacrificed for speed.

**Q: What if we can't fix the infrastructure blockers?**
A: We can work around them (skip secure-boot, ignore test failures), but must fix them eventually for complete coverage.

**Q: How will this impact feature development?**
A: Significant impact for 16 weeks. Consider dedicated test team or pause non-critical features. Long-term benefit: fewer bugs, faster development.

---

## Success Stories (To Study)

**High-Coverage Module Examples:**
- `/home/e/Development/ddf/catalog/validator/catalog-validator-wkt/` (100%)
- `/home/e/Development/ddf/libs/checksum/` (100%)
- `/home/e/Development/ddf/libs/klv/` (95.22%)
- `/home/e/Development/ddf/platform/metrics/metrics-servlet-filter/` (96.91%)

Study these for patterns and test structure!

---

**Full Plan:** [TEST-COVERAGE-IMPROVEMENT-PLAN.md](./TEST-COVERAGE-IMPROVEMENT-PLAN.md)
**Status Report:** [TEST-INFRASTRUCTURE-STATUS.md](./TEST-INFRASTRUCTURE-STATUS.md)
**Coverage Data:** [COVERAGE-REPORT.txt](./COVERAGE-REPORT.txt)
