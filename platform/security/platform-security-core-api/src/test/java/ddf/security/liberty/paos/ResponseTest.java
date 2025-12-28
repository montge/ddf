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

public class ResponseTest {

  @Test
  public void testPaosNsConstant() {
    assertThat(Response.PAOS_NS, is("urn:liberty:paos:2003-08"));
  }

  @Test
  public void testPaosPrefixConstant() {
    assertThat(Response.PAOS_PREFIX, is("paos"));
  }

  @Test
  public void testDefaultElementLocalNameConstant() {
    assertThat(Response.DEFAULT_ELEMENT_LOCAL_NAME, is("Response"));
  }

  @Test
  public void testDefaultElementName() {
    QName elementName = Response.DEFAULT_ELEMENT_NAME;

    assertThat(elementName, is(notNullValue()));
    assertThat(elementName.getNamespaceURI(), is(Response.PAOS_NS));
    assertThat(elementName.getLocalPart(), is(Response.DEFAULT_ELEMENT_LOCAL_NAME));
    assertThat(elementName.getPrefix(), is(Response.PAOS_PREFIX));
  }

  @Test
  public void testTypeLocalNameConstant() {
    assertThat(Response.TYPE_LOCAL_NAME, is("ResponseType"));
  }

  @Test
  public void testTypeName() {
    QName typeName = Response.TYPE_NAME;

    assertThat(typeName, is(notNullValue()));
    assertThat(typeName.getNamespaceURI(), is(Response.PAOS_NS));
    assertThat(typeName.getLocalPart(), is(Response.TYPE_LOCAL_NAME));
    assertThat(typeName.getPrefix(), is(Response.PAOS_PREFIX));
  }

  @Test
  public void testRefToMessageIdAttribNameConstant() {
    assertThat(Response.REF_TO_MESSAGE_ID_ATTRIB_NAME, is("refToMessageID"));
  }
}
