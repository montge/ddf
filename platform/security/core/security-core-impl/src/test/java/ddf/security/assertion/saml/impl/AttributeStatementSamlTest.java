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

import ddf.security.assertion.AttributeStatement;
import ddf.security.assertion.impl.AttributeStatementDefault;
import org.junit.jupiter.api.Test;

public class AttributeStatementSamlTest {

  @Test
  public void testCanInstantiate() {
    AttributeStatementSaml statement = new AttributeStatementSaml();

    assertThat(statement, is(notNullValue()));
  }

  @Test
  public void testExtendsAttributeStatementDefault() {
    AttributeStatementSaml statement = new AttributeStatementSaml();

    assertThat(statement, is(instanceOf(AttributeStatementDefault.class)));
  }

  @Test
  public void testImplementsAttributeStatementInterface() {
    AttributeStatementSaml statement = new AttributeStatementSaml();

    assertThat(statement, is(instanceOf(AttributeStatement.class)));
  }

  @Test
  public void testInheritedGetAttributesInitiallyEmpty() {
    AttributeStatementSaml statement = new AttributeStatementSaml();

    assertThat(statement.getAttributes(), is(notNullValue()));
    assertThat(statement.getAttributes(), hasSize(0));
  }

  @Test
  public void testInheritedAddAttribute() {
    AttributeStatementSaml statement = new AttributeStatementSaml();
    AttributeSaml attribute = new AttributeSaml();
    attribute.setName("testAttribute");

    statement.addAttribute(attribute);

    assertThat(statement.getAttributes(), hasSize(1));
    assertThat(statement.getAttributes().get(0).getName(), is("testAttribute"));
  }

  @Test
  public void testInheritedAddMultipleAttributes() {
    AttributeStatementSaml statement = new AttributeStatementSaml();

    AttributeSaml attr1 = new AttributeSaml();
    attr1.setName("attribute1");
    AttributeSaml attr2 = new AttributeSaml();
    attr2.setName("attribute2");

    statement.addAttribute(attr1);
    statement.addAttribute(attr2);

    assertThat(statement.getAttributes(), hasSize(2));
  }
}
