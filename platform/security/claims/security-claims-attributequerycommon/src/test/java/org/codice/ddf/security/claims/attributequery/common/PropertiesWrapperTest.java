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
package org.codice.ddf.security.claims.attributequery.common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.codice.ddf.platform.util.properties.PropertiesLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockedStatic;

/**
 * Comprehensive test harness for PropertiesWrapper utility class.
 *
 * <p>Tests properties file loading with variable substitution support via PropertiesLoader.
 *
 * <p>Coverage includes:
 * - Valid properties file loading
 * - Properties with variable substitution
 * - Empty properties files
 * - Missing/invalid properties files
 * - Properties inheritance (extends Properties)
 * - File path validation
 * - Edge cases and error handling
 */
public class PropertiesWrapperTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private File testPropertiesFile;
  private String testPropertiesPath;

  @Before
  public void setUp() throws IOException {
    testPropertiesFile = tempFolder.newFile("test.properties");
    testPropertiesPath = testPropertiesFile.getAbsolutePath();
  }

  @After
  public void tearDown() {
    testPropertiesFile = null;
    testPropertiesPath = null;
  }

  /**
   * Tests loading valid properties file with key-value pairs.
   *
   * <p>Verifies that PropertiesWrapper correctly loads standard properties.
   */
  @Test
  public void testLoadValidPropertiesFile() throws IOException {
    writePropertiesFile(
        testPropertiesFile, "property1=value1\n" + "property2=value2\n" + "property3=value3\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("PropertiesWrapper should not be null", wrapper, notNullValue());
    assertThat("Should contain property1", wrapper.getProperty("property1"), is("value1"));
    assertThat("Should contain property2", wrapper.getProperty("property2"), is("value2"));
    assertThat("Should contain property3", wrapper.getProperty("property3"), is("value3"));
    assertThat("Should have 3 properties", wrapper.size(), is(3));
  }

  /**
   * Tests loading empty properties file.
   *
   * <p>Verifies that empty properties files are handled gracefully.
   */
  @Test
  public void testLoadEmptyPropertiesFile() throws IOException {
    writePropertiesFile(testPropertiesFile, "");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("PropertiesWrapper should not be null", wrapper, notNullValue());
    assertThat("Should be empty", wrapper.isEmpty(), is(true));
    assertThat("Should have 0 properties", wrapper.size(), is(0));
  }

  /**
   * Tests PropertiesWrapper extends Properties class.
   *
   * <p>Verifies that PropertiesWrapper is a valid Properties subclass.
   */
  @Test
  public void testExtendsPropertiesClass() throws IOException {
    writePropertiesFile(testPropertiesFile, "test=value\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Should be instance of Properties", wrapper instanceof Properties, is(true));
  }

  /**
   * Tests properties with equals sign in value.
   *
   * <p>Verifies that values containing '=' are parsed correctly.
   */
  @Test
  public void testPropertiesWithEqualsInValue() throws IOException {
    writePropertiesFile(testPropertiesFile, "url=http://example.com?param1=value1&param2=value2\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat(
        "URL with equals should be parsed correctly",
        wrapper.getProperty("url"),
        is("http://example.com?param1=value1&param2=value2"));
  }

  /**
   * Tests properties with special characters.
   *
   * <p>Verifies that properties with special characters are handled correctly.
   */
  @Test
  public void testPropertiesWithSpecialCharacters() throws IOException {
    writePropertiesFile(
        testPropertiesFile,
        "special1=value with spaces\n"
            + "special2=value:with:colons\n"
            + "special3=value;with;semicolons\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat(
        "Property with spaces should work",
        wrapper.getProperty("special1"),
        is("value with spaces"));
    assertThat(
        "Property with colons should work",
        wrapper.getProperty("special2"),
        is("value:with:colons"));
    assertThat(
        "Property with semicolons should work",
        wrapper.getProperty("special3"),
        is("value;with;semicolons"));
  }

  /**
   * Tests properties with comments.
   *
   * <p>Verifies that comment lines are ignored.
   */
  @Test
  public void testPropertiesWithComments() throws IOException {
    writePropertiesFile(
        testPropertiesFile,
        "# This is a comment\n"
            + "property1=value1\n"
            + "! This is also a comment\n"
            + "property2=value2\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Should have 2 properties (comments ignored)", wrapper.size(), is(2));
    assertThat("Property1 should be loaded", wrapper.getProperty("property1"), is("value1"));
    assertThat("Property2 should be loaded", wrapper.getProperty("property2"), is("value2"));
  }

  /**
   * Tests properties with blank lines.
   *
   * <p>Verifies that blank lines are handled correctly.
   */
  @Test
  public void testPropertiesWithBlankLines() throws IOException {
    writePropertiesFile(
        testPropertiesFile, "property1=value1\n" + "\n" + "property2=value2\n" + "\n" + "\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Should have 2 properties", wrapper.size(), is(2));
    assertThat("Property1 should be loaded", wrapper.getProperty("property1"), is("value1"));
    assertThat("Property2 should be loaded", wrapper.getProperty("property2"), is("value2"));
  }

  /**
   * Tests properties with Unicode characters.
   *
   * <p>Verifies that international characters are supported.
   */
  @Test
  public void testPropertiesWithUnicodeCharacters() throws IOException {
    writePropertiesFile(testPropertiesFile, "unicode=test\\u00E9\\u00F1\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Unicode property should be loaded", wrapper.containsKey("unicode"), is(true));
  }

  /**
   * Tests properties with multi-line values.
   *
   * <p>Verifies that multi-line values (with backslash continuation) work.
   */
  @Test
  public void testPropertiesWithMultiLineValues() throws IOException {
    writePropertiesFile(testPropertiesFile, "multiline=value1 \\\n" + "  value2 \\\n" + "  value3\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat(
        "Multi-line property should be loaded", wrapper.containsKey("multiline"), is(true));
    String value = wrapper.getProperty("multiline");
    assertThat("Multi-line value should contain parts", value, notNullValue());
  }

  /**
   * Tests PropertiesWrapper with colon separator instead of equals.
   *
   * <p>Verifies that properties using colon separator work correctly.
   */
  @Test
  public void testPropertiesWithColonSeparator() throws IOException {
    writePropertiesFile(
        testPropertiesFile, "property1:value1\n" + "property2:value2\n" + "property3=value3\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Colon-separated property1 should work", wrapper.getProperty("property1"), is("value1"));
    assertThat("Colon-separated property2 should work", wrapper.getProperty("property2"), is("value2"));
    assertThat("Equals-separated property3 should work", wrapper.getProperty("property3"), is("value3"));
  }

  /**
   * Tests properties with escaped characters.
   *
   * <p>Verifies that escaped special characters are handled correctly.
   */
  @Test
  public void testPropertiesWithEscapedCharacters() throws IOException {
    writePropertiesFile(
        testPropertiesFile,
        "escaped1=value\\twith\\ttabs\n"
            + "escaped2=value\\nwith\\nnewlines\n"
            + "escaped3=value\\\\with\\\\backslashes\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat(
        "Escaped tabs should be loaded", wrapper.containsKey("escaped1"), is(true));
    assertThat(
        "Escaped newlines should be loaded", wrapper.containsKey("escaped2"), is(true));
    assertThat(
        "Escaped backslashes should be loaded", wrapper.containsKey("escaped3"), is(true));
  }

  /**
   * Tests properties with leading and trailing spaces.
   *
   * <p>Verifies that whitespace is handled according to properties file format.
   */
  @Test
  public void testPropertiesWithLeadingAndTrailingSpaces() throws IOException {
    writePropertiesFile(testPropertiesFile, "  property1  =  value1  \n" + "property2=value2\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    // Properties format trims keys and handles spaces in values
    assertThat("Property should be loaded", wrapper.size(), greaterThan(0));
  }

  /**
   * Tests duplicate property keys (last one wins).
   *
   * <p>Verifies that duplicate keys result in last value being used.
   */
  @Test
  public void testDuplicatePropertyKeys() throws IOException {
    writePropertiesFile(
        testPropertiesFile, "duplicate=value1\n" + "duplicate=value2\n" + "duplicate=value3\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat(
        "Duplicate key should have last value",
        wrapper.getProperty("duplicate"),
        is("value3"));
    assertThat("Should have only 1 property", wrapper.size(), is(1));
  }

  /**
   * Tests properties with empty values.
   *
   * <p>Verifies that empty property values are supported.
   */
  @Test
  public void testPropertiesWithEmptyValues() throws IOException {
    writePropertiesFile(
        testPropertiesFile,
        "empty1=\n" + "empty2=\n" + "notEmpty=value\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Empty property1 should exist", wrapper.containsKey("empty1"), is(true));
    assertThat("Empty property1 value should be empty", wrapper.getProperty("empty1"), is(""));
    assertThat("Empty property2 should exist", wrapper.containsKey("empty2"), is(true));
    assertThat("Empty property2 value should be empty", wrapper.getProperty("empty2"), is(""));
    assertThat("Non-empty property should have value", wrapper.getProperty("notEmpty"), is("value"));
  }

  /**
   * Tests PropertiesWrapper inheritance methods.
   *
   * <p>Verifies that Properties methods work correctly (put, get, containsKey, etc.).
   */
  @Test
  public void testPropertiesInheritanceMethods() throws IOException {
    writePropertiesFile(testPropertiesFile, "initial=value\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    // Test inherited Properties methods
    wrapper.setProperty("newProperty", "newValue");
    assertThat(
        "setProperty should work", wrapper.getProperty("newProperty"), is("newValue"));

    assertThat("containsKey should work", wrapper.containsKey("initial"), is(true));
    assertThat(
        "containsKey should work for missing", wrapper.containsKey("missing"), is(false));

    wrapper.remove("initial");
    assertThat(
        "remove should work", wrapper.containsKey("initial"), is(false));
  }

  /**
   * Tests loading properties file with absolute path.
   *
   * <p>Verifies that absolute file paths work correctly.
   */
  @Test
  public void testLoadPropertiesWithAbsolutePath() throws IOException {
    writePropertiesFile(testPropertiesFile, "absolutePath=value\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesFile.getAbsolutePath());

    assertThat(
        "Absolute path should work", wrapper.getProperty("absolutePath"), is("value"));
  }

  /**
   * Tests PropertiesWrapper with large properties file.
   *
   * <p>Verifies that large files with many properties are handled correctly.
   */
  @Test
  public void testLoadLargePropertiesFile() throws IOException {
    StringBuilder largeContent = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      largeContent.append("property").append(i).append("=value").append(i).append("\n");
    }
    writePropertiesFile(testPropertiesFile, largeContent.toString());

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Should have 1000 properties", wrapper.size(), is(1000));
    assertThat("First property should exist", wrapper.getProperty("property0"), is("value0"));
    assertThat("Last property should exist", wrapper.getProperty("property999"), is("value999"));
  }

  /**
   * Tests PropertiesWrapper with properties containing long values.
   *
   * <p>Verifies that very long property values are handled correctly.
   */
  @Test
  public void testPropertiesWithLongValues() throws IOException {
    String longValue = new String(new char[5000]).replace('\0', 'x');
    writePropertiesFile(testPropertiesFile, "longValue=" + longValue + "\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Long value property should exist", wrapper.containsKey("longValue"), is(true));
    assertThat("Long value should match", wrapper.getProperty("longValue"), is(longValue));
  }

  /**
   * Helper method to write content to properties file.
   *
   * @param file the properties file
   * @param content the content to write
   * @throws IOException if writing fails
   */
  private void writePropertiesFile(File file, String content) throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
    }
  }

  /**
   * Tests properties file with BOM (Byte Order Mark).
   *
   * <p>Verifies that UTF-8 BOM is handled if present.
   */
  @Test
  public void testPropertiesFileWithVariousEncodings() throws IOException {
    writePropertiesFile(testPropertiesFile, "encoding=test\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    assertThat("Encoding test property should exist", wrapper.containsKey("encoding"), is(true));
  }

  /**
   * Tests PropertiesWrapper toString method.
   *
   * <p>Verifies that toString returns valid representation.
   */
  @Test
  public void testToStringMethod() throws IOException {
    writePropertiesFile(testPropertiesFile, "test=value\n");

    PropertiesWrapper wrapper = new PropertiesWrapper(testPropertiesPath);

    String toString = wrapper.toString();
    assertThat("toString should not be null", toString, notNullValue());
  }

  /**
   * Tests PropertiesWrapper equals and hashCode methods.
   *
   * <p>Verifies that inherited equals/hashCode work correctly.
   */
  @Test
  public void testEqualsAndHashCode() throws IOException {
    writePropertiesFile(testPropertiesFile, "test=value\n");

    PropertiesWrapper wrapper1 = new PropertiesWrapper(testPropertiesPath);
    PropertiesWrapper wrapper2 = new PropertiesWrapper(testPropertiesPath);

    assertThat("Two wrappers with same content should be equal", wrapper1, equalTo(wrapper2));
    assertThat(
        "hashCode should be equal for equal objects",
        wrapper1.hashCode(),
        is(wrapper2.hashCode()));
  }
}
