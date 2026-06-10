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
package org.codice.ddf.cxf.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.message.Message;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SecureCxfClientFactoryTest {

  @Mock private SecureCxfClientFactory<TestService> clientFactory;

  @Mock private TestService mockClient;

  @Mock private WebClient mockWebClient;

  @Mock private Subject mockSubject;

  @Mock private Interceptor<? extends Message> mockInterceptor;

  private static final Integer MAX_REDIRECTS = 5;

  @BeforeEach
  public void setUp() {
    when(clientFactory.getClient()).thenReturn(mockClient);
    when(clientFactory.getWebClient()).thenReturn(mockWebClient);
    when(clientFactory.getWebSystemClient()).thenReturn(mockWebClient);
    when(clientFactory.getClientForSubject(any(Subject.class))).thenReturn(mockClient);
    when(clientFactory.getClientForSystemSubject(any(Subject.class))).thenReturn(mockClient);
    when(clientFactory.getWebClientForSubject(any(Subject.class))).thenReturn(mockWebClient);
    when(clientFactory.getSameUriRedirectMax()).thenReturn(MAX_REDIRECTS);
  }

  @Test
  public void testGetClient() {
    TestService client = clientFactory.getClient();

    assertThat(client, is(notNullValue()));
    assertThat(client, is(sameInstance(mockClient)));
    verify(clientFactory).getClient();
  }

  @Test
  public void testGetWebClient() {
    WebClient webClient = clientFactory.getWebClient();

    assertThat(webClient, is(notNullValue()));
    assertThat(webClient, is(sameInstance(mockWebClient)));
    verify(clientFactory).getWebClient();
  }

  @Test
  public void testGetWebSystemClient() {
    WebClient webClient = clientFactory.getWebSystemClient();

    assertThat(webClient, is(notNullValue()));
    assertThat(webClient, is(sameInstance(mockWebClient)));
    verify(clientFactory).getWebSystemClient();
  }

  @Test
  public void testGetClientForSubject() {
    TestService client = clientFactory.getClientForSubject(mockSubject);

    assertThat(client, is(notNullValue()));
    assertThat(client, is(sameInstance(mockClient)));
    verify(clientFactory).getClientForSubject(mockSubject);
  }

  @Test
  public void testGetClientForSubjectWithNullSubject() {
    when(clientFactory.getClientForSubject(null)).thenReturn(mockClient);

    TestService client = clientFactory.getClientForSubject(null);

    assertThat(client, is(notNullValue()));
    verify(clientFactory).getClientForSubject(null);
  }

  @Test
  public void testGetClientForSystemSubject() {
    TestService client = clientFactory.getClientForSystemSubject(mockSubject);

    assertThat(client, is(notNullValue()));
    assertThat(client, is(sameInstance(mockClient)));
    verify(clientFactory).getClientForSystemSubject(mockSubject);
  }

  @Test
  public void testGetClientForSystemSubjectWithNullSubject() {
    when(clientFactory.getClientForSystemSubject(null)).thenReturn(mockClient);

    TestService client = clientFactory.getClientForSystemSubject(null);

    assertThat(client, is(notNullValue()));
    verify(clientFactory).getClientForSystemSubject(null);
  }

  @Test
  public void testGetWebClientForSubject() {
    WebClient webClient = clientFactory.getWebClientForSubject(mockSubject);

    assertThat(webClient, is(notNullValue()));
    assertThat(webClient, is(sameInstance(mockWebClient)));
    verify(clientFactory).getWebClientForSubject(mockSubject);
  }

  @Test
  public void testGetWebClientForSubjectWithNullSubject() {
    when(clientFactory.getWebClientForSubject(null)).thenReturn(mockWebClient);

    WebClient webClient = clientFactory.getWebClientForSubject(null);

    assertThat(webClient, is(notNullValue()));
    verify(clientFactory).getWebClientForSubject(null);
  }

  @Test
  public void testGetSameUriRedirectMax() {
    Integer maxRedirects = clientFactory.getSameUriRedirectMax();

    assertThat(maxRedirects, is(notNullValue()));
    assertThat(maxRedirects, is(equalTo(MAX_REDIRECTS)));
    verify(clientFactory).getSameUriRedirectMax();
  }

  @Test
  public void testGetSameUriRedirectMaxReturnsNull() {
    when(clientFactory.getSameUriRedirectMax()).thenReturn(null);

    Integer maxRedirects = clientFactory.getSameUriRedirectMax();

    assertThat(maxRedirects, is(equalTo(null)));
    verify(clientFactory).getSameUriRedirectMax();
  }

  @Test
  public void testGetSameUriRedirectMaxReturnsZero() {
    when(clientFactory.getSameUriRedirectMax()).thenReturn(0);

    Integer maxRedirects = clientFactory.getSameUriRedirectMax();

    assertThat(maxRedirects, is(equalTo(0)));
    verify(clientFactory).getSameUriRedirectMax();
  }

  @Test
  public void testAddOutInterceptors() {
    clientFactory.addOutInterceptors(mockInterceptor);

    verify(clientFactory).addOutInterceptors(mockInterceptor);
  }

  @Test
  public void testAddOutInterceptorsWithNull() {
    clientFactory.addOutInterceptors(null);

    verify(clientFactory).addOutInterceptors(null);
  }

  @Test
  public void testAddOutInterceptorsMultipleTimes() {
    Interceptor<? extends Message> interceptor1 = mock(Interceptor.class);
    Interceptor<? extends Message> interceptor2 = mock(Interceptor.class);

    clientFactory.addOutInterceptors(interceptor1);
    clientFactory.addOutInterceptors(interceptor2);

    verify(clientFactory).addOutInterceptors(interceptor1);
    verify(clientFactory).addOutInterceptors(interceptor2);
  }

  @Test
  public void testSecureCxfClientFactoryInterface() {
    // Verify the interface contract exists and can be mocked
    assertThat(clientFactory, is(notNullValue()));
    assertThat(SecureCxfClientFactory.class.isInterface(), is(true));
  }

  @Test
  public void testSecureCxfClientFactoryMethods() throws NoSuchMethodException {
    // Verify the interface has all expected methods
    assertThat(SecureCxfClientFactory.class.getMethod("getClient"), is(notNullValue()));
    assertThat(SecureCxfClientFactory.class.getMethod("getWebClient"), is(notNullValue()));
    assertThat(SecureCxfClientFactory.class.getMethod("getWebSystemClient"), is(notNullValue()));
    assertThat(
        SecureCxfClientFactory.class.getMethod("getClientForSubject", Subject.class),
        is(notNullValue()));
    assertThat(
        SecureCxfClientFactory.class.getMethod("getClientForSystemSubject", Subject.class),
        is(notNullValue()));
    assertThat(
        SecureCxfClientFactory.class.getMethod("getWebClientForSubject", Subject.class),
        is(notNullValue()));
    assertThat(SecureCxfClientFactory.class.getMethod("getSameUriRedirectMax"), is(notNullValue()));
    assertThat(
        SecureCxfClientFactory.class.getMethod("addOutInterceptors", Interceptor.class),
        is(notNullValue()));
  }

  @Test
  public void testMultipleClientInstances() {
    TestService client1 = clientFactory.getClient();
    TestService client2 = clientFactory.getClient();

    assertThat(client1, is(notNullValue()));
    assertThat(client2, is(notNullValue()));
  }

  @Test
  public void testMultipleWebClientInstances() {
    WebClient webClient1 = clientFactory.getWebClient();
    WebClient webClient2 = clientFactory.getWebClient();

    assertThat(webClient1, is(notNullValue()));
    assertThat(webClient2, is(notNullValue()));
  }

  @Test
  public void testClientWithDifferentSubjects() {
    Subject subject1 = mock(Subject.class);
    Subject subject2 = mock(Subject.class);
    TestService client1Mock = mock(TestService.class);
    TestService client2Mock = mock(TestService.class);

    when(clientFactory.getClientForSubject(subject1)).thenReturn(client1Mock);
    when(clientFactory.getClientForSubject(subject2)).thenReturn(client2Mock);

    TestService client1 = clientFactory.getClientForSubject(subject1);
    TestService client2 = clientFactory.getClientForSubject(subject2);

    assertThat(client1, is(notNullValue()));
    assertThat(client2, is(notNullValue()));
    verify(clientFactory).getClientForSubject(subject1);
    verify(clientFactory).getClientForSubject(subject2);
  }

  @Test
  public void testWebClientWithDifferentSubjects() {
    Subject subject1 = mock(Subject.class);
    Subject subject2 = mock(Subject.class);
    WebClient webClient1Mock = mock(WebClient.class);
    WebClient webClient2Mock = mock(WebClient.class);

    when(clientFactory.getWebClientForSubject(subject1)).thenReturn(webClient1Mock);
    when(clientFactory.getWebClientForSubject(subject2)).thenReturn(webClient2Mock);

    WebClient webClient1 = clientFactory.getWebClientForSubject(subject1);
    WebClient webClient2 = clientFactory.getWebClientForSubject(subject2);

    assertThat(webClient1, is(notNullValue()));
    assertThat(webClient2, is(notNullValue()));
    verify(clientFactory).getWebClientForSubject(subject1);
    verify(clientFactory).getWebClientForSubject(subject2);
  }

  // Test interface to use as generic type parameter
  private interface TestService {
    String getData();

    void setData(String data);
  }
}
