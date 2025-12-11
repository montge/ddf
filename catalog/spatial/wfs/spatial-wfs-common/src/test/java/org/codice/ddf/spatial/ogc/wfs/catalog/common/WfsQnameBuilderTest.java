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
package org.codice.ddf.spatial.ogc.wfs.catalog.common;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link WfsQnameBuilder}. */
public class WfsQnameBuilderTest {

  @Test
  public void testBuildQNameWithDifferentNames() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", "ContentType");

    assertThat(qname.getLocalPart(), is("ContentType"));
    assertThat(qname.getPrefix(), is("MetacardType.ContentType"));
    assertThat(
        qname.getNamespaceURI(), is(WfsConstants.NAMESPACE_URN_ROOT + "MetacardType.ContentType"));
  }

  @Test
  public void testBuildQNameWithSameNames() {
    QName qname = WfsQnameBuilder.buildQName("SameType", "SameType");

    assertThat(qname.getLocalPart(), is("SameType"));
    assertThat(qname.getPrefix(), is("SameType"));
    assertThat(qname.getNamespaceURI(), is(WfsConstants.NAMESPACE_URN_ROOT + "SameType"));
  }

  @Test
  public void testBuildQNameWithNullMetacardTypeName() {
    QName qname = WfsQnameBuilder.buildQName(null, "ContentType");

    assertThat(qname, is(nullValue()));
  }

  @Test
  public void testBuildQNameWithNullContentTypeName() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", null);

    assertThat(qname, is(nullValue()));
  }

  @Test
  public void testBuildQNameWithBothNullNames() {
    QName qname = WfsQnameBuilder.buildQName(null, null);

    assertThat(qname, is(nullValue()));
  }

  @Test
  public void testBuildQNameWithEmptyMetacardTypeName() {
    QName qname = WfsQnameBuilder.buildQName("", "ContentType");

    assertThat(qname, is(nullValue()));
  }

  @Test
  public void testBuildQNameWithEmptyContentTypeName() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", "");

    assertThat(qname, is(nullValue()));
  }

  @Test
  public void testBuildQNameReplacesColons() {
    QName qname = WfsQnameBuilder.buildQName("Meta:card:Type", "Content:Type");

    assertThat(qname.getLocalPart(), is("Content_Type"));
    assertThat(qname.getPrefix(), is("Meta_card_Type.Content_Type"));
    assertThat(
        qname.getNamespaceURI(),
        is(WfsConstants.NAMESPACE_URN_ROOT + "Meta_card_Type.Content_Type"));
  }

  @Test
  public void testBuildQNameRemovesSpaces() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", "Content Type With Spaces");

    assertThat(qname.getLocalPart(), is("ContentTypeWithSpaces"));
    assertThat(qname.getPrefix(), is("MetacardType.ContentTypeWithSpaces"));
    assertThat(
        qname.getNamespaceURI(),
        is(WfsConstants.NAMESPACE_URN_ROOT + "MetacardType.ContentTypeWithSpaces"));
  }

  @Test
  public void testBuildQNameWithMultipleSpaces() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", "Content  Type  Multiple  Spaces");

    assertThat(qname.getLocalPart(), is("ContentTypeMultipleSpaces"));
    assertThat(qname.getPrefix(), is("MetacardType.ContentTypeMultipleSpaces"));
  }

  @Test
  public void testBuildQNameWithColonsAndSpaces() {
    QName qname = WfsQnameBuilder.buildQName("Meta:card Type", "Content :Type Name");

    assertThat(qname.getLocalPart(), is("Content_TypeName"));
    assertThat(qname.getPrefix(), is("Meta_cardType.Content_TypeName"));
  }

  @Test
  public void testBuildQNameWithLeadingAndTrailingSpaces() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", " ContentType ");

    // Spaces in contentTypeName are removed, but not trimmed from ends
    assertThat(qname.getLocalPart(), is("ContentType"));
  }

  @Test
  public void testBuildQNameWithSpecialCharactersExceptColonAndSpace() {
    QName qname = WfsQnameBuilder.buildQName("Metacard-Type_123", "Content.Type$456");

    assertThat(qname.getLocalPart(), is("Content.Type$456"));
    assertThat(qname.getPrefix(), is("Metacard-Type_123.Content.Type$456"));
  }

  @Test
  public void testBuildQNameWithNumericNames() {
    QName qname = WfsQnameBuilder.buildQName("123", "456");

    assertThat(qname.getLocalPart(), is("456"));
    assertThat(qname.getPrefix(), is("123.456"));
    assertThat(qname.getNamespaceURI(), is(WfsConstants.NAMESPACE_URN_ROOT + "123.456"));
  }

  @Test
  public void testBuildQNameNamespaceURN() {
    QName qname = WfsQnameBuilder.buildQName("TestMetacard", "TestContent");

    assertThat(qname.getNamespaceURI().startsWith(WfsConstants.NAMESPACE_URN_ROOT), is(true));
    assertThat(
        qname.getNamespaceURI(), is(WfsConstants.NAMESPACE_URN_ROOT + "TestMetacard.TestContent"));
  }

  @Test
  public void testBuildQNameWithUnicodeCharacters() {
    QName qname = WfsQnameBuilder.buildQName("MetacardType", "ContentTypé");

    assertThat(qname.getLocalPart(), is("ContentTypé"));
    assertThat(qname.getPrefix(), is("MetacardType.ContentTypé"));
  }

  @Test
  public void testBuildQNameCaseSensitivity() {
    QName qname1 = WfsQnameBuilder.buildQName("MetacardType", "ContentType");
    QName qname2 = WfsQnameBuilder.buildQName("METACARDTYPE", "CONTENTTYPE");

    assertThat(qname1.getLocalPart().equals(qname2.getLocalPart()), is(false));
    assertThat(qname1.getPrefix().equals(qname2.getPrefix()), is(false));
  }
}
