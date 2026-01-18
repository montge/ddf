# Change: Document DDF Core Capabilities via Reverse Engineering

## Why
DDF has 455 Maven modules but only 6 OpenSpec specs covering modernization, build, security, and UI. The core capabilities that make DDF a federated metadata catalog - the catalog framework, spatial services, transformers, and OSGi architecture - are undocumented. This creates a barrier for AI-assisted development and makes it difficult to plan changes safely.

## What Changes
- **NEW** `specs/catalog/spec.md` - Catalog framework, plugins, federation, sources
- **NEW** `specs/spatial/spec.md` - WFS, CSW, WMS geospatial services
- **NEW** `specs/transformers/spec.md` - Input, metacard, and query response transformers
- **NEW** `specs/content/spec.md` - Content storage and resource retrieval
- **NEW** `specs/osgi/spec.md` - Blueprint, features, bundle architecture
- **NEW** `specs/admin/spec.md` - Admin UI, configuration management, installer

## Impact
- Affected specs: All new specifications
- Affected code: Read-only analysis, no code changes
- Enables: Better AI assistance for future DDF changes

## Scope

### In Scope
1. Documenting **existing** behavior from code analysis
2. Creating requirements with scenarios that reflect current functionality
3. Identifying extension points and plugin interfaces
4. Mapping module dependencies and architectural patterns

### Out of Scope
1. Proposing new features or changes
2. Modifying any code
3. Fixing bugs or technical debt (that would be separate changes)

## Priority Order

| Priority | Spec | Rationale |
|----------|------|-----------|
| 1 | catalog | Core functionality, most other specs depend on it |
| 2 | osgi | Foundation for all DDF services |
| 3 | transformers | Key extension point, high module count |
| 4 | spatial | Core geospatial capability |
| 5 | content | Content storage/retrieval |
| 6 | admin | UI and configuration |

## Dependencies
- None (documentation only)
- Can proceed in parallel with `ddf-2.29-security-modernization` implementation

## Success Criteria
- [ ] All 6 new specs created with MUST/SHALL requirements
- [ ] Each requirement has at least one scenario
- [ ] Specs validated with `openspec validate --strict`
- [ ] Cross-references between specs are accurate
