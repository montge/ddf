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
package org.codice.ddf.pax.web.jetty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.eclipse.jetty.server.Server;
import org.junit.Test;

/**
 * Comprehensive security test harness for Eclipse Jetty vulnerabilities.
 *
 * <p>This test suite validates that the Eclipse Jetty version in use addresses critical security
 * vulnerabilities. Jetty is the embedded HTTP server and servlet container used by DDF (via
 * Apache Karaf and Pax Web) for all HTTP/HTTPS endpoints.
 *
 * <p><b>CVE-2023-26048: HTTP Request Smuggling via Content-Length Header</b>
 *
 * <p><b>CVSS Score:</b> 5.3 (MEDIUM) - Network exploitable, requires specific conditions
 *
 * <p><b>Description:</b> Jetty's HTTP/1.1 parser incorrectly handles requests with multiple
 * Content-Length headers or conflicting Content-Length and Transfer-Encoding headers. This
 * enables HTTP request smuggling attacks where:
 * <ul>
 *   <li>Attackers can bypass security controls (authentication, authorization)
 *   <li>Poison web caches to serve malicious content to other users
 *   <li>Hijack user sessions by injecting requests
 *   <li>Access restricted endpoints by smuggling requests past security filters
 * </ul>
 *
 * <p><b>Attack Example:</b>
 * <pre>
 * POST /catalog HTTP/1.1
 * Host: ddf.example.com
 * Content-Length: 10
 * Content-Length: 5
 *
 * [Smuggled Request Hidden Here]
 * </pre>
 *
 * <p><b>Affected Versions:</b> Jetty 9.4.0 to 9.4.50, 10.0.0 to 10.0.13, 11.0.0 to 11.0.13
 *
 * <p><b>Fixed Versions:</b> Jetty &gt;= 9.4.51, &gt;= 10.0.14, &gt;= 11.0.14
 *
 * <p><b>CVE-2023-26049: Cookie Parsing Denial of Service</b>
 *
 * <p><b>CVSS Score:</b> 5.3 (MEDIUM) - Network exploitable, denial of service
 *
 * <p><b>Description:</b> Jetty's cookie parser can be exploited to cause excessive CPU
 * consumption when processing maliciously crafted cookie headers containing large numbers of
 * cookies or extremely long cookie values. This leads to:
 * <ul>
 *   <li>Complete denial of service - server becomes unresponsive
 *   <li>Resource exhaustion affecting all users
 *   <li>Application crashes requiring restart
 *   <li>Timeout of legitimate requests
 * </ul>
 *
 * <p><b>Attack Example:</b>
 * <pre>
 * GET /admin HTTP/1.1
 * Cookie: a=1; b=2; c=3; ... [10,000 cookies]
 * </pre>
 *
 * <p><b>Affected Versions:</b> Jetty 9.4.0 to 9.4.50, 10.0.0 to 10.0.13, 11.0.0 to 11.0.13
 *
 * <p><b>Fixed Versions:</b> Jetty &gt;= 9.4.51, &gt;= 10.0.14, &gt;= 11.0.14
 *
 * <p><b>CVE-2022-2047: URI Parsing Ambiguity Leading to Security Bypass</b>
 *
 * <p><b>CVSS Score:</b> 7.5 (HIGH) - Network exploitable, authentication bypass possible
 *
 * <p><b>Description:</b> Jetty's URI normalization fails to properly handle certain ambiguous
 * URIs with encoded characters, path traversal sequences, and special characters. Attackers can
 * exploit this to:
 * <ul>
 *   <li>Bypass security constraints and access control rules
 *   <li>Access protected resources and admin endpoints
 *   <li>Evade web application firewalls and filters
 *   <li>Read arbitrary files via path traversal
 * </ul>
 *
 * <p><b>Attack Example:</b>
 * <pre>
 * GET /admin/..%2F..%2Fpublic/resource HTTP/1.1
 * </pre>
 * This may bypass security constraints on /admin/* and access the resource.
 *
 * <p><b>Affected Versions:</b> Jetty 9.4.0 to 9.4.46, 10.0.0 to 10.0.9, 11.0.0 to 11.0.9
 *
 * <p><b>Fixed Versions:</b> Jetty &gt;= 9.4.46, &gt;= 10.0.10, &gt;= 11.0.10
 *
 * <p><b>DDF Impact:</b>
 *
 * <p>DDF relies on Jetty (via Pax Web) for:
 * <ul>
 *   <li>Admin Console (HTTPS on port 8993)
 *   <li>REST API endpoints (/services/catalog/*)
 *   <li>Search UI and Intrigue
 *   <li>SAML SSO and OAuth endpoints
 *   <li>Federation and CSW/WFS/WMS services
 * </ul>
 *
 * <p>Without proper Jetty version, DDF is vulnerable to:
 * <ul>
 *   <li>Admin console access bypass via request smuggling
 *   <li>Session hijacking affecting all authenticated users
 *   <li>Denial of service affecting catalog availability
 *   <li>Security constraint bypass allowing unauthorized data access
 * </ul>
 *
 * <p><b>Required Version:</b> Jetty &gt;= 9.4.51
 *
 * <p><b>References:</b>
 * <ul>
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2023-26048
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2023-26049
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-2047
 *   <li>https://github.com/eclipse/jetty.project/security/advisories
 * </ul>
 *
 * @since 2.29.0
 */
public class JettySecurityTest {

  private static final int MINIMUM_MAJOR_VERSION = 9;
  private static final int MINIMUM_MINOR_VERSION = 4;
  private static final int MINIMUM_PATCH_VERSION = 51;

  /**
   * Test: Verify Eclipse Jetty version is at least 9.4.51
   *
   * <p>Validates that the Jetty version addresses:
   * <ul>
   *   <li>CVE-2023-26048 (HTTP request smuggling)
   *   <li>CVE-2023-26049 (Cookie parsing DoS)
   *   <li>CVE-2022-2047 (URI parsing ambiguity)
   * </ul>
   *
   * <p><b>Expected Result:</b> Jetty version &gt;= 9.4.51, 10.0.14, or 11.0.14
   */
  @Test
  public void testJettyVersionIsSecure() {
    String version = Server.getVersion();

    assertThat(
        "Jetty version should not be null - ensure Jetty is on classpath",
        version,
        is(notNullValue()));

    // Jetty version format: "9.4.46.v20220331" or "9.4.51-SNAPSHOT"
    // Extract numeric version before 'v' or '-'
    String numericVersion = version.split("[-v]")[0];

    String[] parts = numericVersion.split("\\.");
    assertThat(
        "Jetty version format should be X.Y.Z",
        parts.length >= 3,
        is(true));

    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);
    int patchVersion = Integer.parseInt(parts[2]);

    // Validate version meets minimum requirements
    boolean isVersionSecure = false;

    if (majorVersion > MINIMUM_MAJOR_VERSION) {
      // Version 10.x or 11.x - check specific minimums
      if (majorVersion == 10) {
        // Jetty 10.x requires >= 10.0.14
        isVersionSecure = minorVersion > 0 || (minorVersion == 0 && patchVersion >= 14);
      } else if (majorVersion == 11) {
        // Jetty 11.x requires >= 11.0.14
        isVersionSecure = minorVersion > 0 || (minorVersion == 0 && patchVersion >= 14);
      } else {
        // Jetty 12.x or higher assumed secure
        isVersionSecure = true;
      }
    } else if (majorVersion == MINIMUM_MAJOR_VERSION) {
      if (minorVersion > MINIMUM_MINOR_VERSION) {
        // Version 9.5.x or higher is secure
        isVersionSecure = true;
      } else if (minorVersion == MINIMUM_MINOR_VERSION
          && patchVersion >= MINIMUM_PATCH_VERSION) {
        // Version 9.4.51 or higher is secure
        isVersionSecure = true;
      }
    }

    String errorMessage = String.format(
        "Jetty version %s is vulnerable to CVE-2023-26048 (Request Smuggling), "
            + "CVE-2023-26049 (Cookie DoS), and CVE-2022-2047 (URI Parsing). "
            + "Minimum required version: 9.4.51, 10.0.14, or 11.0.14\n\n"
            + "CVE-2023-26048 (CVSS 5.3 MEDIUM):\n"
            + "  HTTP request smuggling via Content-Length header manipulation allows bypass of\n"
            + "  security controls, session hijacking, and cache poisoning.\n"
            + "  Attack: Multiple Content-Length headers or CL/TE conflicts\n"
            + "  Fixed in Jetty >= 9.4.51, >= 10.0.14, >= 11.0.14\n\n"
            + "CVE-2023-26049 (CVSS 5.3 MEDIUM):\n"
            + "  Cookie parsing denial of service via excessive cookies or long cookie values\n"
            + "  causes CPU exhaustion and application unresponsiveness.\n"
            + "  Attack: Cookie header with thousands of cookies\n"
            + "  Fixed in Jetty >= 9.4.51, >= 10.0.14, >= 11.0.14\n\n"
            + "CVE-2022-2047 (CVSS 7.5 HIGH):\n"
            + "  URI normalization ambiguity allows security constraint bypass and path traversal.\n"
            + "  Attack: Encoded path traversal sequences in URIs (..%%2F..%%2F)\n"
            + "  Fixed in Jetty >= 9.4.46, >= 10.0.10, >= 11.0.10\n\n"
            + "DDF Impact:\n"
            + "  - Admin Console access bypass via request smuggling\n"
            + "  - Catalog service DoS via cookie flooding\n"
            + "  - Unauthorized access to protected endpoints\n"
            + "  - Session hijacking affecting authenticated users\n\n"
            + "References:\n"
            + "  https://nvd.nist.gov/vuln/detail/CVE-2023-26048\n"
            + "  https://nvd.nist.gov/vuln/detail/CVE-2023-26049\n"
            + "  https://nvd.nist.gov/vuln/detail/CVE-2022-2047",
        version);

    assertThat(errorMessage, isVersionSecure, is(true));
  }

  /**
   * Test: Verify Jetty major version
   *
   * <p>Ensures the major version is at least 9.
   */
  @Test
  public void testJettyMajorVersion() {
    String version = Server.getVersion();
    assertThat("Jetty version should not be null", version, is(notNullValue()));

    String numericVersion = version.split("[-v]")[0];
    String[] parts = numericVersion.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    assertThat(
        "Jetty major version should be >= " + MINIMUM_MAJOR_VERSION,
        majorVersion,
        is(greaterThanOrEqualTo(MINIMUM_MAJOR_VERSION)));
  }

  /**
   * Test: Verify Jetty minor version (when major is 9)
   *
   * <p>Ensures the minor version is at least 4 when major version is 9.
   */
  @Test
  public void testJettyMinorVersion() {
    String version = Server.getVersion();
    assertThat("Jetty version should not be null", version, is(notNullValue()));

    String numericVersion = version.split("[-v]")[0];
    String[] parts = numericVersion.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);

    if (majorVersion == MINIMUM_MAJOR_VERSION) {
      assertThat(
          String.format(
              "Jetty minor version should be >= %d when major version is %d",
              MINIMUM_MINOR_VERSION, MINIMUM_MAJOR_VERSION),
          minorVersion,
          is(greaterThanOrEqualTo(MINIMUM_MINOR_VERSION)));
    }
  }

  /**
   * Test: Verify Jetty patch version (when major is 9 and minor is 4)
   *
   * <p>Ensures the patch version is at least 51 when version is 9.4.x.
   */
  @Test
  public void testJettyPatchVersion() {
    String version = Server.getVersion();
    assertThat("Jetty version should not be null", version, is(notNullValue()));

    String numericVersion = version.split("[-v]")[0];
    String[] parts = numericVersion.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);
    int minorVersion = Integer.parseInt(parts[1]);
    int patchVersion = Integer.parseInt(parts[2]);

    if (majorVersion == MINIMUM_MAJOR_VERSION && minorVersion == MINIMUM_MINOR_VERSION) {
      assertThat(
          String.format(
              "Jetty patch version should be >= %d when version is %d.%d.x to address "
                  + "CVE-2023-26048, CVE-2023-26049, and CVE-2022-2047",
              MINIMUM_PATCH_VERSION, MINIMUM_MAJOR_VERSION, MINIMUM_MINOR_VERSION),
          patchVersion,
          is(greaterThanOrEqualTo(MINIMUM_PATCH_VERSION)));
    }
  }

  /**
   * Test: Verify Jetty 10.x version requirements
   *
   * <p>If using Jetty 10.x, ensures version is >= 10.0.14.
   */
  @Test
  public void testJetty10Requirements() {
    String version = Server.getVersion();
    assertThat("Jetty version should not be null", version, is(notNullValue()));

    String numericVersion = version.split("[-v]")[0];
    String[] parts = numericVersion.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    if (majorVersion == 10) {
      int minorVersion = Integer.parseInt(parts[1]);
      int patchVersion = Integer.parseInt(parts[2]);

      boolean isSecure = minorVersion > 0 || (minorVersion == 0 && patchVersion >= 14);

      assertThat(
          String.format(
              "Jetty 10.x version %s must be >= 10.0.14 to address security vulnerabilities",
              version),
          isSecure,
          is(true));
    }
  }

  /**
   * Test: Verify Jetty 11.x version requirements
   *
   * <p>If using Jetty 11.x, ensures version is >= 11.0.14.
   */
  @Test
  public void testJetty11Requirements() {
    String version = Server.getVersion();
    assertThat("Jetty version should not be null", version, is(notNullValue()));

    String numericVersion = version.split("[-v]")[0];
    String[] parts = numericVersion.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    if (majorVersion == 11) {
      int minorVersion = Integer.parseInt(parts[1]);
      int patchVersion = Integer.parseInt(parts[2]);

      boolean isSecure = minorVersion > 0 || (minorVersion == 0 && patchVersion >= 14);

      assertThat(
          String.format(
              "Jetty 11.x version %s must be >= 11.0.14 to address security vulnerabilities",
              version),
          isSecure,
          is(true));
    }
  }
}
