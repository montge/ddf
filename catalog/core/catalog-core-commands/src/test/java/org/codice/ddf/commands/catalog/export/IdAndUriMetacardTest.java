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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import java.net.URI;
import org.junit.jupiter.api.Test;

class IdAndUriMetacardTest {

  @Test
  void testConstructorAndGetters() throws Exception {
    URI uri = new URI("content:abc123");
    IdAndUriMetacard metacard = new IdAndUriMetacard("id123", uri);

    assertThat(metacard.getId(), is("id123"));
    assertThat(metacard.getResourceURI(), is(uri));
  }

  @Test
  void testWithNullUri() {
    IdAndUriMetacard metacard = new IdAndUriMetacard("id123", null);

    assertThat(metacard.getId(), is("id123"));
    assertThat(metacard.getResourceURI(), is(nullValue()));
  }

  @Test
  void testWithNullId() throws Exception {
    URI uri = new URI("content:abc123");
    IdAndUriMetacard metacard = new IdAndUriMetacard(null, uri);

    assertThat(metacard.getId(), is(nullValue()));
    assertThat(metacard.getResourceURI(), is(uri));
  }

  @Test
  void testExtendsMetacardImpl() throws Exception {
    URI uri = new URI("content:abc123");
    IdAndUriMetacard metacard = new IdAndUriMetacard("id123", uri);

    assertThat(metacard, instanceOf(MetacardImpl.class));
  }

  @Test
  void testImplementsMetacard() throws Exception {
    URI uri = new URI("content:abc123");
    IdAndUriMetacard metacard = new IdAndUriMetacard("id123", uri);

    assertThat(metacard, instanceOf(Metacard.class));
  }

  @Test
  void testWithHttpUri() throws Exception {
    URI uri = new URI("http://example.com/resource");
    IdAndUriMetacard metacard = new IdAndUriMetacard("httpId", uri);

    assertThat(metacard.getResourceURI(), is(uri));
    assertThat(metacard.getResourceURI().getScheme(), is("http"));
  }

  @Test
  void testWithFileUri() throws Exception {
    URI uri = new URI("file:///path/to/file.txt");
    IdAndUriMetacard metacard = new IdAndUriMetacard("fileId", uri);

    assertThat(metacard.getResourceURI(), is(uri));
    assertThat(metacard.getResourceURI().getScheme(), is("file"));
  }
}
