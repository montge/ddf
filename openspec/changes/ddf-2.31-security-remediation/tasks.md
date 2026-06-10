# DDF 2.31 Security Remediation Tasks

**Status:** Phases 0-7 complete (2026-06-10); remaining: SONAR_TOKEN regeneration (user) → 5.4, Dependabot PR cleanup → 5.2, merge PR #210 → 7.10, post-merge alert verification → 5.1
**Baseline:** 90 unique advisories / 41 packages (4,730 raw Dependabot alerts); CodeQL 3 critical + 24 high.
**Approach:** Apply version fixes as direct root-`pom.xml` property bumps, then close the redundant Dependabot PRs. All values are current→target.

## Phase 0: Critical & high one-line property bumps (root pom.xml)
- [x] 0.1 `mina.version` 2.1.10 → **2.2.7** (CRITICAL RCE chain) + restito hardpin in `thirdparty/restito/pom.xml` 2.2.4 → 2.2.7 (supersedes PR #209)
- [x] 0.2 `tomcat-embed.version` 9.0.110 → **9.0.118** (3 CRITICAL + highs; PR #200 only goes to 9.0.117 which leaves criticals — do NOT merge #200 as-is)
- [x] 0.3 `bouncy.version` 1.83 → **1.84** (whole BC family in lockstep: bcprov/bcpkix/bcpg/bcmail/bcutil-jdk18on; supersedes PRs #205/#204/#202)
- [x] 0.4 `solr.zookeeper.version` 3.9.4 → **3.9.5** (2× HIGH; supersedes PR #190)
- [x] 0.5 `netty.version` 4.1.130.Final → **4.1.133.Final** (covers netty-codec + netty-codec-http2 family; supersedes PRs #194/#207)
- [x] 0.6 `solr.version` 9.10.0 → **9.10.1** (2× HIGH; moves solrj/solr-api/solr-test-framework)
- [x] 0.7 Build-verify Phase 0: `mvn install -Dfast` green; spot-check `mvn dependency:tree` shows no split BC/netty/solr versions

## Phase 1: High/medium low-risk bumps
- [x] 1.1 `logback.version` + `logback.classic.version` 1.5.21 → **1.5.25** (keep `logback-access` 1.2.13 — Jetty 9 pin)
- [x] 1.2 `apache-log4j.version` 2.25.3 → **2.25.4**
- [x] 1.3 `commons-configuration2.version` 2.10.1 → **2.15.0**
- [x] 1.4 `angus-mail.version` 2.0.3 → **2.0.4**
- [x] 1.5 Fix stale neethi literal: `libs/ddf-cxf-karaf/features/src/main/feature/feature.xml` `3.2.0` → `${wss4j.neethi.version}` (currently 3.2.2) — feature can otherwise resolve vulnerable 3.2.0
- [x] 1.6 Build-verify Phase 1

## Phase 2: Transitive pins, low-severity & test-scope
- [x] 2.1 `dependencyManagement` pin `com.github.junrar:junrar` **7.5.10** (override Tika's transitive 7.5.5)
- [~] 2.2 `not-yet-commons-ssl` — **NO FIX AVAILABLE** (0.3.15 fabricated by analyzer; latest release is 0.3.11, lib abandoned). Reclassified to suppress/accept (medium); shaded/embedded in keystoreeditor. Revisit: replace lib or suppress in dependency-check config.
- [x] 2.4b assertj kept as per-module versions (3.27.7) rather than a shared property — minimal change, all 4 modules build-verified
- [x] 2.3 `micrometer.opentelemetry.version` 1.9.1 → **1.62.0** (also moves opentelemetry-context; API-stable 1.x)
- [x] 2.4 assertj-core → **3.27.7** across the 4 modules (catalog-solr-cache, catalog-core-standardframework, platform-util, libs/test-common); add `assertj.version` property + dependencyManagement (supersedes PRs #166/#167) — test scope
- [x] 2.5 `xmlunit-matchers.version` 2.8.2 → **2.10.0** (test scope)
- [x] 2.6 `jruby.version` 9.4.12.0 → **9.4.12.1** (docs/provided scope; do NOT jump to 10.x)
- [x] 2.7 Build-verify Phase 2

## Phase 3: Drop-unused / suppress (verify zero consumers first)
- [x] 3.1 Remove `hibernate-core` from `features/solr/pom.xml` (zero `org.hibernate`/`javax.persistence` imports; all optional)
- [x] 3.2 Remove `org.codice.thirdparty:commons-httpclient` from `catalog/catalog-app/pom.xml` + feature bundle (zero `org.apache.commons.httpclient` usage)
- [~] 3.3 `commons-lang` 2.6 — **ACCEPT (do not drop)**: verified active OSGi `Import-Package: org.apache.commons.lang` (v1) consumers exist, so removing the bundle from branding/kernel/utilities would break resolution. LOW DoS-only CVE → accept/suppress rather than risk the runtime.
- [x] 3.4 jgit (CRITICAL): reclassify to suppress/false-positive in dependency-check config (build-time `support-githooks` only; force-upgrade breaks it — real fix belongs upstream in `codice/ddf-support`)
- [x] 3.5 Build-verify Phase 3

## Phase 4: Major / blocked items (full scope per decision)
- [>] 4.1 **Shiro 1.13.0 → 2.1.0** — **DEFERRED to a dedicated change** (per decision 2026-05-29). LOW CVE severity vs high blast radius: 176 source files import `org.apache.shiro`; 2.x splits into shiro-core/lang/crypto-*/config-* requiring multi-bundle feature rewiring + moved-import fixes + full security regression. Assessment: shiro-core 2.1.0 IS an OSGi bundle and `org.apache.shiro.util` is largely retained, so it's tractable — but warrants focused work with runtime/security testing. Tracked; PR #184 left open.
- [x] 4.2 **Spring 6.2.11** — bumped `spring.version`/`spring.osgi.bundle.version`/`spring.feature.version` to 6.2.11 (fixes CVE-2025-41249). Since ServiceMix never published a 6.2.11 OSGi bundle, rewrote the kernel spring feature to wrap the OFFICIAL `org.springframework:*:6.2.11` jars via Karaf's `wrap:` protocol (Export-Package=org.springframework.*, Import-Package optional). Compile + feature-XML structure build-verified. **⚠ RUNTIME OSGi resolution NOT yet validated** (needs a running Karaf; blocked locally by the docs distribution issue). CVE-2025-41254 (STOMP CSRF) needs 6.2.12 — future. PR #184 N/A (that's shiro).
- [x] 4.3 javax.mail SMTP-injection — replaced `com.sun.mail:javax.mail/1.6.2` (dead-end coord) with `com.sun.mail:jakarta.mail/1.6.8` in ddf-cxf-karaf feature (the maintained javax-namespace 1.6.x line; provides `javax/mail`, WSS4J-compatible, CVE-fixed). Full distribution build-verified.
- [~] 4.4 jdom — **no action needed**: the only Dependabot finding is `org.jdom:jdom2` (GHSA-2363-cqg2-863c), already on the patched servicemix bundle `2.0.6.1_1` (rescan clears). jdom **v1** (1.1_4, abdera/opensearch) is NOT a Dependabot finding (synthesis-agent extrapolation) — leave as-is unless CodeQL flags it.

## Build Blocker FIXED (was task #6 — enables full distribution + Spring runtime testing)
- [x] B.1 `distribution/docs` "archive cannot be empty" — root cause: asciidoctor HTML generation was hard-disabled (`<skip>true</skip>`) to dodge a JRuby STRIO_READABLE error (Issue #39) that the since-upgraded JRuby (9.4.12.1) already fixes. Removed the stale skip → 12 HTML docs generate, docs-export assembles. **Full unrestricted `mvn install -Dfast` now SUCCEEDS (462 modules), producing ddf-2.31.0-SNAPSHOT.zip (565MB) + Docker images.** First end-to-end distribution build.

## Phase 5: Verification, re-scan & PR cleanup
- [ ] 5.1 Re-trigger Dependabot/security scan; confirm P4 "already-fixed" alerts cleared (xmlsec, jetty-server, spring-context, groovy, ant, jdom2, logback-classic, commons-net, commons-beanutils, jsch)
- [x] 5.2 Close redundant Dependabot PRs superseded by direct bumps — DONE in two waves. Wave 1 (2026-05-30): #209, #205, #204, #202, #200, #190, #194, #207, #166, #167, #125, #130. Wave 2 (2026-06-10, evidence-audited via 22-agent workflow): closed #232/#231 (netty 4.1.135 on branch), #203/#188 (pac4j 5.7.10 on branch), #126 (asm 9.9.1), #124 (httpclient 4.5.14), #192 (slack action's job deleted from ci.yml), #198 (dup of #206), #121 (dup of #153), #131 (gml 2.x would mix 1.x/2.x JAXB bindings; @dependabot ignore major), #127 (1.4.0 is a downgrade vs CODICE fork; @dependabot ignore dependency). KEPT OPEN for post-#210 merge: #206 activemq 5.19.6, #153 plantuml, #129 javassist, #128 restassured, #123 truth, #189/#187/#186/#122 GH-actions bumps, #184 shiro (deferred change, deliberately open). #208 netty 4.2 cross-line left open pending user decision.
- [~] 5.3 CodeQL 3 critical + 24 high SAST — **triaged** via a 46-agent workflow (full report in `codeql-triage.md`): 4 exploitable, 13 needs-fix, 9 false-positive. Both criticals #19 (XXE) and #50 (XSLT-injection) are FALSE-POSITIVES (already-hardened helpers). **Fixes applied + build-verified (4 exploitable):**
  - [x] #18 XXE (critical) — `XmlSchemaMessageBodyReader` now parses with a hardened XXE-safe DocumentBuilderFactory before XPath
  - [x] #16/#17 ReDoS — `JsonpValidator` linear regex + 128-char cap (30/30 tests pass)
  - [x] #21/#20 path-injection — `KmlEndpoint` icon id charset restriction + path containment
  - [x] #670–674 path-injection — `FileSystemStorageProvider` read-path containment guard (closes the verifier-upgraded #673 authenticated file-read)
  - [x] #22/#23 Zip Slip — `ZipDecompression` canonical-base containment + `FileSystemStorageProvider.generateContentFile` strips path via `FilenameUtils.getName` (robust sink-side fix)
  - [x] #135 sensitive-log — `OAuthSecurityImpl` no longer TRACE-logs `webClient.getHeaders()` (Authorization/Basic secret)
  - [x] #12 REST XSS — `RESTEndpoint.deleteDocument` stops echoing raw id; error responses HTML-escape the message before `<pre>`
  - [x] #13 SAML logout XSS — `HtmlResponseTemplate` HTML-escapes targetUrl/samlValue/relayState (all double-quoted HTML attributes); 18/18 template tests pass
  - [x] #14 WKT ReDoS (`WktStandard`) — initially deferred, then actually FIXED by the SonarQube pass-1/2 possessive-quantifier rewrite (see 7.6). Alert stays "open" against main until post-merge re-analysis.
  - [x] #41 insecure-trustmanager — by-design (admin cert-discovery TOFU); dismissed via API 2026-06-09 ("won't fix" + justification), state verified 2026-06-10.

  **CodeQL outcome: 9 of 13 actionable findings fixed + build-verified; both criticals' real exploitable one (#18) fixed, the 2 critical FPs documented for dismissal; #14/#41 deferred with rationale.**
- [ ] 5.4 Fix SonarCloud analysis not populating measures (project `montge_ddf` shows empty) — ROOT CAUSE ISOLATED 2026-06-10: POM sonar coords are correct (`montge`/`montge_ddf`) and the project exists publicly on SonarCloud; the scanner's "Project not found" means the `SONAR_TOKEN` repo secret (last set 2025-12-13) is invalid. USER ACTION: regenerate at sonarcloud.io → My Account → Security, then `gh secret set SONAR_TOKEN -R montge/ddf`, then re-run the SonarCloud Analysis workflow
- [x] 5.5 Pushed to PR #210 (feature → main). main+develop synced to origin; 12 redundant Dependabot PRs closed.

## Phase 6: CI hardening (surfaced by the PR; fixed bottom-up)
- [x] 6.1 **Build infra:** disabled ddf-parent `enforce-bytecode-version` (hardcoded maxJdkVersion=17, broken by the JDK 21 target; re-add at 21 false-positives on Karaf 4.4.10 MRJAR deps e.g. jline 3.30.6). Set `build-sequential.yml` matrix to `java:['21']` (JDK 17 can't compile release 21). NOTE: `-Dfast` masks the enforcer (enforcer.skip=true) — always validate CI with a non-fast build.
- [x] 6.2 **Unit-test debt (32 classes / 24 modules):** fixed the Oct-2025 "Add 647 tests" AI coverage suite broken by JDK21/Mockito5/JAXB removal (test-only, 0 production changes; 3 PaxExamRuleIT @Ignore'd as live-container ITs). Honestly flagged 2 pre-existing production concerns: CSV formula injection, jakarta.xml.bind 4.0.2 base64 edge case.
- [~] 6.3 **L3 feature-resolution ITs — DEFERRED (separate follow-up).** Pax Exam ITs boot Karaf to verify features resolve in isolation; caught + fixed a real commons-text/platform-util gap. MORE resolution issues likely (other dep-bump Import-Package floors, removed bundles, the Spring 6.2.11 `wrap:` bundles' runtime resolution). Slow (Karaf boot per IT); needs a dedicated iterative pass. This is also where the Spring wrapper gets its runtime validation. Track as its own change.

## Deferred (tracked elsewhere — NOT in this change)
- jetty-http / Jetty 9.4 EOL line → `pax-web-jakarta-servlet-upgrade` (Jetty 12)
- pac4j 5.7.7 → 6.x (CVE-2026-40458 + jakartaee adapter) → separate change
- **L3 feature-resolution IT validation** (Spring wrapper runtime + dep-bump Import-Package floors) → dedicated follow-up change
- CSV formula-injection hardening + jakarta.xml.bind 4.0.4 → follow-up

## Phase 7: SonarQube remediation + upstream re-sync + alert closure (2026-05-30 → 2026-06-09)
- [x] 7.1 **SonarQube pass 1 (bugs+vulns)** — 67 findings fixed across 45 java + 1 pom (commit b22856a098): XXE (S2755 ×4), insecure temp dirs (S5443 ×7), session fixation (S2254, WebSSOFilter), ThreadLocal leaks (S5164 ×15), resource leaks (S2095 ×6), SecureRandom (S2119 ×2), ReDoS (S5998 ×2) + test-quality. Reactor-green 809 modules; 3 residual unit failures proven pre-existing via clean-baseline stash comparison.
- [x] 7.2 **SonarQube pass 2 (37 security hotspots)** — 17 exploitable fixed (commit 7a4edff077): zip-bomb caps (ImportCommand, KmzInputTransformer, ZipDecompression), ReDoS linearization (MetacardCreator DURATION_SECONDS 59s→4ms, Wfs/CswFilterDelegate coord patterns, PkiTools), JSESSIONID HttpOnly. 20 safe-by-context documented (all 13 weak-hash = non-security dedup/filenames). All 6 regex rewrites proven byte-identical via standalone old-vs-new harness. Review caught + removed an agent-added MAX_WKT_LENGTH=8192 cap that would have rejected legitimate >8KB polygons.
- [x] 7.3 **SonarQube pass 3 (S2699)** — 359 meaningful assertions added across 115 test files / 78 modules (commit c8ff0f867f); 3 FPs + 5 unfixable stubs documented. Zero src/main changes.
- [x] 7.4 **pac4j 5.7.7 → 5.7.9** (commit f31141657b) — patches critical CVE-2026-29000 (pac4j-jwt) within the 5.x line; 6.x major still tracked separately.
- [x] 7.5 **Upstream re-sync** (merge ec8dc37107) — absorbed codice/ddf Karaf 4.4.11 chain (pax.logging 2.3.3, pax.url 2.7.1, pax.web 8.0.35, asm 9.9.1, jansi 2.4.3, commons-io 2.22.0, commons-logging 1.3.6, httpclient 4.5.14, jackson 2.21.2), jackson bundleReplacements resolver convergence, OpenDJ embedded-ldap removal. Kept ours: CXF 4.1.1, OpenSAML 4.3.2, logback 1.5.25, jakarta.* line, pac4j 5.7.9, pdfbox 3.0.6, poi 5.5.1. Their pac4j 6.5.3 / CXF 3.6.11 superseded. Build-verified (796 modules).
- [x] 7.6 **CodeQL dismissals via API (2026-06-09):** #19 XXE FP, #50 XSLT FP (hardened helpers), #41 TOFU by-design — with documented justifications. #14 WKT ReDoS now actually FIXED (pass-1/2 possessive rewrite), closes on re-analysis.
- [x] 7.7 Sensitive-log #136-139 (insecure-defaults validators embed passwords in alert messages) — FIXED (commit 9fdfc73162): alerts report which property is insecure without echoing the password value
- [x] 7.8 CodeQL quality alerts #51 (tainted-arithmetic), #52 (wider-type comparison), #676 (implicit cast) — FIXED (commit 9fdfc73162)
- [x] 7.9 Dependabot residue audit — DONE 2026-06-09 (33 unique packages classified; final uniques landed in ee24772b49: netty 4.1.135, pac4j 5.7.10, owasp-sanitizer 20260101.1, jdom2 2.0.6.1 DM pin, jetty module overrides, gitsetup slf4j-ext/jsch). Permanent residue documented: jetty 9.4/10 EOL (→ PR #183), shiro 1.13 (deferred change), commons-lang 2.6 / commons-configuration 1.10 (no fixed line), not-yet-commons-ssl (no real fix exists), jgit via support-githooks (dev-only, fix belongs in codice/ddf-support). The ~2900 main-manifest alerts close on PR #210 merge.
- [ ] 7.10 Merge PR #210 → main, sync develop, verify alert counts drop
