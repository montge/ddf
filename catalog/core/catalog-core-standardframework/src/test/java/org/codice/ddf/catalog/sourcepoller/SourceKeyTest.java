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
package org.codice.ddf.catalog.sourcepoller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.source.Source;
import org.junit.jupiter.api.Test;

class SourceKeyTest {

  @Test
  void testConstructorWithValidSource() {
    Source source = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey sourceKey = new SourceKey(source);

    assertThat(sourceKey.toString(), containsString("id1"));
    assertThat(sourceKey.toString(), containsString("Title"));
    assertThat(sourceKey.toString(), containsString("1.0"));
    assertThat(sourceKey.toString(), containsString("Description"));
    assertThat(sourceKey.toString(), containsString("Org"));
  }

  @Test
  void testConstructorWithNullIdThrowsException() {
    Source source = createMockSource(null, "Title", "1.0", "Description", "Org");

    assertThrows(NullPointerException.class, () -> new SourceKey(source));
  }

  @Test
  void testConstructorWithEmptyIdThrowsException() {
    Source source = createMockSource("", "Title", "1.0", "Description", "Org");

    assertThrows(IllegalArgumentException.class, () -> new SourceKey(source));
  }

  @Test
  void testConstructorWithNullSourceThrowsException() {
    assertThrows(NullPointerException.class, () -> new SourceKey(null));
  }

  @Test
  void testEqualsWithSameValues() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org");
    Source source2 = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(true));
  }

  @Test
  void testEqualsWithSelf() {
    Source source = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey key = new SourceKey(source);

    assertThat(key.equals(key), is(true));
  }

  @Test
  void testEqualsWithDifferentId() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org");
    Source source2 = createMockSource("id2", "Title", "1.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(false));
  }

  @Test
  void testEqualsWithDifferentTitle() {
    Source source1 = createMockSource("id1", "Title1", "1.0", "Description", "Org");
    Source source2 = createMockSource("id1", "Title2", "1.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(false));
  }

  @Test
  void testEqualsWithDifferentVersion() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org");
    Source source2 = createMockSource("id1", "Title", "2.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(false));
  }

  @Test
  void testEqualsWithDifferentDescription() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Desc1", "Org");
    Source source2 = createMockSource("id1", "Title", "1.0", "Desc2", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(false));
  }

  @Test
  void testEqualsWithDifferentOrganization() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org1");
    Source source2 = createMockSource("id1", "Title", "1.0", "Description", "Org2");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.equals(key2), is(false));
  }

  @Test
  void testEqualsWithNull() {
    Source source = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey key = new SourceKey(source);

    assertThat(key.equals(null), is(false));
  }

  @Test
  void testEqualsWithDifferentClass() {
    Source source = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey key = new SourceKey(source);

    assertThat(key.equals("not a SourceKey"), is(false));
  }

  @Test
  void testHashCodeConsistency() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org");
    Source source2 = createMockSource("id1", "Title", "1.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.hashCode(), is(key2.hashCode()));
  }

  @Test
  void testHashCodeDifference() {
    Source source1 = createMockSource("id1", "Title", "1.0", "Description", "Org");
    Source source2 = createMockSource("id2", "Title", "1.0", "Description", "Org");

    SourceKey key1 = new SourceKey(source1);
    SourceKey key2 = new SourceKey(source2);

    assertThat(key1.hashCode(), is(not(key2.hashCode())));
  }

  @Test
  void testToStringFormat() {
    Source source = createMockSource("testId", "Test Title", "2.0", "Test Desc", "Test Org");

    SourceKey key = new SourceKey(source);
    String result = key.toString();

    assertThat(result, containsString("version='2.0'"));
    assertThat(result, containsString("id='testId'"));
    assertThat(result, containsString("title='Test Title'"));
    assertThat(result, containsString("description='Test Desc'"));
    assertThat(result, containsString("organization='Test Org'"));
  }

  @Test
  void testWithNullOptionalFields() {
    Source source = createMockSource("id1", null, null, null, null);

    SourceKey key = new SourceKey(source);

    assertThat(key.toString(), containsString("id='id1'"));
    assertThat(key.toString(), containsString("title='null'"));
  }

  private Source createMockSource(
      String id, String title, String version, String description, String organization) {
    Source source = mock(Source.class);
    when(source.getId()).thenReturn(id);
    when(source.getTitle()).thenReturn(title);
    when(source.getVersion()).thenReturn(version);
    when(source.getDescription()).thenReturn(description);
    when(source.getOrganization()).thenReturn(organization);
    return source;
  }
}
