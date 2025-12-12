# AI Agent Workflow Instructions

## Workflow Overview

OpenSpec uses a three-step workflow: **Proposal -> Apply -> Archive**

### 1. Proposal Phase
When starting new work:
1. Create a change folder: `openspec/changes/[feature-name]/`
2. Draft `proposal.md` with rationale and scope
3. Draft `tasks.md` with implementation checklist
4. Create spec deltas in `specs/[domain]/spec.md` if needed

### 2. Apply Phase
When implementing:
1. Reference tasks.md for current work items
2. Follow spec requirements exactly
3. Mark tasks complete as you finish them: `- [x]`
4. Run tests after each significant change
5. Keep commits atomic and well-documented

### 3. Archive Phase
When feature is complete:
1. Merge spec deltas into main specs
2. Move change folder to `openspec/archived/`
3. Update project.md if patterns changed

## Agent Commands

### Starting New Feature
```
/openspec propose [feature-name]
```
Creates change folder with template files.

### Implementing Tasks
```
/openspec apply [feature-name]
```
Load context from change folder and begin implementation.

### Checking Status
```
/openspec status
```
List active changes and task completion percentages.

## Spec Language

### Requirements
- **MUST/SHALL** - Mandatory requirement
- **SHOULD** - Strong recommendation
- **MAY** - Optional capability

### Scenarios
Use Given-When-Then format:
```markdown
#### Scenario: [Name]
- GIVEN [precondition]
- WHEN [action]
- THEN [expected outcome]
```

## DDF-Specific Guidelines

### Before Making Changes
1. Read relevant source files
2. Check Blueprint XML for service registration
3. Understand plugin chain position
4. Review existing tests

### Testing Requirements
- Every new class needs corresponding test class
- Target 80%+ coverage for new code
- Run `mvn test` in affected modules
- Run `mvn fmt:format` before committing

### Security Considerations
- Never hardcode credentials
- Validate all external inputs
- Use parameterized queries
- Log security events appropriately
