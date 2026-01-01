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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.camel.component.catalog.ingest.PostIngestConsumer;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.UpdateResponse;
import java.util.Dictionary;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class PostIngestConsumerTest {
  private CatalogEndpoint mockEndpoint = mock(CatalogEndpoint.class);

  private Processor mockProcessor = mock(Processor.class);

  private ServiceRegistration mockRegistration = mock(ServiceRegistration.class);

  private Exchange mockExchange = mock(Exchange.class);

  private Message mockMessage = mock(Message.class);

  private CreateResponse mockCreateResponse = mock(CreateResponse.class);

  private UpdateResponse mockUpdateResponse = mock(UpdateResponse.class);

  private DeleteResponse mockDeleteResponse = mock(DeleteResponse.class);

  private CatalogComponent mockCatalogComponent = mock(CatalogComponent.class);

  private BundleContext mockBundleContext = mock(BundleContext.class);

  // Use a real CamelContext for Camel 4.x DefaultConsumer requirements
  private CamelContext camelContext;

  private PostIngestConsumer postIngestConsumer;

  @BeforeEach
  public void setUp() throws Exception {
    camelContext = new DefaultCamelContext();
    camelContext.start();

    when(mockEndpoint.getComponent()).thenReturn(mockCatalogComponent);
    when(mockCatalogComponent.getBundleContext()).thenReturn(mockBundleContext);
    when(mockEndpoint
            .getComponent()
            .getBundleContext()
            .registerService(any(String.class), any(Object.class), any(Dictionary.class)))
        .thenReturn(mockRegistration);
    when(mockEndpoint.createExchange()).thenReturn(mockExchange);
    when(mockExchange.getIn()).thenReturn(mockMessage);
    when(mockEndpoint.getCamelContext()).thenReturn(camelContext);

    postIngestConsumer = new PostIngestConsumer(mockEndpoint, mockProcessor);
  }

  @AfterEach
  public void tearDown() throws Exception {
    if (camelContext != null) {
      camelContext.stop();
    }
  }

  @Test
  public void testCreate() throws Exception {
    postIngestConsumer.process(mockCreateResponse);
    verify(mockProcessor, timeout(5000).atLeastOnce()).process(any());
  }

  @Test
  public void testUpdate() throws Exception {
    postIngestConsumer.process(mockUpdateResponse);
    verify(mockProcessor, timeout(5000).atLeastOnce()).process(any());
  }

  @Test
  public void testDelete() throws Exception {
    postIngestConsumer.process(mockDeleteResponse);
    verify(mockProcessor, timeout(5000).atLeastOnce()).process(any());
  }
}
