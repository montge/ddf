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
package ddf.camel.component.catalog.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentComponentResolverTest {

  private Component mockComponent;
  private CamelContext mockContext;
  private ContentComponentResolver resolver;

  @BeforeEach
  void setUp() {
    mockComponent = mock(Component.class);
    mockContext = mock(CamelContext.class);
    resolver = new ContentComponentResolver(mockComponent);
  }

  @Test
  void testResolveComponentWithContentName() throws Exception {
    Component result = resolver.resolveComponent(ContentComponent.NAME, mockContext);

    assertThat(result, is(sameInstance(mockComponent)));
  }

  @Test
  void testResolveComponentWithNonContentName() throws Exception {
    Component result = resolver.resolveComponent("other-component", mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithCatalogName() throws Exception {
    Component result = resolver.resolveComponent("catalog", mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithEmptyName() throws Exception {
    Component result = resolver.resolveComponent("", mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithNullName() throws Exception {
    Component result = resolver.resolveComponent(null, mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithNullContext() throws Exception {
    Component result = resolver.resolveComponent(ContentComponent.NAME, null);

    assertThat(result, is(sameInstance(mockComponent)));
  }

  @Test
  void testContentComponentNameIsContent() {
    assertThat(ContentComponent.NAME, is("content"));
  }

  @Test
  void testResolveComponentReturnsNullForUnknownComponent() throws Exception {
    Component result = resolver.resolveComponent("unknown", mockContext);

    assertThat(result, is(nullValue()));
  }
}
