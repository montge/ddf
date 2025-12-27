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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transformer.xml.binding.IntElement;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntAdapterTest {

  private IntAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new IntAdapter();
  }

  @Test
  void testMarshalSingleValue() {
    Attribute attribute = new AttributeImpl("testAttr", 42);

    IntElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("testAttr"));
    assertThat(element.getValue(), hasSize(1));
    assertThat(element.getValue().get(0), is(42));
  }

  @Test
  void testMarshalZeroValue() {
    Attribute attribute = new AttributeImpl("zeroAttr", 0);

    IntElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().get(0), is(0));
  }

  @Test
  void testMarshalNegativeValue() {
    Attribute attribute = new AttributeImpl("negativeAttr", -100);

    IntElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().get(0), is(-100));
  }

  @Test
  void testMarshalMultipleValues() {
    AttributeImpl attribute = new AttributeImpl("multiAttr", 1);
    attribute.addValue(2);
    attribute.addValue(3);

    IntElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("multiAttr"));
    assertThat(element.getValue(), hasSize(3));
  }

  @Test
  void testUnmarshalSingleValue() {
    IntElement element = new IntElement();
    element.setName("testAttr");
    element.getValue().add(42);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("testAttr"));
    assertThat(attribute.getValue(), is(42));
  }

  @Test
  void testUnmarshalMultipleValues() {
    IntElement element = new IntElement();
    element.setName("multiAttr");
    element.getValue().addAll(Arrays.asList(10, 20, 30));

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("multiAttr"));
    assertThat(attribute.getValues(), hasSize(3));
  }

  @Test
  void testUnmarshalEmptyValues() {
    IntElement element = new IntElement();
    element.setName("emptyAttr");

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(nullValue()));
  }

  @Test
  void testMarshalFromStaticMethod() {
    Attribute attribute = new AttributeImpl("staticTest", 123);

    IntElement element = IntAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("staticTest"));
    assertThat(element.getValue().get(0), is(123));
  }

  @Test
  void testUnmarshalFromStaticMethod() {
    IntElement element = new IntElement();
    element.setName("staticTest");
    element.getValue().add(456);

    Attribute attribute = IntAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("staticTest"));
    assertThat(attribute.getValue(), is(456));
  }

  @Test
  void testRoundTrip() {
    Attribute original = new AttributeImpl("roundTrip", Integer.MAX_VALUE);

    IntElement element = adapter.marshal(original);
    Attribute result = adapter.unmarshal(element);

    assertThat(result.getName(), is(original.getName()));
    assertThat(result.getValue(), is(original.getValue()));
  }

  @Test
  void testMarshalMaxIntValue() {
    Attribute attribute = new AttributeImpl("maxInt", Integer.MAX_VALUE);

    IntElement element = adapter.marshal(attribute);

    assertThat(element.getValue().get(0), is(Integer.MAX_VALUE));
  }

  @Test
  void testMarshalMinIntValue() {
    Attribute attribute = new AttributeImpl("minInt", Integer.MIN_VALUE);

    IntElement element = adapter.marshal(attribute);

    assertThat(element.getValue().get(0), is(Integer.MIN_VALUE));
  }
}
