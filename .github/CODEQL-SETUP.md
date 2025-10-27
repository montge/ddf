# CodeQL Setup and Configuration

## Overview

This repository uses **CodeQL Advanced Setup** for security analysis, configured in `.github/workflows/security-scan.yml`. This provides more control over the analysis process compared to GitHub's default setup.

## Problem: Default Setup vs Advanced Setup Conflict

### The Issue

GitHub Code Scanning prevents SARIF uploads from advanced CodeQL workflows when "CodeQL default setup" is enabled in the repository settings. This results in the error:

```
Code Scanning could not process the submitted SARIF file:
CodeQL analyses from advanced configurations cannot be processed when the default setup is enabled
```

### Why This Happens

- **Default setup** runs automatically based on GitHub's configuration
- **Advanced setup** runs via custom workflow files (`.github/workflows/security-scan.yml`)
- Both cannot be enabled simultaneously to prevent duplicate alerts and confusion

## Solution: Disable Default Setup

### Option 1: Via GitHub API (Recommended for Automation)

Use the GitHub CLI or REST API to disable default setup:

```bash
# Using GitHub CLI
gh api --method PATCH /repos/OWNER/REPO/code-scanning/default-setup \
  -f state=not-configured

# Using curl
curl -L \
  -X PATCH \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer <YOUR-TOKEN>" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  https://api.github.com/repos/OWNER/REPO/code-scanning/default-setup \
  -d '{"state":"not-configured"}'
```

**For this repository:**
```bash
gh api --method PATCH /repos/montge/ddf/code-scanning/default-setup -f state=not-configured
```

### Option 2: Via GitHub Web UI (Manual)

1. Navigate to **Settings** > **Code security and analysis**
2. In the **CodeQL analysis** row, click the dropdown menu
3. Select **Switch to advanced**
4. In the popup, click **Disable CodeQL**
5. The custom workflow in `.github/workflows/security-scan.yml` will now work

### Verify the Configuration

Check the current status:

```bash
gh api repos/montge/ddf/code-scanning/default-setup
```

Expected output when correctly configured for advanced setup:
```json
{
  "state": "not-configured",
  "languages": [...],
  "updated_at": null,
  "schedule": null
}
```

## Why We Use Advanced Setup

Our custom CodeQL workflow provides:

1. **Custom query sets**: `security-extended,security-and-quality`
2. **Build control**: Specific Maven build configuration with `MAVEN_OPTS`
3. **Integration**: Combined with OWASP dependency check, secret scanning, etc.
4. **Scheduling**: Weekly security scans on Sundays at 3 AM UTC
5. **Customization**: Ability to modify queries, languages, and analysis parameters

## Maintaining Advanced Setup

### When Default Setup Gets Re-Enabled

Default setup may be re-enabled if:
- Organization-level security policies are applied
- Repository security configurations change
- Manual re-enablement through settings

If you see the SARIF upload error again:
1. Run the disable command: `gh api --method PATCH /repos/montge/ddf/code-scanning/default-setup -f state=not-configured`
2. Re-run the failed workflow

### Switching Back to Default Setup

If you want to use default setup instead:

1. **Remove or disable** the CodeQL job in `.github/workflows/security-scan.yml`
2. **Enable default setup** in repository settings or via API:
   ```bash
   gh api --method PATCH /repos/montge/ddf/code-scanning/default-setup -f state=configured
   ```

## Best Practices

1. **Choose one approach**: Use either default setup OR advanced setup, not both
2. **Document your choice**: Keep this file updated with the current configuration
3. **Monitor for conflicts**: Watch for SARIF upload errors in workflow runs
4. **Organization policies**: If using organization-level security configurations, ensure they allow "advanced setup"

## Troubleshooting

### Error: "CodeQL analyses from advanced configurations cannot be processed"

**Cause**: Default setup is enabled
**Solution**: Disable default setup using the commands above

### Error: "Not Found" when disabling default setup

**Cause**: Insufficient permissions or incorrect repository path
**Solution**: Ensure you have admin access and verify the repository name

### Workflow succeeds but no results appear

**Cause**: May need to wait for analysis to complete or check permissions
**Solution**: Verify the workflow has `security-events: write` permission

## References

- [GitHub Docs: Advanced Setup](https://docs.github.com/en/code-security/code-scanning/creating-an-advanced-setup-for-code-scanning)
- [GitHub Docs: Default Setup Troubleshooting](https://docs.github.com/en/code-security/code-scanning/troubleshooting-sarif-uploads/default-setup-enabled)
- [CodeQL Action Repository](https://github.com/github/codeql-action)
- [REST API: Code Scanning](https://docs.github.com/en/rest/code-scanning/code-scanning)

## Change Log

- **2025-10-27**: Initial documentation - Disabled default setup to allow advanced workflow
