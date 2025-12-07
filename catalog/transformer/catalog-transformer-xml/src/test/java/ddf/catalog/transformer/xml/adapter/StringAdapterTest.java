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
import ddf.catalog.transformer.xml.binding.StringElement;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class StringAdapterTest {

  private StringAdapter adapter;

  @Before
  public void setUp() {
    adapter = new StringAdapter();
  }

  @Test
  public void testMarshalSingleValue() {
    Attribute attribute = new AttributeImpl("title", "Test Title");

    StringElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("title"));
    assertThat(element.getValue().size(), is(1));
    assertThat(element.getValue().get(0), is("Test Title"));
  }

  @Test
  public void testMarshalMultipleValues() {
    AttributeImpl attribute = new AttributeImpl("keywords", "keyword1");
    attribute.addValue("keyword2");
    attribute.addValue("keyword3");

    StringElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("keywords"));
    assertThat(element.getValue().size(), is(3));
    assertThat(element.getValue().get(0), is("keyword1"));
    assertThat(element.getValue().get(1), is("keyword2"));
    assertThat(element.getValue().get(2), is("keyword3"));
  }

  @Test
  public void testMarshalFromStaticMethod() {
    Attribute attribute = new AttributeImpl("description", "Test Description");

    StringElement element = StringAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("description"));
    assertThat(element.getValue().get(0), is("Test Description"));
  }

  @Test
  public void testUnmarshalSingleValue() {
    StringElement element = new StringElement();
    element.setName("title");
    element.getValue().add("Test Title");

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("title"));
    assertThat(attribute.getValue(), is("Test Title"));
  }

  @Test
  public void testUnmarshalMultipleValues() {
    StringElement element = new StringElement();
    element.setName("keywords");
    element.getValue().addAll(Arrays.asList("keyword1", "keyword2", "keyword3"));

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("keywords"));
    assertThat(attribute.getValues().size(), is(3));
    assertThat(attribute.getValues().contains("keyword1"), is(true));
    assertThat(attribute.getValues().contains("keyword2"), is(true));
    assertThat(attribute.getValues().contains("keyword3"), is(true));
  }

  @Test
  public void testUnmarshalFromStaticMethod() {
    StringElement element = new StringElement();
    element.setName("description");
    element.getValue().add("Test Description");

    Attribute attribute = StringAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("description"));
    assertThat(attribute.getValue(), is("Test Description"));
  }

  @Test
  public void testMarshalWithEmptyString() {
    Attribute attribute = new AttributeImpl("empty", "");

    StringElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("empty"));
    assertThat(element.getValue().size(), is(1));
    assertThat(element.getValue().get(0), is(""));
  }

  @Test
  public void testRoundTrip() {
    Attribute original = new AttributeImpl("test", "value1");
    ((AttributeImpl) original).addValue("value2");

    StringElement element = adapter.marshal(original);
    Attribute result = adapter.unmarshal(element);

    assertThat(result.getName(), is(original.getName()));
    assertThat(result.getValues().size(), is(original.getValues().size()));
  }
}
