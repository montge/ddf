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
package ddf.security.samlp.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.samlp.SignatureException;
import java.time.Duration;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ExtendWith(MockitoExtension.class)
public class SamlValidatorTest {

  private static final String TEST_DESTINATION = "https://test.example.com/logout";
  private static final String TEST_REQUEST_ID = "test-request-id-12345";
  private static final String TEST_ID = "test-id-67890";
  private static final String TEST_SIGNATURE = "testSignature";
  private static final String TEST_SIG_ALGO = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  private static final String TEST_SAML_STRING = "testSamlString";
  private static final String TEST_RELAY_STATE = "testRelayState";
  private static final String TEST_SIGNING_CERT = "testSigningCertificate";

  @Mock private SimpleSign simpleSign;

  @Mock private LogoutRequest logoutRequest;

  @Mock private LogoutResponse logoutResponse;

  @Mock private Issuer issuer;

  @Mock private NameID nameID;

  @Mock private Status status;

  @Mock private StatusCode statusCode;

  @Mock private Signature signature;

  @Mock private Document document;

  @Mock private Element element;

  private SamlValidator.Builder builder;

  @BeforeEach
  public void setUp() {
    builder = new SamlValidator.Builder(simpleSign);
  }

  @Test
  public void testBuilderWithValidLogoutRequest() throws Exception {
    setupValidLogoutRequest();

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testBuilderWithValidLogoutResponse() throws Exception {
    setupValidLogoutResponse();

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutResponse);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testBuilderWithNullBinding() throws Exception {
    setupValidLogoutRequest();

    assertThrows(
        IllegalArgumentException.class, () -> builder.build(TEST_DESTINATION, null, logoutRequest));
  }

  @Test
  public void testBuilderWithNullDestination() throws Exception {
    setupValidLogoutRequest();

    assertThrows(
        IllegalArgumentException.class,
        () -> builder.build(null, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testBuilderWithBlankDestination() throws Exception {
    setupValidLogoutRequest();

    assertThrows(
        IllegalArgumentException.class,
        () -> builder.build("", SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testBuilderWithRedirectBinding() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testBuilderWithRedirectBindingMissingSignature() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, null, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    assertThrows(
        UnsupportedOperationException.class,
        () -> builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest));
  }

  @Test
  public void testBuilderWithRedirectBindingMissingSigAlgo() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, null, TEST_SAML_STRING, TEST_SIGNING_CERT);

    assertThrows(
        UnsupportedOperationException.class,
        () -> builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest));
  }

  @Test
  public void testBuilderWithRedirectBindingMissingSamlString() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, null, TEST_SIGNING_CERT);

    assertThrows(
        UnsupportedOperationException.class,
        () -> builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest));
  }

  @Test
  public void testBuilderWithRedirectBindingMissingSigningCertificate() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, null);

    assertThrows(
        UnsupportedOperationException.class,
        () -> builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest));
  }

  @Test
  public void testSetTimeout() {
    Duration timeout = Duration.ofMinutes(5);
    builder.setTimeout(timeout);

    assertThat(builder.timeout, is(timeout));
  }

  @Test
  public void testSetTimeoutWithNull() {
    assertThrows(IllegalArgumentException.class, () -> builder.setTimeout(null));
  }

  @Test
  public void testSetClockSkew() {
    Duration clockSkew = Duration.ofSeconds(60);
    builder.setClockSkew(clockSkew);

    assertThat(builder.clockSkew, is(clockSkew));
  }

  @Test
  public void testSetClockSkewWithNull() {
    assertThrows(IllegalArgumentException.class, () -> builder.setClockSkew(null));
  }

  @Test
  public void testSetRequestId() {
    builder.setRequestId(TEST_REQUEST_ID);

    assertThat(builder.requestId, is(TEST_REQUEST_ID));
  }

  @Test
  public void testSetRequestIdWithNull() {
    assertThrows(IllegalArgumentException.class, () -> builder.setRequestId(null));
  }

  @Test
  public void testSetRequestIdWithBlank() {
    assertThrows(IllegalArgumentException.class, () -> builder.setRequestId(""));
  }

  @Test
  public void testValidateWithValidTimestamp() throws Exception {
    setupValidLogoutRequest();

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);
  }

  @Test
  public void testValidateWithNullIssueInstant() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getIssueInstant()).thenReturn(null);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithFutureIssueInstant() throws Exception {
    setupValidLogoutRequest();
    DateTime futureTime = DateTime.now().plusHours(1);
    when(logoutRequest.getIssueInstant()).thenReturn(futureTime);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithExpiredIssueInstant() throws Exception {
    setupValidLogoutRequest();
    DateTime pastTime = DateTime.now().minusHours(1);
    when(logoutRequest.getIssueInstant()).thenReturn(pastTime);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithNullSamlVersion() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getVersion()).thenReturn(null);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithInvalidSamlVersion() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getVersion()).thenReturn(SAMLVersion.VERSION_10);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithBlankId() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getID()).thenReturn("");

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithNullId() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getID()).thenReturn(null);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithValidDestination() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getDestination()).thenReturn(TEST_DESTINATION);

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);
  }

  @Test
  public void testValidateWithInvalidDestination() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getDestination()).thenReturn("https://wrong-destination.example.com");

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateWithMalformedDestinationUrl() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getDestination()).thenReturn("not-a-valid-url");

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateLogoutResponseWithRequestId() throws Exception {
    setupValidLogoutResponse();
    when(logoutResponse.getInResponseTo()).thenReturn(TEST_REQUEST_ID);

    builder.setRequestId(TEST_REQUEST_ID);
    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutResponse);
  }

  @Test
  public void testValidateLogoutResponseWithMismatchedRequestId() throws Exception {
    setupValidLogoutResponse();
    when(logoutResponse.getInResponseTo()).thenReturn("different-request-id");

    builder.setRequestId(TEST_REQUEST_ID);
    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutResponse));
  }

  @Test
  public void testValidateWithSignature() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getSignature()).thenReturn(signature);
    when(logoutRequest.getDOM()).thenReturn(element);
    when(element.getOwnerDocument()).thenReturn(document);

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);

    verify(simpleSign).validateSignature(any(Signature.class), any(Document.class));
  }

  @Test
  public void testValidateWithInvalidSignature() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getSignature()).thenReturn(signature);
    when(logoutRequest.getDOM()).thenReturn(element);
    when(element.getOwnerDocument()).thenReturn(document);

    org.mockito.Mockito.doThrow(new SignatureException("Invalid signature"))
        .when(simpleSign)
        .validateSignature(any(Signature.class), any(Document.class));

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest));
  }

  @Test
  public void testValidateRedirectRequest() throws Exception {
    setupValidLogoutRequest();
    when(simpleSign.validateSignature(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(true);

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest);

    verify(simpleSign).validateSignature(anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void testValidateRedirectRequestWithInvalidSignature() throws Exception {
    setupValidLogoutRequest();
    when(simpleSign.validateSignature(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(false);

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    assertThrows(
        ValidationException.class,
        () ->
            builder.buildAndValidate(
                TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest));
  }

  @Test
  public void testDefaultTimeout() {
    assertThat(builder.timeout, is(Duration.ofMinutes(10)));
  }

  @Test
  public void testDefaultClockSkew() {
    assertThat(builder.clockSkew, is(Duration.ofSeconds(30)));
  }

  @Test
  public void testValidateWithDestinationContainingQueryString() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getDestination()).thenReturn(TEST_DESTINATION + "?param=value");

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);
  }

  @Test
  public void testValidateLogoutResponseWithNoDestination() throws Exception {
    setupValidLogoutResponse();
    when(logoutResponse.getDestination()).thenReturn(null);

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutResponse);
  }

  @Test
  public void testValidateLogoutRequestWithNoDestination() throws Exception {
    setupValidLogoutRequest();
    when(logoutRequest.getDestination()).thenReturn(null);

    builder.buildAndValidate(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);
  }

  @Test
  public void testBuildWithPostRequest() throws Exception {
    setupValidLogoutRequest();

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutRequest);

    assertThat(validator instanceof SamlValidator.PostRequest, is(true));
  }

  @Test
  public void testBuildWithPostResponse() throws Exception {
    setupValidLogoutResponse();

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_POST, logoutResponse);

    assertThat(validator instanceof SamlValidator.PostResponse, is(true));
  }

  @Test
  public void testBuildWithRedirectRequest() throws Exception {
    setupValidLogoutRequest();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutRequest);

    assertThat(validator instanceof SamlValidator.RedirectRequest, is(true));
  }

  @Test
  public void testBuildWithRedirectResponse() throws Exception {
    setupValidLogoutResponse();

    builder.setRedirectParams(
        TEST_RELAY_STATE, TEST_SIGNATURE, TEST_SIG_ALGO, TEST_SAML_STRING, TEST_SIGNING_CERT);

    SamlValidator validator =
        builder.build(TEST_DESTINATION, SamlProtocol.Binding.HTTP_REDIRECT, logoutResponse);

    assertThat(validator instanceof SamlValidator.RedirectResponse, is(true));
  }

  private void setupValidLogoutRequest() {
    when(logoutRequest.getVersion()).thenReturn(SAMLVersion.VERSION_20);
    when(logoutRequest.getIssueInstant()).thenReturn(DateTime.now());
    when(logoutRequest.getID()).thenReturn(TEST_ID);
    when(logoutRequest.getDestination()).thenReturn(null);
    when(logoutRequest.getIssuer()).thenReturn(issuer);
    when(logoutRequest.getNameID()).thenReturn(nameID);
    when(logoutRequest.getSessionIndexes()).thenReturn(java.util.Collections.emptyList());
    when(logoutRequest.getSignature()).thenReturn(null);
  }

  private void setupValidLogoutResponse() {
    when(logoutResponse.getVersion()).thenReturn(SAMLVersion.VERSION_20);
    when(logoutResponse.getIssueInstant()).thenReturn(DateTime.now());
    when(logoutResponse.getID()).thenReturn(TEST_ID);
    when(logoutResponse.getDestination()).thenReturn(null);
    when(logoutResponse.getIssuer()).thenReturn(issuer);
    when(logoutResponse.getStatus()).thenReturn(status);
    when(status.getStatusCode()).thenReturn(statusCode);
    when(logoutResponse.getSignature()).thenReturn(null);
    when(logoutResponse.getInResponseTo()).thenReturn(null);
  }
}
