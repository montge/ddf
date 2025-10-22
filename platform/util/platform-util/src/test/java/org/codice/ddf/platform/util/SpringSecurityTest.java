/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.platform.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import org.springframework.core.SpringVersion;

/**
 * Comprehensive security test harness for Spring Framework vulnerabilities.
 *
 * <p>This test suite validates that the Spring Framework version in use addresses critical security
 * vulnerabilities. Spring is used throughout DDF for dependency injection, web services,
 * transaction management, and various utility functions.
 *
 * <p><b>CVE-2022-22965: Spring4Shell - Remote Code Execution via Data Binding</b>
 *
 * <p><b>CVSS Score:</b> 9.8 (CRITICAL) - Network exploitable, no authentication required
 *
 * <p><b>Description:</b> A critical remote code execution vulnerability in Spring's data binding
 * mechanism (similar to Log4Shell). When running on JDK 9+, attackers can exploit parameter binding
 * to gain access to the ClassLoader and modify Tomcat/Jetty logging configuration, leading to:
 *
 * <ul>
 *   <li>Arbitrary code execution via malicious web requests
 *   <li>Complete system compromise with no authentication
 *   <li>Writing web shells to arbitrary filesystem locations
 *   <li>Persistent backdoor installation
 * </ul>
 *
 * <p><b>Attack Example:</b>
 *
 * <pre>
 * POST /catalog HTTP/1.1
 * Content-Type: application/x-www-form-urlencoded
 *
 * class.module.classLoader.resources.context.parent.pipeline.first.pattern=%{...}
 * class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp
 * class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/ROOT
 * class.module.classLoader.resources.context.parent.pipeline.first.prefix=shell
 * class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat=
 * </pre>
 *
 * This writes a JSP web shell to the filesystem.
 *
 * <p><b>Affected Versions:</b> Spring Framework 5.3.0 to 5.3.17, 5.2.0 to 5.2.19
 *
 * <p><b>Fixed Versions:</b> Spring Framework &gt;= 5.3.18 or &gt;= 5.2.20
 *
 * <p><b>CVE-2023-20861: Spring Expression Language (SpEL) Injection</b>
 *
 * <p><b>CVSS Score:</b> 8.8 (HIGH) - Network exploitable, authentication may be required
 *
 * <p><b>Description:</b> Spring Expression Language (SpEL) injection vulnerability in Spring's data
 * binding allows authenticated users to execute arbitrary code via crafted SpEL expressions.
 * Attackers can:
 *
 * <ul>
 *   <li>Execute system commands via Runtime.getRuntime().exec()
 *   <li>Access and modify internal application state
 *   <li>Bypass security controls and access restrictions
 *   <li>Read sensitive files and environment variables
 * </ul>
 *
 * <p><b>Attack Example:</b>
 *
 * <pre>
 * GET /search?query=#{T(java.lang.Runtime).getRuntime().exec('whoami')}
 * </pre>
 *
 * <p><b>Affected Versions:</b> Spring Framework &lt; 5.3.27, 6.0.0 to 6.0.8
 *
 * <p><b>Fixed Versions:</b> Spring Framework &gt;= 5.3.27 or &gt;= 6.0.9
 *
 * <p><b>CVE-2023-20863: Path Traversal in Spring's Resource Handling</b>
 *
 * <p><b>CVSS Score:</b> 7.5 (HIGH) - Network exploitable, no authentication required
 *
 * <p><b>Description:</b> Path traversal vulnerability in Spring's resource handling allows
 * attackers to access files outside the intended directory via specially crafted URLs with encoded
 * path traversal sequences. This enables:
 *
 * <ul>
 *   <li>Reading arbitrary files from the server filesystem
 *   <li>Accessing sensitive configuration files and credentials
 *   <li>Source code disclosure
 *   <li>Information gathering for further attacks
 * </ul>
 *
 * <p><b>Attack Example:</b>
 *
 * <pre>
 * GET /resources/static/..%5c..%5c..%5cetc%5cpasswd HTTP/1.1
 * </pre>
 *
 * <p><b>Affected Versions:</b> Spring Framework 5.3.0 to 5.3.26, 6.0.0 to 6.0.7
 *
 * <p><b>Fixed Versions:</b> Spring Framework &gt;= 5.3.27 or &gt;= 6.0.8
 *
 * <p><b>DDF Impact:</b>
 *
 * <p>DDF uses Spring Framework for:
 *
 * <ul>
 *   <li>Dependency injection and bean management (Spring Context)
 *   <li>Web service clients and REST endpoints (Spring Web)
 *   <li>Transaction management (Spring TX)
 *   <li>Data access and JDBC utilities (Spring JDBC)
 *   <li>Security configuration (integration with Spring Security concepts)
 * </ul>
 *
 * <p>Without proper Spring version, DDF is vulnerable to:
 *
 * <ul>
 *   <li>Remote code execution via Spring4Shell (CVE-2022-22965)
 *   <li>Code execution via SpEL injection in search and metadata fields
 *   <li>Configuration file disclosure via path traversal
 *   <li>Complete system compromise with no authentication required
 * </ul>
 *
 * <p><b>Required Version:</b> Spring Framework &gt;= 5.3.27
 *
 * <p><b>References:</b>
 *
 * <ul>
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-22965
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2023-20861
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2023-20863
 *   <li>https://spring.io/security/cve-2022-22965
 *   <li>https://tanzu.vmware.com/security/cve-2023-20861
 * </ul>
 *
 * @since 2.29.0
 */
public class SpringSecurityTest {

  private static final int MINIMUM_MAJOR_VERSION = 5;
  private static final int MINIMUM_MINOR_VERSION = 3;
  private static final int MINIMUM_PATCH_VERSION = 27;

  /**
   * Test: Verify Spring Framework version is at least 5.3.27
   *
   * <p>Validates that the Spring Framework version addresses:
   *
   * <ul>
   *   <li>CVE-2022-22965 (Spring4Shell RCE)
   *   <li>CVE-2023-20861 (SpEL injection)
   *   <li>CVE-2023-20863 (Path traversal)
   * </ul>
   *
   * <p><b>Expected Result:</b> Spring version &gt;= 5.3.27 or &gt;= 6.0.9
   */
  @Test
  public void testSpringVersionIsSecure() {
    String version = SpringVersion.getVersion();

    assertThat(
        "Spring Framework version should not be null - ensure Spring is on classpath",
        version,
        is(notNullValue()));

    // Parse version string (format: "5.3.14" or "5.3.27-SNAPSHOT")
    String[] parts = version.split("\\.");
    assertThat("Spring version format should be X.Y.Z", parts.length >= 3, is(true));

    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);

    // Handle patch version which may contain "-SNAPSHOT" or other suffixes
    String patchPart = parts[2].split("-")[0];
    int patchVersion = Integer.parseInt(patchPart);

    // Validate version meets minimum requirements
    boolean isVersionSecure = false;

    if (majorVersion > MINIMUM_MAJOR_VERSION) {
      // Version 6.x or higher
      if (majorVersion == 6) {
        // Spring 6.0.x requires >= 6.0.9
        if (minorVersion > 0 || (minorVersion == 0 && patchVersion >= 9)) {
          isVersionSecure = true;
        }
      } else {
        // Spring 7.x or higher assumed secure
        isVersionSecure = true;
      }
    } else if (majorVersion == MINIMUM_MAJOR_VERSION) {
      if (minorVersion > MINIMUM_MINOR_VERSION) {
        // Version 5.4.x or higher is secure
        isVersionSecure = true;
      } else if (minorVersion == MINIMUM_MINOR_VERSION && patchVersion >= MINIMUM_PATCH_VERSION) {
        // Version 5.3.27 or higher is secure
        isVersionSecure = true;
      }
    }

    String errorMessage =
        String.format(
            "Spring Framework version %s is vulnerable to CVE-2022-22965 (Spring4Shell), "
                + "CVE-2023-20861 (SpEL Injection), and CVE-2023-20863 (Path Traversal). "
                + "Minimum required version: 5.3.27 or 6.0.9\n\n"
                + "CVE-2022-22965 - Spring4Shell (CVSS 9.8 CRITICAL):\n"
                + "  Remote Code Execution via parameter binding and ClassLoader manipulation.\n"
                + "  Allows attackers to write web shells and execute arbitrary code.\n"
                + "  Attack: POST with class.module.classLoader.* parameters\n"
                + "  Fixed in Spring >= 5.3.18, >= 5.2.20\n\n"
                + "CVE-2023-20861 - SpEL Injection (CVSS 8.8 HIGH):\n"
                + "  Code execution via Spring Expression Language injection in data binding.\n"
                + "  Allows execution of system commands and access to internal state.\n"
                + "  Attack: #{T(java.lang.Runtime).getRuntime().exec('cmd')}\n"
                + "  Fixed in Spring >= 5.3.27, >= 6.0.9\n\n"
                + "CVE-2023-20863 - Path Traversal (CVSS 7.5 HIGH):\n"
                + "  Access to arbitrary files via encoded path traversal in resource URLs.\n"
                + "  Allows reading sensitive files and configuration disclosure.\n"
                + "  Attack: /resources/..%%5c..%%5c..%%5cetc%%5cpasswd\n"
                + "  Fixed in Spring >= 5.3.27, >= 6.0.8\n\n"
                + "DDF Impact:\n"
                + "  - Remote code execution via Spring4Shell (no auth required)\n"
                + "  - Code execution via SpEL in search queries and metadata\n"
                + "  - Configuration file disclosure via path traversal\n"
                + "  - Complete system compromise possible\n\n"
                + "References:\n"
                + "  https://nvd.nist.gov/vuln/detail/CVE-2022-22965\n"
                + "  https://nvd.nist.gov/vuln/detail/CVE-2023-20861\n"
                + "  https://nvd.nist.gov/vuln/detail/CVE-2023-20863\n"
                + "  https://spring.io/security/cve-2022-22965",
            version);

    assertThat(errorMessage, isVersionSecure, is(true));
  }

  /**
   * Test: Verify Spring Framework major version
   *
   * <p>Ensures the major version is at least 5.
   */
  @Test
  public void testSpringMajorVersion() {
    String version = SpringVersion.getVersion();
    assertThat("Spring version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    assertThat(
        "Spring Framework major version should be >= " + MINIMUM_MAJOR_VERSION,
        majorVersion,
        is(greaterThanOrEqualTo(MINIMUM_MAJOR_VERSION)));
  }

  /**
   * Test: Verify Spring Framework minor version (when major is 5)
   *
   * <p>Ensures the minor version is at least 3 when major version is 5.
   */
  @Test
  public void testSpringMinorVersion() {
    String version = SpringVersion.getVersion();
    assertThat("Spring version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);

    if (majorVersion == MINIMUM_MAJOR_VERSION) {
      assertThat(
          String.format(
              "Spring Framework minor version should be >= %d when major version is %d",
              MINIMUM_MINOR_VERSION, MINIMUM_MAJOR_VERSION),
          minorVersion,
          is(greaterThanOrEqualTo(MINIMUM_MINOR_VERSION)));
    }
  }

  /**
   * Test: Verify Spring Framework patch version (when major is 5 and minor is 3)
   *
   * <p>Ensures the patch version is at least 27 when version is 5.3.x.
   */
  @Test
  public void testSpringPatchVersion() {
    String version = SpringVersion.getVersion();
    assertThat("Spring version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);
    String patchPart = parts[2].split("-")[0];
    int patchVersion = Integer.parseInt(patchPart);

    if (majorVersion == MINIMUM_MAJOR_VERSION && minorVersion == MINIMUM_MINOR_VERSION) {
      assertThat(
          String.format(
              "Spring Framework patch version should be >= %d when version is %d.%d.x to address "
                  + "CVE-2022-22965 (Spring4Shell), CVE-2023-20861 (SpEL), and CVE-2023-20863 (Path Traversal)",
              MINIMUM_PATCH_VERSION, MINIMUM_MAJOR_VERSION, MINIMUM_MINOR_VERSION),
          patchVersion,
          is(greaterThanOrEqualTo(MINIMUM_PATCH_VERSION)));
    }
  }

  /**
   * Test: Verify Spring 6.x version requirements
   *
   * <p>If using Spring 6.x, ensures version is >= 6.0.9.
   */
  @Test
  public void testSpring6Requirements() {
    String version = SpringVersion.getVersion();
    assertThat("Spring version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    if (majorVersion == 6) {
      int minorVersion = Integer.parseInt(parts[1]);
      String patchPart = parts[2].split("-")[0];
      int patchVersion = Integer.parseInt(patchPart);

      boolean isSecure = minorVersion > 0 || (minorVersion == 0 && patchVersion >= 9);

      assertThat(
          String.format(
              "Spring Framework 6.x version %s must be >= 6.0.9 to address CVE-2023-20861 and CVE-2023-20863",
              version),
          isSecure,
          is(true));
    }
  }

  /**
   * Test: Verify Spring4Shell (CVE-2022-22965) mitigation
   *
   * <p>Spring4Shell was fixed in 5.3.18 and 5.2.20. This test ensures we're well beyond those
   * versions with the more comprehensive 5.3.27 requirement.
   */
  @Test
  public void testSpring4ShellMitigation() {
    String version = SpringVersion.getVersion();
    assertThat("Spring version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);
    String patchPart = parts[2].split("-")[0];
    int patchVersion = Integer.parseInt(patchPart);

    // Spring4Shell fixed in 5.3.18, but we require 5.3.27 for other CVEs
    boolean isProtected = false;

    if (majorVersion > 5) {
      isProtected = true;
    } else if (majorVersion == 5) {
      if (minorVersion > 3) {
        isProtected = true;
      } else if (minorVersion == 3 && patchVersion >= 18) {
        isProtected = true;
      } else if (minorVersion == 2 && patchVersion >= 20) {
        isProtected = true;
      }
    }

    assertThat(
        String.format(
            "Spring Framework version %s must be >= 5.3.18 or >= 5.2.20 to mitigate "
                + "CVE-2022-22965 (Spring4Shell RCE)",
            version),
        isProtected,
        is(true));
  }
}
