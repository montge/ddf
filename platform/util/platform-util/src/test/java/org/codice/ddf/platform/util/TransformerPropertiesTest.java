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
package org.codice.ddf.platform.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;

import java.util.Map.Entry;
import java.util.Set;
import javax.xml.transform.ErrorListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransformerPropertiesTest {

  private TransformerProperties transformerProperties;

  @BeforeEach
  public void setUp() {
    transformerProperties = new TransformerProperties();
  }

  @Test
  public void testConstructor() {
    assertThat(transformerProperties, notNullValue());
  }

  @Test
  public void testGetOutputPropertiesWhenEmpty() {
    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, notNullValue());
    assertThat(properties, empty());
  }

  @Test
  public void testAddOutputProperty() {
    transformerProperties.addOutputProperty("key1", "value1");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));
  }

  @Test
  public void testAddMultipleOutputProperties() {
    transformerProperties.addOutputProperty("key1", "value1");
    transformerProperties.addOutputProperty("key2", "value2");
    transformerProperties.addOutputProperty("key3", "value3");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(3));
  }

  @Test
  public void testAddOutputPropertyWithSameKey() {
    transformerProperties.addOutputProperty("key1", "value1");
    transformerProperties.addOutputProperty("key1", "value2");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));

    // Should contain the last value
    String value = properties.iterator().next().getValue();
    assertThat(value, is("value2"));
  }

  @Test
  public void testAddOutputPropertyRetrievesCorrectValue() {
    transformerProperties.addOutputProperty("encoding", "UTF-8");
    transformerProperties.addOutputProperty("indent", "yes");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();

    boolean foundEncoding = false;
    boolean foundIndent = false;

    for (Entry<String, String> entry : properties) {
      if (entry.getKey().equals("encoding") && entry.getValue().equals("UTF-8")) {
        foundEncoding = true;
      }
      if (entry.getKey().equals("indent") && entry.getValue().equals("yes")) {
        foundIndent = true;
      }
    }

    assertThat(foundEncoding, is(true));
    assertThat(foundIndent, is(true));
  }

  @Test
  public void testGetErrorListenerWhenNotSet() {
    ErrorListener listener = transformerProperties.getErrorListener();
    assertThat(listener, nullValue());
  }

  @Test
  public void testSetErrorListener() {
    ErrorListener mockListener = mock(ErrorListener.class);
    transformerProperties.setErrorListener(mockListener);

    ErrorListener retrievedListener = transformerProperties.getErrorListener();
    assertThat(retrievedListener, notNullValue());
    assertThat(retrievedListener, is(mockListener));
  }

  @Test
  public void testSetErrorListenerToNull() {
    ErrorListener mockListener = mock(ErrorListener.class);
    transformerProperties.setErrorListener(mockListener);
    transformerProperties.setErrorListener(null);

    ErrorListener retrievedListener = transformerProperties.getErrorListener();
    assertThat(retrievedListener, nullValue());
  }

  @Test
  public void testSetErrorListenerMultipleTimes() {
    ErrorListener listener1 = mock(ErrorListener.class);
    ErrorListener listener2 = mock(ErrorListener.class);

    transformerProperties.setErrorListener(listener1);
    transformerProperties.setErrorListener(listener2);

    ErrorListener retrievedListener = transformerProperties.getErrorListener();
    assertThat(retrievedListener, is(listener2));
  }

  @Test
  public void testAddOutputPropertyWithNullKey() {
    transformerProperties.addOutputProperty(null, "value");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));
  }

  @Test
  public void testAddOutputPropertyWithNullValue() {
    transformerProperties.addOutputProperty("key", null);

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));
  }

  @Test
  public void testAddOutputPropertyWithEmptyKey() {
    transformerProperties.addOutputProperty("", "value");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));
  }

  @Test
  public void testAddOutputPropertyWithEmptyValue() {
    transformerProperties.addOutputProperty("key", "");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));
  }

  @Test
  public void testAddOutputPropertyWithSpecialCharacters() {
    transformerProperties.addOutputProperty("special@key#1", "value$with%special&chars");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(1));

    Entry<String, String> entry = properties.iterator().next();
    assertThat(entry.getKey(), is("special@key#1"));
    assertThat(entry.getValue(), is("value$with%special&chars"));
  }

  @Test
  public void testGetOutputPropertiesReturnsEntrySet() {
    transformerProperties.addOutputProperty("key1", "value1");
    transformerProperties.addOutputProperty("key2", "value2");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();

    assertThat(properties, notNullValue());
    for (Entry<String, String> entry : properties) {
      assertThat(entry.getKey(), notNullValue());
      assertThat(entry.getValue(), notNullValue());
    }
  }

  @Test
  public void testPropertiesIndependence() {
    TransformerProperties props1 = new TransformerProperties();
    TransformerProperties props2 = new TransformerProperties();

    props1.addOutputProperty("key1", "value1");
    props2.addOutputProperty("key2", "value2");

    assertThat(props1.getOutputProperties(), hasSize(1));
    assertThat(props2.getOutputProperties(), hasSize(1));
  }

  @Test
  public void testCompleteWorkflow() {
    // Test a complete workflow of setting properties and error listener
    transformerProperties.addOutputProperty("method", "xml");
    transformerProperties.addOutputProperty("encoding", "UTF-8");
    transformerProperties.addOutputProperty("indent", "yes");
    transformerProperties.addOutputProperty("omit-xml-declaration", "no");

    ErrorListener listener = mock(ErrorListener.class);
    transformerProperties.setErrorListener(listener);

    assertThat(transformerProperties.getOutputProperties(), hasSize(4));
    assertThat(transformerProperties.getErrorListener(), is(listener));
  }

  @Test
  public void testAddOutputPropertyWithXmlProperties() {
    // Test with common XSLT output properties
    transformerProperties.addOutputProperty("method", "xml");
    transformerProperties.addOutputProperty("version", "1.0");
    transformerProperties.addOutputProperty("encoding", "UTF-8");
    transformerProperties.addOutputProperty("omit-xml-declaration", "no");
    transformerProperties.addOutputProperty("standalone", "yes");
    transformerProperties.addOutputProperty("indent", "yes");

    Set<Entry<String, String>> properties = transformerProperties.getOutputProperties();
    assertThat(properties, hasSize(6));
  }
}
