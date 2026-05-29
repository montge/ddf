I'll produce the remediation-plan section directly from the analyzed records. This is a synthesis task using the provided JSON, so no tool calls are needed.

## Remediation Plan

All version/property facts below are from the analyzed records. Where a `verify` block adjusted a value, the adjusted value is used and flagged.

---

### 1. already-fixed-rescan (no version change; rescan to clear stale alerts)

| Package | severityMax | Current → Fixed | Action |
|---|---|---|---|
| `org.apache.santuario:xmlsec` | high | 2.3.4 → 2.3.4 (already) | None. `${xmlsec.version}=2.3.4` (pom.xml:374) already covers all 3 CVEs. Rescan only. Do NOT move to 3.x (needs jakarta.xml.bind + OpenSAML 5). |
| `org.eclipse.jetty:jetty-server` | high | 9.4.58.v20250814 → **9.4.58.v20250814** (record said 9.4.57; **adjusted up**) | None. `${jetty.version}=9.4.58` already covers all 5 CVEs (incl. CVE-2025-5115 which 9.4.57 does NOT). Do not set a 9.4.57 safe-floor. |
| `org.eclipse.jetty:jetty-xml` | low | 9.4.58 → 9.4.52 (fixed floor) | None. 9.4.58 >> 9.4.52. Rescan. |
| `org.springframework:spring-context` | medium | 6.2.8 → 6.2.7 (floor) | None. `${spring.version}=6.2.8` already > 6.2.7. Rescan. |
| `org.codehaus.groovy:groovy-all` | critical | 2.4.21 → 2.4.21 | None. Hard-pinned at pom.xml:607-611. Rescan. Test/IT-feature scope only. |
| `org.apache.ant:ant` | medium | 1.10.15 → 1.10.11 (floor) | None. `${ant.version}=1.10.15` (catalog/pom.xml:27). Rescan. |
| `org.slf4j:slf4j-ext` | critical | 2.0.17 → **keep 2.0.17** (record's "1.7.26" is a DOWNGRADE — do NOT apply) | None. 2.0.x is unaffected. **Never enter 1.7.26 anywhere** — it would break OSGi `[2.0,3.0)` import ranges and the SLF4J 2.x SPI. Rescan. |
| `org.jdom:jdom2` | high | 2.0.6.1 → 2.0.6.1 | None. ServiceMix bundle `2.0.6.1_1` is the patched build. Rescan. (See Decisions: separate jdom 1.x exposure.) |
| `commons-net:commons-net` | medium | 3.11.1 → 3.9.0 (floor) | None. `${commons-net.version}=3.11.1`. Rescan. |
| `commons-beanutils:commons-beanutils` | high | 1.11.0 → 1.11.0 | None. `${commons-beanutils.version}=1.11.0` (pom.xml:178) == fix. Rescan. |
| `ch.qos.logback:logback-classic` | high | 1.5.21 → 1.5.21 (already) | None. 1.5.21 clears all logback-classic/core CVEs. Rescan. (See logback-core below — that one DOES need a bump.) |
| `com.jcraft:jsch` | medium | not present (mwiede:jsch 0.2.23 in use) → 0.1.54 (floor on the dead coord) | None. DDF uses `com.github.mwiede:jsch:0.2.23` via Camel; legacy `com.jcraft` not on classpath. Re-key SBOM to the mwiede coordinate. |

---

### 2. covered-by-dependabot-pr — **WARNING: every cited Dependabot PR was verified FABRICATED/unrelated.** Treat all as manual bumps.

| Package | severityMax | Current → Fixed | Action (manual — no real PR) |
|---|---|---|---|
| `org.bouncycastle:bcprov-jdk18on` | high | 1.83 → 1.84 | Bump `${bouncy.version}` **1.83 → 1.84** at pom.xml:170. Cited PR #205 is unrelated (GeoNames). STILL EXPOSED to CVE-2026-5598 (HIGH) + CVE-2026-0636. Moves bcprov/bcpkix/bcmail/bcpg/bcutil in lockstep. |
| `org.bouncycastle:bcpkix-jdk18on` | medium | 1.83 → 1.84 | Same single `${bouncy.version}` bump (covers CVE-2026-5588). PR #202 unrelated. |
| `org.apache.zookeeper:zookeeper` | high | 3.9.4 → 3.9.5 | Bump `${solr.zookeeper.version}` **3.9.4 → 3.9.5** at pom.xml:346. PR #190 is a 4-yr-old merged cert PR — fabricated. |
| `org.apache.tomcat.embed:tomcat-embed-core` | high | 9.0.110 → **9.0.118** (record/PR said 9.0.116/9.0.117 — **adjusted up**) | Bump `${tomcat-embed.version}` **9.0.110 → 9.0.118** at pom.xml:384. PR #200 targets only 9.0.117 which **leaves 3 unpatched CRITICAL CVEs** (CVE-2026-43512/43515/41293). Do NOT merge PR #200 as-is. Covers core/jasper/el/websocket. |
| `org.apache.shiro:shiro-core` | low (CVE) / **integration risk HIGH** | 1.13.0 → 2.1.0 | Bump `${apache.shiro.version}` **1.13.0 → 2.1.0** at pom.xml:167 — but this is a **MAJOR upgrade** (shiro-core split into shiro-core/-crypto-*/-lang; OSGi feature/bundle re-wiring). See Decisions. PR #184 cited but unverified. |
| `org.assertj:assertj-core` | high | 2.0.0 / 3.18.1 → 3.27.7 | **All 4 modules still need the bump** (PRs #166/#167 are unrelated merged feature PRs). No property today — see snippet below. |

**assertj-core fix (no property exists; hardcoded in 4 poms, all test/build-only):**
- `catalog/solr/catalog-solr-cache/pom.xml:66` (2.0.0 → 3.27.7)
- `catalog/core/catalog-core-standardframework/pom.xml:176` (2.0.0 → 3.27.7)
- `platform/util/platform-util/pom.xml:90` (3.18.1 → 3.27.7)
- `libs/test-common/pom.xml:204` (3.18.1 → 3.27.7)

Recommended cleanup — add to root pom and converge:
```xml
<properties>
  <assertj.version>3.27.7</assertj.version>
</properties>
<dependencyManagement><dependencies>
  <dependency>
    <groupId>org.assertj</groupId><artifactId>assertj-core</artifactId>
    <version>${assertj.version}</version>
  </dependency>
</dependencies></dependencyManagement>
```

---

### 3. bump-property (single root-pom property change)

| Package | severityMax | Current → Fixed | Property / location |
|---|---|---|---|
| `ch.qos.logback:logback-core` | high | 1.5.21 → 1.5.25 | `${logback.version}` (and matching `logback.classic.version`) at pom.xml:279. Keep `logback-access` at 1.2.13 (Jetty 9 pin). |
| `org.apache.mina:mina-core` | critical | 2.1.10 / 2.2.4 → 2.2.7 | `${mina.version}` **2.1.10 → 2.2.7** at pom.xml:385; also update restito hard-pin `thirdparty/restito/pom.xml:36` 2.2.4 → 2.2.7. PR #209 unrelated. |
| `org.apache.logging.log4j:log4j-core` | medium | 2.25.3 → 2.25.4 | `${apache-log4j.version}` at pom.xml:278. |
| `io.netty:netty-codec-http2` | high | 4.1.130 → 4.1.133.Final | `${netty.version}` at pom.xml:291 (moves whole io.netty family). |
| `io.netty:netty-codec` | high | 4.1.130 → 4.1.133.Final | Same `${netty.version}` bump (one edit covers both netty records). |
| `org.apache.solr:solr-core` | high | 9.10.0 → 9.10.1 | `${solr.version}` at pom.xml:340 (moves solrj/solr-api/solr-test-framework). Leave `solr.docs.version`, `solr.jetty.version` untouched. |
| `org.springframework:spring-core` | high | 6.2.8 → 6.2.11 | `${spring.version}` at pom.xml:352. **OSGi runtime NOT fully fixed** — see flags. |
| `org.xmlunit:xmlunit-core` | low | 2.8.2 → 2.10.0 | `${xmlunit-matchers.version}` **2.8.2 → 2.10.0** at pom.xml:144 (matchers pulls patched core). Test scope. |
| `org.jruby:jruby` | medium | 9.4.12.0 → 9.4.12.1 | `${jruby.version}` at pom.xml:159. Docs-only (provided scope); already suppressed in dependency-check config. Do NOT jump to 10.x. |
| `org.apache.commons:commons-configuration2` | medium | 2.10.1 → 2.15.0 | `${commons-configuration2.version}` at pom.xml:183 (propagates to test dep + security feature bundle). |
| `io.opentelemetry:opentelemetry-api` | medium | 1.9.1 → 1.62.0 | `${micrometer.opentelemetry.version}` at pom.xml:382 (also moves opentelemetry-context). Large 1.x jump, API-stable. |
| `org.apache.neethi:neethi` | high | 3.2.2 (already via property) BUT one stale hardcode | `${wss4j.neethi.version}=3.2.2` (pom.xml:365) is fine, but **fix the stale literal `3.2.0`** at `libs/ddf-cxf-karaf/features/src/main/feature/feature.xml:99` → `${wss4j.neethi.version}`. Until then the ddf-cxf-karaf feature can resolve vulnerable 3.2.0. |
| `com.sun.mail:jakarta.mail` (Angus) | medium | angus-mail/smtp 2.0.3 → 2.0.4 | `${angus-mail.version}` **2.0.3 → 2.0.4** at pom.xml (lines 226-228 region). Secondary: legacy `com.sun.mail:javax.mail/1.6.2` bundle (ddf-cxf-karaf feature.xml:188) is <1.6.8 — see Decisions. |

---

### 4. dependencymgmt-pin (no property; add a managed pin to override transitive)

| Package | severityMax | Current → Fixed | Action |
|---|---|---|---|
| `com.github.junrar:junrar` | medium | 7.5.5 (via Tika 3.2.3) → 7.5.10 | Add root `<dependencyManagement>` pin to override Tika's transitive 7.5.5. |
| `ca.juliusdavies:not-yet-commons-ssl` | medium | 0.3.11 → 0.3.15 | Bump hardcoded literal in `platform/security/certificate/security-certificate-keystoreeditor/pom.xml:48-52` 0.3.11 → 0.3.15 (re-shades patched class). |

junrar pin:
```xml
<dependencyManagement><dependencies>
  <dependency>
    <groupId>com.github.junrar</groupId><artifactId>junrar</artifactId>
    <version>7.5.10</version>
  </dependency>
</dependencies></dependencyManagement>
```

---

### 5. exclude-and-force — **BouncyCastle jdk15on → jdk18on migration (already done; verify only)**

| Package | severityMax | State | Action |
|---|---|---|---|
| `org.bouncycastle:bcprov-jdk15on` | critical | excluded everywhere; replaced by bcprov-jdk18on 1.83 | None. Legacy/EOL; never a real `<dependency>`. |
| `org.bouncycastle:bcpkix-jdk15on` | medium | excluded everywhere; replaced by bcpkix-jdk18on 1.83 | None. |

**What pulls jdk15on (all already carry explicit `jdk15on`/`jdk15on-pkix` exclusions):**
- OpenSAML 4.x: `opensaml-core`, `opensaml-xmlsec-api`, `opensaml-xmlsec-impl`, `opensaml-soap-impl`, `opensaml-security-api`, `opensaml-security-impl`
- `org.apache.santuario:xmlsec`
- `org.cryptacular:cryptacular` 1.2.4
- `org.pac4j:pac4j-jwt`, `pac4j-core`

These are neutralized via **10 `<exclusion>` blocks** in the root pom (lines ~531-585 and ~1050-1145); jdk18on is forced via `${bouncy.version}` dep-mgmt (lines 1019-1043). jdk15on **never reaches the classpath/runtime**. The only residual action is the BC family bump to 1.84 (Section 2) to close the 2026 jdk18on CVEs. No action on the jdk15on coordinates themselves.

---

### 6. needs-major-upgrade

| Package | severityMax | Current → Fixed | Action |
|---|---|---|---|
| `org.eclipse.jetty:jetty-http` | high | 9.4.58 → 12.0.33 | **No 9.4 fix exists for 3 of 4 CVEs.** Bumping within 9.4.x does NOT remediate. Requires Pax Web 8→11 + Jetty 9→12 + javax→jakarta migration (feature branch `pax-web-jakarta-servlet-upgrade`). Track there. |
| `org.hibernate:hibernate-core` | high | 5.4.24.Final → none-available (5.x EOL; fix only in 6.x) | **Preferred interim: drop-unused.** Verified: zero DDF source imports `org.hibernate`/`javax.persistence`; all consumers use `resolution:=optional`; not in any feature.xml. Remove dead `<dependency>` from `features/solr/pom.xml:103-106`. CVE-2026-0603 path is unreachable in DDF. |
| `commons-configuration:commons-configuration` (v1) | low | 1.10 → none-available (v1 EOL) | Migrate `DdfBrandingPlugin.java` to commons-configuration2 API (already at 2.10.1), switch its dep, drop vestigial decls in `catalog-core-directorymonitor`, then remove the v1 `${commons-configuration.version}` property + bundle. Loads a trusted classpath resource → low risk. |

---

### 7. drop-unused

| Package | severityMax | Current → Fixed | Action |
|---|---|---|---|
| `org.eclipse.jgit:org.eclipse.jgit` | critical | 3.2.0 → 5.13.4 (floor) | **Do NOT force-upgrade from DDF** — record's `drop-unused` is wrong: jgit is used pervasively by the precompiled `support-githooks` (build-time only, not deployed). Forcing 5.13.4 against 3.2.0 bytecode → `NoSuchMethodError` (`release()` removed in jgit 4.0). **Reclassify as suppress/false-positive** (already in security spec). A real fix must land upstream in codice/ddf-support. |
| `commons-httpclient:commons-httpclient` | medium | 3.1.0_1 (Codice repackage) → none-available (3.x EOL) | drop-unused: remove `org.codice.thirdparty:commons-httpclient` from `catalog/catalog-app/pom.xml:235-239` + matching feature bundle entry. Zero source consumers (`org.apache.commons.httpclient` grep = 0); HttpComponents 4.5.x already shipped. |

---

### 8. no-fix-available

| Package | severityMax | State | Action |
|---|---|---|---|
| `commons-lang:commons-lang` (v1 2.x) | medium | 2.6 → none (EOL; lang3 path already fixed at 3.20.0) | DoS-only StackOverflow on pathological input; zero first-party usage. Either accept, or drop-unused: remove the 2.6 bundle from `features/branding`, `features/kernel`, `features/utilities` feature.xml + the dep-mgmt entry (verify no third-party bundle Import-Package on `org.apache.commons.lang` first). |

---

## Flags — breakingRisk medium/high or osgiCompatible != yes

- **`org.eclipse.jetty:jetty-http`** — osgiCompatible=**no**, breakingRisk=**high**. Jetty 12 moves to jakarta.servlet 6.x under jetty-core/EE10; requires Pax Web 11+. A drop-in to current javax/Pax Web 8 runtime fails OSGi resolution. Major migration only.
- **`org.springframework:spring-core`** — osgiCompatible=**no**, breakingRisk=**medium**. `spring.version` bump to 6.2.11 fixes the Maven/scan finding ONLY. The deployed Karaf bundle is the ServiceMix wrapper `org.apache.servicemix.bundles.spring-core` pinned at `6.2.8_1` (pom.xml:357-358), and **no 6.2.9_1/6.2.11_1 ServiceMix bundle exists** (HTTP 404). Runtime stays exposed to CVE-2025-41249 until ServiceMix publishes 6.2.11_x or DDF builds its own wrapper. Do NOT bump `spring.osgi.bundle.version`/`spring.feature.version` to a nonexistent `6.2.11_x`.
- **`org.hibernate:hibernate-core`** — osgiCompatible=**no**, breakingRisk=**high** (for the 6.x path; hibernate-osgi module discontinued in 6.x). Mitigated by drop-unused (low risk) since Hibernate is not actually used by DDF.
- **`org.apache.shiro:shiro-core`** — breakingRisk=**high** (integration). CVE is LOW, but 1.13→2.1 is a breaking major spanning 20+ security modules + OSGi feature re-wiring. Needs full security regression + Karaf feature re-validation.
- **`org.eclipse.jgit`** — breakingRisk=**medium**. Force-upgrade breaks git-hook tooling (see Section 7). Suppress instead.
- **`org.apache.neethi:neethi`** — breakingRisk=**medium** (stale hardcoded 3.2.0 in ddf-cxf-karaf feature.xml:99; "already-fixed" is premature until that literal is fixed). 3.2.2 not yet exercised in a resolved build.
- **`org.apache.zookeeper:zookeeper`** — osgiNote sub-claim false (upstream jar is NOT a native bundle; DDF bnd-rewraps it) — harmless; property bump keeps bnd Export-Package versions consistent.
- **`org.assertj:assertj-core`** — breakingRisk=**medium**. 2.0.0→3.27.7 crosses the 2.x→3.x major boundary for 2 modules; call sites look compatible but no build was run to confirm. Test-only.
- **`com.github.junrar:junrar`** — osgiCompatible=**yes** but note: plain JAR (no OSGi headers), embedded inside the Tika parser bundle — pin is fine.
- **`ca.juliusdavies:not-yet-commons-ssl`** — shaded/embedded, not a bundle — bump the embedded literal.

---

## Prioritized execution order

**P0 — Criticals / one-line property bumps, low risk, high impact:**
1. `${mina.version}` 2.1.10 → **2.2.7** (CRITICAL RCE chain) + restito hard-pin 2.2.4 → 2.2.7.
2. `${tomcat-embed.version}` 9.0.110 → **9.0.118** (3 CRITICAL + multiple HIGH; do NOT stop at 9.0.116/117).
3. `${bouncy.version}` 1.83 → **1.84** (HIGH bcprov + medium bcpkix; whole BC family).
4. `${solr.zookeeper.version}` 3.9.4 → **3.9.5** (2× HIGH).
5. `${netty.version}` 4.1.130 → **4.1.133.Final** (HIGH; covers netty-codec + netty-codec-http2 + family).
6. `${solr.version}` 9.10.0 → **9.10.1** (2× HIGH).

**P1 — HIGH/medium property bumps, low risk:**
7. `${spring.version}` 6.2.8 → **6.2.11** (HIGH) — Maven layer now; **flag OSGi runtime as unremediated** (ServiceMix wrapper unavailable).
8. `${logback.version}`/`logback.classic.version` 1.5.21 → **1.5.25**.
9. `${apache-log4j.version}` 2.25.3 → **2.25.4**.
10. `${commons-configuration2.version}` 2.10.1 → **2.15.0**.
11. `${angus-mail.version}` 2.0.3 → **2.0.4**.
12. neethi: fix stale `3.2.0` literal at ddf-cxf-karaf feature.xml:99 → `${wss4j.neethi.version}`.
13. dep-mgmt pin junrar **7.5.10**; bump not-yet-commons-ssl literal **0.3.15**.

**P2 — low-severity / test-scope / large-but-stable:**
14. `${micrometer.opentelemetry.version}` 1.9.1 → **1.62.0**.
15. assertj-core → **3.27.7** across all 4 modules (+ add property/dep-mgmt).
16. `${xmlunit-matchers.version}` 2.8.2 → **2.10.0**.
17. `${jruby.version}` 9.4.12.0 → **9.4.12.1**.

**P3 — drop-unused / cleanup (low risk):**
18. Drop `hibernate-core` from features/solr/pom.xml.
19. Drop `org.codice.thirdparty:commons-httpclient` from catalog-app.
20. Drop/accept `commons-lang` 2.6 bundle.

**P4 — rescans (no change):** xmlsec, jetty-server, jetty-xml, spring-context, groovy-all, ant, slf4j-ext (keep 2.0.17), jdom2, commons-net, commons-beanutils, logback-classic, jsch.

**P5 — majors / tracked separately:** jetty-http (Pax Web 11/Jetty 12 branch), shiro-core 2.x, commons-configuration v1→v2 code migration, jgit suppression upstream.

---

## Decisions needed

1. **Shiro 1.13 → 2.1 major upgrade** — required (no 1.x backport), but breaking across 20+ security modules + OSGi feature re-wiring for a LOW-severity CVE. Schedule as a dedicated change with full security regression, or accept the LOW CVE and defer?
2. **Spring OSGi runtime gap** — bumping `spring.version` to 6.2.11 does NOT fix the deployed bundle (no `org.apache.servicemix.bundles.spring-core/6.2.11_x` published). Wait for ServiceMix, or build/wrap a local 6.2.11 bundle? Until then runtime remains exposed to CVE-2025-41249 (HIGH).
3. **jetty-http (and the whole Jetty 9.4 EOL line)** — only fix is the Pax Web 8→11 / Jetty 9→12 / javax→jakarta migration on the feature branch. Confirm this security change defers to that branch rather than attempting any 9.4.x bump.
4. **jgit** — confirm reclassification from `drop-unused` to **suppress/false-positive** (already in security spec); the real version fix belongs upstream in codice/ddf-support, not DDF.
5. **Legacy `com.sun.mail:javax.mail/1.6.2`** (ddf-cxf-karaf feature, WSS4J 2.4.x) — same SMTP-injection CVE family, <1.6.8. Bump bundle to 1.6.8 or migrate WSS4J off javax.mail? (Secondary to the Angus bump.)
6. **jdom 1.x (`jdom.bundle.version=1.1_4`)** — separate, currently-unpatchable XXE (same advisory, EOL, no fix) shipped at runtime in the `abdera` feature (catalog-app features.xml:179), pulled by opensearch source/endpoint. Open a distinct finding; decide suppress vs. remove abdera feature.
7. **commons-lang 2.6** — accept the LOW DoS-only CVE, or do the drop-unused (remove from 3 feature.xml files) after confirming no third-party bundle imports `org.apache.commons.lang`?
8. **BC version target** — 1.84 closes all current jdk18on CVEs; latest is 1.85/1.86-era. Stay at minimal 1.84 (recommended, fix floor) or track latest?
