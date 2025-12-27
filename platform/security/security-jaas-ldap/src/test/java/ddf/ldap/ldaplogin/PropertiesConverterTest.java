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
package ddf.ldap.ldaplogin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.blueprint.container.ReifiedType;

class PropertiesConverterTest {

  private PropertiesConverter converter;

  private ReifiedType propertiesReifiedType;

  private ReifiedType mapReifiedType;

  @BeforeEach
  void setUp() {
    converter = new PropertiesConverter();
    propertiesReifiedType = mock(ReifiedType.class);
    mapReifiedType = mock(ReifiedType.class);
  }

  @Test
  void testCanConvertWithStringToProperties() {
    when(propertiesReifiedType.getRawClass()).thenReturn((Class) Properties.class);

    boolean result = converter.canConvert("test", propertiesReifiedType);

    assertThat(result, is(true));
  }

  @Test
  void testCanConvertWithNonStringSource() {
    when(propertiesReifiedType.getRawClass()).thenReturn((Class) Properties.class);

    boolean result = converter.canConvert(Integer.valueOf(123), propertiesReifiedType);

    assertThat(result, is(false));
  }

  @Test
  void testCanConvertWithNonPropertiesTarget() {
    when(mapReifiedType.getRawClass()).thenReturn((Class) Map.class);

    boolean result = converter.canConvert("test", mapReifiedType);

    assertThat(result, is(false));
  }

  @Test
  void testConvertSimpleProperty() throws Exception {
    Object result = converter.convert("key=value", propertiesReifiedType);

    assertThat(result, is(notNullValue()));
    assertThat(result, instanceOf(Properties.class));
    Properties properties = (Properties) result;
    assertThat(properties, hasEntry("key", "value"));
  }

  @Test
  void testConvertMultipleProperties() throws Exception {
    String input = "key1=value1\nkey2=value2\nkey3=value3";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties.size(), is(3));
    assertThat(properties, hasEntry("key1", "value1"));
    assertThat(properties, hasEntry("key2", "value2"));
    assertThat(properties, hasEntry("key3", "value3"));
  }

  @Test
  void testConvertWithCarriageReturnNewline() throws Exception {
    String input = "key1=value1\r\nkey2=value2";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties.size(), is(2));
    assertThat(properties, hasEntry("key1", "value1"));
    assertThat(properties, hasEntry("key2", "value2"));
  }

  @Test
  void testConvertWithBackslashReplacement() throws Exception {
    String input = "path=C:\\Users\\Documents";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties, hasEntry("path", "C:/Users/Documents"));
  }

  @Test
  void testConvertTrimsWhitespace() throws Exception {
    String input = "  key  =  value  ";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties, hasEntry("key", "value"));
  }

  @Test
  void testConvertIgnoresLinesWithoutEquals() throws Exception {
    String input = "key=value\ninvalidline\nkey2=value2";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties.size(), is(2));
    assertThat(properties, hasEntry("key", "value"));
    assertThat(properties, hasEntry("key2", "value2"));
  }

  @Test
  void testConvertIgnoresLinesStartingWithEquals() throws Exception {
    String input = "=nokey\nkey=value";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties.size(), is(1));
    assertThat(properties, hasEntry("key", "value"));
  }

  @Test
  void testConvertWithValueContainingEquals() throws Exception {
    String input = "equation=2+2=4";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties, hasEntry("equation", "2+2=4"));
  }

  @Test
  void testConvertEmptyString() throws Exception {
    String input = "";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties.isEmpty(), is(true));
  }

  @Test
  void testConvertEmptyValue() throws Exception {
    String input = "key=";

    Object result = converter.convert(input, propertiesReifiedType);

    Properties properties = (Properties) result;
    assertThat(properties, hasEntry("key", ""));
  }
}
