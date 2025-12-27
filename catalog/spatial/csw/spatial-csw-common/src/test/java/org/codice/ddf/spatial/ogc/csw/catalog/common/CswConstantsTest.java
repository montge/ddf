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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

class CswConstantsTest {

  @Test
  void testCswAttributePrefixValue() {
    assertThat(CswConstants.CSW_ATTRIBUTE_PREFIX, is("csw."));
  }

  @Test
  void testCswNamespaceUri() {
    assertThat(CswConstants.CSW_NAMESPACE_URI, is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testCswMetacardTypeName() {
    assertThat(CswConstants.CSW_METACARD_TYPE_NAME, is("csw:Record"));
  }

  @Test
  void testCswTitleContainsPrefix() {
    assertThat(CswConstants.CSW_TITLE, is("csw.title"));
  }

  @Test
  void testCswModifiedContainsPrefix() {
    assertThat(CswConstants.CSW_MODIFIED, is("csw.modified"));
  }

  @Test
  void testCswCreatedContainsPrefix() {
    assertThat(CswConstants.CSW_CREATED, is("csw.created"));
  }

  @Test
  void testGetCapabilitiesConstant() {
    assertThat(CswConstants.GET_CAPABILITIES, is("GetCapabilities"));
  }

  @Test
  void testDescribeRecordConstant() {
    assertThat(CswConstants.DESCRIBE_RECORD, is("DescribeRecord"));
  }

  @Test
  void testGetRecordsConstant() {
    assertThat(CswConstants.GET_RECORDS, is("GetRecords"));
  }

  @Test
  void testGetRecordByIdConstant() {
    assertThat(CswConstants.GET_RECORD_BY_ID, is("GetRecordById"));
  }

  @Test
  void testTransactionConstant() {
    assertThat(CswConstants.TRANSACTION, is("Transaction"));
  }

  @Test
  void testCswServiceConstant() {
    assertThat(CswConstants.CSW, is("CSW"));
  }

  @Test
  void testVersionConstants() {
    assertThat(CswConstants.VERSION_2_0_2, is("2.0.2"));
    assertThat(CswConstants.VERSION_2_0_1, is("2.0.1"));
  }

  @Test
  void testConstraintLanguageConstants() {
    assertThat(CswConstants.CONSTRAINT_LANGUAGE_FILTER, is("Filter"));
    assertThat(CswConstants.CONSTRAINT_LANGUAGE_CQL, is("CQL_Text"));
  }

  @Test
  void testConstraintLanguagesList() {
    assertThat(CswConstants.CONSTRAINT_LANGUAGES, hasSize(2));
    assertThat(
        CswConstants.CONSTRAINT_LANGUAGES,
        containsInAnyOrder(
            CswConstants.CONSTRAINT_LANGUAGE_FILTER, CswConstants.CONSTRAINT_LANGUAGE_CQL));
  }

  @Test
  void testOwsNamespace() {
    assertThat(CswConstants.OWS_NAMESPACE, is("http://www.opengis.net/ows"));
  }

  @Test
  void testPostQNameIsNotNull() {
    assertThat(CswConstants.POST, is(notNullValue()));
    assertThat(CswConstants.POST.getLocalPart(), is("Post"));
  }

  @Test
  void testGetQNameIsNotNull() {
    assertThat(CswConstants.GET, is(notNullValue()));
    assertThat(CswConstants.GET.getLocalPart(), is("Get"));
  }

  @Test
  void testWildcardConstant() {
    assertThat(CswConstants.WILD_CARD, is("*"));
  }

  @Test
  void testEscapeConstant() {
    assertThat(CswConstants.ESCAPE, is("!"));
  }

  @Test
  void testSingleCharConstant() {
    assertThat(CswConstants.SINGLE_CHAR, is("#"));
  }

  @Test
  void testAnyTextConstant() {
    assertThat(CswConstants.ANY_TEXT, is("AnyText"));
  }

  @Test
  void testDublinCoreSchema() {
    assertThat(CswConstants.DUBLIN_CORE_SCHEMA, is("http://purl.org/dc/elements/1.1/"));
  }

  @Test
  void testDublinCoreTermsSchema() {
    assertThat(CswConstants.DUBLIN_CORE_TERMS_SCHEMA, is("http://purl.org/dc/terms/"));
  }

  @Test
  void testCswOutputSchema() {
    assertThat(CswConstants.CSW_OUTPUT_SCHEMA, is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testGmlSchema() {
    assertThat(CswConstants.GML_SCHEMA, is("http://www.opengis.net/gml"));
  }

  @Test
  void testOgcSchema() {
    assertThat(CswConstants.OGC_SCHEMA, is("http://www.opengis.net/ogc"));
  }

  @Test
  void testEarthMeanRadiusMeters() {
    assertThat(CswConstants.EARTH_MEAN_RADIUS_METERS, is(6371008.7714));
  }

  @Test
  void testDegreesToRadiansConversion() {
    assertThat(CswConstants.DEGREES_TO_RADIANS, is(Math.PI / 180.0));
  }

  @Test
  void testRadiansToDegreesConversion() {
    assertThat(CswConstants.RADIANS_TO_DEGREES, is(1 / (Math.PI / 180.0)));
  }

  @Test
  void testSrsNameConstant() {
    assertThat(CswConstants.SRS_NAME, is("EPSG:4326"));
  }

  @Test
  void testSrsUrlConstant() {
    assertThat(CswConstants.SRS_URL, is("urn:x-ogc:def:crs:EPSG:6.11:4326"));
  }

  @Test
  void testDcIdentifierQName() {
    QName qname = CswConstants.DC_IDENTIFIER_QNAME;
    assertThat(qname, is(notNullValue()));
    assertThat(qname.getLocalPart(), is("identifier"));
    assertThat(qname.getNamespaceURI(), is(CswConstants.DUBLIN_CORE_SCHEMA));
  }

  @Test
  void testDcTitleQName() {
    QName qname = CswConstants.DC_TITLE_QNAME;
    assertThat(qname, is(notNullValue()));
    assertThat(qname.getLocalPart(), is("title"));
  }

  @Test
  void testRequiredFieldsContainsTitleAndIdentifier() {
    assertThat(CswConstants.REQUIRED_FIELDS, hasSize(2));
    assertThat(
        CswConstants.REQUIRED_FIELDS,
        containsInAnyOrder(CswConstants.CSW_IDENTIFIER_QNAME, CswConstants.CSW_TITLE_QNAME));
  }

  @Test
  void testBriefCswRecordFieldsSize() {
    assertThat(CswConstants.BRIEF_CSW_RECORD_FIELDS, hasSize(4));
  }

  @Test
  void testSummaryCswRecordFieldsSize() {
    assertThat(CswConstants.SUMMARY_CSW_RECORD_FIELDS, hasSize(10));
  }

  @Test
  void testFullCswRecordFieldsIsNotEmpty() {
    assertThat(CswConstants.FULL_CSW_RECORD_FIELDS.size(), is(greaterThan(20)));
  }

  @Test
  void testValidSchemaLanguagesContainsFourEntries() {
    assertThat(CswConstants.VALID_SCHEMA_LANGUAGES, hasSize(4));
    assertThat(
        CswConstants.VALID_SCHEMA_LANGUAGES,
        containsInAnyOrder(
            CswConstants.SCHEMA_LANGUAGE_X_SCHEMA,
            CswConstants.SCHEMA_LANGUAGE_XML_SCHEMA,
            CswConstants.SCHEMA_LANGUAGE_X_SCHEMA_2001,
            CswConstants.SCHEMA_LANGUAGE_XML_TR));
  }

  @Test
  void testBinarySpatialOperandEnumValues() {
    CswConstants.BinarySpatialOperand[] values = CswConstants.BinarySpatialOperand.values();
    assertThat(values.length, is(3));
  }

  @Test
  void testBinarySpatialOperandGeometry() {
    assertThat(
        CswConstants.BinarySpatialOperand.valueOf("GEOMETRY"),
        is(CswConstants.BinarySpatialOperand.GEOMETRY));
  }

  @Test
  void testBinarySpatialOperandEnvelope() {
    assertThat(
        CswConstants.BinarySpatialOperand.valueOf("ENVELOPE"),
        is(CswConstants.BinarySpatialOperand.ENVELOPE));
  }

  @Test
  void testBinarySpatialOperandNone() {
    assertThat(
        CswConstants.BinarySpatialOperand.valueOf("NONE"),
        is(CswConstants.BinarySpatialOperand.NONE));
  }

  @Test
  void testExceptionCodeConstants() {
    assertThat(CswConstants.VERSION_NEGOTIATION_FAILED, is("VersionNegotiationFailed"));
    assertThat(CswConstants.MISSING_PARAMETER_VALUE, is("MissingParameterValue"));
    assertThat(CswConstants.INVALID_PARAMETER_VALUE, is("InvalidParameterValue"));
    assertThat(CswConstants.OPERATION_NOT_SUPPORTED, is("OperationNotSupported"));
    assertThat(CswConstants.NO_APPLICABLE_CODE, is("NoApplicableCode"));
    assertThat(CswConstants.TRANSACTION_FAILED, is("TransactionFailed"));
  }

  @Test
  void testTransactionNodeConstants() {
    assertThat(CswConstants.CSW_TRANSACTION, is("csw:Transaction"));
    assertThat(CswConstants.CSW_TRANSACTION_INSERT_NODE, is("csw:Insert"));
    assertThat(CswConstants.CSW_TRANSACTION_UPDATE_NODE, is("csw:Update"));
    assertThat(CswConstants.CSW_TRANSACTION_DELETE_NODE, is("csw:Delete"));
  }

  @Test
  void testRecordLocalNames() {
    assertThat(CswConstants.CSW_RECORD_LOCAL_NAME, is("Record"));
    assertThat(CswConstants.CSW_SUMMARY_RECORD_LOCAL_NAME, is("SummaryRecord"));
    assertThat(CswConstants.CSW_BRIEF_RECORD_LOCAL_NAME, is("BriefRecord"));
  }

  @Test
  void testFullRecordNames() {
    assertThat(CswConstants.CSW_RECORD, is("csw:Record"));
    assertThat(CswConstants.CSW_SUMMARY_RECORD, is("csw:SummaryRecord"));
    assertThat(CswConstants.CSW_BRIEF_RECORD, is("csw:BriefRecord"));
  }

  @Test
  void testNamespacePrefixes() {
    assertThat(CswConstants.OWS_NAMESPACE_PREFIX, is("ows"));
    assertThat(CswConstants.OGC_NAMESPACE_PREFIX, is("ogc"));
    assertThat(CswConstants.GML_NAMESPACE_PREFIX, is("gml"));
    assertThat(CswConstants.CSW_NAMESPACE_PREFIX, is("csw"));
    assertThat(CswConstants.DUBLIN_CORE_NAMESPACE_PREFIX, is("dc"));
    assertThat(CswConstants.DUBLIN_CORE_TERMS_NAMESPACE_PREFIX, is("dct"));
  }

  @Test
  void testConstraintVersion() {
    assertThat(CswConstants.CONSTRAINT_VERSION, is("1.1.0"));
  }

  @Test
  void testGmlGeometryTypes() {
    assertThat(CswConstants.GML_POINT, is("Point"));
    assertThat(CswConstants.GML_LINESTRING, is("LineString"));
    assertThat(CswConstants.GML_POLYGON, is("Polygon"));
  }
}
