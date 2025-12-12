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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.security.liberty.paos.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.common.xml.SAMLConstants;

class RequestBuilderTest {

  private RequestBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new RequestBuilder();
  }

  @Test
  void testBuildObjectReturnsRequestImpl() {
    RequestImpl request = builder.buildObject();

    assertThat(request, is(notNullValue()));
    assertThat(request, is(instanceOf(RequestImpl.class)));
    assertThat(request, is(instanceOf(Request.class)));
  }

  @Test
  void testBuildObjectUsesCorrectNamespace() {
    RequestImpl request = builder.buildObject();

    assertThat(request.getElementQName().getNamespaceURI(), is(SAMLConstants.PAOS_NS));
    assertThat(request.getElementQName().getLocalPart(), is(Request.DEFAULT_ELEMENT_LOCAL_NAME));
    assertThat(request.getElementQName().getPrefix(), is(SAMLConstants.PAOS_PREFIX));
  }

  @Test
  void testBuildObjectWithCustomParameters() {
    String customUri = "urn:custom:namespace";
    String customLocalName = "CustomRequest";
    String customPrefix = "custom";

    RequestImpl request = builder.buildObject(customUri, customLocalName, customPrefix);

    assertThat(request, is(notNullValue()));
    assertThat(request.getElementQName().getNamespaceURI(), is(customUri));
    assertThat(request.getElementQName().getLocalPart(), is(customLocalName));
    assertThat(request.getElementQName().getPrefix(), is(customPrefix));
  }

  @Test
  void testBuildObjectWithNullUri() {
    RequestImpl request = builder.buildObject(null, "Request", "paos");

    assertThat(request, is(notNullValue()));
  }

  @Test
  void testBuildObjectWithNullPrefix() {
    RequestImpl request = builder.buildObject(SAMLConstants.PAOS_NS, "Request", null);

    assertThat(request, is(notNullValue()));
  }

  @Test
  void testMultipleBuildObjectCallsReturnNewInstances() {
    RequestImpl request1 = builder.buildObject();
    RequestImpl request2 = builder.buildObject();

    assertThat(request1, is(notNullValue()));
    assertThat(request2, is(notNullValue()));
    assertThat(request1 == request2, is(false));
  }
}
