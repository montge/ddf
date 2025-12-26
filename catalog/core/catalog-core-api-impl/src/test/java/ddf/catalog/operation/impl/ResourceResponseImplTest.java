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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.resource.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResourceResponseImplTest {

  @Test
  void testConstructorWithResourceOnly() {
    Resource resource = mock(Resource.class);

    ResourceResponseImpl response = new ResourceResponseImpl(resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
    assertThat(response.getRequest(), is(nullValue()));
  }

  @Test
  void testConstructorWithRequestAndResource() {
    ResourceRequest request = mock(ResourceRequest.class);
    Resource resource = mock(Resource.class);

    ResourceResponseImpl response = new ResourceResponseImpl(request, resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
    assertThat(response.getRequest(), is(sameInstance(request)));
  }

  @Test
  void testConstructorWithAllParameters() {
    ResourceRequest request = mock(ResourceRequest.class);
    Resource resource = mock(Resource.class);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key", "value");

    ResourceResponseImpl response = new ResourceResponseImpl(request, properties, resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
    assertThat(response.getRequest(), is(sameInstance(request)));
    assertThat(response.getProperties(), hasEntry("key", "value"));
  }

  @Test
  void testGetResourceReturnsCorrectValue() {
    Resource resource = mock(Resource.class);

    ResourceResponseImpl response = new ResourceResponseImpl(resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
  }

  @Test
  void testWithNullResource() {
    ResourceResponseImpl response = new ResourceResponseImpl(null);

    assertThat(response.getResource(), is(nullValue()));
  }

  @Test
  void testWithNullRequest() {
    Resource resource = mock(Resource.class);

    ResourceResponseImpl response = new ResourceResponseImpl(null, resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
    assertThat(response.getRequest(), is(nullValue()));
  }

  @Test
  void testWithNullProperties() {
    ResourceRequest request = mock(ResourceRequest.class);
    Resource resource = mock(Resource.class);

    ResourceResponseImpl response = new ResourceResponseImpl(request, null, resource);

    assertThat(response.getResource(), is(sameInstance(resource)));
    assertThat(response.getRequest(), is(sameInstance(request)));
  }

  @Test
  void testWithAllNullParameters() {
    ResourceResponseImpl response = new ResourceResponseImpl(null, null, null);

    assertThat(response.getResource(), is(nullValue()));
    assertThat(response.getRequest(), is(nullValue()));
  }
}
