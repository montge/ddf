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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.resource.Resource;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import net.opengis.cat.csw.v_2_0_2.ElementSetType;
import net.opengis.cat.csw.v_2_0_2.GetRecordsType;
import net.opengis.cat.csw.v_2_0_2.ResultType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CswRecordCollectionTest {

  private CswRecordCollection collection;

  @BeforeEach
  void setUp() {
    collection = new CswRecordCollection();
  }

  @Test
  void testDefaultCswRecordsIsEmptyList() {
    assertThat(collection.getCswRecords(), is(notNullValue()));
    assertThat(collection.getCswRecords(), is(empty()));
  }

  @Test
  void testSetAndGetCswRecords() {
    List<Metacard> records = Arrays.asList(mock(Metacard.class), mock(Metacard.class));

    collection.setCswRecords(records);

    assertThat(collection.getCswRecords(), hasSize(2));
    assertThat(collection.getCswRecords(), is(sameInstance(records)));
  }

  @Test
  void testDefaultNumberOfRecordsReturnedIsZero() {
    assertThat(collection.getNumberOfRecordsReturned(), is(0L));
  }

  @Test
  void testSetAndGetNumberOfRecordsReturned() {
    collection.setNumberOfRecordsReturned(100L);

    assertThat(collection.getNumberOfRecordsReturned(), is(100L));
  }

  @Test
  void testDefaultNumberOfRecordsMatchedIsZero() {
    assertThat(collection.getNumberOfRecordsMatched(), is(0L));
  }

  @Test
  void testSetAndGetNumberOfRecordsMatched() {
    collection.setNumberOfRecordsMatched(250L);

    assertThat(collection.getNumberOfRecordsMatched(), is(250L));
  }

  @Test
  void testDefaultIsByIdIsFalse() {
    assertThat(collection.isById(), is(false));
  }

  @Test
  void testSetAndGetIsById() {
    collection.setById(true);

    assertThat(collection.isById(), is(true));
  }

  @Test
  void testDefaultElementSetTypeIsNull() {
    assertThat(collection.getElementSetType(), is(nullValue()));
  }

  @Test
  void testSetAndGetElementSetType() {
    collection.setElementSetType(ElementSetType.FULL);

    assertThat(collection.getElementSetType(), is(ElementSetType.FULL));
  }

  @Test
  void testSetAndGetElementName() {
    List<QName> elementNames = Arrays.asList(new QName("http://test", "element1"));

    collection.setElementName(elementNames);

    assertThat(collection.getElementName(), is(sameInstance(elementNames)));
  }

  @Test
  void testDefaultOutputSchemaIsNull() {
    assertThat(collection.getOutputSchema(), is(nullValue()));
  }

  @Test
  void testSetAndGetOutputSchema() {
    collection.setOutputSchema("http://www.opengis.net/cat/csw/2.0.2");

    assertThat(collection.getOutputSchema(), is("http://www.opengis.net/cat/csw/2.0.2"));
  }

  @Test
  void testSetAndGetSourceResponse() {
    SourceResponse response = mock(SourceResponse.class);

    collection.setSourceResponse(response);

    assertThat(collection.getSourceResponse(), is(sameInstance(response)));
  }

  @Test
  void testSetAndGetMimeType() {
    collection.setMimeType("application/xml");

    assertThat(collection.getMimeType(), is("application/xml"));
  }

  @Test
  void testDefaultStartPositionIsZero() {
    assertThat(collection.getStartPosition(), is(0));
  }

  @Test
  void testSetAndGetStartPosition() {
    collection.setStartPosition(10);

    assertThat(collection.getStartPosition(), is(10));
  }

  @Test
  void testSetAndGetResultType() {
    collection.setResultType(ResultType.RESULTS);

    assertThat(collection.getResultType(), is(ResultType.RESULTS));
  }

  @Test
  void testDefaultDoWriteNamespacesIsFalse() {
    assertThat(collection.isDoWriteNamespaces(), is(false));
  }

  @Test
  void testSetAndGetDoWriteNamespaces() {
    collection.setDoWriteNamespaces(true);

    assertThat(collection.isDoWriteNamespaces(), is(true));
  }

  @Test
  void testSetAndGetResource() {
    Resource resource = mock(Resource.class);

    collection.setResource(resource);

    assertThat(collection.getResource(), is(sameInstance(resource)));
  }

  @Test
  void testDefaultResourcePropertiesIsEmptyMap() {
    assertThat(collection.getResourceProperties(), is(notNullValue()));
    assertThat(collection.getResourceProperties().isEmpty(), is(true));
  }

  @Test
  void testSetAndGetResourceProperties() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    collection.setResourceProperties(properties);

    assertThat(collection.getResourceProperties(), is(sameInstance(properties)));
    assertThat(collection.getResourceProperties().get("key1"), is("value1"));
  }

  @Test
  void testSetAndGetRequest() {
    GetRecordsType request = mock(GetRecordsType.class);

    collection.setRequest(request);

    assertThat(collection.getRequest(), is(sameInstance(request)));
  }
}
