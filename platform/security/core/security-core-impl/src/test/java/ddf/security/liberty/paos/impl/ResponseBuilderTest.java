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

import ddf.security.liberty.paos.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensaml.saml.common.xml.SAMLConstants;

class ResponseBuilderTest {

  private ResponseBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new ResponseBuilder();
  }

  @Test
  void testBuildObjectReturnsResponseImpl() {
    ResponseImpl response = builder.buildObject();

    assertThat(response, is(notNullValue()));
    assertThat(response, is(instanceOf(ResponseImpl.class)));
    assertThat(response, is(instanceOf(Response.class)));
  }

  @Test
  void testBuildObjectUsesCorrectNamespace() {
    ResponseImpl response = builder.buildObject();

    assertThat(response.getElementQName().getNamespaceURI(), is(SAMLConstants.PAOS_NS));
    assertThat(response.getElementQName().getLocalPart(), is(Response.DEFAULT_ELEMENT_LOCAL_NAME));
    assertThat(response.getElementQName().getPrefix(), is(SAMLConstants.PAOS_PREFIX));
  }

  @Test
  void testBuildObjectWithCustomParameters() {
    String customUri = "urn:custom:namespace";
    String customLocalName = "CustomResponse";
    String customPrefix = "custom";

    ResponseImpl response = builder.buildObject(customUri, customLocalName, customPrefix);

    assertThat(response, is(notNullValue()));
    assertThat(response.getElementQName().getNamespaceURI(), is(customUri));
    assertThat(response.getElementQName().getLocalPart(), is(customLocalName));
    assertThat(response.getElementQName().getPrefix(), is(customPrefix));
  }

  @Test
  void testBuildObjectWithNullUri() {
    ResponseImpl response = builder.buildObject(null, "Response", "paos");

    assertThat(response, is(notNullValue()));
  }

  @Test
  void testBuildObjectWithNullPrefix() {
    ResponseImpl response = builder.buildObject(SAMLConstants.PAOS_NS, "Response", null);

    assertThat(response, is(notNullValue()));
  }

  @Test
  void testMultipleBuildObjectCallsReturnNewInstances() {
    ResponseImpl response1 = builder.buildObject();
    ResponseImpl response2 = builder.buildObject();

    assertThat(response1, is(notNullValue()));
    assertThat(response2, is(notNullValue()));
    assertThat(response1 == response2, is(false));
  }
}
