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
package ddf.catalog.filter.delegate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ddf.catalog.data.Metacard;
import ddf.catalog.filter.FilterDelegate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagsFilterDelegateTest {

  @Test
  void testDefaultConstructor() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "anyTag", true), is(true));
  }

  @Test
  void testConstructorWithSingleType() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", true), is(true));
    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "other", true), is(false));
  }

  @Test
  void testConstructorWithTypeSet() {
    Set<String> types = new HashSet<>(Arrays.asList("resource", "revision"));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types);

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", true), is(true));
    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "revision", true), is(true));
    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "other", true), is(false));
  }

  @Test
  void testConstructorWithWildcardMatches() {
    Set<String> types = new HashSet<>(Collections.singletonList("resource"));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types, true);

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", true), is(true));
    assertThat(
        delegate.propertyIsEqualTo(Metacard.TAGS, FilterDelegate.WILDCARD_CHAR, true), is(true));
  }

  @Test
  void testConstructorWithoutWildcardMatches() {
    Set<String> types = new HashSet<>(Collections.singletonList("resource"));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types, false);

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", true), is(true));
    assertThat(
        delegate.propertyIsEqualTo(Metacard.TAGS, FilterDelegate.WILDCARD_CHAR, true), is(false));
  }

  @Test
  void testDefaultOperationReturnsFalse() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.defaultOperation("property", "literal", String.class, null), is(false));
  }

  @Test
  void testAndReturnsTrueIfAnyOperandTrue() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.and(Arrays.asList(true, false, false)), is(true));
    assertThat(delegate.and(Arrays.asList(false, false, true)), is(true));
    assertThat(delegate.and(Arrays.asList(false, false, false)), is(false));
  }

  @Test
  void testOrReturnsTrueIfAnyOperandTrue() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.or(Arrays.asList(true, false, false)), is(true));
    assertThat(delegate.or(Arrays.asList(false, true, false)), is(true));
    assertThat(delegate.or(Arrays.asList(false, false, false)), is(false));
  }

  @Test
  void testNotWithNullTagsReturnsOperand() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.not(true), is(true));
    assertThat(delegate.not(false), is(false));
  }

  @Test
  void testNotWithTagsSetReturnsFalse() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.not(true), is(false));
    assertThat(delegate.not(false), is(false));
  }

  @Test
  void testPropertyIsEqualToWithTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", true), is(true));
    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "resource", false), is(true));
  }

  @Test
  void testPropertyIsEqualToWithNonTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.propertyIsEqualTo("other-property", "resource", true), is(false));
    assertThat(delegate.propertyIsEqualTo(Metacard.ID, "resource", true), is(false));
  }

  @Test
  void testPropertyIsNullWithTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.propertyIsNull(Metacard.TAGS), is(true));
  }

  @Test
  void testPropertyIsNullWithNonTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.propertyIsNull("other-property"), is(false));
  }

  @Test
  void testPropertyIsNullWithNullTagsInSet() {
    Set<String> types = new HashSet<>(Collections.singletonList(TagsFilterDelegate.NULL_TAGS));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types);

    assertThat(delegate.propertyIsNull(Metacard.TAGS), is(true));
  }

  @Test
  void testPropertyIsNullWithWildcardInSet() {
    Set<String> types = new HashSet<>(Collections.singletonList("resource"));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types, true);

    assertThat(delegate.propertyIsNull(Metacard.TAGS), is(true));
  }

  @Test
  void testPropertyIsNullWithoutNullTagsOrWildcard() {
    Set<String> types = new HashSet<>(Collections.singletonList("resource"));
    TagsFilterDelegate delegate = new TagsFilterDelegate(types, false);

    assertThat(delegate.propertyIsNull(Metacard.TAGS), is(false));
  }

  @Test
  void testPropertyIsLikeWithTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.propertyIsLike(Metacard.TAGS, "resource", true), is(true));
    assertThat(delegate.propertyIsLike(Metacard.TAGS, "other", true), is(false));
  }

  @Test
  void testPropertyIsLikeWithNonTagsProperty() {
    TagsFilterDelegate delegate = new TagsFilterDelegate("resource");

    assertThat(delegate.propertyIsLike("other-property", "resource", true), is(false));
  }

  @Test
  void testNullTagsConstant() {
    assertThat(TagsFilterDelegate.NULL_TAGS, is(Metacard.TAGS + "_NULL"));
  }

  @Test
  void testPropertyIsLikeWithNullTags() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.propertyIsLike(Metacard.TAGS, "anything", true), is(true));
  }

  @Test
  void testPropertyIsEqualToWithNullTags() {
    TagsFilterDelegate delegate = new TagsFilterDelegate();

    assertThat(delegate.propertyIsEqualTo(Metacard.TAGS, "anything", true), is(true));
  }
}
