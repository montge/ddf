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
package ddf.security.liberty.paos.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.liberty.paos.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class RequestMarshallerTest {

  @BeforeAll
  static void initOpenSaml() throws InitializationException {
    InitializationService.initialize();
  }

  private static final String NAMESPACE_URI = "urn:liberty:paos:2003-08";
  private static final String ELEMENT_LOCAL_NAME = "Request";
  private static final String NAMESPACE_PREFIX = "paos";
  private static final String TEST_URL = "https://example.com/acs";
  private static final String TEST_SERVICE = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp";
  private static final String TEST_MESSAGE_ID = "message-id-123";
  private static final String TEST_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next";

  private RequestMarshaller marshaller;
  private RequestImpl request;
  private Element mockElement;

  @BeforeEach
  void setUp() {
    marshaller = new RequestMarshaller();
    request = new RequestImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
    mockElement = mock(Element.class);
    Document mockDocument = mock(Document.class);
    Attr mockAttr = mock(Attr.class);
    when(mockElement.getOwnerDocument()).thenReturn(mockDocument);
    when(mockDocument.createAttributeNS(any(), any())).thenReturn(mockAttr);
  }

  @Test
  void testMarshallerIsNotNull() {
    assertThat(marshaller, is(notNullValue()));
  }

  @Test
  void testMarshallAttributesWithResponseConsumerURL() throws MarshallingException {
    request.setResponseConsumerURL(TEST_URL);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement)
        .setAttributeNS(isNull(), eq(Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME), eq(TEST_URL));
  }

  @Test
  void testMarshallAttributesWithService() throws MarshallingException {
    request.setService(TEST_SERVICE);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement).setAttributeNS(isNull(), eq(Request.SERVICE_ATTRIB_NAME), eq(TEST_SERVICE));
  }

  @Test
  void testMarshallAttributesWithMessageID() throws MarshallingException {
    request.setMessageID(TEST_MESSAGE_ID);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement)
        .setAttributeNS(isNull(), eq(Request.MESSAGE_ID_ATTRIB_NAME), eq(TEST_MESSAGE_ID));
  }

  @Test
  void testMarshallAttributesWithSOAP11Actor() throws MarshallingException {
    request.setSOAP11Actor(TEST_ACTOR);

    marshaller.marshallAttributes(request, mockElement);

    // SOAP Actor (and MustUnderstand) are set via setAttributeNodeNS
    verify(mockElement, atLeastOnce()).setAttributeNodeNS(any());
  }

  @Test
  void testMarshallAttributesWithNullResponseConsumerURL() throws MarshallingException {
    request.setResponseConsumerURL(null);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement, never())
        .setAttributeNS(isNull(), eq(Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME), any());
  }

  @Test
  void testMarshallAttributesWithNullService() throws MarshallingException {
    request.setService(null);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement, never()).setAttributeNS(isNull(), eq(Request.SERVICE_ATTRIB_NAME), any());
  }

  @Test
  void testMarshallAttributesWithNullMessageID() throws MarshallingException {
    request.setMessageID(null);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement, never())
        .setAttributeNS(isNull(), eq(Request.MESSAGE_ID_ATTRIB_NAME), any());
  }

  @Test
  void testMarshallAttributesWithMustUnderstand() throws MarshallingException {
    request.setSOAP11MustUnderstand(new XSBooleanValue(true, true));

    marshaller.marshallAttributes(request, mockElement);

    // MustUnderstand is set via setAttributeNodeNS
    verify(mockElement).setAttributeNodeNS(any());
  }

  @Test
  void testMarshallAttributesWithAllValues() throws MarshallingException {
    request.setResponseConsumerURL(TEST_URL);
    request.setService(TEST_SERVICE);
    request.setMessageID(TEST_MESSAGE_ID);
    request.setSOAP11Actor(TEST_ACTOR);
    request.setSOAP11MustUnderstand(Boolean.TRUE);

    marshaller.marshallAttributes(request, mockElement);

    verify(mockElement)
        .setAttributeNS(isNull(), eq(Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME), eq(TEST_URL));
    verify(mockElement).setAttributeNS(isNull(), eq(Request.SERVICE_ATTRIB_NAME), eq(TEST_SERVICE));
    verify(mockElement)
        .setAttributeNS(isNull(), eq(Request.MESSAGE_ID_ATTRIB_NAME), eq(TEST_MESSAGE_ID));
  }
}
