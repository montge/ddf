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
package ddf.security.liberty.paos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.Test;

public class RequestTest {

  @Test
  public void testPaosNsConstant() {
    assertThat(Request.PAOS_NS, is("urn:liberty:paos:2003-08"));
  }

  @Test
  public void testPaosPrefixConstant() {
    assertThat(Request.PAOS_PREFIX, is("paos"));
  }

  @Test
  public void testDefaultElementLocalNameConstant() {
    assertThat(Request.DEFAULT_ELEMENT_LOCAL_NAME, is("Request"));
  }

  @Test
  public void testDefaultElementName() {
    QName elementName = Request.DEFAULT_ELEMENT_NAME;

    assertThat(elementName, is(notNullValue()));
    assertThat(elementName.getNamespaceURI(), is(Request.PAOS_NS));
    assertThat(elementName.getLocalPart(), is(Request.DEFAULT_ELEMENT_LOCAL_NAME));
    assertThat(elementName.getPrefix(), is(Request.PAOS_PREFIX));
  }

  @Test
  public void testTypeLocalNameConstant() {
    assertThat(Request.TYPE_LOCAL_NAME, is("RequestType"));
  }

  @Test
  public void testTypeName() {
    QName typeName = Request.TYPE_NAME;

    assertThat(typeName, is(notNullValue()));
    assertThat(typeName.getNamespaceURI(), is(Request.PAOS_NS));
    assertThat(typeName.getLocalPart(), is(Request.TYPE_LOCAL_NAME));
    assertThat(typeName.getPrefix(), is(Request.PAOS_PREFIX));
  }

  @Test
  public void testResponseConsumerUrlAttribNameConstant() {
    assertThat(Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME, is("responseConsumerURL"));
  }

  @Test
  public void testServiceAttribNameConstant() {
    assertThat(Request.SERVICE_ATTRIB_NAME, is("service"));
  }

  @Test
  public void testMessageIdAttribNameConstant() {
    assertThat(Request.MESSAGE_ID_ATTRIB_NAME, is("messageID"));
  }

  @Test
  public void testEcpServiceConstant() {
    assertThat(Request.ECP_SERVICE, is("urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp"));
  }
}
