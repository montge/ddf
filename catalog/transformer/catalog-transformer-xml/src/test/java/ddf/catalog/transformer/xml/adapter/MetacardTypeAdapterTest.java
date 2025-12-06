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
package ddf.catalog.transformer.xml.adapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MetacardTypeAdapterTest {

  private MetacardTypeAdapter adapter;

  private MetacardType mockMetacardType1;

  private MetacardType mockMetacardType2;

  private List<MetacardType> metacardTypes;

  @Before
  public void setUp() {
    mockMetacardType1 = mock(MetacardType.class);
    when(mockMetacardType1.getName()).thenReturn("custom.type.1");

    mockMetacardType2 = mock(MetacardType.class);
    when(mockMetacardType2.getName()).thenReturn("custom.type.2");

    metacardTypes = new ArrayList<>(Arrays.asList(mockMetacardType1, mockMetacardType2));
    adapter = new MetacardTypeAdapter(metacardTypes);
  }

  @Test
  public void testConstructorWithTypes() {
    MetacardTypeAdapter adapterWithTypes = new MetacardTypeAdapter(metacardTypes);
    assertThat(adapterWithTypes, is(notNullValue()));
  }

  @Test
  public void testConstructorWithoutTypes() {
    MetacardTypeAdapter adapterWithoutTypes = new MetacardTypeAdapter();
    assertThat(adapterWithoutTypes, is(notNullValue()));
  }

  @Test
  public void testMarshalValidMetacardType() throws CatalogTransformerException {
    String result = adapter.marshal(mockMetacardType1);
    assertThat(result, is(equalTo("custom.type.1")));
  }

  @Test(expected = CatalogTransformerException.class)
  public void testMarshalNullMetacardType() throws CatalogTransformerException {
    adapter.marshal(null);
  }

  @Test
  public void testMarshalBasicMetacardType() throws CatalogTransformerException {
    String result = adapter.marshal(MetacardImpl.BASIC_METACARD);
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD.getName())));
  }

  @Test
  public void testUnmarshalWithMatchingType() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal("custom.type.1");
    assertThat(result, is(equalTo(mockMetacardType1)));
  }

  @Test
  public void testUnmarshalWithSecondMatchingType() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal("custom.type.2");
    assertThat(result, is(equalTo(mockMetacardType2)));
  }

  @Test
  public void testUnmarshalWithNullTypeName() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal(null);
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithEmptyTypeName() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal("");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithBlankTypeName() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal("   ");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithBasicMetacardTypeName() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal(MetacardImpl.BASIC_METACARD.getName());
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithUnknownTypeName() throws CatalogTransformerException {
    MetacardType result = adapter.unmarshal("unknown.type");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithNullTypesList() throws CatalogTransformerException {
    MetacardTypeAdapter adapterWithNullList = new MetacardTypeAdapter(null);
    MetacardType result = adapterWithNullList.unmarshal("custom.type.1");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithEmptyTypesList() throws CatalogTransformerException {
    MetacardTypeAdapter adapterWithEmptyList = new MetacardTypeAdapter(Collections.emptyList());
    MetacardType result = adapterWithEmptyList.unmarshal("custom.type.1");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalCaseSensitive() throws CatalogTransformerException {
    // Type names are case-sensitive, so this should not match
    MetacardType result = adapter.unmarshal("CUSTOM.TYPE.1");
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithMultipleTypesFirstMatch() throws CatalogTransformerException {
    // When the type name matches the first type in the list
    MetacardType result = adapter.unmarshal("custom.type.1");
    assertThat(result, is(equalTo(mockMetacardType1)));
  }

  @Test
  public void testUnmarshalWithMultipleTypesSecondMatch() throws CatalogTransformerException {
    // When the type name matches the second type in the list
    MetacardType result = adapter.unmarshal("custom.type.2");
    assertThat(result, is(equalTo(mockMetacardType2)));
  }

  @Test
  public void testRoundTripMarshalUnmarshal() throws CatalogTransformerException {
    String marshaled = adapter.marshal(mockMetacardType1);
    MetacardType unmarshaled = adapter.unmarshal(marshaled);
    assertThat(unmarshaled, is(equalTo(mockMetacardType1)));
  }

  @Test
  public void testRoundTripMarshalUnmarshalBasicMetacard() throws CatalogTransformerException {
    String marshaled = adapter.marshal(MetacardImpl.BASIC_METACARD);
    MetacardType unmarshaled = adapter.unmarshal(marshaled);
    assertThat(unmarshaled, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testUnmarshalWithTypeNotInList() throws CatalogTransformerException {
    // Create a type that's not in the list
    MetacardType result = adapter.unmarshal("nonexistent.type");
    // Should return BASIC_METACARD as fallback
    assertThat(result, is(equalTo(MetacardImpl.BASIC_METACARD)));
  }

  @Test
  public void testMarshalDifferentTypes() throws CatalogTransformerException {
    String result1 = adapter.marshal(mockMetacardType1);
    String result2 = adapter.marshal(mockMetacardType2);

    assertThat(result1, is(equalTo("custom.type.1")));
    assertThat(result2, is(equalTo("custom.type.2")));
  }

  @Test
  public void testUnmarshalPreservesTypeIdentity() throws CatalogTransformerException {
    // Unmarshal twice with the same name should return the same instance
    MetacardType result1 = adapter.unmarshal("custom.type.1");
    MetacardType result2 = adapter.unmarshal("custom.type.1");
    assertThat(result1, is(equalTo(result2)));
  }
}
