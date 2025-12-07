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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.transformer.xml.binding.BooleanElement;
import ddf.catalog.transformer.xml.binding.DoubleElement;
import ddf.catalog.transformer.xml.binding.FloatElement;
import ddf.catalog.transformer.xml.binding.IntElement;
import ddf.catalog.transformer.xml.binding.LongElement;
import ddf.catalog.transformer.xml.binding.ShortElement;
import org.junit.Test;

public class NumericAdaptersTest {

  @Test
  public void testIntAdapterMarshal() {
    IntAdapter adapter = new IntAdapter();
    Attribute attribute = new AttributeImpl("count", 42);

    IntElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("count"));
    assertThat(element.getValue().size(), is(1));
    assertThat(element.getValue().get(0), is(42));
  }

  @Test
  public void testIntAdapterUnmarshal() {
    IntAdapter adapter = new IntAdapter();
    IntElement element = new IntElement();
    element.setName("count");
    element.getValue().add(99);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("count"));
    assertThat(attribute.getValue(), is(99));
  }

  @Test
  public void testIntAdapterMultipleValues() {
    IntAdapter adapter = new IntAdapter();
    AttributeImpl attribute = new AttributeImpl("numbers", 1);
    attribute.addValue(2);
    attribute.addValue(3);

    IntElement element = adapter.marshal(attribute);

    assertThat(element.getValue().size(), is(3));
    assertThat(element.getValue().get(0), is(1));
    assertThat(element.getValue().get(1), is(2));
    assertThat(element.getValue().get(2), is(3));
  }

  @Test
  public void testLongAdapterMarshal() {
    LongAdapter adapter = new LongAdapter();
    Attribute attribute = new AttributeImpl("bigNumber", 9223372036854775807L);

    LongElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("bigNumber"));
    assertThat(element.getValue().get(0), is(9223372036854775807L));
  }

  @Test
  public void testLongAdapterUnmarshal() {
    LongAdapter adapter = new LongAdapter();
    LongElement element = new LongElement();
    element.setName("bigNumber");
    element.getValue().add(123456789L);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getValue(), is(123456789L));
  }

  @Test
  public void testDoubleAdapterMarshal() {
    DoubleAdapter adapter = new DoubleAdapter();
    Attribute attribute = new AttributeImpl("price", 99.99);

    DoubleElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("price"));
    assertThat(element.getValue().get(0), is(99.99));
  }

  @Test
  public void testDoubleAdapterUnmarshal() {
    DoubleAdapter adapter = new DoubleAdapter();
    DoubleElement element = new DoubleElement();
    element.setName("price");
    element.getValue().add(12.34);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getValue(), is(12.34));
  }

  @Test
  public void testFloatAdapterMarshal() {
    FloatAdapter adapter = new FloatAdapter();
    Attribute attribute = new AttributeImpl("temperature", 98.6f);

    FloatElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("temperature"));
    assertThat(element.getValue().get(0), is(98.6f));
  }

  @Test
  public void testFloatAdapterUnmarshal() {
    FloatAdapter adapter = new FloatAdapter();
    FloatElement element = new FloatElement();
    element.setName("temperature");
    element.getValue().add(72.5f);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getValue(), is(72.5f));
  }

  @Test
  public void testShortAdapterMarshal() {
    ShortAdapter adapter = new ShortAdapter();
    Attribute attribute = new AttributeImpl("age", (short) 25);

    ShortElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("age"));
    assertThat(element.getValue().get(0), is((short) 25));
  }

  @Test
  public void testShortAdapterUnmarshal() {
    ShortAdapter adapter = new ShortAdapter();
    ShortElement element = new ShortElement();
    element.setName("age");
    element.getValue().add((short) 30);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getValue(), is((short) 30));
  }

  @Test
  public void testBooleanAdapterMarshal() {
    BooleanAdapter adapter = new BooleanAdapter();
    Attribute attribute = new AttributeImpl("active", true);

    BooleanElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("active"));
    assertThat(element.getValue().get(0), is(true));
  }

  @Test
  public void testBooleanAdapterUnmarshal() {
    BooleanAdapter adapter = new BooleanAdapter();
    BooleanElement element = new BooleanElement();
    element.setName("active");
    element.getValue().add(false);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getValue(), is(false));
  }

  @Test
  public void testBooleanAdapterMultipleValues() {
    BooleanAdapter adapter = new BooleanAdapter();
    AttributeImpl attribute = new AttributeImpl("flags", true);
    attribute.addValue(false);
    attribute.addValue(true);

    BooleanElement element = adapter.marshal(attribute);

    assertThat(element.getValue().size(), is(3));
    assertThat(element.getValue().get(0), is(true));
    assertThat(element.getValue().get(1), is(false));
    assertThat(element.getValue().get(2), is(true));
  }

  @Test
  public void testDoubleAdapterRoundTrip() {
    DoubleAdapter adapter = new DoubleAdapter();
    Attribute original = new AttributeImpl("value", 3.14159);

    DoubleElement element = adapter.marshal(original);
    Attribute result = adapter.unmarshal(element);

    assertThat(result.getName(), is(original.getName()));
    assertThat(result.getValue(), is(original.getValue()));
  }

  @Test
  public void testIntAdapterStaticMethods() {
    Attribute attribute = new AttributeImpl("number", 100);

    IntElement element = IntAdapter.marshalFrom(attribute);
    Attribute result = IntAdapter.unmarshalFrom(element);

    assertThat(element.getName(), is("number"));
    assertThat(result.getValue(), is(100));
  }

  @Test
  public void testLongAdapterStaticMethods() {
    Attribute attribute = new AttributeImpl("bigNum", 999999999L);

    LongElement element = LongAdapter.marshalFrom(attribute);
    Attribute result = LongAdapter.unmarshalFrom(element);

    assertThat(element.getName(), is("bigNum"));
    assertThat(result.getValue(), is(999999999L));
  }
}
