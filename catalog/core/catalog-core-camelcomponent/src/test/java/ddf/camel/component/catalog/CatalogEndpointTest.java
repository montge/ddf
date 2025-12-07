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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;

import ddf.camel.component.catalog.framework.FrameworkProducer;
import ddf.camel.component.catalog.ingest.PostIngestConsumer;
import ddf.camel.component.catalog.inputtransformer.InputTransformerConsumer;
import ddf.camel.component.catalog.inputtransformer.InputTransformerProducer;
import ddf.camel.component.catalog.metacardtransformer.MetacardTransformerProducer;
import ddf.camel.component.catalog.queryresponsetransformer.QueryResponseTransformerConsumer;
import ddf.camel.component.catalog.queryresponsetransformer.QueryResponseTransformerProducer;
import ddf.catalog.CatalogFramework;
import ddf.mime.MimeTypeMapper;
import org.apache.camel.Consumer;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CatalogEndpointTest {

  @Mock private CatalogComponent mockComponent;

  @Mock private CatalogFramework mockCatalogFramework;

  @Mock private MimeTypeMapper mockMimeTypeMapper;

  @Mock private Processor mockProcessor;

  private static final String TEST_URI = "catalog:test";

  @Before
  public void setup() {
    // Mock setup if needed
  }

  @Test
  public void testEndpointCreation() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint, notNullValue());
    assertThat(endpoint.getTransformerId(), is("transformerId"));
    assertThat(endpoint.getMimeType(), is("text/xml"));
    assertThat(endpoint.getContextPath(), is("inputtransformer"));
    assertThat(endpoint.isSynchronous(), is(true));
  }

  @Test
  public void testEndpointWithNullTransformerId() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            null,
            "text/xml",
            "framework",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.getTransformerId(), nullValue());
  }

  @Test
  public void testGetComponent() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.getComponent(), is(mockComponent));
  }

  @Test
  public void testGetExchangePattern() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.getExchangePattern(), is(ExchangePattern.InOut));
  }

  @Test
  public void testIsSingleton() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.isSingleton(), is(true));
  }

  @Test
  public void testIsMultipleConsumersSupported() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.isMultipleConsumersSupported(), is(true));
  }

  @Test
  public void testCreateInputTransformerProducer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "xml",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Producer producer = endpoint.createProducer();

    assertThat(producer, notNullValue());
    assertThat(producer, instanceOf(InputTransformerProducer.class));
  }

  @Test
  public void testCreateQueryResponseTransformerProducer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "geojson",
            "application/json",
            "queryresponsetransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Producer producer = endpoint.createProducer();

    assertThat(producer, notNullValue());
    assertThat(producer, instanceOf(QueryResponseTransformerProducer.class));
  }

  @Test
  public void testCreateFrameworkProducer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            null,
            null,
            "framework",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Producer producer = endpoint.createProducer();

    assertThat(producer, notNullValue());
    assertThat(producer, instanceOf(FrameworkProducer.class));
  }

  @Test
  public void testCreateMetacardTransformerProducer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "xml",
            "text/xml",
            "metacardtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Producer producer = endpoint.createProducer();

    assertThat(producer, notNullValue());
    assertThat(producer, instanceOf(MetacardTransformerProducer.class));
  }

  @Test
  public void testCreateProducerWithInvalidContextPath() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "xml",
            "text/xml",
            "invalidcontext",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThrows(IllegalArgumentException.class, () -> endpoint.createProducer());
  }

  @Test
  public void testCreateInputTransformerConsumer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "xml",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Consumer consumer = endpoint.createConsumer(mockProcessor);

    assertThat(consumer, notNullValue());
    assertThat(consumer, instanceOf(InputTransformerConsumer.class));
  }

  @Test
  public void testCreateQueryResponseTransformerConsumer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "geojson",
            "application/json",
            "queryresponsetransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Consumer consumer = endpoint.createConsumer(mockProcessor);

    assertThat(consumer, notNullValue());
    assertThat(consumer, instanceOf(QueryResponseTransformerConsumer.class));
  }

  @Test
  public void testCreatePostIngestConsumer() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            null,
            null,
            "postingest",
            mockCatalogFramework,
            mockMimeTypeMapper);

    Consumer consumer = endpoint.createConsumer(mockProcessor);

    assertThat(consumer, notNullValue());
    assertThat(consumer, instanceOf(PostIngestConsumer.class));
  }

  @Test
  public void testCreateConsumerWithInvalidContextPath() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            null,
            null,
            "invalidcontext",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThrows(IllegalArgumentException.class, () -> endpoint.createConsumer(mockProcessor));
  }

  @Test
  public void testGettersAndSetters() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.getTransformerId(), is("transformerId"));
    assertThat(endpoint.getMimeType(), is("text/xml"));
    assertThat(endpoint.getContextPath(), is("inputtransformer"));
    assertThat(endpoint.getMimeTypeMapper(), is(mockMimeTypeMapper));

    endpoint.setSynchronous(false);
    assertThat(endpoint.isSynchronous(), is(false));

    endpoint.setSynchronous(true);
    assertThat(endpoint.isSynchronous(), is(true));
  }

  @Test
  public void testEquals() {
    CatalogEndpoint endpoint1 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    CatalogEndpoint endpoint2 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint1.equals(endpoint2), is(true));
    assertThat(endpoint1.hashCode(), is(endpoint2.hashCode()));
  }

  @Test
  public void testNotEquals() {
    CatalogEndpoint endpoint1 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    CatalogEndpoint endpoint2 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "differentId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint1.equals(endpoint2), is(false));
  }

  @Test
  public void testEqualsWithSelf() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.equals(endpoint), is(true));
  }

  @Test
  public void testEqualsWithNull() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.equals(null), is(false));
  }

  @Test
  public void testEqualsWithDifferentClass() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint.equals("not an endpoint"), is(false));
  }

  @Test
  public void testHashCode() {
    CatalogEndpoint endpoint1 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    CatalogEndpoint endpoint2 =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            "transformerId",
            "text/xml",
            "inputtransformer",
            mockCatalogFramework,
            mockMimeTypeMapper);

    assertThat(endpoint1.hashCode(), is(endpoint2.hashCode()));
  }

  @Test
  public void testCreateProducerWithNonSynchronousMode() {
    CatalogEndpoint endpoint =
        new CatalogEndpoint(
            TEST_URI,
            mockComponent,
            null,
            null,
            "framework",
            mockCatalogFramework,
            mockMimeTypeMapper);

    endpoint.setSynchronous(false);

    Producer producer = endpoint.createProducer();

    assertThat(producer, notNullValue());
  }
}
