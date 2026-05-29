# DDF 2.31 Security Remediation Tasks

**Status:** Phases 0-3 complete & build-verified (2026-05-29, 459/459 modules); Phase 4 (majors) pending
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
- [ ] 4.3 Legacy `com.sun.mail:javax.mail/1.6.2` in ddf-cxf-karaf feature (<1.6.8 SMTP-injection): bump to 1.6.8 or migrate WSS4J off javax.mail
- [ ] 4.4 jdom 1.x (`jdom.bundle.version=1.1_4`) XXE in abdera feature: decide suppress vs remove abdera (opensearch source/endpoint dependency)

## Phase 5: Verification, re-scan & PR cleanup
- [ ] 5.1 Re-trigger Dependabot/security scan; confirm P4 "already-fixed" alerts cleared (xmlsec, jetty-server, spring-context, groovy, ant, jdom2, logback-classic, commons-net, commons-beanutils, jsch)
- [ ] 5.2 Close redundant Dependabot PRs superseded by direct bumps: #209, #205, #204, #202, #200, #190, #194, #207, #166, #167 (and stale #125 karaf, #130 usng4j — already in via merge)
- [ ] 5.3 Triage CodeQL 3 critical + 24 high SAST findings (separate sub-effort)
- [ ] 5.4 Fix SonarCloud analysis not populating measures (project `montge_ddf` shows empty) — needs working analysis run + coverage
- [ ] 5.5 Final `mvn install -Dfast` + push branch; confirm CI green

## Deferred (tracked elsewhere — NOT in this change)
- jetty-http / Jetty 9.4 EOL line → `pax-web-jakarta-servlet-upgrade` (Jetty 12)
- pac4j 5.7.7 → 6.x (CVE-2026-40458 + jakartaee adapter) → separate change
- `distribution/docs` empty-archive blocker → separate fix (prerequisite for full distribution zip)
