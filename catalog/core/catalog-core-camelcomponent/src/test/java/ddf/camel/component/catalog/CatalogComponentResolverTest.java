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
package ddf.camel.component.catalog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.spi.TypeConverterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogComponentResolverTest {

  private Component mockComponent;
  private CamelContext mockContext;
  private TypeConverterRegistry mockTypeConverterRegistry;
  private CatalogComponentResolver resolver;

  @BeforeEach
  void setUp() {
    mockComponent = mock(Component.class);
    mockContext = mock(CamelContext.class);
    mockTypeConverterRegistry = mock(TypeConverterRegistry.class);

    when(mockContext.getTypeConverterRegistry()).thenReturn(mockTypeConverterRegistry);

    resolver = new CatalogComponentResolver(mockComponent);
  }

  @Test
  void testResolveComponentWithCatalogName() {
    Component result = resolver.resolveComponent(CatalogComponent.NAME, mockContext);

    assertThat(result, is(sameInstance(mockComponent)));
  }

  @Test
  void testResolveComponentWithNonCatalogName() {
    Component result = resolver.resolveComponent("other-component", mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentRegistersTypeConverters() {
    resolver.resolveComponent(CatalogComponent.NAME, mockContext);

    verify(mockContext, atLeastOnce()).getTypeConverterRegistry();
  }

  @Test
  void testResolveComponentWithNullContext() {
    Component result = resolver.resolveComponent(CatalogComponent.NAME, null);

    assertThat(result, is(sameInstance(mockComponent)));
  }

  @Test
  void testResolveComponentWithNullContextAndNonCatalogName() {
    Component result = resolver.resolveComponent("other", null);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithEmptyName() {
    Component result = resolver.resolveComponent("", mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentWithNullName() {
    Component result = resolver.resolveComponent(null, mockContext);

    assertThat(result, is(nullValue()));
  }

  @Test
  void testResolveComponentCatalogNameIsCatalog() {
    assertThat(CatalogComponent.NAME, is("catalog"));
  }
}
