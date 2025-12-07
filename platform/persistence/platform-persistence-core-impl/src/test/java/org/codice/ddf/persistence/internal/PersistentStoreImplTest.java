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
package org.codice.ddf.persistence.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.codice.ddf.persistence.PersistenceException;
import org.codice.ddf.persistence.PersistentItem;
import org.codice.solr.factory.SolrClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersistentStoreImplTest {

  @Mock private SolrClientFactory solrClientFactory;

  @Mock private SolrClient solrClient;

  @Mock private SolrPingResponse pingResponse;

  private PersistentStoreImpl persistentStore;

  @Captor private ArgumentCaptor<SolrParams> solrParamsArgumentCaptor;

  @Before
  public void setup() throws Exception {
    when(solrClientFactory.newClient(any())).thenReturn(solrClient);
    when(solrClient.ping()).thenReturn(pingResponse);
    when(pingResponse.getResponse()).thenReturn(new NamedList<>(Map.of("status", "OK")));
    persistentStore = new PersistentStoreImpl(solrClientFactory);
  }

  @Test
  public void testAdd() throws Exception {
    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    PersistentItem props = new PersistentItem();
    props.addProperty("property", "value");
    persistentStore.add("testcore", props);
    verify(solrClient).add(captor.capture(), any(Integer.class));
    List docs = captor.getValue();
    assertThat(docs.size(), equalTo(1));
  }

  @Test(expected = PersistenceException.class)
  public void testAddNoType() throws Exception {
    PersistentItem props = new PersistentItem();
    props.addProperty("property", "value");
    persistentStore.add(null, props);
  }

  @Test
  public void testAddEmptyItems() throws Exception {
    persistentStore.add("testcore", Collections.emptyList());
    verify(solrClient, never()).add(any(SolrInputDocument.class));
  }

  @Test
  public void testAddEmptyProperties() throws Exception {
    PersistentItem props = new PersistentItem();
    persistentStore.add("testcore", props);
    verify(solrClient, never()).add(any(SolrInputDocument.class));
  }

  @Test
  public void testGet() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(2);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);
    List<Map<String, Object>> items = persistentStore.get("testcore");
    assertThat(items.size(), equalTo(2));
    assertThat(items.get(0).get("id_txt"), equalTo("idvalue1"));
    assertThat(items.get(1).get("id_txt"), equalTo("idvalue2"));
  }

  @Test
  public void testGetWithStartIndexAndPageSize()
      throws PersistenceException, IOException, SolrServerException {
    QueryResponse response = mock(QueryResponse.class);

    when(response.getResults()).thenReturn(getSolrDocuments(1));
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);
    List<Map<String, Object>> items = persistentStore.get("testcore", "", 0, 1);

    verify(solrClient).query(solrParamsArgumentCaptor.capture(), eq(METHOD.POST));

    final SolrParams solrParams = solrParamsArgumentCaptor.getValue();

    assertThat(items.size(), equalTo(1));
    assertThat(solrParams.get("start"), is("0"));
    assertThat(solrParams.get("rows"), is("1"));

    assertThat(items.get(0).get("id_txt"), equalTo("idvalue1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetWithInvalidStartIndex() throws Exception {
    persistentStore.get("testcore", "", -1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetWithInvalidPageSize() throws Exception {
    persistentStore.get("testcore", "", 0, 5000);
  }

  @Test(expected = PersistenceException.class)
  public void testGetInvalidQuery() throws Exception {
    List<Map<String, Object>> items = persistentStore.get("testcore", "property LIKE 'value'");
    assertThat(items.size(), equalTo(1));
    verify(solrClient, never()).query(any(), eq(SolrRequest.METHOD.POST));
  }

  @Test
  public void testDelete() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(2);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    int deletedCount = persistentStore.delete("testcore", "");

    assertThat(deletedCount, equalTo(2));
    verify(solrClient).deleteById(any(List.class));
  }

  @Test
  public void testDeleteWithCql() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(1);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    int deletedCount = persistentStore.delete("testcore", "");

    assertThat(deletedCount, equalTo(1));
    verify(solrClient).deleteById(any(List.class));
  }

  @Test
  public void testDeleteWithStartIndexAndPageSize() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(3);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    int deletedCount = persistentStore.delete("testcore", "", 0, 3);

    assertThat(deletedCount, equalTo(3));
    verify(solrClient).deleteById(any(List.class));
  }

  @Test
  public void testDeleteNoResults() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = new SolrDocumentList();

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    int deletedCount = persistentStore.delete("testcore", "");

    assertThat(deletedCount, equalTo(0));
    verify(solrClient, never()).deleteById(any(List.class));
  }

  @Test(expected = PersistenceException.class)
  public void testDeleteSolrServerException() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(1);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);
    when(solrClient.deleteById(any(List.class))).thenThrow(new SolrServerException("Test"));

    persistentStore.delete("testcore", "");
  }

  @Test(expected = PersistenceException.class)
  public void testDeleteIOException() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(1);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);
    when(solrClient.deleteById(any(List.class))).thenThrow(new IOException("Test"));

    persistentStore.delete("testcore", "");
  }

  @Test(expected = PersistenceException.class)
  public void testDeleteRuntimeException() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(1);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);
    when(solrClient.deleteById(any(List.class))).thenThrow(new RuntimeException("Test exception"));

    persistentStore.delete("testcore", "");
  }

  @Test(expected = PersistenceException.class)
  public void testAddSolrServerException() throws Exception {
    PersistentItem props = new PersistentItem();
    props.addProperty("property", "value");
    when(solrClient.add(any(List.class), any(Integer.class)))
        .thenThrow(new SolrServerException("Test"));

    persistentStore.add("testcore", props);
  }

  @Test(expected = PersistenceException.class)
  public void testAddIOException() throws Exception {
    PersistentItem props = new PersistentItem();
    props.addProperty("property", "value");
    when(solrClient.add(any(List.class), any(Integer.class))).thenThrow(new IOException("Test"));

    persistentStore.add("testcore", props);
  }

  @Test(expected = PersistenceException.class)
  public void testAddRuntimeException() throws Exception {
    PersistentItem props = new PersistentItem();
    props.addProperty("property", "value");
    when(solrClient.add(any(List.class), any(Integer.class)))
        .thenThrow(new RuntimeException("Test exception"));

    persistentStore.add("testcore", props);
  }

  @Test
  public void testAddCollection() throws Exception {
    ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
    List<Map<String, Object>> items = new ArrayList<>();

    PersistentItem props1 = new PersistentItem();
    props1.addProperty("property1", "value1");
    items.add(props1);

    PersistentItem props2 = new PersistentItem();
    props2.addProperty("property2", "value2");
    items.add(props2);

    persistentStore.add("testcore", items);

    verify(solrClient).add(captor.capture(), any(Integer.class));
    List docs = captor.getValue();
    assertThat(docs.size(), equalTo(2));
  }

  @Test
  public void testAddNullItems() throws Exception {
    persistentStore.add("testcore", (Collection<Map<String, Object>>) null);
    verify(solrClient, never()).add(any(List.class), any(Integer.class));
  }

  @Test(expected = PersistenceException.class)
  public void testGetBlankType() throws Exception {
    persistentStore.get("", "");
  }

  @Test(expected = PersistenceException.class)
  public void testGetNullType() throws Exception {
    persistentStore.get(null, "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetWithZeroPageSize() throws Exception {
    persistentStore.get("testcore", "", 0, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetWithNegativePageSize() throws Exception {
    persistentStore.get("testcore", "", 0, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetWithPageSizeExceedsMax() throws Exception {
    persistentStore.get("testcore", "", 0, 1001);
  }

  @Test
  public void testGetWithMaxPageSize() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = new SolrDocumentList();

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    persistentStore.get("testcore", "", 0, 1000);

    verify(solrClient).query(solrParamsArgumentCaptor.capture(), eq(METHOD.POST));
    final SolrParams solrParams = solrParamsArgumentCaptor.getValue();
    assertThat(solrParams.get("rows"), is("1000"));
  }

  @Test(expected = PersistenceException.class)
  public void testGetSolrServerException() throws Exception {
    when(solrClient.query(any(), eq(METHOD.POST))).thenThrow(new SolrServerException("Test"));
    persistentStore.get("testcore");
  }

  @Test(expected = PersistenceException.class)
  public void testGetIOException() throws Exception {
    when(solrClient.query(any(), eq(METHOD.POST))).thenThrow(new IOException("Test"));
    persistentStore.get("testcore");
  }

  @Test(expected = PersistenceException.class)
  public void testGetCQLException() throws Exception {
    persistentStore.get("testcore", "INVALID CQL");
  }

  @Test
  public void testGetWithEmptyCql() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = getSolrDocuments(1);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    List<Map<String, Object>> items = persistentStore.get("testcore", "");

    assertThat(items.size(), equalTo(1));
  }

  @Test
  public void testGetWithDifferentFieldTypes() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = new SolrDocumentList();

    SolrDocument solrDocument = new SolrDocument();
    solrDocument.addField("id_txt", "testid");
    solrDocument.addField("name_txt", "testname");
    solrDocument.addField("count_int", 42);
    solrDocument.addField("size_lng", 1000L);
    @SuppressWarnings("JdkObsolete")
    java.util.Date createdDate = new java.util.Date();
    solrDocument.addField("created_tdt", createdDate);
    solrDocument.addField("content_xml", "<xml>test</xml>");
    solrDocument.addField("data_bin", new byte[] {1, 2, 3});
    docList.add(solrDocument);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    List<Map<String, Object>> items = persistentStore.get("testcore");

    assertThat(items.size(), equalTo(1));
    Map<String, Object> item = items.get(0);
    assertThat(item.get("id_txt"), equalTo("testid"));
    assertThat(item.get("name_txt"), equalTo("testname"));
    assertThat(item.get("count_int"), equalTo(42));
    assertThat(item.get("size_lng"), equalTo(1000L));
    assertThat(item.containsKey("created_tdt"), is(true));
    assertThat(item.get("content_xml"), equalTo("<xml>test</xml>"));
    assertThat(item.containsKey("data_bin"), is(true));
  }

  @Test
  public void testGetWithMultiValuedTextField() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = new SolrDocumentList();

    SolrDocument solrDocument = new SolrDocument();
    solrDocument.addField("id_txt", "testid");
    solrDocument.addField("tags_txt", "tag1");
    solrDocument.addField("tags_txt", "tag2");
    solrDocument.addField("tags_txt", "tag3");
    docList.add(solrDocument);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    List<Map<String, Object>> items = persistentStore.get("testcore");

    assertThat(items.size(), equalTo(1));
    Map<String, Object> item = items.get(0);
    assertThat(item.get("tags_txt") instanceof java.util.Set, is(true));
  }

  @Test
  public void testGetWithFieldsWithInvalidSuffix() throws Exception {
    QueryResponse response = mock(QueryResponse.class);
    SolrDocumentList docList = new SolrDocumentList();

    SolrDocument solrDocument = new SolrDocument();
    solrDocument.addField("id_txt", "testid");
    solrDocument.addField("invalid_suffix", "shouldBeIgnored");
    docList.add(solrDocument);

    when(response.getResults()).thenReturn(docList);
    when(solrClient.query(any(), eq(METHOD.POST))).thenReturn(response);

    List<Map<String, Object>> items = persistentStore.get("testcore");

    assertThat(items.size(), equalTo(1));
    Map<String, Object> item = items.get(0);
    assertThat(item.containsKey("invalid_suffix"), is(false));
    assertThat(item.get("id_txt"), equalTo("testid"));
  }

  private SolrDocumentList getSolrDocuments(int numDocuments) {
    final SolrDocumentList docList = new SolrDocumentList();

    for (int i = 0; i < numDocuments; i++) {
      SolrDocument solrDocument = new SolrDocument();
      solrDocument.addField("id_txt", String.format("idvalue%d", i + 1));
      docList.add(solrDocument);
    }

    return docList;
  }
}
