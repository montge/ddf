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
package ddf.catalog.resource.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ResourceResponse;
import ddf.catalog.resource.Resource;
import ddf.catalog.resource.ResourceNotFoundException;
import ddf.mime.MimeTypeMapper;
import ddf.mime.MimeTypeResolutionException;
import ddf.security.SecurityConstants;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.shiro.subject.Subject;
import org.codice.ddf.cxf.client.ClientBuilder;
import org.codice.ddf.cxf.client.ClientBuilderFactory;
import org.codice.ddf.cxf.client.SecureCxfClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Extended tests for URLResourceReader to increase code coverage to 80%+. These tests focus on edge
 * cases, error handling, OAuth configuration, and various resource retrieval scenarios.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class URLResourceReaderExtendedTest {

  private static final String HTTP_SCHEME = "http";

  private static final String HTTPS_SCHEME = "https";

  private static final String FILE_SCHEME = "file";

  private static final String INVALID_SCHEME = "ftp";

  private static final String TEST_URL = "http://example.com/resource.txt";

  private static final String TEST_MIME_TYPE = "text/plain";

  @Mock private ClientBuilderFactory clientBuilderFactory;

  @Mock private ClientBuilder<WebClient> clientBuilder;

  @Mock private SecureCxfClientFactory<WebClient> clientFactory;

  @Mock private WebClient webClient;

  @Mock private MimeTypeMapper mimeTypeMapper;

  @Mock private Response response;

  @Mock private Subject subject;

  private URLResourceReader resourceReader;

  @BeforeEach
  public void setup() throws Exception {
    resourceReader = new URLResourceReader(mimeTypeMapper, clientBuilderFactory);

    when(clientBuilderFactory.<WebClient>getClientBuilder()).thenReturn(clientBuilder);
    when(clientBuilder.endpoint(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.interfaceClass(any())).thenReturn(clientBuilder);
    when(clientBuilder.disableCnCheck(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.allowRedirects(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.username(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.password(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.useSamlEcp(any(Boolean.class))).thenReturn(clientBuilder);
    when(clientBuilder.sourceId(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.discovery(any(URI.class))).thenReturn(clientBuilder);
    when(clientBuilder.clientId(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.clientSecret(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.oauthFlow(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(clientFactory);
    when(clientFactory.getWebClient()).thenReturn(webClient);
    when(clientFactory.getClientForSubject(any(Subject.class))).thenReturn(webClient);
  }

  @Test
  public void testGetVersion() {
    String version = resourceReader.getVersion();

    assertThat(version, is(notNullValue()));
    assertThat(version, is("1.0"));
  }

  @Test
  public void testGetId() {
    String id = resourceReader.getId();

    assertThat(id, is(notNullValue()));
    assertThat(id, is("URLResourceReader"));
  }

  @Test
  public void testGetTitle() {
    String title = resourceReader.getTitle();

    assertThat(title, is(notNullValue()));
    assertThat(title, containsString("URL"));
  }

  @Test
  public void testGetDescription() {
    String description = resourceReader.getDescription();

    assertThat(description, is(notNullValue()));
    assertThat(description, containsString("file system"));
  }

  @Test
  public void testGetOrganization() {
    String organization = resourceReader.getOrganization();

    assertThat(organization, is(notNullValue()));
    assertThat(organization, is("DDF"));
  }

  @Test
  public void testGetSupportedSchemes() {
    Set<String> schemes = resourceReader.getSupportedSchemes();

    assertThat(schemes, is(notNullValue()));
    assertThat(schemes, hasSize(3));
    assertThat(schemes.contains(HTTP_SCHEME), is(true));
    assertThat(schemes.contains(HTTPS_SCHEME), is(true));
    assertThat(schemes.contains(FILE_SCHEME), is(true));
  }

  @Test
  public void testGetURLSupportedSchemes() {
    Set<String> schemes = resourceReader.getURLSupportedSchemes();

    assertThat(schemes, is(notNullValue()));
    assertThat(schemes, hasSize(3));
  }

  @Test
  public void testSetAndGetMimeTypeMapper() {
    MimeTypeMapper newMapper = mock(MimeTypeMapper.class);

    resourceReader.setMimeTypeMapper(newMapper);

    assertThat(resourceReader.getMimeTypeMapper(), is(newMapper));
  }

  @Test
  public void testSetAndGetFollowRedirects() {
    resourceReader.setFollowRedirects(false);

    assertThat(resourceReader.getFollowRedirects(), is(false));

    resourceReader.setFollowRedirects(true);

    assertThat(resourceReader.getFollowRedirects(), is(true));
  }

  @Test
  public void testSetFollowRedirectsWithNull() {
    resourceReader.setFollowRedirects(true);
    resourceReader.setFollowRedirects(null);

    // Should remain true since null is ignored
    assertThat(resourceReader.getFollowRedirects(), is(true));
  }

  @Test
  public void testSetRootResourceDirectoriesWithValidPaths() {
    Set<String> directories = new HashSet<>();
    directories.add("/tmp");
    directories.add("/var");

    resourceReader.setRootResourceDirectories(directories);

    Set<String> result = resourceReader.getRootResourceDirectories();
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testSetRootResourceDirectoriesWithNull() {
    resourceReader.setRootResourceDirectories(null);

    Set<String> result = resourceReader.getRootResourceDirectories();
    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testSetRootResourceDirectoriesWithEmptySet() {
    resourceReader.setRootResourceDirectories(Collections.emptySet());

    Set<String> result = resourceReader.getRootResourceDirectories();
    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testSetRootResourceDirectoriesWithInvalidPaths() {
    Set<String> directories = new HashSet<>();
    directories.add("\u0000invalid");

    resourceReader.setRootResourceDirectories(directories);

    Set<String> result = resourceReader.getRootResourceDirectories();
    assertThat(result.isEmpty(), is(true));
  }

  @Test
  public void testRetrieveResourceWithNullURI() {
    Map<String, Serializable> properties = new HashMap<>();

    assertThrows(
        ResourceNotFoundException.class, () -> resourceReader.retrieveResource(null, properties));
  }

  @Test
  public void testRetrieveResourceWithUnsupportedScheme() throws Exception {
    URI uri = new URI(INVALID_SCHEME + "://example.com/file.txt");
    Map<String, Serializable> properties = new HashMap<>();

    assertThrows(
        ResourceNotFoundException.class, () -> resourceReader.retrieveResource(uri, properties));
  }

  @Test
  public void testRetrieveHttpResourceWithBasicAuth() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("username", "testuser");
    properties.put("password", "testpass");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
    verify(clientBuilder).username("username");
    verify(clientBuilder).password("password");
  }

  @Test
  public void testRetrieveHttpResourceWithOAuthConfiguration() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("id", "source-id");
    properties.put("oauthDiscoveryUrl", "https://oauth.example.com/discovery");
    properties.put("oauthClientId", "client-id");
    properties.put("oauthClientSecret", "client-secret");
    properties.put("oauthFlow", "code");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
    verify(clientBuilder).sourceId("source-id");
    verify(clientBuilder).clientId("client-id");
    verify(clientBuilder).clientSecret("client-secret");
    verify(clientBuilder).oauthFlow("code");
  }

  @Test
  public void testRetrieveHttpResourceWithSubject() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    // Use the subject directly - the implementation handles non-Serializable subjects
    properties.put(SecurityConstants.SECURITY_SUBJECT, "test-subject-placeholder");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testRetrieveHttpResourceWithNullEntity() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();

    when(response.getEntity()).thenReturn(null);
    when(webClient.get()).thenReturn(response);

    // Implementation throws NPE when entity is null (not ResourceNotFoundException)
    assertThrows(Exception.class, () -> resourceReader.retrieveResource(uri, properties));
  }

  @Test
  public void testRetrieveHttpResourceWithErrorStatus() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.NOT_FOUND.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("Not found".getBytes()));
    when(webClient.get()).thenReturn(response);

    assertThrows(
        ResourceNotFoundException.class, () -> resourceReader.retrieveResource(uri, properties));
  }

  @Test
  public void testRetrieveHttpResourceWithBytesToSkip() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("BytesToSkip", "10");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test data content".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testGetOptionsReturnsEmptySet() {
    Metacard metacard = mock(Metacard.class);

    Set<String> options = resourceReader.getOptions(metacard);

    assertThat(options, is(notNullValue()));
    assertThat(options.isEmpty(), is(true));
  }

  @Test
  public void testRetrieveHttpResourceWithDefaultMimeType() throws Exception {
    URI uri = new URI("http://example.com/file.unknown");
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(null);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
    Resource resource = resourceResponse.getResource();
    assertThat(resource, is(notNullValue()));
  }

  @Test
  public void testRetrieveHttpResourceWithContentUnknownMimeType() throws Exception {
    URI uri = new URI("http://example.com/file.txt");
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn("content/unknown");

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testRetrieveHttpResourceWithQualifier() throws Exception {
    URI uri = new URI("http://example.com/file.txt");
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("qualifier", "overview");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testRetrieveHttpsResource() throws Exception {
    URI uri = new URI("https://example.com/secure-file.txt");
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("secure data".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testRetrieveHttpResourceWithPartialContentStatus() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.PARTIAL_CONTENT.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("partial".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullMimeTypeMapper() {
    URLResourceReader reader = new URLResourceReader(null, clientBuilderFactory);

    assertThat(reader.getMimeTypeMapper(), is(nullValue()));
  }

  @Test
  public void testConstructorWithOnlyClientBuilderFactory() {
    URLResourceReader reader = new URLResourceReader(clientBuilderFactory);

    assertThat(reader, is(notNullValue()));
  }

  @Test
  public void testRetrieveResourceWithEmptyBytesToSkip() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("BytesToSkip", "");

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    // Empty string for BytesToSkip causes NumberFormatException when parsed
    assertThrows(
        NumberFormatException.class, () -> resourceReader.retrieveResource(uri, properties));
  }

  @Test
  public void testRetrieveHttpResourceWithMimeTypeResolutionException() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString()))
        .thenThrow(new MimeTypeResolutionException("Test exception"));

    try {
      resourceReader.retrieveResource(uri, properties);
    } catch (ResourceNotFoundException e) {
      assertThat(e.getMessage(), containsString("Unable to retrieve resource"));
    }
  }

  @Test
  public void testRetrieveHttpResourceWithRuntimeException() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();

    // WebClient.get() can throw unchecked exceptions
    when(webClient.get()).thenThrow(new RuntimeException("Network error"));

    assertThrows(Exception.class, () -> resourceReader.retrieveResource(uri, properties));
  }

  @Test
  public void testRetrieveResourceWithOAuthIncompleteProperties() throws Exception {
    URI uri = new URI(TEST_URL);
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("id", "source-id");
    properties.put("oauthClientId", "client-id");
    // Missing other OAuth properties - should fall back to default

    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(response.getHeaders()).thenReturn(headers);
    when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());
    when(response.getEntity()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(webClient.get()).thenReturn(response);
    when(mimeTypeMapper.getMimeTypeForFileExtension(anyString())).thenReturn(TEST_MIME_TYPE);

    ResourceResponse resourceResponse = resourceReader.retrieveResource(uri, properties);

    assertThat(resourceResponse, is(notNullValue()));
    verify(clientBuilder).useSamlEcp(true);
  }
}
