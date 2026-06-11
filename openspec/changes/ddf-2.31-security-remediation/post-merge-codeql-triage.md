# Post-Merge CodeQL Triage (2026-06-10)

After PR #210 merged to `main` (merge commit `290d2a5b30`) and the SonarCloud
`SONAR_TOKEN` was regenerated, a fresh CodeQL analysis of `main` left **5 high**
security alerts (0 critical). Each was re-triaged adversarially against the merged
code (triage → independent refutation pass). Result: **1 real residual
vulnerability, 4 hardened false positives.**

## Real residual vulnerability — FIXED in PR #237

### #22 `java/zipslip` (high) — `ImportCommand.java:205` → `FileSystemStorageProvider.rollback`
Authenticated path traversal → arbitrary directory deletion. `ddf:import` passes a
zip entry's `pathParts[2]` verbatim as the storage request id; `create()` registers
it in `updateMap` unconditionally, so `commit()` → `commitUpdates()` →
`finally { rollback() }` always runs, and `rollback()` deleted
`Paths.get(baseContentTmpDirectory, id)` with no containment. An entry like
`a/b/../content/x` (id = `..`) deletes `<content>/store`. `ContentItemValidator`
gates the write sinks but not rollback. **Fixed** by containing `rollback()` and
`getTempContentItemDir()` within the temp base via the existing
`resolveWithinBase()` helper; regression test added. See PR #237.

## Hardened false positives — recommend Dismiss ("won't fix" / "false positive")

Each was confirmed safe by reading the merged code; CodeQL retains taint only
because it cannot model the specific barrier in use.

### #973 `java/sensitive-log` — `OAuthSecurityImpl.java:338`
`sanitizeFormParameters()` masks the `password` entry with same-length asterisks
immediately before the `LOGGER.trace`; `Form.asMap()` returns the live internal map
(jakarta.ws.rs-api 3.1.0), so the mask is in the logged instance. The genuinely
sensitive `webClient.getHeaders()` Basic secret was already removed (commit
`e3a9f74dd4`, alert #135). CodeQL cannot model element-replacement in an aliased
collection as a sanitizer. Residual disclosure: password length only, at TRACE.

### #972 `java/zipslip` — `ZipDecompression.java:108`
Entry name validated before concatenation: `Paths.get(filename).normalize()`
rejected if `isAbsolute()` or `startsWith("..")`. Sole caller supplies a
`dir + separator` base; extraction writes only regular files/dirs (no symlinks).
CodeQL tracks the raw String to the sink, not the check on the derived normalized
Path.

### #20 `java/path-injection` — `KmlEndpoint.java:437`
Classpath sink `getResourceAsStream("icons/" + id)` constrained by
`@Path("/icons/{id:[A-Za-z0-9._-]+}")`, enforced by the CXF runtime before method
entry — `/`, `\`, `%` are all outside the charset, so no separator reaches `id`;
a dots-only segment resolves to nothing (404). The filesystem branch has an
independent `normalize()+startsWith` guard CodeQL already accepts (commit
`1df889cb36`). CodeQL does not model declarative `@Path` regex constraints.

### #13 `java/xss` — `LogoutRequestService.java:493`
RelayState reaches the `@GET` return only after `buildAndValidateSaml()`, which for
HTTP-Redirect validates the IdP signature over the RelayState — only IdP-signed
values reach the sink. Every rendering is output-encoded (POST: HTML-escape into a
double-quoted attribute; Redirect: percent-encode via UriBuilder then HTML-escape
the URL; SOAP: unused). CodeQL keeps taint because `escapeHtml` is chained
`String.replace` (taint-preserving) and signature validation is not a recognized
barrier.

## Suggested dismissal API call (after authorization)
```
gh api -X PATCH repos/montge/ddf/code-scanning/alerts/<N> \
  -f state=dismissed -f dismissed_reason="false positive" \
  -f dismissed_comment="<justification above>"
```
for N in 973, 972, 20, 13.
