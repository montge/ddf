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
package ddf.camel.component.catalog.framework;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.CreateResponse;
import ddf.catalog.operation.DeleteRequest;
import ddf.catalog.operation.DeleteResponse;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.UpdateResponse;
import ddf.catalog.operation.impl.UpdateImpl;
import ddf.catalog.source.IngestException;
import ddf.catalog.source.SourceUnavailableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FrameworkProducerTest {

  private static final String OPERATION_HEADER = "operation";

  @Mock private Endpoint mockEndpoint;

  @Mock private CatalogFramework mockCatalogFramework;

  @Mock private Exchange mockExchange;

  @Mock private Message mockMessage;

  @Mock private CreateResponse mockCreateResponse;

  @Mock private UpdateResponse mockUpdateResponse;

  @Mock private DeleteResponse mockDeleteResponse;

  private FrameworkProducer producer;

  @Before
  public void setup() {
    producer = new FrameworkProducer(mockEndpoint, mockCatalogFramework);
    when(mockExchange.getIn()).thenReturn(mockMessage);
  }

  @Test(expected = FrameworkProducerException.class)
  public void testProcessWithNoOperationHeader() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn(null);

    producer.process(mockExchange);
  }

  @Test
  public void testProcessWithInvalidOperation() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("INVALID");
    when(mockMessage.getBody()).thenReturn(new ArrayList<>());

    producer.process(mockExchange);

    verify(mockCatalogFramework, never()).create(any(CreateRequest.class));
    verify(mockCatalogFramework, never()).update(any(UpdateRequest.class));
    verify(mockCatalogFramework, never()).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testCreateWithSingleMetacard() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard));
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
    verify(mockMessage).setBody(any(List.class));
  }

  @Test
  public void testCreateWithMultipleMetacards() throws Exception {
    List<Metacard> metacards = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("test-id-" + i);
      metacards.add(metacard);
    }

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(metacards);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(metacards);
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
    verify(mockMessage).setBody(metacards);
  }

  @Test
  public void testCreateWithEmptyList() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(new ArrayList<>());

    producer.process(mockExchange);

    verify(mockCatalogFramework, never()).create(any(CreateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testCreateWithNullResponse() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(null);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testCreateWithNullCreatedMetacards() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(null);
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testCreateWithPartialSuccess() throws Exception {
    List<Metacard> inputMetacards = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("test-id-" + i);
      inputMetacards.add(metacard);
    }

    List<Metacard> createdMetacards = inputMetacards.subList(0, 2);

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(inputMetacards);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(createdMetacards);
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
    verify(mockMessage).setBody(createdMetacards);
  }

  @Test(expected = FrameworkProducerException.class)
  public void testCreateWithSourceUnavailableException() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCatalogFramework.create(any(CreateRequest.class)))
        .thenThrow(new SourceUnavailableException("Test exception"));

    producer.process(mockExchange);
  }

  @Test(expected = FrameworkProducerException.class)
  public void testCreateWithIngestException() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCatalogFramework.create(any(CreateRequest.class)))
        .thenThrow(new IngestException("Test exception"));

    producer.process(mockExchange);
  }

  @Test
  public void testUpdateWithSingleMetacard() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    UpdateImpl update = new UpdateImpl(metacard, null, metacard);

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockUpdateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));
    when(mockCatalogFramework.update(any(UpdateRequest.class))).thenReturn(mockUpdateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).update(any(UpdateRequest.class));
    verify(mockMessage).setBody(any(List.class));
  }

  @Test
  public void testUpdateWithMultipleMetacards() throws Exception {
    List<Metacard> metacards = new ArrayList<>();
    List<UpdateImpl> updates = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("test-id-" + i);
      metacards.add(metacard);
      updates.add(new UpdateImpl(metacard, null, metacard));
    }

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(metacards);
    when(mockUpdateResponse.getUpdatedMetacards()).thenReturn(updates);
    when(mockCatalogFramework.update(any(UpdateRequest.class))).thenReturn(mockUpdateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).update(any(UpdateRequest.class));
    verify(mockMessage).setBody(updates);
  }

  @Test
  public void testUpdateWithEmptyList() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(new ArrayList<>());

    producer.process(mockExchange);

    verify(mockCatalogFramework, never()).update(any(UpdateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testUpdateWithNullResponse() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCatalogFramework.update(any(UpdateRequest.class))).thenReturn(null);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).update(any(UpdateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test(expected = FrameworkProducerException.class)
  public void testUpdateWithSourceUnavailableException() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCatalogFramework.update(any(UpdateRequest.class)))
        .thenThrow(new SourceUnavailableException("Test exception"));

    producer.process(mockExchange);
  }

  @Test
  public void testDeleteWithSingleId() throws Exception {
    String metacardId = "test-id";
    MetacardImpl deletedMetacard = new MetacardImpl();
    deletedMetacard.setId(metacardId);

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockMessage.getBody(String.class)).thenReturn(metacardId);
    when(mockDeleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(deletedMetacard));
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(mockDeleteResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(any(List.class));
  }

  @Test
  public void testDeleteWithMultipleIds() throws Exception {
    List<String> metacardIds = Arrays.asList("id-1", "id-2", "id-3");
    List<Metacard> deletedMetacards = new ArrayList<>();
    for (String id : metacardIds) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId(id);
      deletedMetacards.add(metacard);
    }

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(metacardIds);
    when(mockMessage.getBody(String.class)).thenReturn(null);
    when(mockDeleteResponse.getDeletedMetacards()).thenReturn(deletedMetacards);
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(mockDeleteResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(deletedMetacards);
  }

  @Test
  public void testDeleteWithEmptyList() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(new ArrayList<>());
    when(mockMessage.getBody(String.class)).thenReturn(null);

    producer.process(mockExchange);

    verify(mockCatalogFramework, never()).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testDeleteWithNullResponse() throws Exception {
    String metacardId = "test-id";

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockMessage.getBody(String.class)).thenReturn(metacardId);
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(null);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test
  public void testDeleteWithNullDeletedMetacards() throws Exception {
    String metacardId = "test-id";

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockMessage.getBody(String.class)).thenReturn(metacardId);
    when(mockDeleteResponse.getDeletedMetacards()).thenReturn(null);
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(mockDeleteResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test(expected = FrameworkProducerException.class)
  public void testDeleteWithSourceUnavailableException() throws Exception {
    String metacardId = "test-id";

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockMessage.getBody(String.class)).thenReturn(metacardId);
    when(mockCatalogFramework.delete(any(DeleteRequest.class)))
        .thenThrow(new SourceUnavailableException("Test exception"));

    producer.process(mockExchange);
  }

  @Test
  public void testCreateWithCaseInsensitiveOperation() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("create");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard));
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
  }

  @Test
  public void testUpdateWithCaseInsensitiveOperation() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    UpdateImpl update = new UpdateImpl(metacard, null, metacard);

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("update");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockUpdateResponse.getUpdatedMetacards()).thenReturn(Collections.singletonList(update));
    when(mockCatalogFramework.update(any(UpdateRequest.class))).thenReturn(mockUpdateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).update(any(UpdateRequest.class));
  }

  @Test
  public void testDeleteWithCaseInsensitiveOperation() throws Exception {
    String metacardId = "test-id";
    MetacardImpl deletedMetacard = new MetacardImpl();
    deletedMetacard.setId(metacardId);

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("delete");
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockMessage.getBody(String.class)).thenReturn(metacardId);
    when(mockDeleteResponse.getDeletedMetacards())
        .thenReturn(Collections.singletonList(deletedMetacard));
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(mockDeleteResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
  }

  @Test
  public void testCreateWithOperationHavingWhitespace() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("  CREATE  ");
    when(mockMessage.getBody(Metacard.class)).thenReturn(metacard);
    when(mockMessage.getBody(List.class)).thenReturn(null);
    when(mockCreateResponse.getCreatedMetacards()).thenReturn(Collections.singletonList(metacard));
    when(mockCatalogFramework.create(any(CreateRequest.class))).thenReturn(mockCreateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).create(any(CreateRequest.class));
  }

  @Test
  public void testProcessWithNullBody() throws Exception {
    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody()).thenReturn(null);
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(null);

    producer.process(mockExchange);

    verify(mockCatalogFramework, never()).create(any(CreateRequest.class));
    verify(mockMessage).setBody(any(ArrayList.class));
  }

  @Test(expected = FrameworkProducerException.class)
  public void testCreateWithInvalidListType() throws Exception {
    List<String> invalidList = Arrays.asList("not", "metacards");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("CREATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(invalidList);

    producer.process(mockExchange);
  }

  @Test(expected = FrameworkProducerException.class)
  public void testUpdateWithInvalidListType() throws Exception {
    List<String> invalidList = Arrays.asList("not", "metacards");

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(invalidList);

    producer.process(mockExchange);
  }

  @Test(expected = FrameworkProducerException.class)
  public void testDeleteWithInvalidListType() throws Exception {
    List<MetacardImpl> invalidList = new ArrayList<>();
    invalidList.add(new MetacardImpl());

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(invalidList);
    when(mockMessage.getBody(String.class)).thenReturn(null);

    producer.process(mockExchange);
  }

  @Test
  public void testDeleteWithPartialSuccess() throws Exception {
    List<String> metacardIds = Arrays.asList("id-1", "id-2", "id-3");
    List<Metacard> deletedMetacards = new ArrayList<>();
    // Only 2 out of 3 deleted
    for (int i = 0; i < 2; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id-" + (i + 1));
      deletedMetacards.add(metacard);
    }

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("DELETE");
    when(mockMessage.getBody(List.class)).thenReturn(metacardIds);
    when(mockMessage.getBody(String.class)).thenReturn(null);
    when(mockDeleteResponse.getDeletedMetacards()).thenReturn(deletedMetacards);
    when(mockCatalogFramework.delete(any(DeleteRequest.class))).thenReturn(mockDeleteResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).delete(any(DeleteRequest.class));
    verify(mockMessage).setBody(deletedMetacards);
  }

  @Test
  public void testUpdateWithPartialSuccess() throws Exception {
    List<Metacard> metacards = new ArrayList<>();
    List<UpdateImpl> updates = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("test-id-" + i);
      metacards.add(metacard);
    }
    // Only 2 out of 3 updated
    for (int i = 0; i < 2; i++) {
      updates.add(new UpdateImpl(metacards.get(i), null, metacards.get(i)));
    }

    when(mockMessage.getHeader(OPERATION_HEADER)).thenReturn("UPDATE");
    when(mockMessage.getBody(Metacard.class)).thenReturn(null);
    when(mockMessage.getBody(List.class)).thenReturn(metacards);
    when(mockUpdateResponse.getUpdatedMetacards()).thenReturn(updates);
    when(mockCatalogFramework.update(any(UpdateRequest.class))).thenReturn(mockUpdateResponse);

    producer.process(mockExchange);

    verify(mockCatalogFramework, times(1)).update(any(UpdateRequest.class));
    verify(mockMessage).setBody(updates);
  }
}
