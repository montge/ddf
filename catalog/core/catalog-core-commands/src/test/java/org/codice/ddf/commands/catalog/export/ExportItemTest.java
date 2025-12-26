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
package org.codice.ddf.commands.catalog.export;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportItemTest {

  @Test
  void testConstructorAndGetters() throws Exception {
    URI resourceUri = new URI("content:123");
    List<String> derivedUris = Arrays.asList("derived:1", "derived:2");

    ExportItem item = new ExportItem("id123", "resource", resourceUri, derivedUris);

    assertThat(item.getId(), is("id123"));
    assertThat(item.getMetacardTag(), is("resource"));
    assertThat(item.getResourceUri(), is(resourceUri));
    assertThat(item.getDerivedUris(), hasSize(2));
  }

  @Test
  void testConstructorWithNullDerivedUris() throws Exception {
    URI resourceUri = new URI("content:123");

    ExportItem item = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item.getDerivedUris(), is(empty()));
  }

  @Test
  void testConstructorWithEmptyDerivedUris() throws Exception {
    URI resourceUri = new URI("content:123");

    ExportItem item = new ExportItem("id123", "resource", resourceUri, Collections.emptyList());

    assertThat(item.getDerivedUris(), is(empty()));
  }

  @Test
  void testConstructorWithNullResourceUri() {
    ExportItem item = new ExportItem("id123", "resource", null, null);

    assertThat(item.getResourceUri(), is(nullValue()));
  }

  @Test
  void testToString() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item = new ExportItem("id123", "resource", resourceUri, null);

    String result = item.toString();

    assertThat(result, containsString("id123"));
    assertThat(result, containsString("resource"));
    assertThat(result, containsString("content:123"));
  }

  @Test
  void testEqualsWithSameValues() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item1 = new ExportItem("id123", "resource", resourceUri, null);
    ExportItem item2 = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item1.equals(item2), is(true));
  }

  @Test
  void testEqualsWithDifferentIds() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item1 = new ExportItem("id123", "resource", resourceUri, null);
    ExportItem item2 = new ExportItem("id456", "resource", resourceUri, null);

    assertThat(item1.equals(item2), is(false));
  }

  @Test
  void testEqualsWithDifferentTags() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item1 = new ExportItem("id123", "resource", resourceUri, null);
    ExportItem item2 = new ExportItem("id123", "other", resourceUri, null);

    assertThat(item1.equals(item2), is(false));
  }

  @Test
  void testEqualsWithDifferentResourceUri() throws Exception {
    ExportItem item1 = new ExportItem("id123", "resource", new URI("content:123"), null);
    ExportItem item2 = new ExportItem("id123", "resource", new URI("content:456"), null);

    assertThat(item1.equals(item2), is(false));
  }

  @Test
  void testEqualsWithSelf() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item.equals(item), is(true));
  }

  @Test
  void testEqualsWithNull() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item.equals(null), is(false));
  }

  @Test
  void testEqualsWithDifferentClass() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item.equals("not an ExportItem"), is(false));
  }

  @Test
  void testHashCodeConsistency() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item1 = new ExportItem("id123", "resource", resourceUri, null);
    ExportItem item2 = new ExportItem("id123", "resource", resourceUri, null);

    assertThat(item1.hashCode(), is(item2.hashCode()));
  }

  @Test
  void testHashCodeDifference() throws Exception {
    URI resourceUri = new URI("content:123");
    ExportItem item1 = new ExportItem("id123", "resource", resourceUri, null);
    ExportItem item2 = new ExportItem("id456", "resource", resourceUri, null);

    assertThat(item1.hashCode(), is(not(item2.hashCode())));
  }
}
