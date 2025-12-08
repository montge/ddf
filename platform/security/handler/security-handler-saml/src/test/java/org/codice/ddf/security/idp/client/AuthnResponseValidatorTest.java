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
package org.codice.ddf.security.idp.client;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.encryption.EncryptionService;
import ddf.security.samlp.SignatureException;
import ddf.security.samlp.impl.SimpleSign;
import ddf.security.samlp.impl.SystemCrypto;
import ddf.security.samlp.impl.ValidationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Document;

@RunWith(MockitoJUnitRunner.class)
public class AuthnResponseValidatorTest {

  @Mock private SimpleSign simpleSign;

  @Mock private Response response;

  @Mock private Status status;

  @Mock private StatusCode statusCode;

  @Mock private Assertion assertion;

  @Mock private Signature signature;

  @Mock private Document document;

  @Mock private org.w3c.dom.Element element;

  private AuthnResponseValidator validator;

  private static SystemCrypto systemCrypto;

  @BeforeClass
  public static void setupClass() throws Exception {
    EncryptionService encryptionService = mock(EncryptionService.class);
    systemCrypto =
        new SystemCrypto("encryption.properties", "signature.properties", encryptionService);
  }

  @Before
  public void setup() {
    when(response.getStatus()).thenReturn(status);
    when(status.getStatusCode()).thenReturn(statusCode);
  }

  @Test
  public void testValidateSuccessfulResponse() throws Exception {
    // Setup successful response
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);

    validator = new AuthnResponseValidator(simpleSign, false);

    // Should not throw exception
    validator.validate(response);

    verify(simpleSign, never()).validateSignature(any(), any());
  }

  @Test
  public void testValidateInvalidXmlObject() {
    XMLObject invalidObject = mock(XMLObject.class);
    validator = new AuthnResponseValidator(simpleSign, false);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(invalidObject);
        });
  }

  @Test
  public void testValidateInvalidXmlObjectMessage() {
    XMLObject invalidObject = mock(XMLObject.class);
    validator = new AuthnResponseValidator(simpleSign, false);

    try {
      validator.validate(invalidObject);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("Invalid AuthN response XML"));
    }
  }

  @Test
  public void testValidateUnsuccessfulStatus() {
    when(statusCode.getValue()).thenReturn(StatusCode.AUTHN_FAILED);
    validator = new AuthnResponseValidator(simpleSign, false);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(response);
        });
  }

  @Test
  public void testValidateUnsuccessfulStatusMessage() {
    when(statusCode.getValue()).thenReturn(StatusCode.REQUEST_DENIED);

    validator = new AuthnResponseValidator(simpleSign, false);

    try {
      validator.validate(response);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("AuthN request was unsuccessful"));
      assertThat(e.getMessage(), containsString(StatusCode.REQUEST_DENIED));
    }
  }

  @Test
  public void testValidateMissingAssertion() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    when(response.getAssertions()).thenReturn(new ArrayList<>());
    validator = new AuthnResponseValidator(simpleSign, false);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(response);
        });
  }

  @Test
  public void testValidateMissingAssertionMessage() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    when(response.getAssertions()).thenReturn(new ArrayList<>());

    validator = new AuthnResponseValidator(simpleSign, false);

    try {
      validator.validate(response);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("Assertion missing in AuthN response"));
    }
  }

  @Test
  public void testValidateMultipleAssertions() throws Exception {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    assertions.add(mock(Assertion.class));
    assertions.add(mock(Assertion.class));
    when(response.getAssertions()).thenReturn(assertions);

    validator = new AuthnResponseValidator(simpleSign, false);

    // Should not throw exception, just log info
    validator.validate(response);
  }

  @Test
  public void testValidateRedirectSignedNullDestination() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getDestination()).thenReturn(null);
    validator = new AuthnResponseValidator(simpleSign, true);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(response);
        });
  }

  @Test
  public void testValidateRedirectSignedNullDestinationMessage() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getDestination()).thenReturn(null);

    validator = new AuthnResponseValidator(simpleSign, true);

    try {
      validator.validate(response);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("Invalid Destination attribute"));
      assertThat(e.getMessage(), containsString("must be not null"));
    }
  }

  @Test
  public void testValidateRedirectSignedInvalidDestination() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getDestination()).thenReturn("https://wrong.destination.com/sso");
    validator = new AuthnResponseValidator(simpleSign, true);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(response);
        });
  }

  @Test
  public void testValidateRedirectSignedInvalidDestinationMessage() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getDestination()).thenReturn("https://wrong.destination.com/sso");

    validator = new AuthnResponseValidator(simpleSign, true);

    try {
      validator.validate(response);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("Invalid Destination attribute"));
      assertThat(e.getMessage(), containsString("does not match requested destination"));
    }
  }

  @Test
  public void testValidateWithValidSignature() throws Exception {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getSignature()).thenReturn(signature);
    when(response.getDOM()).thenReturn(element);
    when(element.getOwnerDocument()).thenReturn(document);

    validator = new AuthnResponseValidator(simpleSign, false);
    validator.validate(response);

    verify(simpleSign).validateSignature(signature, document);
  }

  @Test
  public void testValidateWithInvalidSignature() {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getSignature()).thenReturn(signature);
    when(response.getDOM()).thenReturn(element);
    when(element.getOwnerDocument()).thenReturn(document);
    doThrow(new SignatureException("Invalid signature"))
        .when(simpleSign)
        .validateSignature(any(), any());
    validator = new AuthnResponseValidator(simpleSign, false);

    assertThrows(
        ValidationException.class,
        () -> {
          validator.validate(response);
        });
  }

  @Test
  public void testValidateWithInvalidSignatureMessage() throws Exception {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getSignature()).thenReturn(signature);
    when(response.getDOM()).thenReturn(element);
    when(element.getOwnerDocument()).thenReturn(document);
    doThrow(new SignatureException("Invalid signature"))
        .when(simpleSign)
        .validateSignature(any(), any());

    validator = new AuthnResponseValidator(simpleSign, false);

    try {
      validator.validate(response);
      fail("Expected ValidationException");
    } catch (ValidationException e) {
      assertThat(e.getMessage(), containsString("Invalid or untrusted signature"));
    }
  }

  @Test
  public void testValidateAllStatusCodes() throws Exception {
    validator = new AuthnResponseValidator(simpleSign, false);

    String[] failureCodes = {
      StatusCode.REQUESTER,
      StatusCode.RESPONDER,
      StatusCode.VERSION_MISMATCH,
      StatusCode.REQUEST_DENIED,
      StatusCode.NO_PASSIVE,
      StatusCode.UNKNOWN_PRINCIPAL
    };

    for (String code : failureCodes) {
      when(statusCode.getValue()).thenReturn(code);
      try {
        validator.validate(response);
        fail("Expected ValidationException for status code: " + code);
      } catch (ValidationException e) {
        assertThat(e.getMessage(), containsString("AuthN request was unsuccessful"));
      }
    }
  }

  @Test
  public void testValidateResponseNotRedirectSigned() throws Exception {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getDestination()).thenReturn(null);

    validator = new AuthnResponseValidator(simpleSign, false);

    // Should not throw exception when wasRedirectSigned is false
    validator.validate(response);
  }

  @Test
  public void testValidateNoSignature() throws Exception {
    when(statusCode.getValue()).thenReturn(StatusCode.SUCCESS);
    List<Assertion> assertions = new ArrayList<>();
    assertions.add(assertion);
    when(response.getAssertions()).thenReturn(assertions);
    when(response.getSignature()).thenReturn(null);

    validator = new AuthnResponseValidator(simpleSign, false);

    // Should not throw exception
    validator.validate(response);

    verify(simpleSign, never()).validateSignature(any(), any());
  }
}
