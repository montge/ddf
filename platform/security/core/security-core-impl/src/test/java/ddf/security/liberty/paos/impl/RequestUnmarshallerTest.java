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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.security.liberty.paos.Request;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;
import org.w3c.dom.Attr;

class RequestUnmarshallerTest {

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

  private RequestUnmarshaller unmarshaller;
  private RequestImpl request;

  @BeforeEach
  void setUp() {
    unmarshaller = new RequestUnmarshaller();
    request = new RequestImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
  }

  @Test
  void testUnmarshallerIsNotNull() {
    assertThat(unmarshaller, is(notNullValue()));
  }

  @Test
  void testProcessAttributeResponseConsumerURL() throws UnmarshallingException {
    Attr attr = createMockAttr(Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME, TEST_URL, null, null);

    unmarshaller.processAttribute(request, attr);

    assertThat(request.getResponseConsumerURL(), is(TEST_URL));
  }

  @Test
  void testProcessAttributeService() throws UnmarshallingException {
    Attr attr = createMockAttr(Request.SERVICE_ATTRIB_NAME, TEST_SERVICE, null, null);

    unmarshaller.processAttribute(request, attr);

    assertThat(request.getService(), is(TEST_SERVICE));
  }

  @Test
  void testProcessAttributeMessageID() throws UnmarshallingException {
    Attr attr = createMockAttr(Request.MESSAGE_ID_ATTRIB_NAME, TEST_MESSAGE_ID, null, null);

    unmarshaller.processAttribute(request, attr);

    assertThat(request.getMessageID(), is(TEST_MESSAGE_ID));
  }

  @Test
  void testProcessAttributeMustUnderstandTrue() throws UnmarshallingException {
    Attr attr =
        createMockAttr(
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_LOCAL_NAME,
            "true",
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getNamespaceURI(),
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getPrefix());

    unmarshaller.processAttribute(request, attr);

    assertThat(request.isSOAP11MustUnderstand(), is(true));
  }

  @Test
  void testProcessAttributeMustUnderstandFalse() throws UnmarshallingException {
    Attr attr =
        createMockAttr(
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_LOCAL_NAME,
            "false",
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getNamespaceURI(),
            MustUnderstandBearing.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getPrefix());

    unmarshaller.processAttribute(request, attr);

    assertThat(request.isSOAP11MustUnderstand(), is(false));
  }

  @Test
  void testProcessAttributeActor() throws UnmarshallingException {
    Attr attr =
        createMockAttr(
            ActorBearing.SOAP11_ACTOR_ATTR_LOCAL_NAME,
            TEST_ACTOR,
            ActorBearing.SOAP11_ACTOR_ATTR_NAME.getNamespaceURI(),
            ActorBearing.SOAP11_ACTOR_ATTR_NAME.getPrefix());

    unmarshaller.processAttribute(request, attr);

    assertThat(request.getSOAP11Actor(), is(TEST_ACTOR));
  }

  @Test
  void testProcessAttributeUnknownAttribute() throws UnmarshallingException {
    Attr attr = createMockAttr("unknownAttribute", "value", null, null);

    // Should not throw exception for unknown attributes
    unmarshaller.processAttribute(request, attr);

    // Request fields should remain unchanged
    assertThat(request.getResponseConsumerURL(), is((String) null));
    assertThat(request.getService(), is((String) null));
    assertThat(request.getMessageID(), is((String) null));
  }

  private Attr createMockAttr(String localName, String value, String namespaceUri, String prefix) {
    Attr attr = mock(Attr.class);
    when(attr.getLocalName()).thenReturn(localName);
    when(attr.getValue()).thenReturn(value);
    when(attr.getNamespaceURI()).thenReturn(namespaceUri);
    when(attr.getPrefix()).thenReturn(prefix);
    if (namespaceUri != null) {
      when(attr.getNodeName()).thenReturn(prefix != null ? prefix + ":" + localName : localName);
    } else {
      when(attr.getNodeName()).thenReturn(localName);
    }
    return attr;
  }
}
