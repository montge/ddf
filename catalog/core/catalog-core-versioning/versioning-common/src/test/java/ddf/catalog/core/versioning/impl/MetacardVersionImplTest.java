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
package ddf.catalog.core.versioning.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.core.versioning.MetacardVersion;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import org.junit.jupiter.api.Test;

/** Tests for {@link MetacardVersionImpl} class static methods and type. */
public class MetacardVersionImplTest {

  @Test
  public void testGetMetacardVersionTypeNotNull() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type, is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeName() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getName(), is(MetacardVersion.PREFIX));
  }

  @Test
  public void testGetMetacardVersionTypeHasActionAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.ACTION), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasEditedByAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.EDITED_BY), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionedOnAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.VERSIONED_ON), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionOfIdAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.VERSION_OF_ID), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionTagsAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.VERSION_TAGS), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionTypeAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(type.getAttributeDescriptor(MetacardVersion.VERSION_TYPE), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionTypeBinaryAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(
        type.getAttributeDescriptor(MetacardVersion.VERSION_TYPE_BINARY), is(notNullValue()));
  }

  @Test
  public void testGetMetacardVersionTypeHasVersionedResourceUriAttribute() {
    MetacardType type = MetacardVersionImpl.getMetacardVersionType();
    assertThat(
        type.getAttributeDescriptor(MetacardVersion.VERSIONED_RESOURCE_URI), is(notNullValue()));
  }

  @Test
  public void testIsVersionWithNull() {
    assertThat(MetacardVersionImpl.isVersion(null), is(false));
  }

  @Test
  public void testIsNotVersionWithNull() {
    assertThat(MetacardVersionImpl.isNotVersion(null), is(true));
  }

  @Test
  public void testIsVersionWithRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    assertThat(MetacardVersionImpl.isVersion(metacard), is(false));
  }

  @Test
  public void testIsNotVersionWithRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    assertThat(MetacardVersionImpl.isNotVersion(metacard), is(true));
  }

  @Test
  public void testIsVersionWithVersionTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    assertThat(MetacardVersionImpl.isVersion(metacard), is(true));
  }

  @Test
  public void testIsNotVersionWithVersionTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    assertThat(MetacardVersionImpl.isNotVersion(metacard), is(false));
  }

  @Test
  public void testIsVersionWithDifferentTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn("ddf.metacard");
    assertThat(MetacardVersionImpl.isVersion(metacard), is(false));
  }
}
