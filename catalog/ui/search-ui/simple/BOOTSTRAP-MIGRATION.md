# Bootstrap 5 Migration Plan

## Overview

This document outlines the migration from Bootstrap 3.4.1 (with Bootstrap 2 patterns) to Bootstrap 5.3 for the DDF Search UI Simple module.

**Scope:** 3 files
- `src/main/resources/SearchPage.jsp` (main search interface)
- `src/main/resources/RecordView.html` (record display template)
- `src/main/resources/SearchHelp.html` (help documentation)

## Current State

### Dependencies
```
lib/bootstrap-3.4.1/css/bootstrap.min.css
lib/bootstrap-3.4.1/js/bootstrap.min.js
lib/font-awesome/css/font-awesome.min.css
lib/bootstrap-extensions/js/partial-affix.js
```

### Bootstrap 2/3 Patterns in Use
The codebase uses legacy Bootstrap 2 patterns that were deprecated in Bootstrap 3 and removed in Bootstrap 4+:

| Pattern | Usage Count | Bootstrap 5 Equivalent |
|---------|-------------|----------------------|
| `row-fluid` | 8 | `row` |
| `span*` (span3, span5, etc.) | 30+ | `col-*` |
| `navbar-inner` | 1 | Removed (use navbar directly) |
| `brand` | 1 | `navbar-brand` |
| `nav-collapse` | 1 | `navbar-collapse` |
| `modal hide` | 2 | `modal fade` |
| `well`, `well-small` | 1 | `card` or custom |
| `nav-list` | 1 | `list-group` or custom |
| `input-prepend/append` | 15+ | `input-group` |
| `add-on` | 20+ | `input-group-text` |
| `btn-mini` | 10+ | `btn-sm` |
| `pull-left/right` | 4 | `float-start/end` or flexbox |
| `form-actions` | 1 | `d-flex gap-2` |

### Data Attributes
| Bootstrap 3 | Bootstrap 5 |
|-------------|-------------|
| `data-toggle` | `data-bs-toggle` |
| `data-dismiss` | `data-bs-dismiss` |
| `data-target` | `data-bs-target` |

### Icon Classes
Currently using Font Awesome 3 (`icon-*` prefix):
- `icon-globe`, `icon-white`, `icon-search`, `icon-time`, `icon-plus`, `icon-exclamation`

Options:
1. Migrate to Font Awesome 6 (`fa-solid fa-*`)
2. Use Bootstrap Icons (`bi bi-*`)

## Migration Steps

### Phase 1: Preparation
- [ ] 1.1 Create feature branch `feature/bootstrap5-migration`
- [ ] 1.2 Set up local testing environment
- [ ] 1.3 Document current functionality with screenshots
- [ ] 1.4 Add Playwright E2E tests for critical user flows

### Phase 2: Update Dependencies
- [ ] 2.1 Replace Bootstrap 3.4.1 with Bootstrap 5.3.x
  ```html
  <!-- Old -->
  <link href="lib/bootstrap-3.4.1/css/bootstrap.min.css" rel="stylesheet">
  <script src="lib/bootstrap-3.4.1/js/bootstrap.min.js"></script>

  <!-- New -->
  <link href="lib/bootstrap-5.3.x/css/bootstrap.min.css" rel="stylesheet">
  <script src="lib/bootstrap-5.3.x/js/bootstrap.bundle.min.js"></script>
  ```
- [ ] 2.2 Update Font Awesome to version 6
- [ ] 2.3 Remove `partial-affix.js` (affix removed in BS4, use CSS `position: sticky`)

### Phase 3: Grid System Migration
- [ ] 3.1 Replace `row-fluid` with `row`
- [ ] 3.2 Convert `span*` to `col-*`:
  - `span3` → `col-md-3`
  - `span5` → `col-md-5`
  - `span6` → `col-md-6`
  - `span7` → `col-md-7`
  - `span8` → `col-md-8`
  - `span9` → `col-md-9`
  - `span11` → `col-md-11`
  - `span12` → `col-md-12` or `col-12`

### Phase 4: Component Migration

#### 4.1 Navbar
```html
<!-- Old (Bootstrap 2/3) -->
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="#">Brand</a>
      <div class="nav-collapse collapse">
        <ul class="nav">...</ul>
      </div>
    </div>
  </div>
</div>

<!-- New (Bootstrap 5) -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
  <div class="container">
    <a class="navbar-brand" href="#">Brand</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">...</ul>
    </div>
  </div>
</nav>
```

#### 4.2 Modals
```html
<!-- Old -->
<div id="myModal" class="modal hide" tabindex="-1">
  <div class="modal-header">...</div>
  <div class="modal-body">...</div>
</div>

<!-- New -->
<div id="myModal" class="modal fade" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">...</div>
      <div class="modal-body">...</div>
    </div>
  </div>
</div>
```

#### 4.3 Input Groups
```html
<!-- Old -->
<div class="input-prepend input-append">
  <span class="add-on">Label</span>
  <input type="text">
  <span class="add-on">Suffix</span>
</div>

<!-- New -->
<div class="input-group">
  <span class="input-group-text">Label</span>
  <input type="text" class="form-control">
  <span class="input-group-text">Suffix</span>
</div>
```

#### 4.4 Buttons
- [ ] `btn-mini` → `btn-sm`
- [ ] `btn-large` → `btn-lg`
- [ ] Button groups: Update `data-toggle="buttons-radio"` to use radio inputs with btn-check

#### 4.5 Navigation/Tabs
```html
<!-- Old -->
<div class="btn-group" data-toggle="buttons-radio">
  <button data-target="#tab1" data-toggle="tab">Tab 1</button>
</div>

<!-- New -->
<ul class="nav nav-tabs" role="tablist">
  <li class="nav-item">
    <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tab1">Tab 1</button>
  </li>
</ul>
```

#### 4.6 Well Component
```html
<!-- Old -->
<ul class="nav nav-list well well-small">

<!-- New (use card or custom styling) -->
<ul class="list-group rounded p-3 bg-light">
```

### Phase 5: JavaScript Migration
- [ ] 5.1 Update modal initialization:
  ```javascript
  // Old
  $('#modal').modal({backdrop: 'static'})

  // New
  const modal = new bootstrap.Modal(document.getElementById('modal'), {backdrop: 'static'})
  modal.show()
  ```
- [ ] 5.2 Update tab/collapse data attributes
- [ ] 5.3 Replace affix with CSS `position: sticky`

### Phase 6: CSS Migration
- [ ] 6.1 Update `pull-left/right` to flexbox or `float-start/end`
- [ ] 6.2 Review custom CSS in `Search-min.css` for Bootstrap class dependencies
- [ ] 6.3 Update responsive breakpoint classes if needed

### Phase 7: Icon Migration
- [ ] 7.1 Update Font Awesome icons:
  - `icon-globe` → `fa-solid fa-globe`
  - `icon-search` → `fa-solid fa-magnifying-glass`
  - `icon-time` → `fa-solid fa-clock`
  - `icon-plus` → `fa-solid fa-plus`
  - `icon-exclamation` → `fa-solid fa-exclamation`
  - `icon-white` → Use CSS color instead

### Phase 8: Testing & Validation
- [ ] 8.1 Run Playwright E2E tests
- [ ] 8.2 Manual testing of all form interactions
- [ ] 8.3 Test modal functionality
- [ ] 8.4 Test responsive behavior at various breakpoints
- [ ] 8.5 Cross-browser testing (Chrome, Firefox)
- [ ] 8.6 Accessibility audit

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| JavaScript conflicts | High | Thorough testing, staged rollout |
| Broken layouts | Medium | Comprehensive visual regression |
| Modal behavior changes | Medium | Update JS initialization code |
| Custom CSS conflicts | Medium | Review and update searchPage.css |

## Resources

- [Bootstrap 5 Migration Guide](https://getbootstrap.com/docs/5.3/migration/)
- [Bootstrap 4 Migration from 3](https://getbootstrap.com/docs/4.6/migration/)
- [Font Awesome 6 Icons](https://fontawesome.com/icons)

## Estimated Effort

- Grid system changes: 2-3 hours
- Component migration: 4-6 hours
- JavaScript updates: 2-3 hours
- Testing & fixes: 4-6 hours

**Total: ~12-18 hours**
