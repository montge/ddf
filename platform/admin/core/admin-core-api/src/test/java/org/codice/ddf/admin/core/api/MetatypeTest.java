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
package org.codice.ddf.admin.core.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link Metatype} interface constants and default methods. */
public class MetatypeTest {

  private TestMetatype metatype;

  @BeforeEach
  public void setUp() {
    metatype = new TestMetatype();
  }

  @Test
  public void testIdConstant() {
    assertThat(Metatype.ID, is("id"));
  }

  @Test
  public void testNameConstant() {
    assertThat(Metatype.NAME, is("name"));
  }

  @Test
  public void testAttributeDefinitionsConstant() {
    assertThat(Metatype.ATTRIBUTE_DEFINITIONS, is("metatype"));
  }

  @Test
  public void testGetIdReturnsNull() {
    assertThat(metatype.getId(), is(nullValue()));
  }

  @Test
  public void testSetAndGetId() {
    metatype.setId("test-id");
    assertThat(metatype.getId(), is("test-id"));
  }

  @Test
  public void testGetNameReturnsNull() {
    assertThat(metatype.getName(), is(nullValue()));
  }

  @Test
  public void testSetAndGetName() {
    metatype.setName("Test Metatype");
    assertThat(metatype.getName(), is("Test Metatype"));
  }

  @Test
  public void testGetAttributeDefinitionsReturnsNull() {
    assertThat(metatype.getAttributeDefinitions(), is(nullValue()));
  }

  @Test
  public void testSetAndGetAttributeDefinitions() {
    List<MetatypeAttribute> attrs = new ArrayList<>();
    metatype.setAttributeDefinitions(attrs);
    assertThat(metatype.getAttributeDefinitions(), is(attrs));
  }

  @Test
  public void testIdIsStoredInMap() {
    metatype.setId("my-id");
    assertThat(metatype.get(Metatype.ID), is("my-id"));
  }

  @Test
  public void testNameIsStoredInMap() {
    metatype.setName("My Name");
    assertThat(metatype.get(Metatype.NAME), is("My Name"));
  }

  @Test
  public void testAttributeDefinitionsAreStoredInMap() {
    List<MetatypeAttribute> attrs = new ArrayList<>();
    metatype.setAttributeDefinitions(attrs);
    assertThat(metatype.get(Metatype.ATTRIBUTE_DEFINITIONS), is(attrs));
  }

  /** Test implementation of Metatype that extends HashMap. */
  private static class TestMetatype extends HashMap<String, Object> implements Metatype {}
}
