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
package ddf.catalog.core.versioning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import org.junit.jupiter.api.Test;

/** Tests for {@link MetacardVersion} interface constants and Action enum. */
public class MetacardVersionTest {

  @Test
  public void testPrefixConstant() {
    assertThat(MetacardVersion.PREFIX, is("metacard.version"));
  }

  @Test
  public void testPrefixerFunction() {
    String result = MetacardVersion.PREFIXER.apply("test");
    assertThat(result, is("metacard.version.test"));
  }

  @Test
  public void testSkipVersioningConstant() {
    assertThat(MetacardVersion.SKIP_VERSIONING, is("skip-versioning"));
  }

  @Test
  public void testHistoryMetacardsPropertyConstant() {
    assertThat(MetacardVersion.HISTORY_METACARDS_PROPERTY, is("history-metacards"));
  }

  @Test
  public void testVersionTagConstant() {
    assertThat(MetacardVersion.VERSION_TAG, is("revision"));
  }

  @Test
  public void testActionAttributeName() {
    assertThat(MetacardVersion.ACTION, is("metacard.version.action"));
  }

  @Test
  public void testEditedByAttributeName() {
    assertThat(MetacardVersion.EDITED_BY, is("metacard.version.edited-by"));
  }

  @Test
  public void testVersionedOnAttributeName() {
    assertThat(MetacardVersion.VERSIONED_ON, is("metacard.version.versioned-on"));
  }

  @Test
  public void testVersionOfIdAttributeName() {
    assertThat(MetacardVersion.VERSION_OF_ID, is("metacard.version.id"));
  }

  @Test
  public void testVersionTagsAttributeName() {
    assertThat(MetacardVersion.VERSION_TAGS, is("metacard.version.tags"));
  }

  @Test
  public void testVersionTypeAttributeName() {
    assertThat(MetacardVersion.VERSION_TYPE, is("metacard.version.type"));
  }

  @Test
  public void testVersionTypeBinaryAttributeName() {
    assertThat(MetacardVersion.VERSION_TYPE_BINARY, is("metacard.version.type-binary"));
  }

  @Test
  public void testVersionedResourceUriAttributeName() {
    assertThat(MetacardVersion.VERSIONED_RESOURCE_URI, is("metacard.version.resource-uri"));
  }

  // Action enum tests

  @Test
  public void testActionDeletedKey() {
    assertThat(MetacardVersion.Action.DELETED.getKey(), is("Deleted"));
  }

  @Test
  public void testActionDeletedContentKey() {
    assertThat(MetacardVersion.Action.DELETED_CONTENT.getKey(), is("Deleted-Content"));
  }

  @Test
  public void testActionVersionedKey() {
    assertThat(MetacardVersion.Action.VERSIONED.getKey(), is("Versioned"));
  }

  @Test
  public void testActionVersionedContentKey() {
    assertThat(MetacardVersion.Action.VERSIONED_CONTENT.getKey(), is("Versioned-Content"));
  }

  @Test
  public void testActionFromKeyDeleted() {
    MetacardVersion.Action action = MetacardVersion.Action.fromKey("Deleted");
    assertThat(action, is(MetacardVersion.Action.DELETED));
  }

  @Test
  public void testActionFromKeyVersioned() {
    MetacardVersion.Action action = MetacardVersion.Action.fromKey("Versioned");
    assertThat(action, is(MetacardVersion.Action.VERSIONED));
  }

  @Test
  public void testActionFromKeyInvalid() {
    MetacardVersion.Action action = MetacardVersion.Action.fromKey("Invalid");
    assertThat(action, is(nullValue()));
  }

  @Test
  public void testIsVersionWithMetacardVersion() {
    MetacardVersion versionMetacard = mock(MetacardVersion.class);
    assertThat(MetacardVersion.Action.isVersion(versionMetacard), is(true));
  }

  @Test
  public void testIsVersionWithVersionTypeMetacard() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    assertThat(MetacardVersion.Action.isVersion(metacard), is(true));
  }

  @Test
  public void testIsVersionWithRegularMetacard() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn("ddf.metacard");
    assertThat(MetacardVersion.Action.isVersion(metacard), is(false));
  }

  @Test
  public void testIsNotVersionWithRegularMetacard() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn("ddf.metacard");
    assertThat(MetacardVersion.Action.isNotVersion(metacard), is(true));
  }

  @Test
  public void testOfMetacardWithVersionedAction() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    Attribute actionAttribute = mock(Attribute.class);

    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    when(metacard.getAttribute(MetacardVersion.ACTION)).thenReturn(actionAttribute);
    when(actionAttribute.getValue()).thenReturn("Versioned");

    MetacardVersion.Action action = MetacardVersion.Action.ofMetacard(metacard);
    assertThat(action, is(MetacardVersion.Action.VERSIONED));
  }

  @Test
  public void testOfMetacardWithDeletedAction() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    Attribute actionAttribute = mock(Attribute.class);

    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    when(metacard.getAttribute(MetacardVersion.ACTION)).thenReturn(actionAttribute);
    when(actionAttribute.getValue()).thenReturn("Deleted");

    MetacardVersion.Action action = MetacardVersion.Action.ofMetacard(metacard);
    assertThat(action, is(MetacardVersion.Action.DELETED));
  }

  @Test
  public void testOfMetacardWithNonVersionThrowsException() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn("regular.metacard");
    when(metacard.getId()).thenReturn("test-id");

    assertThrows(IllegalArgumentException.class, () -> MetacardVersion.Action.ofMetacard(metacard));
  }

  @Test
  public void testOfMetacardWithNonStringActionThrowsException() {
    Metacard metacard = mock(Metacard.class);
    MetacardType metacardType = mock(MetacardType.class);
    Attribute actionAttribute = mock(Attribute.class);

    when(metacard.getMetacardType()).thenReturn(metacardType);
    when(metacardType.getName()).thenReturn(MetacardVersion.PREFIX);
    when(metacard.getAttribute(MetacardVersion.ACTION)).thenReturn(actionAttribute);
    when(actionAttribute.getValue()).thenReturn(123); // Non-string value

    assertThrows(IllegalArgumentException.class, () -> MetacardVersion.Action.ofMetacard(metacard));
  }

  @Test
  public void testAllActionValuesHaveKeys() {
    for (MetacardVersion.Action action : MetacardVersion.Action.values()) {
      assertThat(action.getKey(), is(notNullValue()));
    }
  }

  @Test
  public void testAllActionKeysAreUnique() {
    MetacardVersion.Action[] actions = MetacardVersion.Action.values();
    for (int i = 0; i < actions.length; i++) {
      for (int j = i + 1; j < actions.length; j++) {
        assertThat(
            "Action keys should be unique",
            actions[i].getKey().equals(actions[j].getKey()),
            is(false));
      }
    }
  }
}
