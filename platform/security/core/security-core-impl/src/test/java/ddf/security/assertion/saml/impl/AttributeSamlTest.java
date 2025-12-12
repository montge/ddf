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
package ddf.security.assertion.saml.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import ddf.security.assertion.Attribute;
import ddf.security.assertion.impl.AttributeDefault;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AttributeSamlTest {

  @Test
  public void testCanInstantiate() {
    AttributeSaml attribute = new AttributeSaml();

    assertThat(attribute, is(notNullValue()));
  }

  @Test
  public void testExtendsAttributeDefault() {
    AttributeSaml attribute = new AttributeSaml();

    assertThat(attribute, is(instanceOf(AttributeDefault.class)));
  }

  @Test
  public void testImplementsAttributeInterface() {
    AttributeSaml attribute = new AttributeSaml();

    assertThat(attribute, is(instanceOf(Attribute.class)));
  }

  @Test
  public void testInheritedNameProperty() {
    AttributeSaml attribute = new AttributeSaml();
    attribute.setName("testName");

    assertThat(attribute.getName(), is("testName"));
  }

  @Test
  public void testInheritedNameFormatProperty() {
    AttributeSaml attribute = new AttributeSaml();
    attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

    assertThat(attribute.getNameFormat(), is("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"));
  }

  @Test
  public void testInheritedValuesProperty() {
    AttributeSaml attribute = new AttributeSaml();
    attribute.setValues(Arrays.asList("value1", "value2"));

    assertThat(attribute.getValues(), hasSize(2));
  }

  @Test
  public void testInheritedAddValue() {
    AttributeSaml attribute = new AttributeSaml();
    attribute.addValue("newValue");

    assertThat(attribute.getValues(), hasSize(1));
    assertThat(attribute.getValues().get(0), is("newValue"));
  }

  @Test
  public void testDefaultStateIsNull() {
    AttributeSaml attribute = new AttributeSaml();

    assertThat(attribute.getName(), is(nullValue()));
    assertThat(attribute.getNameFormat(), is(nullValue()));
    assertThat(attribute.getValues(), hasSize(0));
  }
}
