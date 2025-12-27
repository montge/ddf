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
package ddf.catalog.pubsub.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class PubSubConstantsTest {

  // Header key constants tests

  @Test
  void testHeaderOperationKey() {
    assertThat(PubSubConstants.HEADER_OPERATION_KEY, is("operation"));
  }

  @Test
  void testHeaderContextualKey() {
    assertThat(PubSubConstants.HEADER_CONTEXTUAL_KEY, is("contextualInput"));
  }

  @Test
  void testHeaderGeospatialKey() {
    assertThat(PubSubConstants.HEADER_GEOSPATIAL_KEY, is("geospatialInput"));
  }

  @Test
  void testHeaderTemporalKey() {
    assertThat(PubSubConstants.HEADER_TEMPORAL_KEY, is("temporalInput"));
  }

  @Test
  void testHeaderXpathKey() {
    assertThat(PubSubConstants.HEADER_XPATH_KEY, is("xpathInput"));
  }

  @Test
  void testHeaderEntryKey() {
    assertThat(PubSubConstants.HEADER_ENTRY_KEY, is("entry"));
  }

  @Test
  void testHeaderDeliveryMethod() {
    assertThat(PubSubConstants.HEADER_DELIVERY_METHOD, is("delivery_method"));
  }

  @Test
  void testHeaderContentTypeKey() {
    assertThat(PubSubConstants.HEADER_CONTENT_TYPE_KEY, is("content_type"));
  }

  @Test
  void testHeaderIdKey() {
    assertThat(PubSubConstants.HEADER_ID_KEY, is("id"));
  }

  @Test
  void testHeaderDadKey() {
    assertThat(PubSubConstants.HEADER_DAD_KEY, is("dad"));
  }

  // Operation constants tests

  @Test
  void testCreateOperation() {
    assertThat(PubSubConstants.CREATE, is("CREATE"));
  }

  @Test
  void testDeleteOperation() {
    assertThat(PubSubConstants.DELETE, is("DELETE"));
  }

  @Test
  void testUpdateOperation() {
    assertThat(PubSubConstants.UPDATE, is("UPDATE"));
  }

  // Other constants tests

  @Test
  void testWkt() {
    assertThat(PubSubConstants.WKT, is("geometry"));
  }

  @Test
  void testGmlNamespace() {
    assertThat(PubSubConstants.GML_NAMESPACE, is("http://www.opengis.net/gml"));
  }

  @Test
  void testXmlParsers() {
    assertThat(PubSubConstants.XML_PARSERS, is("javax.xml.parsers.DocumentBuilderFactory"));
  }

  @Test
  void testXercesImplementation() {
    assertThat(PubSubConstants.XERCES_IMPLEMENTATION, containsString("xerces"));
  }

  @Test
  void testEndpointEvaluation() {
    assertThat(PubSubConstants.ENDPOINT_EVALUATION, containsString("seda:"));
    assertThat(PubSubConstants.ENDPOINT_EVALUATION, containsString("PubSub"));
  }

  @Test
  void testPublishedEventTopicName() {
    assertThat(PubSubConstants.PUBLISHED_EVENT_TOPIC_NAME, is("ddf/pubsub/publish/event"));
  }

  @Test
  void testEventsEntryName() {
    assertThat(PubSubConstants.EVENTS_ENTRY_NAME, is("entry"));
  }

  @Test
  void testEventsTimeName() {
    assertThat(PubSubConstants.EVENTS_TIME_NAME, is("time"));
  }

  @Test
  void testMetadataDeleted() {
    assertThat(PubSubConstants.METADATA_DELETED, is("<deleted/>"));
  }

  @Test
  void testAllConstantsNotNull() {
    assertThat(PubSubConstants.HEADER_OPERATION_KEY, is(notNullValue()));
    assertThat(PubSubConstants.HEADER_CONTEXTUAL_KEY, is(notNullValue()));
    assertThat(PubSubConstants.HEADER_GEOSPATIAL_KEY, is(notNullValue()));
    assertThat(PubSubConstants.HEADER_TEMPORAL_KEY, is(notNullValue()));
    assertThat(PubSubConstants.CREATE, is(notNullValue()));
    assertThat(PubSubConstants.DELETE, is(notNullValue()));
    assertThat(PubSubConstants.UPDATE, is(notNullValue()));
  }
}
