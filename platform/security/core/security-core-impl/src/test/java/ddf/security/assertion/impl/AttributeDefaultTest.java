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
package ddf.security.assertion.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttributeDefaultTest {

  private AttributeDefault attribute;

  @Before
  public void setUp() {
    attribute = new AttributeDefault();
  }

  @Test
  public void testGetName() {
    attribute.setName("testAttribute");

    assertThat(attribute.getName(), is("testAttribute"));
  }

  @Test
  public void testSetName() {
    attribute.setName("myAttribute");

    assertThat(attribute.getName(), is("myAttribute"));
  }

  @Test
  public void testGetNameFormat() {
    attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

    assertThat(attribute.getNameFormat(), is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
  }

  @Test
  public void testSetNameFormat() {
    attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");

    assertThat(attribute.getNameFormat(), is("urn:oasis:names:tc:SAML:2.0:attrname-format:basic"));
  }

  @Test
  public void testGetValuesInitiallyEmpty() {
    List<String> values = attribute.getValues();

    assertThat(values, is(notNullValue()));
    assertThat(values, hasSize(0));
  }

  @Test
  public void testSetValues() {
    List<String> values = Arrays.asList("value1", "value2", "value3");
    attribute.setValues(values);

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(3));
    assertThat(result, containsInAnyOrder("value1", "value2", "value3"));
  }

  @Test
  public void testSetValuesReplacesExisting() {
    attribute.setValues(Arrays.asList("oldValue1", "oldValue2"));
    attribute.setValues(Arrays.asList("newValue1", "newValue2"));

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("newValue1", "newValue2"));
  }

  @Test
  public void testAddValue() {
    attribute.addValue("value1");
    attribute.addValue("value2");

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(2));
    assertThat(result, containsInAnyOrder("value1", "value2"));
  }

  @Test
  public void testAddValueToExistingValues() {
    attribute.setValues(Arrays.asList("value1", "value2"));
    attribute.addValue("value3");

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(3));
    assertThat(result, containsInAnyOrder("value1", "value2", "value3"));
  }

  @Test
  public void testAddNullValue() {
    attribute.addValue(null);

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(1));
    assertThat(result.contains(null), is(true));
  }

  @Test
  public void testAddDuplicateValues() {
    attribute.addValue("value1");
    attribute.addValue("value1");

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is("value1"));
  }

  @Test
  public void testGetValuesReturnsUnmodifiableList() {
    attribute.addValue("value1");

    List<String> result = attribute.getValues();

    try {
      result.add("value2");
      assertThat("Should have thrown UnsupportedOperationException", false);
    } catch (UnsupportedOperationException e) {
      assertThat(attribute.getValues(), hasSize(1));
    }
  }

  @Test
  public void testSetValuesWithEmptyList() {
    attribute.setValues(Arrays.asList("value1", "value2"));
    attribute.setValues(Arrays.asList());

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(0));
  }

  @Test
  public void testSetValuesWithSingleValue() {
    attribute.setValues(Arrays.asList("singleValue"));

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is("singleValue"));
  }

  @Test
  public void testDefaultConstructor() {
    AttributeDefault newAttribute = new AttributeDefault();

    assertThat(newAttribute.getName(), is(nullValue()));
    assertThat(newAttribute.getNameFormat(), is(nullValue()));
    assertThat(newAttribute.getValues(), is(notNullValue()));
    assertThat(newAttribute.getValues(), hasSize(0));
  }

  @Test
  public void testSetNameWithNull() {
    attribute.setName("testName");
    attribute.setName(null);

    assertThat(attribute.getName(), is(nullValue()));
  }

  @Test
  public void testSetNameFormatWithNull() {
    attribute.setNameFormat("testFormat");
    attribute.setNameFormat(null);

    assertThat(attribute.getNameFormat(), is(nullValue()));
  }

  @Test
  public void testSetValuesWithNullValues() {
    attribute.setValues(Arrays.asList("value1", null, "value2"));

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(3));
    assertThat(result.contains(null), is(true));
    assertThat(result, containsInAnyOrder("value1", null, "value2"));
  }

  @Test
  public void testMultipleSetValuesOperations() {
    attribute.setValues(Arrays.asList("a", "b"));
    attribute.setValues(Arrays.asList("c", "d", "e"));
    attribute.setValues(Arrays.asList("f"));

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is("f"));
  }

  @Test
  public void testGetValuesAfterMultipleAdditions() {
    attribute.addValue("value1");
    attribute.addValue("value2");
    attribute.addValue("value3");
    attribute.addValue("value4");

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(4));
  }

  @Test
  public void testSetValuesDoesNotAffectOriginalList() {
    List<String> originalList = Arrays.asList("value1", "value2");
    attribute.setValues(originalList);

    List<String> result = attribute.getValues();

    assertThat(result, is(notNullValue()));
    assertThat(result == originalList, is(false));
  }

  @Test
  public void testSetNameWithEmptyString() {
    attribute.setName("");

    assertThat(attribute.getName(), is(""));
  }

  @Test
  public void testSetNameFormatWithEmptyString() {
    attribute.setNameFormat("");

    assertThat(attribute.getNameFormat(), is(""));
  }

  @Test
  public void testAddValueWithEmptyString() {
    attribute.addValue("");

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(1));
    assertThat(result.get(0), is(""));
  }

  @Test
  public void testSetValuesWithSpecialCharacters() {
    List<String> values =
        Arrays.asList(
            "value@special",
            "value#with#hashes",
            "value with spaces",
            "value\twith\ttabs",
            "value\nwith\nnewlines");
    attribute.setValues(values);

    List<String> result = attribute.getValues();

    assertThat(result, hasSize(5));
    assertThat(result, containsInAnyOrder(values.toArray(new String[0])));
  }
}
