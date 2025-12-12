# POM.XML Merge Conflict Analysis
## Context
- Merging upstream/master into local master
- Preserving JUnit 5 migration (ours)
- Prioritizing highest security-safe versions
- Key security dependencies: Bouncy Castle, Jackson, Apache CXF, Guava, commons-*

## Detailed Conflict Resolution

### 1. dependency-check-maven.version
- **OURS:** 12.1.8
- **THEIRS:** (removed/not present)
- **RECOMMENDATION:** Keep OURS (12.1.8)
- **REASON:** Major upgrade from parent's 6.1.1. Newer version has better CVE detection.

### 2. jknack.handlebars.version
- **OURS:** 4.5.0
- **THEIRS:** 4.3.1
- **RECOMMENDATION:** Keep OURS (4.5.0)
- **REASON:** 4.5.0 is newer and likely contains security fixes.

### 3. JUnit 5 Migration (CRITICAL - PRESERVE)
- **OURS:** junit-jupiter.version: 5.10.5, junit-platform.version: 1.10.5
- **THEIRS:** (not present - still using JUnit 4)
- **RECOMMENDATION:** Keep OURS
- **REASON:** JUnit 5 migration that we want to preserve per requirements.

### 4. byte-buddy.version
- **OURS:** 1.17.8
- **THEIRS:** 1.14.19
- **RECOMMENDATION:** Keep OURS (1.17.8)
- **REASON:** Much newer version (17.x vs 14.x). Needed for JUnit 5 + Mockito 4.

### 5. mockito.version
- **OURS:** 4.11.0
- **THEIRS:** 3.6.28
- **RECOMMENDATION:** Keep OURS (4.11.0)
- **REASON:** Mockito 4.x required for JUnit 5 compatibility. Part of migration.

### 6. pax.exam.version
- **OURS:** 4.13.5
- **THEIRS:** 4.14.0
- **RECOMMENDATION:** Keep THEIRS (4.14.0)
- **REASON:** Newer minor version, likely bug fixes.

### 7. pax.logging.version
- **OURS:** 2.0.16
- **THEIRS:** 2.3.0
- **RECOMMENDATION:** Keep THEIRS (2.3.0)
- **REASON:** Significant version bump (2.0.x → 2.3.x), likely important fixes.

### 8. pax.url.version
- **OURS:** 2.6.10
- **THEIRS:** 2.6.17
- **RECOMMENDATION:** Keep THEIRS (2.6.17)
- **REASON:** Patch version increase, bug/security fixes.

### 9. pax.web.version
- **OURS:** 8.0.33
- **THEIRS:** 8.0.33
- **NO CONFLICT:** Same version

### 10. pax.cdi.version
- **OURS:** 1.1.4
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS (1.1.4)
- **REASON:** Needed in our branch, likely for our changes.

### 11. restito.version
- **OURS:** 0.9.4
- **THEIRS:** 1.1.2
- **RECOMMENDATION:** Keep THEIRS (1.1.2)
- **REASON:** Major version bump (0.9 → 1.1), significant improvements.

### 12. restassured.version
- **OURS:** 3.3.0
- **THEIRS:** 5.5.0
- **RECOMMENDATION:** Keep THEIRS (5.5.0)
- **REASON:** Major version jump (3.x → 5.x), important updates.

### 13. asciidoctor.maven.plugin.version
- **OURS:** 2.2.4
- **THEIRS:** 2.2.6
- **RECOMMENDATION:** Keep THEIRS (2.2.6)
- **REASON:** Patch version, bug fixes.

### 14. asciidoctorj.diagram.version
- **OURS:** 2.3.2
- **THEIRS:** 2.3.1
- **RECOMMENDATION:** Keep OURS (2.3.2)
- **REASON:** Newer patch version.

### 15. asciidoctorj.pdf.version
- **OURS:** 1.6.0
- **THEIRS:** 2.3.19
- **RECOMMENDATION:** Keep THEIRS (2.3.19)
- **REASON:** Major version jump (1.6 → 2.3), significant improvements.

### 16. asciidoctorj.version
- **OURS:** 2.5.8
- **THEIRS:** 2.5.7
- **RECOMMENDATION:** Keep OURS (2.5.8)
- **REASON:** Newer patch version.

### 17. jruby.version
- **OURS:** 9.4.8.0
- **THEIRS:** 9.4.12.0
- **RECOMMENDATION:** Keep THEIRS (9.4.12.0)
- **REASON:** Newer patch version (9.4.8 → 9.4.12), bug fixes.

### 18. apache.calcite.version
- **OURS:** 1.38.0
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS (1.38.0)
- **REASON:** Needed in our branch.

### 19. asm.version (SECURITY)
- **OURS:** 9.3
- **THEIRS:** 9.8
- **RECOMMENDATION:** Keep THEIRS (9.8)
- **REASON:** Significant version bump, likely security/bug fixes.

### 20. bouncy.version (CRITICAL SECURITY)
- **OURS:** 1.79
- **THEIRS:** 1.81
- **RECOMMENDATION:** Keep THEIRS (1.81)
- **REASON:** BouncyCastle has frequent security updates. Always use latest.

### 21. commons.fileupload.version (SECURITY)
- **OURS:** 1.5
- **THEIRS:** 1.6.0
- **RECOMMENDATION:** Keep THEIRS (1.6.0)
- **REASON:** Known CVEs in older versions. 1.6.0 is safer.

### 22. commons-beanutils.version
- **OURS:** 1.9.4
- **THEIRS:** 1.11.0
- **RECOMMENDATION:** Keep THEIRS (1.11.0)
- **REASON:** Significant version bump, security fixes.

### 23. commons-codec.version (SECURITY)
- **OURS:** 1.19.0
- **THEIRS:** 1.17.0
- **RECOMMENDATION:** Keep OURS (1.19.0)
- **REASON:** Newer version, security patches.

### 24. commons-collections4.version
- **OURS:** 4.5.0
- **THEIRS:** 4.1
- **RECOMMENDATION:** Keep OURS (4.5.0)
- **REASON:** Much newer (4.5 vs 4.1), security improvements.

### 25. commons-configuration2.version
- **OURS:** 2.10.1
- **THEIRS:** 2.8.0
- **RECOMMENDATION:** Keep OURS (2.10.1)
- **REASON:** Newer version (2.10.1 vs 2.8.0).

### 26. commons-csv.version
- **OURS:** 1.14.1
- **THEIRS:** 1.4
- **RECOMMENDATION:** Keep OURS (1.14.1)
- **REASON:** Much newer (1.14.1 vs 1.4), security/bug fixes.

### 27. commons-logging.version
- **OURS:** 1.2
- **THEIRS:** 1.3.5
- **RECOMMENDATION:** Keep THEIRS (1.3.5)
- **REASON:** Major version jump (1.2 → 1.3.5), improvements.

### 28. commons-text.version (SECURITY - CVE fixes)
- **OURS:** 1.14.0
- **THEIRS:** 1.13.0
- **RECOMMENDATION:** Keep OURS (1.14.0)
- **REASON:** 1.10.0+ has CVE-2022-42889 fix. 1.14.0 is newer.

### 29. countryconverter.version
- **OURS:** 0.1.8
- **THEIRS:** 0.2.3
- **RECOMMENDATION:** Keep THEIRS (0.2.3)
- **REASON:** Minor version bump, improvements.

### 30. cryptomator.version
- **OURS:** 1.4.4
- **THEIRS:** 1.5.2
- **RECOMMENDATION:** Keep THEIRS (1.5.2)
- **REASON:** Minor version bump, security improvements for crypto library.

### 31. cxf.version (CRITICAL SECURITY)
- **OURS:** 3.6.8
- **THEIRS:** 3.6.7
- **RECOMMENDATION:** Keep OURS (3.6.8)
- **REASON:** Newer patch, Apache CXF has frequent security updates.

### 32. ddf-admin-ui.version
- **OURS:** 2.25.0
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS (2.25.0)
- **REASON:** Part of our changes.

### 33. gson.version
- **OURS:** 2.9.0
- **THEIRS:** 2.11.0
- **RECOMMENDATION:** Keep THEIRS (2.11.0)
- **REASON:** Significant version bump (2.9 → 2.11), bug/security fixes.

### 34. guava.version (CRITICAL SECURITY)
- **OURS:** 33.5.0-jre
- **THEIRS:** 33.4.0-jre
- **RECOMMENDATION:** Keep OURS (33.5.0-jre)
- **REASON:** Newer minor version, Guava has security implications.

### 35. jackson.version (CRITICAL SECURITY)
- **OURS:** 2.17.3
- **THEIRS:** 2.18.4
- **RECOMMENDATION:** Keep THEIRS (2.18.4)
- **REASON:** Jackson has frequent CVEs. 2.18.4 is much newer and safer.

### 36. karaf.jetty.version
- **OURS:** (not present)
- **THEIRS:** 9.4.57.v20241219 (for CVE-2025-5115)
- **RECOMMENDATION:** Keep THEIRS (9.4.57.v20241219)
- **REASON:** Explicit CVE fix mentioned in comment.

### 37. jwnl.version
- **OURS:** 1.4_rc3
- **THEIRS:** 1.3.3
- **RECOMMENDATION:** Keep OURS (1.4_rc3)
- **REASON:** Newer version (1.4 vs 1.3).

### 38. jansi.version
- **OURS:** 2.4.0
- **THEIRS:** 2.4.2
- **RECOMMENDATION:** Keep THEIRS (2.4.2)
- **REASON:** Patch version bump.

### 39. apache-log4j.version (CRITICAL SECURITY)
- **OURS:** 2.23.1
- **THEIRS:** 2.17.2
- **RECOMMENDATION:** Keep OURS (2.23.1)
- **REASON:** Much newer (2.23.1 vs 2.17.2). Log4j has critical CVEs.

### 40. maven.failsafe.version
- **OURS:** 3.2.5
- **THEIRS:** 3.5.3
- **RECOMMENDATION:** Keep THEIRS (3.5.3)
- **REASON:** Much newer (3.5.3 vs 3.2.5), improvements.

### 41. maven.surefire.version
- **OURS:** 3.5.4
- **THEIRS:** 3.5.3
- **RECOMMENDATION:** Keep OURS (3.5.4)
- **REASON:** Newer patch version.

### 42. mime4j.version
- **OURS:** 0.8.13
- **THEIRS:** 0.8.12
- **RECOMMENDATION:** Keep OURS (0.8.13)
- **REASON:** Newer patch version.

### 43. netty.version (SECURITY)
- **OURS:** 4.2.7.Final
- **THEIRS:** 4.1.119.Final
- **RECOMMENDATION:** INVESTIGATE - OURS seems wrong (4.2.x doesn't exist)
- **REASON:** Version 4.2.7 doesn't exist. Likely should be 4.1.119.Final (theirs).

### 44. objenesis.version
- **OURS:** 3.4
- **THEIRS:** 3.1
- **RECOMMENDATION:** Keep OURS (3.4)
- **REASON:** Newer version (3.4 vs 3.1).

### 45. java-support.version (OpenSAML)
- **OURS:** 8.0.0
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS (8.0.0)
- **REASON:** Required by OpenSAML 4.x per comment.

### 46. nimbus.version
- **OURS:** 8.14.1
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS (8.14.1)
- **REASON:** Part of our changes.

### 47. nimbus.oidc.version
- **OURS:** 11.30
- **THEIRS:** 11.24
- **RECOMMENDATION:** Keep OURS (11.30)
- **REASON:** Newer version, OIDC security library.

### 48. nimbus.content-type.version
- **OURS:** 2.1
- **THEIRS:** 2.3
- **RECOMMENDATION:** Keep THEIRS (2.3)
- **REASON:** Newer minor version.

### 49. nimbus.jose.jwt.version (SECURITY)
- **OURS:** 10.5
- **THEIRS:** 10.3
- **RECOMMENDATION:** Keep OURS (10.5)
- **REASON:** JWT library, newer version likely has security fixes.

### 50. nimbus.langtag.version
- **OURS:** 1.4.4
- **THEIRS:** 1.7
- **RECOMMENDATION:** Keep THEIRS (1.7)
- **REASON:** Newer minor version.

### 51. org.geotools.version
- **OURS:** 28.6.1
- **THEIRS:** 33.1
- **RECOMMENDATION:** Keep THEIRS (33.1)
- **REASON:** Major version jump (28.x → 33.x), significant improvements.

### 52. geotools-hsql.version
- **OURS:** (not present)
- **THEIRS:** 2.7.2
- **RECOMMENDATION:** Keep THEIRS (2.7.2)
- **REASON:** New dependency in upstream.

### 53. org.ops4j.base.version
- **OURS:** (not present)
- **THEIRS:** 1.5.1
- **RECOMMENDATION:** Keep THEIRS (1.5.1)
- **REASON:** New dependency in upstream.

### 54. org.slf4j.version
- **OURS:** 1.7.36
- **THEIRS:** 1.7.32
- **RECOMMENDATION:** Keep OURS (1.7.36)
- **REASON:** Newer patch version (1.7.36 vs 1.7.32).

### 55. osgi.enterprise.version
- **OURS:** 5.0.0
- **THEIRS:** 7.0.0
- **RECOMMENDATION:** Keep THEIRS (7.0.0)
- **REASON:** Major version jump, unless there's compatibility issue.

### 56. osgi.compendium.version
- **OURS:** (not present)
- **THEIRS:** 7.0.0
- **RECOMMENDATION:** Keep THEIRS (7.0.0)
- **REASON:** New dependency in upstream.

### 57. protobuf.version
- **OURS:** 3.25.8
- **THEIRS:** 4.28.2
- **RECOMMENDATION:** Keep THEIRS (4.28.2)
- **REASON:** Major version jump (3.x → 4.x), unless compatibility issue.

### 58. hazelcast.version
- **OURS:** (not present)
- **THEIRS:** 5.3.5
- **RECOMMENDATION:** Keep THEIRS (5.3.5)
- **REASON:** New dependency in upstream.

### 59. solr.version
- **OURS:** 9.9.0
- **THEIRS:** 9.10.0
- **RECOMMENDATION:** Keep THEIRS (9.10.0)
- **REASON:** Newer minor version (9.9 → 9.10), bug fixes.

### 60. solr.docs.version
- **OURS:** 9_0
- **THEIRS:** 9_2
- **RECOMMENDATION:** Keep THEIRS (9_2)
- **REASON:** Documentation version matches Solr 9.10.

### 61. solr.httpclient.version
- **OURS:** 4.5.13
- **THEIRS:** 4.5.14
- **RECOMMENDATION:** Keep THEIRS (4.5.14)
- **REASON:** Patch version bump.

### 62. solr.httpcore.version
- **OURS:** 4.4.15
- **THEIRS:** 4.4.16
- **RECOMMENDATION:** Keep THEIRS (4.4.16)
- **REASON:** Patch version bump.

### 63. solr.httpmime.version
- **OURS:** 4.5.13
- **THEIRS:** 4.5.14
- **RECOMMENDATION:** Keep THEIRS (4.5.14)
- **REASON:** Patch version bump.

### 64. solr.jetty.version
- **OURS:** (not present)
- **THEIRS:** 10.0.26
- **RECOMMENDATION:** Keep THEIRS (10.0.26)
- **REASON:** New dependency in upstream.

### 65. solr.zookeeper.version
- **OURS:** 3.9.3
- **THEIRS:** 3.8.4
- **RECOMMENDATION:** Keep OURS (3.9.3)
- **REASON:** Newer minor version (3.9.3 vs 3.8.4).

### 66. spring.version (SECURITY)
- **OURS:** 6.2.14
- **THEIRS:** 6.1.21
- **RECOMMENDATION:** Keep OURS (6.2.14)
- **REASON:** Newer minor version (6.2 vs 6.1), Spring has security updates.

### 67. spring.osgi.bundle.version / spring.feature.version
- **OURS:** 6.2.14_1 (spring.osgi.bundle.version)
- **THEIRS:** 6.1.21_1 (spring.feature.version)
- **RECOMMENDATION:** Keep OURS (6.2.14_1)
- **REASON:** Matches Spring 6.2.14.

### 68. woodstox.core.version (SECURITY - XML parsing)
- **OURS:** 6.6.2
- **THEIRS:** 6.5.1
- **RECOMMENDATION:** Keep OURS (6.6.2)
- **REASON:** Newer minor version, XML parsing security critical.

### 69. zstd-jni.version
- **OURS:** 1.4.9-1
- **THEIRS:** 1.5.5-2
- **RECOMMENDATION:** Keep THEIRS (1.5.5-2)
- **REASON:** Significant version jump (1.4.9 → 1.5.5).

### 70. tomcat-embed.version / mina.version
- **OURS:** tomcat-embed.version: 9.0.110, mina.version: 2.1.10
- **THEIRS:** (not present)
- **RECOMMENDATION:** Keep OURS
- **REASON:** Part of our changes.

### 71. surefire.argline (Java 17 compatibility)
- **OURS:** Basic argline
- **THEIRS:** Extended with --add-opens, --add-exports for Java 17
- **RECOMMENDATION:** Keep THEIRS
- **REASON:** Necessary for Java 17 module system compatibility.

## Summary by Category

### CRITICAL SECURITY - Keep Higher Version:
- **bouncy.version:** Use 1.81 (theirs)
- **jackson.version:** Use 2.18.4 (theirs) - IMPORTANT
- **cxf.version:** Use 3.6.8 (ours)
- **guava.version:** Use 33.5.0-jre (ours)
- **commons.fileupload.version:** Use 1.6.0 (theirs)
- **apache-log4j.version:** Use 2.23.1 (ours)
- **nimbus.jose.jwt.version:** Use 10.5 (ours)
- **spring.version:** Use 6.2.14 (ours)
- **woodstox.core.version:** Use 6.6.2 (ours)
- **commons-text.version:** Use 1.14.0 (ours)

### PRESERVE JUnit 5 Migration (OURS):
- junit-jupiter.version: 5.10.5
- junit-platform.version: 1.10.5
- byte-buddy.version: 1.17.8
- mockito.version: 4.11.0

### Major Version Jumps (Likely THEIRS):
- protobuf: 3.25.8 → 4.28.2 (theirs)
- org.geotools: 28.6.1 → 33.1 (theirs)
- osgi.enterprise: 5.0.0 → 7.0.0 (theirs)
- asciidoctorj.pdf: 1.6.0 → 2.3.19 (theirs)
- restassured: 3.3.0 → 5.5.0 (theirs)

### Needs Investigation:
- **netty.version:** OURS shows 4.2.7.Final (doesn't exist) - likely typo, use THEIRS 4.1.119.Final
