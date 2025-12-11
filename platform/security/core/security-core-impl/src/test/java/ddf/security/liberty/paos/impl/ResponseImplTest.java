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

import ddf.security.liberty.paos.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.core.xml.schema.XSBooleanValue;

public class ResponseImplTest {

  private static final String NAMESPACE_URI = "urn:liberty:paos:2003-08";
  private static final String ELEMENT_LOCAL_NAME = "Response";
  private static final String NAMESPACE_PREFIX = "paos";
  private static final String TEST_REF_TO_MESSAGE_ID = "message-id-123";
  private static final String TEST_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next";

  private ResponseImpl response;

  @BeforeEach
  public void setUp() {
    response = new ResponseImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
  }

  @Test
  public void testConstructor() {
    ResponseImpl resp = new ResponseImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
    assertThat(resp, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullNamespaceUri() {
    ResponseImpl resp = new ResponseImpl(null, ELEMENT_LOCAL_NAME, NAMESPACE_PREFIX);
    assertThat(resp, is(notNullValue()));
  }

  @Test
  public void testConstructorWithNullNamespacePrefix() {
    ResponseImpl resp = new ResponseImpl(NAMESPACE_URI, ELEMENT_LOCAL_NAME, null);
    assertThat(resp, is(notNullValue()));
  }

  @Test
  public void testGetSetRefToMessageID() {
    assertThat(response.getRefToMessageID(), is(nullValue()));

    response.setRefToMessageID(TEST_REF_TO_MESSAGE_ID);

    assertThat(response.getRefToMessageID(), is(notNullValue()));
    assertThat(response.getRefToMessageID(), is(TEST_REF_TO_MESSAGE_ID));
  }

  @Test
  public void testGetSetSOAP11Actor() {
    assertThat(response.getSOAP11Actor(), is(nullValue()));

    response.setSOAP11Actor(TEST_ACTOR);

    assertThat(response.getSOAP11Actor(), is(notNullValue()));
    assertThat(response.getSOAP11Actor(), is(TEST_ACTOR));
  }

  @Test
  public void testIsSOAP11MustUnderstandDefaultValue() {
    Boolean mustUnderstand = response.isSOAP11MustUnderstand();
    assertThat(mustUnderstand, is(notNullValue()));
    assertThat(mustUnderstand, is(Boolean.FALSE));
  }

  @Test
  public void testSetSOAP11MustUnderstandBoolean() {
    response.setSOAP11MustUnderstand(Boolean.TRUE);

    assertThat(response.isSOAP11MustUnderstand(), is(Boolean.TRUE));

    response.setSOAP11MustUnderstand(Boolean.FALSE);

    assertThat(response.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testSetSOAP11MustUnderstandNull() {
    response.setSOAP11MustUnderstand(Boolean.TRUE);
    response.setSOAP11MustUnderstand((Boolean) null);

    assertThat(response.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testGetSetSOAP11MustUnderstandXSBoolean() {
    assertThat(response.isSOAP11MustUnderstandXSBoolean(), is(nullValue()));

    XSBooleanValue xsBoolean = new XSBooleanValue(Boolean.TRUE, true);
    response.setSOAP11MustUnderstand(xsBoolean);

    XSBooleanValue result = response.isSOAP11MustUnderstandXSBoolean();
    assertThat(result, is(notNullValue()));
    assertThat(result.getValue(), is(Boolean.TRUE));
  }

  @Test
  public void testSetSOAP11MustUnderstandXSBooleanNull() {
    response.setSOAP11MustUnderstand(new XSBooleanValue(Boolean.TRUE, true));
    response.setSOAP11MustUnderstand((XSBooleanValue) null);

    assertThat(response.isSOAP11MustUnderstandXSBoolean(), is(nullValue()));
    assertThat(response.isSOAP11MustUnderstand(), is(Boolean.FALSE));
  }

  @Test
  public void testGetOrderedChildren() {
    assertThat(response.getOrderedChildren(), is(nullValue()));
  }

  @Test
  public void testAllFieldsSet() {
    response.setRefToMessageID(TEST_REF_TO_MESSAGE_ID);
    response.setSOAP11Actor(TEST_ACTOR);
    response.setSOAP11MustUnderstand(Boolean.TRUE);

    assertThat(response.getRefToMessageID(), is(TEST_REF_TO_MESSAGE_ID));
    assertThat(response.getSOAP11Actor(), is(TEST_ACTOR));
    assertThat(response.isSOAP11MustUnderstand(), is(Boolean.TRUE));
  }

  @Test
  public void testImplementsInterfaces() {
    assertThat(response instanceof Response, is(true));
  }
}
