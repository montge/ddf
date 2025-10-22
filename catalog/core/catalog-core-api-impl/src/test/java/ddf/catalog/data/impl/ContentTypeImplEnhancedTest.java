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
package ddf.catalog.data.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.ContentType;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Enhanced comprehensive test suite for {@link ContentTypeImpl} class.
 *
 * <p>Tests all constructors, getters, setters, equals/hashCode contract, and edge cases for content
 * type metadata used throughout the catalog.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentTypeImplEnhancedTest {

  private static final String CONTENT_TYPE_NAME = "nitf";
  private static final String CONTENT_TYPE_VERSION = "2.1";
  private static final String NAMESPACE_STRING = "http://example.com/nitf/2.1";

  private ContentTypeImpl contentType;
  private URI namespace;

  @Before
  public void setUp() throws URISyntaxException {
    namespace = new URI(NAMESPACE_STRING);
    contentType = new ContentTypeImpl();
  }

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests default constructor creates valid ContentTypeImpl.
   *
   * <p>Verifies that all fields are null by default.
   */
  @Test
  public void testDefaultConstructor() {
    ContentTypeImpl defaultContent = new ContentTypeImpl();

    assertThat(defaultContent, is(notNullValue()));
    assertThat(defaultContent.getName(), is(nullValue()));
    assertThat(defaultContent.getVersion(), is(nullValue()));
    assertThat(defaultContent.getNamespace(), is(nullValue()));
  }

  /**
   * Tests constructor with name and version.
   *
   * <p>Verifies proper initialization of name and version.
   */
  @Test
  public void testConstructorWithNameAndVersion() {
    ContentTypeImpl content = new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION);

    assertThat(content.getName(), is(equalTo(CONTENT_TYPE_NAME)));
    assertThat(content.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
    assertThat(content.getNamespace(), is(nullValue()));
  }

  /**
   * Tests constructor with name, version, and namespace.
   *
   * <p>Verifies complete initialization with all fields.
   */
  @Test
  public void testConstructorWithAllFields() {
    ContentTypeImpl content =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content.getName(), is(equalTo(CONTENT_TYPE_NAME)));
    assertThat(content.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
    assertThat(content.getNamespace(), is(equalTo(namespace)));
  }

  /**
   * Tests constructor with null name and version.
   *
   * <p>Verifies that null values are accepted in constructors.
   */
  @Test
  public void testConstructorWithNullValues() {
    ContentTypeImpl content = new ContentTypeImpl(null, null);

    assertThat(content.getName(), is(nullValue()));
    assertThat(content.getVersion(), is(nullValue()));
  }

  /**
   * Tests constructor with null namespace.
   *
   * <p>Verifies that null namespace is accepted.
   */
  @Test
  public void testConstructorWithNullNamespace() {
    ContentTypeImpl content = new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, null);

    assertThat(content.getName(), is(equalTo(CONTENT_TYPE_NAME)));
    assertThat(content.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
    assertThat(content.getNamespace(), is(nullValue()));
  }

  /**
   * Tests constructor with empty strings.
   *
   * <p>Verifies that empty strings are valid values.
   */
  @Test
  public void testConstructorWithEmptyStrings() {
    ContentTypeImpl content = new ContentTypeImpl("", "");

    assertThat(content.getName(), is(equalTo("")));
    assertThat(content.getVersion(), is(equalTo("")));
  }

  // ====================
  // Name Tests
  // ====================

  /**
   * Tests setting and getting name.
   *
   * <p>Verifies name can be set and retrieved.
   */
  @Test
  public void testSetAndGetName() {
    contentType.setName(CONTENT_TYPE_NAME);

    assertThat(contentType.getName(), is(equalTo(CONTENT_TYPE_NAME)));
  }

  /**
   * Tests setting name to null.
   *
   * <p>Verifies that name can be cleared.
   */
  @Test
  public void testSetNameToNull() {
    contentType.setName(CONTENT_TYPE_NAME);
    contentType.setName(null);

    assertThat(contentType.getName(), is(nullValue()));
  }

  /**
   * Tests setting name to empty string.
   *
   * <p>Verifies that empty string is a valid name.
   */
  @Test
  public void testSetNameToEmptyString() {
    contentType.setName("");

    assertThat(contentType.getName(), is(equalTo("")));
  }

  /**
   * Tests replacing name.
   *
   * <p>Verifies that name can be changed.
   */
  @Test
  public void testReplaceName() {
    contentType.setName("nitf");
    assertThat(contentType.getName(), is(equalTo("nitf")));

    contentType.setName("jpeg");
    assertThat(contentType.getName(), is(equalTo("jpeg")));
  }

  /**
   * Tests name with special characters.
   *
   * <p>Verifies that names with special characters are handled.
   */
  @Test
  public void testNameWithSpecialCharacters() {
    String specialName = "type-with_special.chars";
    contentType.setName(specialName);

    assertThat(contentType.getName(), is(equalTo(specialName)));
  }

  /**
   * Tests very long name.
   *
   * <p>Verifies that long names are handled correctly.
   */
  @Test
  public void testVeryLongName() {
    StringBuilder longName = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longName.append("a");
    }
    contentType.setName(longName.toString());

    assertThat(contentType.getName(), is(equalTo(longName.toString())));
  }

  // ====================
  // Version Tests
  // ====================

  /**
   * Tests setting and getting version.
   *
   * <p>Verifies version can be set and retrieved.
   */
  @Test
  public void testSetAndGetVersion() {
    contentType.setVersion(CONTENT_TYPE_VERSION);

    assertThat(contentType.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
  }

  /**
   * Tests setting version to null.
   *
   * <p>Verifies that version can be cleared.
   */
  @Test
  public void testSetVersionToNull() {
    contentType.setVersion(CONTENT_TYPE_VERSION);
    contentType.setVersion(null);

    assertThat(contentType.getVersion(), is(nullValue()));
  }

  /**
   * Tests various version formats.
   *
   * <p>Verifies that different version string formats are supported.
   */
  @Test
  public void testVariousVersionFormats() {
    String[] versions = {"1.0", "2.1.3", "v1", "2020-01-01", "1.0-SNAPSHOT"};

    for (String version : versions) {
      contentType.setVersion(version);
      assertThat(contentType.getVersion(), is(equalTo(version)));
    }
  }

  /**
   * Tests replacing version.
   *
   * <p>Verifies that version can be changed.
   */
  @Test
  public void testReplaceVersion() {
    contentType.setVersion("1.0");
    assertThat(contentType.getVersion(), is(equalTo("1.0")));

    contentType.setVersion("2.0");
    assertThat(contentType.getVersion(), is(equalTo("2.0")));
  }

  // ====================
  // Namespace Tests
  // ====================

  /**
   * Tests setting and getting namespace.
   *
   * <p>Verifies namespace can be set and retrieved.
   */
  @Test
  public void testSetAndGetNamespace() {
    contentType.setNamespace(namespace);

    assertThat(contentType.getNamespace(), is(equalTo(namespace)));
  }

  /**
   * Tests setting namespace to null.
   *
   * <p>Verifies that namespace can be cleared.
   */
  @Test
  public void testSetNamespaceToNull() {
    contentType.setNamespace(namespace);
    contentType.setNamespace(null);

    assertThat(contentType.getNamespace(), is(nullValue()));
  }

  /**
   * Tests various namespace URI formats.
   *
   * <p>Verifies that different URI formats are supported.
   */
  @Test
  public void testVariousNamespaceFormats() throws URISyntaxException {
    URI httpUri = new URI("http://example.com/namespace");
    URI httpsUri = new URI("https://secure.example.com/ns");
    URI urnUri = new URI("urn:example:namespace:v1");

    contentType.setNamespace(httpUri);
    assertThat(contentType.getNamespace(), is(equalTo(httpUri)));

    contentType.setNamespace(httpsUri);
    assertThat(contentType.getNamespace(), is(equalTo(httpsUri)));

    contentType.setNamespace(urnUri);
    assertThat(contentType.getNamespace(), is(equalTo(urnUri)));
  }

  /**
   * Tests replacing namespace.
   *
   * <p>Verifies that namespace can be changed.
   */
  @Test
  public void testReplaceNamespace() throws URISyntaxException {
    URI firstNamespace = new URI("http://first.example.com");
    URI secondNamespace = new URI("http://second.example.com");

    contentType.setNamespace(firstNamespace);
    assertThat(contentType.getNamespace(), is(equalTo(firstNamespace)));

    contentType.setNamespace(secondNamespace);
    assertThat(contentType.getNamespace(), is(equalTo(secondNamespace)));
  }

  // ====================
  // Equals Tests
  // ====================

  /**
   * Tests equals with same object.
   *
   * <p>Verifies reflexive property of equals.
   */
  @Test
  public void testEqualsWithSameObject() {
    ContentTypeImpl content =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content.equals(content), is(true));
  }

  /**
   * Tests equals with equivalent objects.
   *
   * <p>Verifies symmetric property of equals.
   */
  @Test
  public void testEqualsWithEquivalentObjects() {
    ContentTypeImpl content1 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(true));
    assertThat(content2.equals(content1), is(true));
  }

  /**
   * Tests equals with different names.
   *
   * <p>Verifies that different names make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentNames() {
    ContentTypeImpl content1 = new ContentTypeImpl("nitf", CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 = new ContentTypeImpl("jpeg", CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(false));
  }

  /**
   * Tests equals with different versions.
   *
   * <p>Verifies that different versions make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentVersions() {
    ContentTypeImpl content1 = new ContentTypeImpl(CONTENT_TYPE_NAME, "1.0", namespace);
    ContentTypeImpl content2 = new ContentTypeImpl(CONTENT_TYPE_NAME, "2.0", namespace);

    assertThat(content1.equals(content2), is(false));
  }

  /**
   * Tests equals with different namespaces.
   *
   * <p>Verifies that different namespaces make objects unequal.
   */
  @Test
  public void testEqualsWithDifferentNamespaces() throws URISyntaxException {
    URI namespace1 = new URI("http://example1.com");
    URI namespace2 = new URI("http://example2.com");

    ContentTypeImpl content1 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace1);
    ContentTypeImpl content2 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace2);

    assertThat(content1.equals(content2), is(false));
  }

  /**
   * Tests equals with null name.
   *
   * <p>Verifies equals handles null names correctly.
   */
  @Test
  public void testEqualsWithNullName() {
    ContentTypeImpl content1 = new ContentTypeImpl(null, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 = new ContentTypeImpl(null, CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(true));
  }

  /**
   * Tests equals with one null name.
   *
   * <p>Verifies that null and non-null names are not equal.
   */
  @Test
  public void testEqualsWithOneNullName() {
    ContentTypeImpl content1 = new ContentTypeImpl(null, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(false));
  }

  /**
   * Tests equals with null version.
   *
   * <p>Verifies equals handles null versions correctly.
   */
  @Test
  public void testEqualsWithNullVersion() {
    ContentTypeImpl content1 = new ContentTypeImpl(CONTENT_TYPE_NAME, null, namespace);
    ContentTypeImpl content2 = new ContentTypeImpl(CONTENT_TYPE_NAME, null, namespace);

    assertThat(content1.equals(content2), is(true));
  }

  /**
   * Tests equals with null namespace.
   *
   * <p>Verifies equals handles null namespaces correctly.
   */
  @Test
  public void testEqualsWithNullNamespace() {
    ContentTypeImpl content1 = new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, null);
    ContentTypeImpl content2 = new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, null);

    assertThat(content1.equals(content2), is(true));
  }

  /**
   * Tests equals with null object.
   *
   * <p>Verifies that equals handles null gracefully.
   */
  @Test
  public void testEqualsWithNull() {
    ContentTypeImpl content =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content.equals(null), is(false));
  }

  /**
   * Tests equals with different class.
   *
   * <p>Verifies that equals returns false for different types.
   */
  @Test
  public void testEqualsWithDifferentClass() {
    ContentTypeImpl content =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);
    String differentObject = "not a ContentType";

    assertThat(content.equals(differentObject), is(false));
  }

  // ====================
  // HashCode Tests
  // ====================

  /**
   * Tests hashCode consistency with equals.
   *
   * <p>Verifies that equal objects have equal hashCodes.
   */
  @Test
  public void testHashCodeConsistencyWithEquals() {
    ContentTypeImpl content1 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(true));
    assertThat(content1.hashCode(), is(equalTo(content2.hashCode())));
  }

  /**
   * Tests hashCode with different data.
   *
   * <p>Verifies that different data produces different hashCodes (usually).
   */
  @Test
  public void testHashCodeWithDifferentData() {
    ContentTypeImpl content1 = new ContentTypeImpl("nitf", "1.0", namespace);
    ContentTypeImpl content2 = new ContentTypeImpl("jpeg", "2.0", namespace);

    // Different content should produce different hashCodes (highly likely but not guaranteed)
    assertThat(content1.hashCode(), is(not(equalTo(content2.hashCode()))));
  }

  /**
   * Tests hashCode consistency across calls.
   *
   * <p>Verifies that multiple hashCode calls return the same value.
   */
  @Test
  public void testHashCodeConsistency() {
    ContentTypeImpl content =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    int hash1 = content.hashCode();
    int hash2 = content.hashCode();

    assertThat(hash1, is(equalTo(hash2)));
  }

  /**
   * Tests hashCode with null fields.
   *
   * <p>Verifies that hashCode handles null fields correctly.
   */
  @Test
  public void testHashCodeWithNullFields() {
    ContentTypeImpl content1 = new ContentTypeImpl(null, null, null);
    ContentTypeImpl content2 = new ContentTypeImpl(null, null, null);

    assertThat(content1.hashCode(), is(equalTo(content2.hashCode())));
  }

  // ====================
  // Interface Compliance Tests
  // ====================

  /**
   * Tests ContentType interface compliance.
   *
   * <p>Verifies that ContentTypeImpl properly implements ContentType interface.
   */
  @Test
  public void testContentTypeInterfaceCompliance() {
    ContentType interfaceRef =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(interfaceRef, is(notNullValue()));
    assertThat(interfaceRef.getName(), is(equalTo(CONTENT_TYPE_NAME)));
    assertThat(interfaceRef.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
    assertThat(interfaceRef.getNamespace(), is(equalTo(namespace)));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete NITF content type.
   *
   * <p>Simulates a real NITF content type definition.
   */
  @Test
  public void testNitfContentType() throws URISyntaxException {
    URI nitfNamespace = new URI("urn:us:gov:ic:nitf");
    ContentTypeImpl nitf = new ContentTypeImpl("nitf", "2.1", nitfNamespace);

    assertThat(nitf.getName(), is(equalTo("nitf")));
    assertThat(nitf.getVersion(), is(equalTo("2.1")));
    assertThat(nitf.getNamespace().toString(), is(equalTo("urn:us:gov:ic:nitf")));
  }

  /**
   * Tests generic image content type.
   *
   * <p>Simulates a simple image content type.
   */
  @Test
  public void testImageContentType() {
    ContentTypeImpl image = new ContentTypeImpl("image", "1.0");

    assertThat(image.getName(), is(equalTo("image")));
    assertThat(image.getVersion(), is(equalTo("1.0")));
    assertThat(image.getNamespace(), is(nullValue()));
  }

  /**
   * Tests content type modification after creation.
   *
   * <p>Verifies that all fields can be modified after construction.
   */
  @Test
  public void testModificationAfterCreation() throws URISyntaxException {
    ContentTypeImpl content = new ContentTypeImpl();

    content.setName(CONTENT_TYPE_NAME);
    content.setVersion(CONTENT_TYPE_VERSION);
    content.setNamespace(namespace);

    assertThat(content.getName(), is(equalTo(CONTENT_TYPE_NAME)));
    assertThat(content.getVersion(), is(equalTo(CONTENT_TYPE_VERSION)));
    assertThat(content.getNamespace(), is(equalTo(namespace)));
  }

  /**
   * Tests transitive equality.
   *
   * <p>Verifies transitive property: if a=b and b=c, then a=c.
   */
  @Test
  public void testTransitiveEquality() {
    ContentTypeImpl content1 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content2 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);
    ContentTypeImpl content3 =
        new ContentTypeImpl(CONTENT_TYPE_NAME, CONTENT_TYPE_VERSION, namespace);

    assertThat(content1.equals(content2), is(true));
    assertThat(content2.equals(content3), is(true));
    assertThat(content1.equals(content3), is(true));
  }
}
