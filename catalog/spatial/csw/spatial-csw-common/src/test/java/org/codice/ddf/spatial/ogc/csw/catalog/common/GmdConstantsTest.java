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
package org.codice.ddf.spatial.ogc.csw.catalog.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

class GmdConstantsTest {

  @Test
  void testGmdPrefix() {
    assertThat(GmdConstants.GMD_PREFIX, is("gmd"));
  }

  @Test
  void testGmdLocalName() {
    assertThat(GmdConstants.GMD_LOCAL_NAME, is("MD_Metadata"));
  }

  @Test
  void testGmdMetacardTypeName() {
    assertThat(GmdConstants.GMD_METACARD_TYPE_NAME, is("gmd.MD_Metadata"));
  }

  @Test
  void testGmdNamespace() {
    assertThat(GmdConstants.GMD_NAMESPACE, is("http://www.isotc211.org/2005/gmd"));
  }

  @Test
  void testGcoNamespace() {
    assertThat(GmdConstants.GCO_NAMESPACE, is("http://www.isotc211.org/2005/gco"));
  }

  @Test
  void testGcoPrefix() {
    assertThat(GmdConstants.GCO_PREFIX, is("gco"));
  }

  @Test
  void testMetacardUri() {
    assertThat(GmdConstants.METACARD_URI, is("urn:catalog:metacard"));
  }

  @Test
  void testApisoPrefix() {
    assertThat(GmdConstants.APISO_PREFIX, is("apiso."));
  }

  @Test
  void testApisoBoundingBox() {
    assertThat(GmdConstants.APISO_BOUNDING_BOX, is("apiso.BoundingBox"));
  }

  // QName tests

  @Test
  void testGmdRevisionDateQName() {
    assertThat(GmdConstants.GMD_REVISION_DATE_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_REVISION_DATE_QNAME.getLocalPart(), is("RevisionDate"));
    assertThat(GmdConstants.GMD_REVISION_DATE_QNAME.getPrefix(), is("gmd"));
    assertThat(
        GmdConstants.GMD_REVISION_DATE_QNAME.getNamespaceURI(), is(GmdConstants.GMD_NAMESPACE));
  }

  @Test
  void testGmdAbstractQName() {
    assertThat(GmdConstants.GMD_ABSTRACT_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_ABSTRACT_QNAME.getLocalPart(), is("Abstract"));
  }

  @Test
  void testGmdLanguageQName() {
    assertThat(GmdConstants.GMD_LANGUAGE_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_LANGUAGE_QNAME.getLocalPart(), is("Language"));
  }

  @Test
  void testGmdSubjectQName() {
    assertThat(GmdConstants.GMD_SUBJECT_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_SUBJECT_QNAME.getLocalPart(), is("Subject"));
  }

  @Test
  void testGmdCreationDateQName() {
    assertThat(GmdConstants.GMD_CREATION_DATE_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_CREATION_DATE_QNAME.getLocalPart(), is("CreationDate"));
  }

  @Test
  void testGmdPublicationDateQName() {
    assertThat(GmdConstants.GMD_PUBLICATION_DATE_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_PUBLICATION_DATE_QNAME.getLocalPart(), is("PublicationDate"));
  }

  @Test
  void testGmdOrganizationNameQName() {
    assertThat(GmdConstants.GMD_ORGANIZATION_NAME_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_ORGANIZATION_NAME_QNAME.getLocalPart(), is("OrganisationName"));
  }

  @Test
  void testGmdFormatQName() {
    assertThat(GmdConstants.GMD_FORMAT_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_FORMAT_QNAME.getLocalPart(), is("Format"));
  }

  @Test
  void testGmdModifiedQName() {
    assertThat(GmdConstants.GMD_MODIFIED_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_MODIFIED_QNAME.getLocalPart(), is("Modified"));
  }

  @Test
  void testGmdTypeQName() {
    assertThat(GmdConstants.GMD_TYPE_QNAME, is(notNullValue()));
    assertThat(GmdConstants.GMD_TYPE_QNAME.getLocalPart(), is("Type"));
  }

  // Path constants tests

  @Test
  void testDateStampPath() {
    assertThat(GmdConstants.DATE_STAMP_PATH, is("/MD_Metadata/dateStamp/gco:Date"));
  }

  @Test
  void testDateTimeStampPath() {
    assertThat(GmdConstants.DATE_TIME_STAMP_PATH, is("/MD_Metadata/dateStamp/gco:DateTime"));
  }

  @Test
  void testTitlePath() {
    assertThat(GmdConstants.TITLE_PATH, startsWith("/MD_Metadata/identificationInfo"));
  }

  @Test
  void testAbstractPath() {
    assertThat(GmdConstants.ABSTRACT_PATH, startsWith("/MD_Metadata/identificationInfo"));
  }

  @Test
  void testFileIdentifierPath() {
    assertThat(
        GmdConstants.FILE_IDENTIFIER_PATH, is("/MD_Metadata/fileIdentifier/gco:CharacterString"));
  }

  // Date type constants tests

  @Test
  void testLastUpdate() {
    assertThat(GmdConstants.LAST_UPDATE, is("lastUpdate"));
  }

  @Test
  void testRevision() {
    assertThat(GmdConstants.REVISION, is("revision"));
  }

  @Test
  void testCreation() {
    assertThat(GmdConstants.CREATION, is("creation"));
  }

  @Test
  void testExpiry() {
    assertThat(GmdConstants.EXPIRY, is("expiry"));
  }

  @Test
  void testPublication() {
    assertThat(GmdConstants.PUBLICATION, is("publication"));
  }

  @Test
  void testResourceStatus() {
    assertThat(GmdConstants.RESOURCE_STATUS, is("ext.resource-status"));
  }
}
