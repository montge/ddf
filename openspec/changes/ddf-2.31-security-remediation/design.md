# Design & Decisions — DDF 2.31 Security Remediation

The grouped, per-package remediation detail lives in `remediation-plan.md` (verbatim output of the `ddf-security-remediation-plan` workflow). This file records the cross-cutting engineering decisions and the corrections applied to that output.

## Corrections to the workflow output
The synthesis agent flagged "every Dependabot PR is fabricated." This was a **verify-layer error**: agents queried `codice/ddf` PR numbers instead of this fork `montge/ddf`. Ground truth (`gh pr list --repo montge/ddf`) confirms the PRs are real and on-target: #209 mina-core 2.2.7, #205/#204/#202 BouncyCastle 1.84, #190 zookeeper 3.9.5, #184 shiro 2.1.0, #166/#167 assertj 3.27.7, #200 tomcat (9.0.117 — insufficient), #194/#207 netty. `tasks.md` reflects the reconciled mapping.

Other agent factual slips (do not affect conclusions): wrong BSN for BouncyCastle bundles (actual `bcprov`, not `org.bouncycastle.bcprov-jdk18on`); wrong fix-version annotations for CVE-2023-33201/33202 (real: 1.74/1.73, still ≤ the chosen 1.84).

## Key decisions

### BouncyCastle `jdk15on` (critical) — already neutralized
`bcprov-jdk15on`/`bcpkix-jdk15on` are never a direct dependency; they are excluded in ~10 root-pom `<exclusion>` blocks (OpenSAML 4.x, Santuario xmlsec, cryptacular, pac4j) and forced to `jdk18on` via `${bouncy.version}`. `mvn dependency:tree` on consuming modules confirms zero `jdk15on` on the classpath. **Action: none beyond the family bump to 1.84** (closes the 2026 jdk18on CVEs CVE-2026-5598/0636).

### Tomcat embed — 9.0.118, not 9.0.117
PR #200 targets 9.0.117, which still leaves 3 CRITICAL CVEs (CVE-2026-43512/43515/41293). 9.0.118 is published and is the correct floor. Apply 9.0.118 directly; do not merge #200 as-is.

### Spring — source fixed, runtime OSGi gap (DECISION: attempt local wrapper)
Bumping `${spring.version}` to 6.2.11 fixes the OWASP/scan finding, but the **deployed Karaf bundle is the ServiceMix wrapper `org.apache.servicemix.bundles.spring-core` pinned at `6.2.8_1`**, and no `6.2.9_1`/`6.2.11_1` ServiceMix bundle is published (404). Per scope decision ("everything incl. majors"), Phase 4.2 investigates **building a local ServiceMix-style `spring-core 6.2.11` OSGi wrapper bundle** to close runtime CVE-2025-41249/41254. If infeasible within reasonable effort, fall back to documenting residual runtime exposure and waiting for ServiceMix. Never point `spring.osgi.bundle.version`/`spring.feature.version` at a nonexistent coordinate.

### Shiro 1.13 → 2.1 (DECISION: in scope, Phase 4.1)
CVE severity is LOW, but the user elected to include the breaking major. 2.x splits `shiro-core` into `shiro-core` + `shiro-crypto-*` + `shiro-lang` and changes OSGi packaging; requires feature/bundle re-wiring across 20+ security modules and a full security regression. PR #184 exists but a manual, coordinated upgrade is required. High integration risk — sequence last, behind its own build/regression gate.

### Jetty 9.4 EOL (DEFERRED)
`jetty-http`/`jetty-server` highs on the 9.4 line have no 9.4.x fix for 3 of 4 CVEs; resolution requires Jetty 12 (jakarta.servlet 6.x) via Pax Web 11. This is exactly the `pax-web-jakarta-servlet-upgrade` branch — defer there, do not attempt any 9.4.x bump here.

### jgit (critical) — suppress, don't upgrade
Used only by the precompiled build-time `support-githooks`; forcing 5.13.4 over 3.2.0 bytecode throws `NoSuchMethodError` (`release()` removed in jgit 4.0). Reclassify as suppress/false-positive (build-time, not deployed); the real version fix belongs upstream in `codice/ddf-support`.

### Hibernate / commons-httpclient / commons-lang 2.6 — drop-unused
No first-party source consumers; remove the dead declarations rather than chase EOL upgrades. Verify no other bundle `Import-Package`s the removed packages before deleting from feature.xml.

## Lockstep families (must move together)
- **BouncyCastle:** all `*-jdk18on` via `${bouncy.version}`.
- **Netty:** all `io.netty:*` via `${netty.version}`.
- **Solr:** solr-core/solrj/solr-api/solr-test-framework via `${solr.version}` (leave `solr.docs.version`, `solr.jetty.version`).
- **Logback:** `logback.version` + `logback.classic.version` together (NOT `logback-access`).

## Verification protocol
Each phase ends with `mvn install -Dfast`; for family bumps, `mvn dependency:tree` spot-checks for split versions. Final re-scan confirms "already-fixed" alerts clear. The pre-existing `DDF DOCS` empty-archive failure is expected and excluded (tracked separately).
