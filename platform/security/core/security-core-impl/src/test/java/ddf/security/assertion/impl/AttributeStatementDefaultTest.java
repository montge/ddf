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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;

import ddf.security.assertion.Attribute;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttributeStatementDefaultTest {

  private AttributeStatementDefault attributeStatement;

  @BeforeEach
  public void setUp() {
    attributeStatement = new AttributeStatementDefault();
  }

  @Test
  public void testConstructor() {
    AttributeStatementDefault statement = new AttributeStatementDefault();
    assertThat(statement, is(notNullValue()));
  }

  @Test
  public void testGetAttributesEmpty() {
    List<Attribute> attributes = attributeStatement.getAttributes();
    assertThat(attributes, is(notNullValue()));
    assertThat(attributes, is(empty()));
  }

  @Test
  public void testAddAttribute() {
    Attribute attribute = new AttributeDefault();
    attribute.setName("testAttribute");

    attributeStatement.addAttribute(attribute);

    List<Attribute> attributes = attributeStatement.getAttributes();
    assertThat(attributes, hasSize(1));
    assertThat(attributes, contains(attribute));
  }

  @Test
  public void testAddMultipleAttributes() {
    Attribute attribute1 = new AttributeDefault();
    attribute1.setName("attribute1");

    Attribute attribute2 = new AttributeDefault();
    attribute2.setName("attribute2");

    Attribute attribute3 = new AttributeDefault();
    attribute3.setName("attribute3");

    attributeStatement.addAttribute(attribute1);
    attributeStatement.addAttribute(attribute2);
    attributeStatement.addAttribute(attribute3);

    List<Attribute> attributes = attributeStatement.getAttributes();
    assertThat(attributes, hasSize(3));
    assertThat(attributes.get(0), is(attribute1));
    assertThat(attributes.get(1), is(attribute2));
    assertThat(attributes.get(2), is(attribute3));
  }

  @Test
  public void testGetAttributesReturnsUnmodifiableList() {
    Attribute attribute = new AttributeDefault();
    attributeStatement.addAttribute(attribute);

    List<Attribute> attributes = attributeStatement.getAttributes();
    assertThrows(UnsupportedOperationException.class, () -> attributes.add(new AttributeDefault()));
  }

  @Test
  public void testGetAttributesReturnsUnmodifiableView() {
    List<Attribute> firstList = attributeStatement.getAttributes();
    assertThat(firstList, is(empty()));

    Attribute attribute = new AttributeDefault();
    attributeStatement.addAttribute(attribute);

    // The unmodifiable view reflects changes to the underlying list
    assertThat(firstList, hasSize(1));

    // A new call should also return a view of the updated list
    List<Attribute> secondList = attributeStatement.getAttributes();
    assertThat(secondList, hasSize(1));
  }

  @Test
  public void testAddNullAttribute() {
    attributeStatement.addAttribute(null);

    List<Attribute> attributes = attributeStatement.getAttributes();
    assertThat(attributes, hasSize(1));
    // Null values are allowed in the list
    assertThat(attributes.get(0), is((Attribute) null));
  }
}
