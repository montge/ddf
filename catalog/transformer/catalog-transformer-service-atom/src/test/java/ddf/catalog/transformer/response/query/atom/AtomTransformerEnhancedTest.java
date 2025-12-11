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
package ddf.catalog.transformer.response.query.atom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.action.Action;
import ddf.action.ActionProvider;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.BinaryContentImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.Query;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.MetacardTransformer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AtomTransformerEnhancedTest {

  @Mock private ActionProvider mockViewMetacardActionProvider;

  @Mock private ActionProvider mockResourceActionProvider;

  @Mock private ActionProvider mockThumbnailActionProvider;

  @Mock private MetacardTransformer mockMetacardTransformer;

  private AtomTransformer transformer;

  @BeforeEach
  public void setUp() {
    transformer = new AtomTransformer();
    transformer.setViewMetacardActionProvider(mockViewMetacardActionProvider);
    transformer.setResourceActionProvider(mockResourceActionProvider);
    transformer.setThumbnailActionProvider(mockThumbnailActionProvider);
    transformer.setMetacardTransformer(mockMetacardTransformer);
  }

  @Test
  public void testTransformNullSourceResponse() {
    assertThrows(
        CatalogTransformerException.class,
        () -> transformer.transform(null, Collections.emptyMap()));
  }

  @Test
  public void testTransformEmptyResults() throws CatalogTransformerException, IOException {
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Collections.emptyList());
    when(sourceResponse.getHits()).thenReturn(0L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(content, is(notNullValue()));
    assertThat(content.getMimeTypeValue(), is("application/atom+xml"));

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("<feed"));
    assertThat(atomXml, containsString(AtomTransformer.DEFAULT_FEED_TITLE));
  }

  @Test
  public void testTransformSingleResult() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("<entry"));
    assertThat(atomXml, containsString("Test Title"));
    assertThat(atomXml, containsString("urn:catalog:id:test-id"));
  }

  @Test
  public void testTransformMultipleResults() throws CatalogTransformerException, IOException {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      MetacardImpl metacard = createBasicMetacard("id-" + i, "Title " + i);
      results.add(new ResultImpl(metacard));
    }

    SourceResponse sourceResponse = createSourceResponse(results, 5L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("Title 0"));
    assertThat(atomXml, containsString("Title 4"));
  }

  @Test
  public void testTransformWithNullMetacardInResult()
      throws CatalogTransformerException, IOException {
    List<Result> results = new ArrayList<>();
    results.add(new ResultImpl(createBasicMetacard("id1", "Valid")));
    results.add(new ResultImpl(null)); // Null metacard
    results.add(new ResultImpl(createBasicMetacard("id2", "Also Valid")));

    SourceResponse sourceResponse = createSourceResponse(results, 3L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("Valid"));
    assertThat(atomXml, containsString("Also Valid"));
    // Should skip null metacard without failing
  }

  @Test
  public void testTransformIncludesHits() throws CatalogTransformerException, IOException {
    SourceResponse sourceResponse = createSourceResponse(Collections.emptyList(), 42L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("totalResults"));
    assertThat(atomXml, containsString("42"));
  }

  @Test
  public void testTransformWithNegativeHits() throws CatalogTransformerException, IOException {
    SourceResponse sourceResponse = createSourceResponse(Collections.emptyList(), -1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    // Should not include totalResults when hits is -1
    assertThat(atomXml, org.hamcrest.Matchers.not(containsString("totalResults")));
  }

  @Test
  public void testTransformIncludesOpenSearchElements()
      throws CatalogTransformerException, IOException {
    Query query = new QueryImpl(null, 1, 10, null, false, 0);
    QueryRequest queryRequest = new QueryRequestImpl(query);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Collections.emptyList());
    when(sourceResponse.getHits()).thenReturn(100L);
    when(sourceResponse.getRequest()).thenReturn(queryRequest);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("itemsPerPage"));
    assertThat(atomXml, containsString("startIndex"));
    assertThat(atomXml, containsString("10"));
    assertThat(atomXml, containsString("1"));
  }

  @Test
  public void testTransformWithNegativePageSize() throws CatalogTransformerException, IOException {
    Query query = new QueryImpl(null, 1, -1, null, false, 0);
    QueryRequest queryRequest = new QueryRequestImpl(query);

    List<Result> results = new ArrayList<>();
    results.add(new ResultImpl(createBasicMetacard("id1", "Title1")));
    results.add(new ResultImpl(createBasicMetacard("id2", "Title2")));

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(results);
    when(sourceResponse.getHits()).thenReturn(2L);
    when(sourceResponse.getRequest()).thenReturn(queryRequest);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    // Should use result size when page size is negative
    assertThat(atomXml, containsString("itemsPerPage"));
    assertThat(atomXml, containsString("2"));
  }

  @Test
  public void testTransformWithRelevanceScore() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    ResultImpl result = new ResultImpl(metacard);
    result.setRelevanceScore(0.85);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("score"));
    assertThat(atomXml, containsString("0.85"));
  }

  @Test
  public void testTransformWithNullRelevanceScore()
      throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    ResultImpl result = new ResultImpl(metacard);
    result.setRelevanceScore(null);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    // Should not include relevance score
    assertThat(atomXml, org.hamcrest.Matchers.not(containsString("relevance:score")));
  }

  @Test
  public void testTransformWithSourceIdExtension() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setSourceId("test-source");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("resultSource"));
    assertThat(atomXml, containsString("test-source"));
  }

  @Test
  public void testTransformWithNullSourceId() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setSourceId(null);
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    // Should use default source id
    assertThat(atomXml, containsString("resultSource"));
    assertThat(atomXml, containsString(AtomTransformer.DEFAULT_SOURCE_ID));
  }

  @Test
  public void testTransformWithMetacardTransformer() throws Exception {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    String mockXmlContent = "<metadata><title>Test</title></metadata>";
    BinaryContent mockBinaryContent =
        new BinaryContentImpl(
            new ByteArrayInputStream(mockXmlContent.getBytes(StandardCharsets.UTF_8)));

    when(mockMetacardTransformer.transform(any(Metacard.class), any()))
        .thenReturn(mockBinaryContent);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("Test"));
  }

  @Test
  public void testTransformWithMetacardTransformerException() throws Exception {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    when(mockMetacardTransformer.transform(any(Metacard.class), any()))
        .thenThrow(new CatalogTransformerException("Transform failed"));

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    // Should handle exception gracefully
    assertThat(content, is(notNullValue()));
    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("test-id"));
  }

  @Test
  public void testTransformWithResourceLink()
      throws CatalogTransformerException, IOException, URISyntaxException, MalformedURLException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setResourceURI(new URI("http://example.com/resource"));
    metacard.setResourceSize("12345");
    Result result = new ResultImpl(metacard);

    Action mockAction = mock(Action.class);
    when(mockAction.getUrl()).thenReturn(new URL("http://example.com/resource"));
    when(mockAction.getTitle()).thenReturn("View Resource");
    when(mockResourceActionProvider.getAction(any(Metacard.class))).thenReturn(mockAction);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("http://example.com/resource"));
    assertThat(atomXml, containsString("12345"));
  }

  @Test
  public void testTransformWithInvalidResourceSize()
      throws CatalogTransformerException, IOException, URISyntaxException, MalformedURLException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setResourceURI(new URI("http://example.com/resource"));
    metacard.setResourceSize("not-a-number");
    Result result = new ResultImpl(metacard);

    Action mockAction = mock(Action.class);
    when(mockAction.getUrl()).thenReturn(new URL("http://example.com/resource"));
    when(mockAction.getTitle()).thenReturn("View Resource");
    when(mockResourceActionProvider.getAction(any(Metacard.class))).thenReturn(mockAction);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    // Should handle invalid resource size gracefully
    assertThat(content, is(notNullValue()));
  }

  @Test
  public void testTransformWithThumbnailLink()
      throws CatalogTransformerException, IOException, MalformedURLException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setThumbnail(new byte[] {1, 2, 3});
    Result result = new ResultImpl(metacard);

    Action mockAction = mock(Action.class);
    when(mockAction.getUrl()).thenReturn(new URL("http://example.com/thumbnail"));
    when(mockAction.getTitle()).thenReturn("Thumbnail");
    when(mockThumbnailActionProvider.getAction(any(Metacard.class))).thenReturn(mockAction);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("http://example.com/thumbnail"));
  }

  @Test
  public void testTransformWithViewMetacardLink()
      throws CatalogTransformerException, IOException, MalformedURLException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    Action mockAction = mock(Action.class);
    when(mockAction.getUrl()).thenReturn(new URL("http://example.com/view"));
    when(mockAction.getTitle()).thenReturn("View Metacard");
    when(mockViewMetacardActionProvider.getAction(any(Metacard.class))).thenReturn(mockAction);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("http://example.com/view"));
  }

  @Test
  public void testTransformWithNullActionProvider()
      throws CatalogTransformerException, IOException {
    transformer.setResourceActionProvider(null);
    transformer.setThumbnailActionProvider(null);
    transformer.setViewMetacardActionProvider(null);

    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    // Should handle null action providers gracefully
    assertThat(content, is(notNullValue()));
  }

  @Test
  public void testTransformWithActionProviderReturningNull()
      throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Result result = new ResultImpl(metacard);

    when(mockResourceActionProvider.getAction(any(Metacard.class))).thenReturn(null);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    // Should handle null action gracefully
    assertThat(content, is(notNullValue()));
  }

  @Test
  public void testTransformWithGeometry() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setLocation("POINT (1 2)");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("georss"));
  }

  @Test
  public void testTransformWithInvalidGeometry() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setLocation("INVALID WKT");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    // Should handle invalid geometry gracefully
    assertThat(content, is(notNullValue()));
  }

  @Test
  public void testTransformWithContentTypeName() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    metacard.setContentTypeName("Document");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("category"));
    assertThat(atomXml, containsString("Document"));
  }

  @Test
  public void testTransformWithDates() throws CatalogTransformerException, IOException {
    MetacardImpl metacard = createBasicMetacard("test-id", "Test Title");
    Date now = new Date();
    metacard.setCreatedDate(now);
    metacard.setModifiedDate(now);
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = createSourceResponse(Collections.singletonList(result), 1L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    String atomXml = new String(content.getByteArray(), StandardCharsets.UTF_8);
    assertThat(atomXml, containsString("published"));
    assertThat(atomXml, containsString("updated"));
  }

  @Test
  public void testTransformMimeType() throws CatalogTransformerException {
    SourceResponse sourceResponse = createSourceResponse(Collections.emptyList(), 0L);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(content.getMimeType().getPrimaryType(), is("application"));
    assertThat(content.getMimeType().getSubType(), is("atom+xml"));
  }

  @Test
  public void testTransformWithCountProperty() throws CatalogTransformerException, IOException {
    Query query = new QueryImpl(null, 1, 10, null, false, 0);
    QueryRequest queryRequest = new QueryRequestImpl(query);

    Map<String, Serializable> properties = new HashMap<>();
    properties.put("count", "5");
    queryRequest.getProperties().putAll(properties);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Collections.emptyList());
    when(sourceResponse.getHits()).thenReturn(0L);
    when(sourceResponse.getRequest()).thenReturn(queryRequest);

    BinaryContent content = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(content, is(notNullValue()));
  }

  private MetacardImpl createBasicMetacard(String id, String title) {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId(id);
    metacard.setTitle(title);
    return metacard;
  }

  private SourceResponse createSourceResponse(List<Result> results, long hits) {
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(results);
    when(sourceResponse.getHits()).thenReturn(hits);
    return sourceResponse;
  }
}
