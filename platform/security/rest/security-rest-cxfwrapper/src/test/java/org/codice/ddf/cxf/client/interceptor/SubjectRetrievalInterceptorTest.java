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
package org.codice.ddf.cxf.client.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.Subject;
import ddf.security.service.SecurityManager;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.MessageTrustDecider;
import org.apache.cxf.transport.https.HttpsURLConnectionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubjectRetrievalInterceptorTest {

  @Mock private SecurityManager securityManager;

  @Mock private Message message;

  @Mock private InterceptorChain interceptorChain;

  @Mock private Exchange exchange;

  @Mock private WrappedMessageContext wrappedMessageContext;

  private SubjectRetrievalInterceptor interceptor;

  @BeforeEach
  void setUp() {
    interceptor = new SubjectRetrievalInterceptor(securityManager);
  }

  @Test
  void testConstructor() {
    SubjectRetrievalInterceptor newInterceptor = new SubjectRetrievalInterceptor(securityManager);

    assertThat(newInterceptor, is(notNullValue()));
  }

  @Test
  void testGetPhase() {
    assertThat(interceptor.getPhase(), is(Phase.PRE_STREAM));
  }

  @Test
  void testHandleMessageWhenNotRequestor() {
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(null);

    interceptor.handleMessage(message);

    // Not a requestor -> method returns early: no trust decider installed and the interceptor
    // chain is never touched.
    verify(message, never()).put(eq(MessageTrustDecider.class), any(MessageTrustDecider.class));
    verify(message, never()).getInterceptorChain();
  }

  @Test
  void testHandleMessageWhenNotHttps() {
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(Boolean.TRUE);
    when(message.get("http.scheme")).thenReturn("http");

    interceptor.handleMessage(message);

    // Non-https scheme -> method returns early: no trust decider installed and the interceptor
    // chain is never touched.
    verify(message, never()).put(eq(MessageTrustDecider.class), any(MessageTrustDecider.class));
    verify(message, never()).getInterceptorChain();
  }

  @Test
  void testHandleMessageWithHttpsRequestor() {
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(Boolean.TRUE);
    when(message.get("http.scheme")).thenReturn("https");
    when(message.get(MessageTrustDecider.class)).thenReturn(null);
    when(message.getInterceptorChain()).thenReturn(interceptorChain);

    interceptor.handleMessage(message);

    verify(message).put(eq(MessageTrustDecider.class), any(MessageTrustDecider.class));
    verify(interceptorChain).add(any(Interceptor.class));
  }

  @Test
  void testHandleMessageWithExistingTrustDecider() {
    MessageTrustDecider existingDecider = mock(MessageTrustDecider.class);
    when(message.get(Message.REQUESTOR_ROLE)).thenReturn(Boolean.TRUE);
    when(message.get("http.scheme")).thenReturn("https");
    when(message.get(MessageTrustDecider.class)).thenReturn(existingDecider);
    when(message.getInterceptorChain()).thenReturn(interceptorChain);

    interceptor.handleMessage(message);

    verify(message).put(eq(MessageTrustDecider.class), any(MessageTrustDecider.class));
  }

  @Test
  void testHandleWrappedMessageContext() {
    Message innerMessage = mock(Message.class);
    when(wrappedMessageContext.getWrappedMessage()).thenReturn(innerMessage);
    when(innerMessage.get(Message.REQUESTOR_ROLE)).thenReturn(null);

    boolean result = interceptor.handleMessage(wrappedMessageContext);

    assertThat(result, is(true));
  }

  @Test
  void testHandleFault() {
    boolean result = interceptor.handleFault(wrappedMessageContext);

    assertThat(result, is(true));
  }

  @Test
  void testClose() {
    // close() is a documented no-op; assert it completes without throwing.
    assertDoesNotThrow(() -> interceptor.close(wrappedMessageContext));
  }

  @Test
  void testEventSecurityEndingInterceptorConstruction() {
    SubjectRetrievalInterceptor.EventSecurityEndingInterceptor endingInterceptor =
        new SubjectRetrievalInterceptor.EventSecurityEndingInterceptor();

    assertThat(endingInterceptor, is(notNullValue()));
    assertThat(endingInterceptor.getPhase(), is(Phase.SETUP_ENDING));
  }

  @Test
  void testEventSecurityEndingInterceptorHandleMessage() {
    SubjectRetrievalInterceptor.EventSecurityEndingInterceptor endingInterceptor =
        new SubjectRetrievalInterceptor.EventSecurityEndingInterceptor();

    Message inMessage = mock(Message.class);
    Subject mockSubject = mock(Subject.class);
    Map<String, Object> protocolHeaders = new HashMap<>();

    when(message.getExchange()).thenReturn(exchange);
    when(exchange.getInMessage()).thenReturn(inMessage);
    when(inMessage.get(Message.PROTOCOL_HEADERS)).thenReturn(protocolHeaders);
    when(message.get(Subject.class)).thenReturn(mockSubject);

    endingInterceptor.handleMessage(message);

    assertThat(protocolHeaders.containsKey(Subject.class.toString()), is(true));
  }

  @Test
  void testReceiverTrustDeciderConstruction() {
    MessageTrustDecider originalDecider = mock(MessageTrustDecider.class);
    SubjectRetrievalInterceptor.ReceiverTrustDecider trustDecider =
        interceptor.new ReceiverTrustDecider(originalDecider);

    assertThat(trustDecider, is(notNullValue()));
  }

  @Test
  void testReceiverTrustDeciderConstructionWithNullOriginal() {
    SubjectRetrievalInterceptor.ReceiverTrustDecider trustDecider =
        interceptor.new ReceiverTrustDecider(null);

    assertThat(trustDecider, is(notNullValue()));
  }

  @Test
  void testReceiverTrustDeciderEstablishTrustWithOriginalDecider() throws Exception {
    MessageTrustDecider originalDecider = mock(MessageTrustDecider.class);
    SubjectRetrievalInterceptor.ReceiverTrustDecider trustDecider =
        interceptor.new ReceiverTrustDecider(originalDecider);

    HttpsURLConnectionInfo connectionInfo = mock(HttpsURLConnectionInfo.class);
    X509Certificate[] certs = new X509Certificate[] {mock(X509Certificate.class)};
    Subject mockSubject = mock(Subject.class);

    when(connectionInfo.getServerCertificates()).thenReturn(certs);
    when(connectionInfo.getURI()).thenReturn(new URI("https://example.com"));
    when(securityManager.getSubject(any())).thenReturn(mockSubject);

    trustDecider.establishTrust("conduit", connectionInfo, message);

    verify(originalDecider).establishTrust("conduit", connectionInfo, message);
    verify(message).put(eq(Subject.class), eq(mockSubject));
  }

  @Test
  void testReceiverTrustDeciderEstablishTrustWithoutOriginalDecider() throws Exception {
    SubjectRetrievalInterceptor.ReceiverTrustDecider trustDecider =
        interceptor.new ReceiverTrustDecider(null);

    HttpsURLConnectionInfo connectionInfo = mock(HttpsURLConnectionInfo.class);
    X509Certificate[] certs = new X509Certificate[] {mock(X509Certificate.class)};
    Subject mockSubject = mock(Subject.class);

    when(connectionInfo.getServerCertificates()).thenReturn(certs);
    when(connectionInfo.getURI()).thenReturn(new URI("https://example.com"));
    when(securityManager.getSubject(any())).thenReturn(mockSubject);

    trustDecider.establishTrust("conduit", connectionInfo, message);

    verify(message).put(eq(Subject.class), eq(mockSubject));
  }
}
