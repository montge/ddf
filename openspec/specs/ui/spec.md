# UI Modernization Specification

## Purpose
Define requirements for modernizing the DDF user interface from legacy jQuery/JSP to a modern framework with proper testing.

## Current State

### Technology Stack (As-Is)
| Component | Version | Status |
|-----------|---------|--------|
| jQuery | 3.7.1 | Current |
| jQuery UI | 1.13.3 | Current |
| Bootstrap | 3.4.1 | **Legacy** (EOL 2019) |
| Handlebars | 4.7.8 | Current |
| Underscore.js | 1.13.7 | Current |
| Playwright | 1.49.1 | ✅ Current |
| TypeScript | N/A | Not used |

### Architecture (As-Is)
- Server-side rendering with JSP
- Client-side enhancement with jQuery
- No module bundler (Webpack, Vite)
- YUI Compressor for minification
- No modern testing framework

### UI Modules
1. **Catalog Search UI** - `/catalog/ui/search-ui/simple/`
2. **Admin UI** - `/platform/admin/ui/`
3. **Admin Modules** - `/platform/admin/modules/`
   - Installer
   - Configuration
   - Metrics

---

## Requirements

### Requirement: Modern UI Framework
The system MUST migrate to a modern JavaScript framework for improved maintainability.

#### Scenario: Framework Selection
- GIVEN the need for a modern UI framework
- WHEN evaluating framework options (React, Angular, Vue.js)
- THEN React MUST be selected as the primary framework due to its large ecosystem and TypeScript support

### Requirement: Bootstrap Upgrade
The system MUST upgrade from Bootstrap 3.4.1 to Bootstrap 5.x.

#### Scenario: Bootstrap CSS Migration
- GIVEN the current Bootstrap 3.4.1 EOL status
- WHEN CSS classes are migrated to Bootstrap 5
- THEN all legacy Bootstrap 3 classes MUST be replaced with Bootstrap 5 equivalents

#### Scenario: Accessibility Compliance
- GIVEN Bootstrap 5 accessibility improvements
- WHEN the upgrade is complete
- THEN all UI components MUST meet WCAG 2.1 AA standards

### Requirement: Modern E2E Testing
The system MUST replace CasperJS with Playwright for E2E testing.

#### Scenario: UI E2E Test Execution
- GIVEN the DDF distribution is running
- WHEN Playwright tests execute
- THEN all critical user workflows MUST pass

### Requirement: TypeScript Adoption
The system MUST use TypeScript for all new UI code.

#### Scenario: New Component Development
- GIVEN new UI components are being developed
- WHEN code is written
- THEN TypeScript MUST be used with strict mode enabled

#### Scenario: Type Safety Enforcement
- GIVEN TypeScript compilation
- WHEN build runs
- THEN all type errors MUST be resolved before deployment

---

## Playwright Testing Infrastructure

### Installation
```bash
npm init playwright@latest
```

### Test Structure
```
/catalog/ui/search-ui/
├── e2e/
│   ├── search.spec.ts
│   ├── results.spec.ts
│   └── fixtures/
├── playwright.config.ts
└── package.json
```

### Key Workflows to Test
1. Search functionality
2. Result display and pagination
3. Metacard detail view
4. Download/export operations
5. Authentication flows (SAML/OIDC)

### CI Integration
```yaml
# .github/workflows/playwright.yml
- name: Run Playwright tests
  run: npx playwright test
```

---

## Migration Strategy

### Phase 1: Testing Infrastructure (Low Risk) - ✅ COMPLETE
- [x] Add Playwright to Search UI (`@playwright/test@^1.49.1`)
- [x] Write E2E tests for existing functionality (8 tests: auth.spec.ts, search.spec.ts)
- [x] Retire CasperJS tests (removed from codebase)
- [x] Add to CI pipeline (`.github/workflows/playwright.yml`)

### Phase 2: Bootstrap 5 Migration (Medium Risk)
- [x] Audit Bootstrap 3 usage (2025-12-22)
  - Scope: Only `catalog/ui/search-ui/simple` uses Bootstrap
  - Platform/admin modules: NO Bootstrap dependencies
  - ~100+ Bootstrap 3-specific classes found
- [ ] Create migration plan
  - Grid: 30+ `span*` → `col-*` replacements
  - Forms: 20+ `input-append/prepend` → `input-group`
  - Nav: `navbar-inverse`, `nav-collapse` → Bootstrap 5 equivalents
  - Icons: `icon-*` → `fa-*` (Font Awesome upgrade)
  - Custom: Replace `partial-affix.js` with CSS `position: sticky`
- [ ] Update CSS framework
- [ ] Fix responsive layouts

### Phase 3: Build Modernization (Medium Risk)
- [ ] Add Node.js/npm to build
- [ ] Integrate Vite or Webpack
- [ ] Add TypeScript compilation
- [ ] Replace YUI Compressor

### Phase 4: Framework Migration (High Risk)
- [ ] Choose framework (React/Angular/Vue)
- [ ] Create component library
- [ ] Incremental page migration
- [ ] Full SPA architecture

---

## Dependencies

### Current Build Tools
- Maven (primary build)
- YUI Compressor 1.5.1 (minification)
- JSLint 1.0.1 (linting)

### Target Build Tools
- Vite (modern bundler)
- ESLint + Prettier (linting/formatting)
- TypeScript compiler
- Playwright (E2E testing)
- Vitest (unit testing)
