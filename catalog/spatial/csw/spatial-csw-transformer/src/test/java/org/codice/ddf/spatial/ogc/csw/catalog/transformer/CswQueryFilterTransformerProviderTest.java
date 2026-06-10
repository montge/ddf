/*
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */

package org.codice.ddf.spatial.ogc.csw.catalog.transformer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.transform.QueryFilterTransformer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class CswQueryFilterTransformerProviderTest extends CswQueryFilterTransformerProvider {
  private BundleContext bundleContext = mock(BundleContext.class);

  private static final String NAMESPACE = "{namespace}test";

  private static final String TYPE_NAME = "typeName";

  private static final String ID_KEY = "id";

  private QueryFilterTransformer transformer;

  private ServiceReference<QueryFilterTransformer> serviceReference;

  @SuppressWarnings("unchecked")
  @BeforeEach
  public void setUp() {
    transformer = mock(QueryFilterTransformer.class);
    serviceReference = mock(ServiceReference.class);
    when(serviceReference.getProperty(ID_KEY)).thenReturn(NAMESPACE);
    when(bundleContext.getService(serviceReference)).thenReturn(transformer);
    when(serviceReference.getProperty(
            QueryFilterTransformer.QUERY_FILTER_TRANSFORMER_TYPE_NAMES_FIELD))
        .thenReturn(Collections.singletonList(TYPE_NAME));
  }

  @Test
  public void testAddingTransformer() {
    bind(serviceReference);

    Optional<QueryFilterTransformer> resultByQname = getTransformer(QName.valueOf(NAMESPACE));
    assertThat(resultByQname.isPresent(), equalTo(true));
    assertThat(resultByQname.get(), equalTo(transformer));

    Optional<QueryFilterTransformer> resultByTypeName = getTransformer(TYPE_NAME);
    assertThat(resultByTypeName.isPresent(), equalTo(true));
    assertThat(resultByTypeName.get(), equalTo(transformer));
  }

  @Test
  public void testRemovingTransformer() {
    bind(serviceReference);
    unbind(serviceReference);

    Optional<QueryFilterTransformer> resultByQname = getTransformer(QName.valueOf(NAMESPACE));
    assertThat(resultByQname.isPresent(), equalTo(false));

    Optional<QueryFilterTransformer> resultByTypeName = getTransformer(TYPE_NAME);
    assertThat(resultByTypeName.isPresent(), equalTo(false));
  }

  @Test
  public void testNullServiceReferenceOnBind() {
    assertDoesNotThrow(() -> bind(null));

    // binding a null reference must be a no-op: nothing should be registered
    assertThat(getTransformer(QName.valueOf(NAMESPACE)).isPresent(), equalTo(false));
    assertThat(getTransformer(TYPE_NAME).isPresent(), equalTo(false));
  }

  @Test
  public void testNullServiceReferenceOnUnbind() {
    assertDoesNotThrow(() -> unbind(null));

    // unbinding a null reference must return early and never touch the bundle context
    verify(bundleContext, never()).ungetService(any());
  }

  @Test
  public void testGettingBadTransformer() {
    Optional<QueryFilterTransformer> result = getTransformer(QName.valueOf("fake"));
    assertThat(result.isPresent(), equalTo(false));
  }

  @Test
  public void testGetTransformerNullQName() {
    Optional<QueryFilterTransformer> result = getTransformer((QName) null);
    assertThat(result.isPresent(), equalTo(false));
  }

  @Test
  public void testGetTransformerEmptyTypeName() {
    Optional<QueryFilterTransformer> result = getTransformer("");
    assertThat(result.isPresent(), equalTo(false));
  }

  @Test
  public void testNullServiceReferenceId() {
    when(serviceReference.getProperty(ID_KEY)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> bind(serviceReference));
  }

  @Test
  public void testListOfServiceReferenceIds() {
    List<String> namespaces = Arrays.asList("{namespace}one", "{namespace}two", "{namespace}three");
    when(serviceReference.getProperty(ID_KEY)).thenReturn(namespaces);

    bind(serviceReference);

    for (String namespace : namespaces) {
      Optional<QueryFilterTransformer> result = getTransformer(QName.valueOf(namespace));
      assertThat(result.isPresent(), equalTo(true));
      assertThat(result.get(), equalTo(transformer));
    }
  }

  @Test
  public void testNullTransformer() {
    when(bundleContext.getService(serviceReference)).thenReturn(null);
    assertThrows(IllegalStateException.class, () -> bind(serviceReference));
  }

  @Override
  BundleContext getBundleContext() {
    return bundleContext;
  }
}
