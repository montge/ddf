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
package ddf.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.resource.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class ResourceDataSourceTest {

  @Test
  void testConstructorWithResource() {
    Resource resource = mock(Resource.class);
    InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
    when(resource.getMimeTypeValue()).thenReturn("text/plain");
    when(resource.getInputStream()).thenReturn(inputStream);
    when(resource.getName()).thenReturn("test-file.txt");

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getContentType(), is("text/plain"));
    assertThat(dataSource.getName(), is("test-file.txt"));
  }

  @Test
  void testConstructorWithNullResource() {
    ResourceDataSource dataSource = new ResourceDataSource(null);

    assertThat(dataSource.getContentType(), is(nullValue()));
    assertThat(dataSource.getName(), is(nullValue()));
  }

  @Test
  void testGetContentType() {
    Resource resource = mock(Resource.class);
    when(resource.getMimeTypeValue()).thenReturn("application/json");

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getContentType(), is("application/json"));
  }

  @Test
  void testGetInputStream() throws IOException {
    Resource resource = mock(Resource.class);
    InputStream inputStream = new ByteArrayInputStream("data".getBytes());
    when(resource.getInputStream()).thenReturn(inputStream);

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getInputStream(), is(sameInstance(inputStream)));
  }

  @Test
  void testGetName() {
    Resource resource = mock(Resource.class);
    when(resource.getName()).thenReturn("document.pdf");

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getName(), is("document.pdf"));
  }

  @Test
  void testGetOutputStreamReturnsNull() throws IOException {
    Resource resource = mock(Resource.class);

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getOutputStream(), is(nullValue()));
  }

  @Test
  void testToString() {
    Resource resource = mock(Resource.class);
    when(resource.getMimeTypeValue()).thenReturn("text/plain");
    when(resource.getName()).thenReturn("test.txt");

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    String result = dataSource.toString();
    assertThat(result, is(notNullValue()));
    assertThat(result, containsString("ResourceDataSource"));
  }

  @Test
  void testWithNullMimeType() {
    Resource resource = mock(Resource.class);
    when(resource.getMimeTypeValue()).thenReturn(null);
    when(resource.getName()).thenReturn("file.bin");

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getContentType(), is(nullValue()));
    assertThat(dataSource.getName(), is("file.bin"));
  }

  @Test
  void testWithNullName() {
    Resource resource = mock(Resource.class);
    when(resource.getMimeTypeValue()).thenReturn("image/png");
    when(resource.getName()).thenReturn(null);

    ResourceDataSource dataSource = new ResourceDataSource(resource);

    assertThat(dataSource.getContentType(), is("image/png"));
    assertThat(dataSource.getName(), is(nullValue()));
  }
}
