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
import ddf.catalog.transformer.xml.binding.Base64BinaryElement;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class Base64BinaryAdapterTest {

  private Base64BinaryAdapter adapter;

  @Before
  public void setUp() {
    adapter = new Base64BinaryAdapter();
  }

  @Test
  public void testMarshalBinaryAttribute() {
    byte[] binaryData = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05};
    Attribute attribute = new AttributeImpl("thumbnail", binaryData);

    Base64BinaryElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("thumbnail"));
    assertThat(element.getValue().size(), is(1));
    assertThat(Arrays.equals(element.getValue().get(0), binaryData), is(true));
  }

  @Test
  public void testUnmarshalBinaryElement() {
    Base64BinaryElement element = new Base64BinaryElement();
    element.setName("data");

    byte[] binaryData = new byte[] {0x0A, 0x0B, 0x0C, 0x0D};
    element.getValue().add(binaryData);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("data"));
    assertThat(attribute.getValue() instanceof byte[], is(true));

    byte[] resultData = (byte[]) attribute.getValue();
    assertThat(Arrays.equals(resultData, binaryData), is(true));
  }

  @Test
  public void testMarshalMultipleBinaryValues() {
    byte[] data1 = new byte[] {0x01, 0x02};
    byte[] data2 = new byte[] {0x03, 0x04};
    byte[] data3 = new byte[] {0x05, 0x06};

    AttributeImpl attribute = new AttributeImpl("images", data1);
    attribute.addValue(data2);
    attribute.addValue(data3);

    Base64BinaryElement element = adapter.marshal(attribute);

    assertThat(element.getValue().size(), is(3));
    assertThat(Arrays.equals(element.getValue().get(0), data1), is(true));
    assertThat(Arrays.equals(element.getValue().get(1), data2), is(true));
    assertThat(Arrays.equals(element.getValue().get(2), data3), is(true));
  }

  @Test
  public void testUnmarshalMultipleBinaryValues() {
    Base64BinaryElement element = new Base64BinaryElement();
    element.setName("attachments");

    byte[] data1 = new byte[] {0x10, 0x20};
    byte[] data2 = new byte[] {0x30, 0x40};

    element.getValue().add(data1);
    element.getValue().add(data2);

    Attribute attribute = adapter.unmarshal(element);

    assertThat(attribute.getValues().size(), is(2));
  }

  @Test
  public void testMarshalEmptyByteArray() {
    byte[] emptyData = new byte[0];
    Attribute attribute = new AttributeImpl("empty", emptyData);

    Base64BinaryElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().size(), is(1));
    assertThat(element.getValue().get(0).length, is(0));
  }

  @Test
  public void testMarshalLargeBinaryData() {
    byte[] largeData = new byte[1024 * 1024]; // 1 MB
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }

    Attribute attribute = new AttributeImpl("largeFile", largeData);

    Base64BinaryElement element = adapter.marshal(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getValue().get(0).length, is(1024 * 1024));
  }

  @Test
  public void testRoundTrip() {
    byte[] originalData =
        new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC, 0x00, 0x01, 0x02, 0x03};
    Attribute originalAttribute = new AttributeImpl("binary", originalData);

    Base64BinaryElement element = adapter.marshal(originalAttribute);
    Attribute resultAttribute = adapter.unmarshal(element);

    assertThat(resultAttribute.getName(), is(originalAttribute.getName()));
    byte[] resultData = (byte[]) resultAttribute.getValue();
    assertThat(Arrays.equals(resultData, originalData), is(true));
  }

  @Test
  public void testMarshalFromStaticMethod() {
    byte[] binaryData = new byte[] {0x11, 0x22, 0x33};
    Attribute attribute = new AttributeImpl("resource", binaryData);

    Base64BinaryElement element = Base64BinaryAdapter.marshalFrom(attribute);

    assertThat(element, is(notNullValue()));
    assertThat(element.getName(), is("resource"));
    assertThat(element.getValue().size(), is(1));
  }

  @Test
  public void testUnmarshalFromStaticMethod() {
    Base64BinaryElement element = new Base64BinaryElement();
    element.setName("content");
    byte[] binaryData = new byte[] {0x44, 0x55, 0x66};
    element.getValue().add(binaryData);

    Attribute attribute = Base64BinaryAdapter.unmarshalFrom(element);

    assertThat(attribute, is(notNullValue()));
    assertThat(attribute.getName(), is("content"));
  }

  @Test
  public void testMarshalWithSpecialBytes() {
    // Test with all possible byte values
    byte[] specialBytes =
        new byte[] {
          Byte.MIN_VALUE,
          Byte.MAX_VALUE,
          0,
          -1,
          1,
          (byte) 0x00,
          (byte) 0xFF,
          (byte) 0x7F,
          (byte) 0x80
        };
    Attribute attribute = new AttributeImpl("special", specialBytes);

    Base64BinaryElement element = adapter.marshal(attribute);
    Attribute result = adapter.unmarshal(element);

    byte[] resultBytes = (byte[]) result.getValue();
    assertThat(Arrays.equals(resultBytes, specialBytes), is(true));
  }
}
