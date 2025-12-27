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
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transformer.xml.binding.FloatElement;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FloatAdapterTest {

  private FloatAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new FloatAdapter();
  }

  @Test
  void testMarshalSingleValue() {
    Attribute attribute = new AttributeImpl("testAttr", 3.14f);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("testAttr"));
    assertThat(element.getValue(), hasSize(1));
    assertThat((double) element.getValue().get(0), closeTo(3.14, 0.001));
  }

  @Test
  void testMarshalZeroValue() {
    Attribute attribute = new AttributeImpl("zeroAttr", 0.0f);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().get(0), is(0.0f));
  }

  @Test
  void testMarshalNegativeValue() {
    Attribute attribute = new AttributeImpl("negativeAttr", -2.5f);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().get(0), is(-2.5f));
  }

  @Test
  void testMarshalMultipleValues() {
    AttributeImpl attribute = new AttributeImpl("multiAttr", 1.1f);
    attribute.addValue(2.2f);
    attribute.addValue(3.3f);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("multiAttr"));
    assertThat(element.getValue(), hasSize(3));
  }

  @Test
  void testUnmarshalSingleValue() {
    FloatElement element = new FloatElement();
    element.setName("testAttr");
    element.getValue().add(1.5f);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("testAttr"));
    assertThat(attribute.getValue(), is(1.5f));
  }

  @Test
  void testUnmarshalMultipleValues() {
    FloatElement element = new FloatElement();
    element.setName("multiAttr");
    element.getValue().addAll(Arrays.asList(1.0f, 2.0f, 3.0f));

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("multiAttr"));
    assertThat(attribute.getValues(), hasSize(3));
  }

  @Test
  void testUnmarshalEmptyValues() {
    FloatElement element = new FloatElement();
    element.setName("emptyAttr");

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(nullValue()));
  }

  @Test
  void testMarshalFromStaticMethod() {
    Attribute attribute = new AttributeImpl("staticTest", 9.99f);

    FloatElement element = FloatAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("staticTest"));
  }

  @Test
  void testUnmarshalFromStaticMethod() {
    FloatElement element = new FloatElement();
    element.setName("staticTest");
    element.getValue().add(5.5f);

    Attribute attribute = FloatAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("staticTest"));
    assertThat(attribute.getValue(), is(5.5f));
  }

  @Test
  void testRoundTrip() {
    Attribute original = new AttributeImpl("roundTrip", 123.456f);

    FloatElement element = adapter.marshal(original);
    Attribute result = adapter.unmarshal(element);

    assertThat(result.getName(), is(original.getName()));
    assertThat(result.getValue(), is(original.getValue()));
  }

  @Test
  void testMarshalMaxFloatValue() {
    Attribute attribute = new AttributeImpl("maxFloat", Float.MAX_VALUE);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element.getValue().get(0), is(Float.MAX_VALUE));
  }

  @Test
  void testMarshalMinFloatValue() {
    Attribute attribute = new AttributeImpl("minFloat", Float.MIN_VALUE);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element.getValue().get(0), is(Float.MIN_VALUE));
  }
}
