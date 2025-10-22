# Test Coverage Improvement - Quick Start Guide

**IMMEDIATE ACTIONS REQUIRED BEFORE STARTING**

---

## Step 1: Fix Test Infrastructure (BLOCKING - 4-6 hours)

### Task 1.1: Upgrade gmavenplus-plugin (1-2 hours)

**File:** `/home/e/Development/ddf/pom.xml`

**Action:**
```bash
# 1. Backup current pom.xml
cp /home/e/Development/ddf/pom.xml /home/e/Development/ddf/pom.xml.backup-$(date +%Y%m%d)

# 2. Find current version
grep -n "gmavenplus.version" /home/e/Development/ddf/pom.xml
```

**Expected output:**
```
<gmavenplus.version>1.12.0</gmavenplus.version>
```

**Change to:**
```xml
<gmavenplus.version>3.0.2</gmavenplus.version>
```

**Verification:**
```bash
cd /home/e/Development/ddf/platform/security/secure-boot
mvn clean test

# Expected: Tests should compile and run without "Unsupported class file major version 65" error
```

**If successful:**
```bash
# Commit the fix
cd /home/e/Development/ddf
git add pom.xml
git commit -m "Upgrade gmavenplus-plugin to 3.0.2 to support Java 21

Fixes Groovy compilation error in secure-boot module.
Error was: 'Unsupported class file major version 65'

Version 3.0.2 (June 2023) supports Java 21 while maintaining
backward compatibility with Java 11 target.

Related to: Test Coverage Improvement Initiative
"
```

---

### Task 1.2: Fix resource-bundle-locator Tests (2-4 hours)

**Action:**
```bash
cd /home/e/Development/ddf/platform/resource-bundle-locator

# 1. Review test failures
cat target/surefire-reports/*.txt

# 2. Identify root cause
mvn clean test -X  # Debug mode for detailed output

# 3. Fix test logic (specifics depend on failure type)
# Common issues:
#   - Environmental configuration missing
#   - Expected values changed
#   - Resource files not loaded correctly
#   - Locale-specific issues

# 4. Verify fix
mvn clean test

# Expected: All tests pass
```

**Verification:**
```bash
# Test should show something like:
# Tests run: X, Failures: 0, Errors: 0, Skipped: 0

# Check coverage improves from 2.86%
mvn clean test jacoco:report
awk -F, 'NR>1 {im+=$4; ic+=$5} END {
  printf "Coverage: %.2f%%\n", (ic/(im+ic))*100
}' target/site/jacoco/jacoco.csv
```

---

## Step 2: Generate Complete Coverage Baseline (30 minutes)

**Action:**
```bash
cd /home/e/Development/ddf

# Run full test suite with coverage
mvn clean test jacoco:report -T 1C

# This will take 10-15 minutes
# -T 1C uses 1 thread per CPU core for parallel execution
```

**Extract Metrics:**
```bash
# Overall project coverage
find . -name jacoco.csv -path "*/target/site/jacoco/*" | while read csv; do
  awk -F, 'NR>1 {im+=$4; ic+=$5; bm+=$6; bc+=$7}' "$csv"
done | awk '{tim+=$1; tic+=$2; tbm+=$3; tbc+=$4} END {
  printf "Instruction Coverage: %.2f%% (%d/%d)\n", (tic/(tim+tic))*100, tic, tim+tic;
  printf "Branch Coverage: %.2f%% (%d/%d)\n", (tbc/(tbm+tbc))*100, tbc, tbm+tbc
}'

# Modules below 80%
find . -name jacoco.csv -path "*/target/site/jacoco/*" | while read csv; do
  module=$(echo "$csv" | sed 's|./||' | sed 's|/target.*||')
  coverage=$(awk -F, 'NR>1 {im+=$4; ic+=$5} END {
    if(im+ic>0) printf "%.2f", (ic/(im+ic))*100; else print "0"
  }' "$csv")

  # Only show modules below 80%
  if [ $(echo "$coverage < 80" | bc -l) -eq 1 ]; then
    printf "%-60s %6.2f%%\n" "$module" "$coverage"
  fi
done | sort -t' ' -k2 -n

# Count modules
echo ""
echo "Total modules tested: $(find . -name jacoco.csv -path "*/target/site/jacoco/*" | wc -l)"
```

**Save Baseline:**
```bash
# Create baseline report
cat > /home/e/Development/ddf/TEST-COVERAGE-BASELINE-$(date +%Y%m%d).md << 'BASELINE'
# Test Coverage Baseline

**Date:** $(date)
**Git Commit:** $(git rev-parse HEAD)

## Overall Metrics
- Instruction Coverage: XX.XX%
- Branch Coverage: XX.XX%
- Modules Tested: XXX
- Modules Below 80%: XXX

## Modules Below 80%
[Insert list from above command]

## Modules with Excellent Coverage (>=95%)
[Insert modules with high coverage]

BASELINE
```

---

## Step 3: Configure CI/CD Coverage Gates (1 hour)

**Create Coverage Check Script:**
```bash
cat > /home/e/Development/ddf/scripts/check-coverage.sh << 'SCRIPT'
#!/bin/bash
# Coverage enforcement script

OVERALL_TARGET=95
MODULE_TARGET=80
COVERAGE_CSV="target/site/jacoco/jacoco.csv"

# Check overall coverage
if [ ! -f "$COVERAGE_CSV" ]; then
  echo "ERROR: Coverage report not found. Run: mvn test jacoco:report"
  exit 1
fi

overall=$(awk -F, 'NR>1 {im+=$4; ic+=$5} END {
  if(im+ic>0) printf "%.2f", (ic/(im+ic))*100; else print "0"
}' "$COVERAGE_CSV")

echo "Overall Coverage: $overall%"

if (( $(echo "$overall < $OVERALL_TARGET" | bc -l) )); then
  echo "WARNING: Overall coverage $overall% is below target $OVERALL_TARGET%"
  # Don't fail yet - we're working toward target
fi

# Check per-module coverage
echo ""
echo "Checking module coverage (target: $MODULE_TARGET%)..."
failures=0

find . -name jacoco.csv -path "*/target/site/jacoco/*" | while read csv; do
  module=$(echo "$csv" | sed 's|./||' | sed 's|/target.*||')
  coverage=$(awk -F, 'NR>1 {im+=$4; ic+=$5} END {
    if(im+ic>0) printf "%.2f", (ic/(im+ic))*100; else print "0"
  }' "$csv")

  if (( $(echo "$coverage < $MODULE_TARGET" | bc -l) )); then
    echo "  WARNING: $module: $coverage% < $MODULE_TARGET%"
    ((failures++))
  fi
done

echo ""
echo "Modules below target: $failures"
echo "Coverage check complete."
exit 0  # Don't fail builds yet during improvement phase
SCRIPT

chmod +x /home/e/Development/ddf/scripts/check-coverage.sh
```

**Test the script:**
```bash
cd /home/e/Development/ddf
./scripts/check-coverage.sh
```

---

## Step 4: Set Up Weekly Tracking (30 minutes)

**Create tracking spreadsheet or document:**
```bash
cat > /home/e/Development/ddf/TEST-COVERAGE-TRACKING.md << 'TRACKING'
# Test Coverage Improvement Tracking

## Week 0 - Baseline ($(date +%Y-%m-%d))

| Metric | Value | Target | Gap |
|--------|-------|--------|-----|
| Overall Instruction | XX.XX% | 95% | -XX.XX% |
| Overall Branch | XX.XX% | 80% | -XX.XX% |
| Modules ≥80% | XX/457 | 457 | -XXX |

### Modules Completed This Week
- None (baseline week)

### Modules In Progress
- None yet

### Next Week Plan
- Fix infrastructure blockers
- Begin P0 security modules

---

## Week 1 - Infrastructure & First P0 Module (YYYY-MM-DD)

| Metric | Value | Last Week | Change | Target | Status |
|--------|-------|-----------|--------|--------|--------|
| Overall Instruction | XX.XX% | XX.XX% | +/-X.XX% | 95% | 🔴/🟡/🟢 |
| Overall Branch | XX.XX% | XX.XX% | +/-X.XX% | 80% | 🔴/🟡/🟢 |
| Modules ≥80% | XX/457 | XX/457 | +/-XX | 457 | 🔴/🟡/🟢 |

### Completed
- [ ] gmavenplus-plugin upgraded
- [ ] resource-bundle-locator fixed
- [ ] Baseline generated
- [ ] First P0 module: security-core-impl

### In Progress
- Module X: XX% → target 80%

### Next Week
- Continue P0 security modules

---

[Template for subsequent weeks...]

TRACKING
```

---

## Step 5: Review Full Plan (1 hour)

**Read these documents in order:**

1. **TEST-COVERAGE-SUMMARY.md** (10 min)
   - Quick overview of the plan
   - Top 10 priority modules
   - Investment breakdown

2. **TEST-COVERAGE-ROADMAP.md** (15 min)
   - Visual roadmap
   - Week-by-week breakdown
   - Success criteria

3. **TEST-COVERAGE-IMPROVEMENT-PLAN.md** (30 min)
   - Full detailed plan
   - Testing patterns and best practices
   - Module-by-module analysis

4. **TEST-INFRASTRUCTURE-STATUS.md** (5 min)
   - Current status
   - Known blockers

---

## Verification Checklist

Before starting Phase 1 (Week 1), verify:

- [ ] **Infrastructure Fixed**
  - [ ] Groovy plugin upgraded to 3.0.2
  - [ ] secure-boot tests compile and run
  - [ ] resource-bundle-locator tests pass
  - [ ] All tests can be run successfully

- [ ] **Baseline Established**
  - [ ] Complete coverage baseline generated
  - [ ] Baseline documented
  - [ ] Modules below 80% identified
  - [ ] Coverage metrics tracked

- [ ] **Tooling Ready**
  - [ ] Coverage check script working
  - [ ] JaCoCo reports generating correctly
  - [ ] CI/CD integration planned
  - [ ] Tracking document created

- [ ] **Team Aligned**
  - [ ] Plan reviewed and approved
  - [ ] Resources allocated
  - [ ] Priorities understood
  - [ ] Test patterns documented

---

## Quick Commands Reference

**Run tests for specific module:**
```bash
cd [module-path]
mvn clean test
```

**Run tests with coverage:**
```bash
cd [module-path]
mvn clean test jacoco:report
```

**View coverage report:**
```bash
# Command line
awk -F, 'NR>1 {im+=$4; ic+=$5} END {
  printf "Coverage: %.2f%%\n", (ic/(im+ic))*100
}' target/site/jacoco/jacoco.csv

# Or open in browser
xdg-open target/site/jacoco/index.html
```

**Run full project tests:**
```bash
cd /home/e/Development/ddf
mvn clean test -T 1C  # Parallel execution
```

**Check coverage for all modules:**
```bash
./scripts/check-coverage.sh
```

---

## First Week Schedule (After Infrastructure Fixed)

### Monday
- Sprint planning
- Review P0 module: security-core-impl
- Setup test infrastructure for module

### Tuesday-Thursday
- Write tests for security-core-impl
- Target: 45% → 80% coverage
- Estimated: 25-35 tests
- Daily: Run coverage to track progress

### Friday
- Code review
- Address feedback
- Merge if coverage target met
- Update tracking document
- Weekly retrospective

---

## Getting Help

**Reference High-Coverage Modules:**
- `/home/e/Development/ddf/catalog/validator/catalog-validator-wkt/` (100%)
- `/home/e/Development/ddf/libs/checksum/` (100%)
- `/home/e/Development/ddf/libs/klv/` (95.22%)

**Testing Patterns:**
- See TEST-COVERAGE-IMPROVEMENT-PLAN.md sections:
  - "Coverage Strategy & Patterns"
  - "Testing Best Practices for DDF"

**Questions:**
- Review TEST-COVERAGE-SUMMARY.md "Questions & Answers" section

---

## Success Criteria

**Week 1 Success:**
- ✓ Infrastructure stable (tests run cleanly)
- ✓ Complete baseline documented
- ✓ First P0 module ≥80% coverage
- ✓ Test patterns established

**Week 4 Success:**
- ✓ All P0 modules ≥80%
- ✓ Overall coverage ≥70%
- ✓ Security & core catalog tested

**Week 16 Success:**
- ✓ All modules ≥80%
- ✓ Overall coverage ≥95%
- ✓ Maintenance plan documented

---

## Next Steps

1. **Today:** Fix infrastructure blockers (Tasks 1.1, 1.2)
2. **Tomorrow:** Generate baseline (Task 2)
3. **This Week:** Configure tooling (Tasks 3, 4)
4. **Next Week:** Begin Phase 1 test development

---

**Ready to start? Fix the infrastructure blockers first!**

```bash
# Start here:
cd /home/e/Development/ddf
grep -n "gmavenplus.version" pom.xml
# Then follow Task 1.1 above
```
