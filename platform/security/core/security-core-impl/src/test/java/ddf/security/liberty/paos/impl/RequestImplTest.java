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
import static org.hamcrest.Matchers.nullValue;

import ddf.security.liberty.paos.Request;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.schema.XSBooleanValue;

public class RequestImplTest {

  private static final String NAMESPACE_URI = "urn:liberty:paos:2003-08";
  private static final String ELEMENT_LOCAL_NAME = "Request";
  private static final String NAMESPACE_PREFIX = "paos";
  private static final String TEST_URL = "https://example.com/acs";
  private static final String TEST_SERVICE = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp";
  private static final String TEST_MESSAGE_ID = "message-id-123";
  private static final String TEST_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next";

  private RequestImpl request;

  @Before
  public void setUp() {
    request = new RequestImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
  }

  @Test
  public void testConstructor() {
    RequestImpl req = new RequestImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
    assertThat(req, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullNamespaceUri() {
    RequestImpl req = new RequestImpl(null, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
    assertThat(req, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullNamespacePrefix() {
    RequestImpl req = new RequestImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, null);
    assertThat(req, is(notNullValue()));
  }

  @Test
  public void testGetSetResponseConsumerURL() {
    assertThat(request.getResponseConsumerURL(), is(nullValue()));

    request.setResponseConsumerURL(TEST_URL);

    assertThat(request.getResponseConsumerURL(), is(notNullValue()));
    assertThat(request.getResponseConsumerURL(), is(TEST_URL));
  }

  @Test
  public void testGetSetService() {
    assertThat(request.getService(), is(nullValue()));

    request.setService(TEST_SERVICE);

    assertThat(request.getService(), is(notNullValue()));
    assertThat(request.getService(), is(TEST_SERVICE));
  }

  @Test
  public void testGetSetMessageID() {
    assertThat(request.getMessageID(), is(nullValue()));

    request.setMessageID(TEST_MESSAGE_ID);

    assertThat(request.getMessageID(), is(notNullValue()));
    assertThat(request.getMessageID(), is(TEST_MESSAGE_ID));
  }

  @Test
  public void testGetSetSOAP11Actor() {
    assertThat(request.getSOAP11Actor(), is(nullValue()));

    request.setSOAP11Actor(TEST_ACTOR);

    assertThat(request.getSOAP11Actor(), is(notNullValue()));
    assertThat(request.getSOAP11Actor(), is(TEST_ACTOR));
  }

  @Test
  public void testIsSOAP11MustUnderstandDefaultValue() {
    Boolean mustUnderstand = request.isSOAP11MustUnderstand();
    assertThat(mustUnderstand, is(notNullValue()));
    assertThat(mustUnderstand, is(Boolean.FALSE));
  }

  @Test
  public void testSetSOAP11MustUnderstandBoolean() {
    request.setSOAP11MustUnderstand(Boolean.TRUE);

    assertThat(request.isSOAP11MustUnderstand(), is(Boolean.TRUE));

    request.setSOAP11MustUnderstand(Boolean.FALSE);

    assertThat(request.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testSetSOAP11MustUnderstandNull() {
    request.setSOAP11MustUnderstand(Boolean.TRUE);
    request.setSOAP11MustUnderstand((Boolean) null);

    assertThat(request.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testGetSetSOAP11MustUnderstandXSBoolean() {
    assertThat(request.isSOAP11MustUnderstandXSBoolean(), is(nullValue()));

    XSBooleanValue xsBoolean = new XSBooleanValue(Boolean.TRUE, true);
    request.setSOAP11MustUnderstand(xsBoolean);

    XSBooleanValue result = request.isSOAP11MustUnderstandXSBoolean();
    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is(Boolean.TRUE));
  }

  @Test
  public void testSetSOAP11MustUnderstandXSBooleanNull() {
    request.setSOAP11MustUnderstand(new XSBooleanValue(Boolean.TRUE, true));
    request.setSOAP11MustUnderstand((XSBooleanValue) null);

    assertThat(request.isSOAP11MustUnderstandXSBoolean(), is(nullValue()));
    assertThat(request.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testGetOrderedChildren() {
    assertThat(request.getOrderedChildren(), is(nullValue()));
  }

  @Test
  public void testAllFieldsSet() {
    request.setResponseConsumerURL(TEST_URL);
    request.setService(TEST_SERVICE);
    request.setMessageID(TEST_MESSAGE_ID);
    request.setSOAP11Actor(TEST_ACTOR);
    request.setSOAP11MustUnderstand(Boolean.TRUE);

    assertThat(request.getResponseConsumerURL(), is(TEST_URL));
    assertThat(request.getService(), is(TEST_SERVICE));
    assertThat(request.getMessageID(), is(TEST_MESSAGE_ID));
    assertThat(request.getSOAP11Actor(), is(TEST_ACTOR));
    assertThat(request.isSOAP11MustUnderstand(), is(Boolean.TRUE));
  }

  @Test
  public void testImplementsInterfaces() {
    assertThat(request instanceof Request, is(true));
  }
}
