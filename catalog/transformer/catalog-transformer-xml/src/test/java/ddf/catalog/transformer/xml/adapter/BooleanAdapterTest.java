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
import ddf.catalog.transformer.xml.binding.BooleanElement;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BooleanAdapterTest {

  private BooleanAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new BooleanAdapter();
  }

  @Test
  void testMarshalSingleTrueValue() {
    Attribute attribute = new AttributeImpl("testAttr", true);

    BooleanElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("testAttr"));
    assertThat(element.getValue(), hasSize(1));
    assertThat(element.getValue().get(0), is(true));
  }

  @Test
  void testMarshalSingleFalseValue() {
    Attribute attribute = new AttributeImpl("testAttr", false);

    BooleanElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("testAttr"));
    assertThat(element.getValue(), hasSize(1));
    assertThat(element.getValue().get(0), is(false));
  }

  @Test
  void testMarshalMultipleValues() {
    AttributeImpl attribute = new AttributeImpl("multiAttr", true);
    attribute.addValue(false);
    attribute.addValue(true);

    BooleanElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("multiAttr"));
    assertThat(element.getValue(), hasSize(3));
  }

  @Test
  void testUnmarshalSingleTrueValue() {
    BooleanElement element = new BooleanElement();
    element.setName("testAttr");
    element.getValue().add(true);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("testAttr"));
    assertThat(attribute.getValue(), is(true));
  }

  @Test
  void testUnmarshalSingleFalseValue() {
    BooleanElement element = new BooleanElement();
    element.setName("testAttr");
    element.getValue().add(false);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("testAttr"));
    assertThat(attribute.getValue(), is(false));
  }

  @Test
  void testUnmarshalMultipleValues() {
    BooleanElement element = new BooleanElement();
    element.setName("multiAttr");
    element.getValue().addAll(Arrays.asList(true, false, true));

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("multiAttr"));
    assertThat(attribute.getValues(), hasSize(3));
  }

  @Test
  void testUnmarshalEmptyValues() {
    BooleanElement element = new BooleanElement();
    element.setName("emptyAttr");

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(nullValue()));
  }

  @Test
  void testMarshalFromStaticMethod() {
    Attribute attribute = new AttributeImpl("staticTest", true);

    BooleanElement element = BooleanAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("staticTest"));
  }

  @Test
  void testUnmarshalFromStaticMethod() {
    BooleanElement element = new BooleanElement();
    element.setName("staticTest");
    element.getValue().add(false);

    Attribute attribute = BooleanAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("staticTest"));
    assertThat(attribute.getValue(), is(false));
  }

  @Test
  void testRoundTrip() {
    Attribute original = new AttributeImpl("roundTrip", true);

    BooleanElement element = adapter.marshal(original);
    Attribute result = adapter.unmarshal(element);

    assertThat(result.getName(), is(original.getName()));
    assertThat(result.getValue(), is(original.getValue()));
  }
}
