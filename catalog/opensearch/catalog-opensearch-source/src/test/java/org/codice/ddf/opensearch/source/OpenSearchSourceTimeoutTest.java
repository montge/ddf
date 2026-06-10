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
package org.codice.ddf.opensearch.source;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ddf.catalog.filter.FilterAdapter;
import ddf.catalog.filter.FilterBuilder;
import ddf.catalog.filter.proxy.adapter.GeotoolsFilterAdapterImpl;
import ddf.catalog.filter.proxy.builder.GeotoolsFilterBuilder;
import ddf.catalog.operation.QueryRequest;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.impl.QueryImpl;
import ddf.catalog.operation.impl.QueryRequestImpl;
import ddf.catalog.source.UnsupportedQueryException;
import ddf.security.encryption.EncryptionService;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.cxf.client.ClientBuilderFactory;
import org.codice.ddf.cxf.client.SecureCxfClientFactory;
import org.geotools.api.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Tests timeout and error handling for OpenSearchSource */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OpenSearchSourceTimeoutTest {

  private static final String SOURCE_ID = "TEST-TIMEOUT";

  private static final FilterAdapter FILTER_ADAPTER = new GeotoolsFilterAdapterImpl();

  private static final FilterBuilder FILTER_BUILDER = new GeotoolsFilterBuilder();

  @Mock private WebClient webClient;

  @Mock private SecureCxfClientFactory factory;

  @Mock private ClientBuilderFactory clientBuilderFactory;

  @Mock private EncryptionService encryptionService;

  @Mock private Response response;

  private OpenSearchParser openSearchParser;

  private OpenSearchFilterVisitor openSearchFilterVisitor;

  private OpenSearchSource source;

  @BeforeEach
  public void setUp() throws Exception {
    openSearchParser = new OpenSearchParserImpl();
    openSearchFilterVisitor = new OpenSearchFilterVisitor();

    when(factory.getWebClient()).thenReturn(webClient);
    when(factory.getWebClientForSubject(any())).thenReturn(webClient);
    when(webClient.replaceQueryParam(any(String.class), any(Object.class))).thenReturn(webClient);

    source =
        new OpenSearchSource(
            FILTER_ADAPTER,
            openSearchParser,
            openSearchFilterVisitor,
            encryptionService,
            clientBuilderFactory) {
          @Override
          protected SecureCxfClientFactory createClientFactory(
              java.net.URI url, String username, String password) {
            return factory;
          }
        };

    source.setShortname(SOURCE_ID);
    source.setEndpointUrl("http://localhost:8181/services/catalog/query");
    source.setParameters(
        java.util.Arrays.asList(
            "q",
            "src",
            "mr",
            "start",
            "count",
            "mt",
            "dn",
            "lat",
            "lon",
            "radius",
            "bbox",
            "polygon",
            "dtstart",
            "dtend",
            "dateName",
            "filter",
            "sort"));
    source.init();
  }

  @Test
  public void testQueryTimeout() {
    when(webClient.get()).thenThrow(new ProcessingException(new SocketTimeoutException("timeout")));

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(ProcessingException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQueryProcessingException() {
    when(webClient.get()).thenThrow(new ProcessingException("Connection refused"));

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(ProcessingException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQuery404NotFound() {
    when(webClient.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(Response.Status.NOT_FOUND.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("Not Found".getBytes(UTF_8)));

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(UnsupportedQueryException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQuery500InternalServerError() {
    when(webClient.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    when(response.getEntity())
        .thenReturn(new ByteArrayInputStream("Internal Server Error".getBytes(UTF_8)));

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(UnsupportedQueryException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQuery401Unauthorized() {
    when(webClient.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(Response.Status.UNAUTHORIZED.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("Unauthorized".getBytes(UTF_8)));

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(UnsupportedQueryException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQueryNullEntity() {
    when(webClient.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(null);

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    assertThrows(UnsupportedQueryException.class, () -> source.query(queryRequest));
  }

  @Test
  public void testQueryEmptyResponse() throws Exception {
    String emptyFeed =
        "<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:os=\"http://a9.com/-/spec/opensearch/1.1/\">"
            + "<title>Empty Response</title>"
            + "<os:totalResults>0</os:totalResults>"
            + "</feed>";

    InputStream is = new ByteArrayInputStream(emptyFeed.getBytes(StandardCharsets.UTF_8));
    when(webClient.get()).thenReturn(response);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(is);

    Filter filter = FILTER_BUILDER.attribute("anyText").like().text("test");
    QueryRequest queryRequest = new QueryRequestImpl(new QueryImpl(filter));

    SourceResponse sourceResponse = source.query(queryRequest);

    assertThat(sourceResponse, notNullValue());
    assertThat(sourceResponse.getHits(), is(0L));
    assertThat(sourceResponse.getResults().size(), is(0));
  }

  @Test
  public void testConnectionTimeoutConfiguration() {
    source.setConnectionTimeout(5000);
    assertThat(source.getConnectionTimeout(), is(5000));

    source.setConnectionTimeout(10000);
    assertThat(source.getConnectionTimeout(), is(10000));
  }

  @Test
  public void testReceiveTimeoutConfiguration() {
    source.setReceiveTimeout(30000);
    assertThat(source.getReceiveTimeout(), is(30000));

    source.setReceiveTimeout(60000);
    assertThat(source.getReceiveTimeout(), is(60000));
  }

  @Test
  public void testDisableCnCheckConfiguration() {
    source.setDisableCnCheck(true);
    assertThat(source.getDisableCnCheck(), is(true));

    source.setDisableCnCheck(false);
    assertThat(source.getDisableCnCheck(), is(false));
  }

  @Test
  public void testAllowRedirectsConfiguration() {
    source.setAllowRedirects(true);
    assertThat(source.getAllowRedirects(), is(true));

    source.setAllowRedirects(false);
    assertThat(source.getAllowRedirects(), is(false));
  }

  @Test
  public void testPollIntervalConfiguration() {
    source.setPollInterval(10);
    assertThat(source.pollInterval, is(10));

    source.setPollInterval(5);
    assertThat(source.pollInterval, is(5));

    // setPollInterval clamps with Math.max(1, interval): values below 1 become 1
    source.setPollInterval(0);
    assertThat(source.pollInterval, is(1));
  }
}
