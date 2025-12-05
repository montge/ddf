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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.core.versioning.DeletedMetacard;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.MetacardImpl;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/** Tests for {@link DeletedMetacardImpl} class static methods and type. */
public class DeletedMetacardImplTest {

  @Test
  public void testGetDeletedMetacardTypeNotNull() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(type, is(notNullValue()));
  }

  @Test
  public void testGetDeletedMetacardTypeName() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(type.getName(), is(DeletedMetacard.PREFIX));
  }

  @Test
  public void testGetDeletedMetacardTypeHasDeletedByAttribute() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(type.getAttributeDescriptor(DeletedMetacard.DELETED_BY), is(notNullValue()));
  }

  @Test
  public void testGetDeletedMetacardTypeHasDeletionOfIdAttribute() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(type.getAttributeDescriptor(DeletedMetacard.DELETION_OF_ID), is(notNullValue()));
  }

  @Test
  public void testGetDeletedMetacardTypeHasLastVersionIdAttribute() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(type.getAttributeDescriptor(DeletedMetacard.LAST_VERSION_ID), is(notNullValue()));
  }

  @Test
  public void testGetDeletedMetacardTypeHasDeletedMetacardTagsAttribute() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(
        type.getAttributeDescriptor(DeletedMetacard.DELETED_METACARD_TAGS), is(notNullValue()));
  }

  @Test
  public void testGetDeletedMetacardTypeHasMetacardDeletedDateAttribute() {
    MetacardType type = DeletedMetacardImpl.getDeletedMetacardType();
    assertThat(
        type.getAttributeDescriptor(DeletedMetacard.METACARD_DELETED_DATE), is(notNullValue()));
  }

  @Test
  public void testIsDeletedWithNull() {
    assertThat(DeletedMetacardImpl.isDeleted(null), is(false));
  }

  @Test
  public void testIsNotDeletedWithNull() {
    assertThat(DeletedMetacardImpl.isNotDeleted(null), is(true));
  }

  @Test
  public void testIsDeletedWithRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    assertThat(DeletedMetacardImpl.isDeleted(metacard), is(false));
  }

  @Test
  public void testIsNotDeletedWithRegularMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    assertThat(DeletedMetacardImpl.isNotDeleted(metacard), is(true));
  }

  @Test
  public void testIsDeletedWithDeletedTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(DeletedMetacard.PREFIX);
    assertThat(DeletedMetacardImpl.isDeleted(metacard), is(true));
  }

  @Test
  public void testIsNotDeletedWithDeletedTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(DeletedMetacard.PREFIX);
    assertThat(DeletedMetacardImpl.isNotDeleted(metacard), is(false));
  }

  @Test
  public void testIsDeletedWithDifferentTypeName() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn("ddf.metacard");
    assertThat(DeletedMetacardImpl.isDeleted(metacard), is(false));
  }

  @Test
  public void testConstructorSetsProperties() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    sourceMetacard.setTitle("Test Title");
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    tags.add("test");
    sourceMetacard.setTags(tags);

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl(
            "deleted-id", "deletion-of-id", "user@example.com", "last-version-id", sourceMetacard);

    assertThat(deleted.getId(), is("deleted-id"));
    assertThat(deleted.getDeletionOfId(), is("deletion-of-id"));
    assertThat(deleted.getDeletedBy(), is("user@example.com"));
    assertThat(deleted.getLastVersionId(), is("last-version-id"));
    assertThat(deleted.getTags(), hasItem(DeletedMetacard.DELETED_TAG));
  }

  @Test
  public void testSetAndGetDeletionOfId() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    sourceMetacard.setTags(Collections.emptySet());

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    deleted.setDeletionOfId("new-deletion-id");
    assertThat(deleted.getDeletionOfId(), is("new-deletion-id"));
  }

  @Test
  public void testSetAndGetDeletedBy() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    sourceMetacard.setTags(Collections.emptySet());

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    deleted.setDeletedBy("newuser@test.com");
    assertThat(deleted.getDeletedBy(), is("newuser@test.com"));
  }

  @Test
  public void testSetAndGetLastVersionId() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    sourceMetacard.setTags(Collections.emptySet());

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    deleted.setLastVersionId("new-version-id");
    assertThat(deleted.getLastVersionId(), is("new-version-id"));
  }

  @Test
  public void testGetDeletedMetacardTagsWithEmptyTags() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    sourceMetacard.setTags(Collections.emptySet());

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    assertThat(deleted.getDeletedMetacardTags().isEmpty(), is(true));
  }

  @Test
  public void testGetDeletedMetacardTagsWithTags() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    tags.add("custom-tag");
    sourceMetacard.setTags(tags);

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    Set<String> deletedTags = deleted.getDeletedMetacardTags();
    assertThat(deletedTags.size(), is(2));
    assertThat(deletedTags, hasItem("resource"));
    assertThat(deletedTags, hasItem("custom-tag"));
  }

  @Test
  public void testSetDeletedMetacardTagsWithNull() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    sourceMetacard.setTags(tags);

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    deleted.setDeletedMetacardTags(null);
    assertThat(deleted.getDeletedMetacardTags().isEmpty(), is(true));
  }

  @Test
  public void testSetDeletedMetacardTagsWithEmptySet() {
    MetacardImpl sourceMetacard = new MetacardImpl();
    sourceMetacard.setId("source-id");
    Set<String> tags = new HashSet<>();
    tags.add("resource");
    sourceMetacard.setTags(tags);

    DeletedMetacardImpl deleted =
        new DeletedMetacardImpl("id", "original", "user", "version", sourceMetacard);

    deleted.setDeletedMetacardTags(Collections.emptySet());
    assertThat(deleted.getDeletedMetacardTags().isEmpty(), is(true));
  }
}
