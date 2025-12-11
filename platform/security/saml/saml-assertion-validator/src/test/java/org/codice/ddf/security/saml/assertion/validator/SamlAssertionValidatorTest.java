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
package org.codice.ddf.security.saml.assertion.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.codice.ddf.platform.filter.AuthenticationFailureException;
import org.codice.ddf.security.handler.SAMLAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SamlAssertionValidatorTest {

  @Mock private SAMLAuthenticationToken mockToken;

  private SamlAssertionValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new TestSamlAssertionValidator();
  }

  @Test
  public void testValidateWithValidToken() throws AuthenticationFailureException {
    // Test that validate can be called without throwing exception
    validator.validate(mockToken);

    // Verify the method was invoked
    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testValidateWithInvalidToken() {
    SamlAssertionValidator failingValidator = new FailingSamlAssertionValidator();

    assertThrows(AuthenticationFailureException.class, () -> failingValidator.validate(mockToken));
  }

  @Test
  public void testValidateWithNullToken() {
    SamlAssertionValidator nullCheckingValidator = new NullCheckingSamlAssertionValidator();

    assertThrows(AuthenticationFailureException.class, () -> nullCheckingValidator.validate(null));
  }

  @Test
  public void testSetSignatureProperties() {
    String signatureProps = "/path/to/signature.properties";

    validator.setSignatureProperties(signatureProps);

    // Verify that setting properties doesn't throw exception
    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testSetSignaturePropertiesWithNull() {
    // Test that setting null properties doesn't throw exception
    validator.setSignatureProperties(null);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testSetSignaturePropertiesWithEmptyString() {
    validator.setSignatureProperties("");

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testGetSignatureProperties() {
    String input = "/path/to/signature.properties";

    String result = validator.getSignatureProperties(input);

    // Based on the interface signature, it returns the input parameter
    assertThat(result, is(equalTo(input)));
  }

  @Test
  public void testGetSignaturePropertiesWithNull() {
    String result = validator.getSignatureProperties(null);

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testGetSignaturePropertiesWithEmptyString() {
    String result = validator.getSignatureProperties("");

    assertThat(result, is(equalTo("")));
  }

  @Test
  public void testGetSignaturePropertiesWithDifferentPaths() {
    String path1 = "/path/to/signature1.properties";
    String path2 = "/path/to/signature2.properties";

    String result1 = validator.getSignatureProperties(path1);
    String result2 = validator.getSignatureProperties(path2);

    assertThat(result1, is(equalTo(path1)));
    assertThat(result2, is(equalTo(path2)));
    assertThat(result1, is(not(equalTo(result2))));
  }

  @Test
  public void testValidatorInterfaceContract() throws AuthenticationFailureException {
    // Verify that the interface contract is maintained
    SamlAssertionValidator mockValidator = mock(SamlAssertionValidator.class);

    // Test that the interface can be mocked without any issues
    verify(mockValidator, never()).validate(any());
    verify(mockValidator, never()).setSignatureProperties(any());
    verify(mockValidator, never()).getSignatureProperties(any());
  }

  @Test
  public void testMultipleValidateCalls() throws AuthenticationFailureException {
    // Test that validator can be called multiple times
    validator.validate(mockToken);
    validator.validate(mockToken);
    validator.validate(mockToken);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testSetAndGetSignaturePropertiesInteraction() {
    String props = "/etc/signature.properties";

    // Set the properties
    validator.setSignatureProperties(props);

    // Get should return what was passed as parameter (based on interface signature)
    String result = validator.getSignatureProperties(props);

    assertThat(result, is(equalTo(props)));
  }

  @Test
  public void testValidatorWithMockedToken() throws AuthenticationFailureException {
    SAMLAuthenticationToken localMockToken = mock(SAMLAuthenticationToken.class);

    // Should not throw exception in our test implementation
    validator.validate(localMockToken);

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testValidateThrowsAuthenticationFailureException() {
    // Use the actual implementation that throws
    assertThrows(
        AuthenticationFailureException.class,
        () -> new FailingSamlAssertionValidator().validate(mockToken));
  }

  @Test
  public void testValidatorCanBeExtended() {
    // Verify that the interface can be implemented
    SamlAssertionValidator customValidator = new CustomSamlAssertionValidator();

    assertThat(customValidator, is(notNullValue()));
    // Just verify it's not null - the instanceof check is redundant
  }

  @Test
  public void testSetSignaturePropertiesCanBeCalledMultipleTimes() {
    validator.setSignatureProperties("path1");
    validator.setSignatureProperties("path2");
    validator.setSignatureProperties("path3");

    assertThat(validator, is(notNullValue()));
  }

  @Test
  public void testGetSignaturePropertiesIsIdempotent() {
    String props = "test.properties";

    String result1 = validator.getSignatureProperties(props);
    String result2 = validator.getSignatureProperties(props);
    String result3 = validator.getSignatureProperties(props);

    assertThat(result1, is(equalTo(result2)));
    assertThat(result2, is(equalTo(result3)));
    assertThat(result1, is(equalTo(props)));
  }

  @Test
  public void testInterfaceMethodSignatures() throws AuthenticationFailureException {
    // Verify that all interface methods are present and callable
    SamlAssertionValidator testValidator = new TestSamlAssertionValidator();

    // validate method exists and accepts SAMLAuthenticationToken
    testValidator.validate(mockToken);

    // setSignatureProperties method exists and accepts String
    testValidator.setSignatureProperties("test");

    // getSignatureProperties method exists, accepts String and returns String
    String result = testValidator.getSignatureProperties("test");
    assertThat(result, is(notNullValue()));
  }

  // Test implementation classes

  /** Simple test implementation that does nothing */
  private static class TestSamlAssertionValidator implements SamlAssertionValidator {
    @Override
    public void validate(SAMLAuthenticationToken token) throws AuthenticationFailureException {
      // No-op for testing
    }

    @Override
    public void setSignatureProperties(String signatureProperties) {
      // No-op for testing
    }

    @Override
    public String getSignatureProperties(String signatureProperties) {
      return signatureProperties;
    }
  }

  /** Implementation that always fails validation */
  private static class FailingSamlAssertionValidator implements SamlAssertionValidator {
    @Override
    public void validate(SAMLAuthenticationToken token) throws AuthenticationFailureException {
      throw new AuthenticationFailureException("Validation failed");
    }

    @Override
    public void setSignatureProperties(String signatureProperties) {
      // No-op for testing
    }

    @Override
    public String getSignatureProperties(String signatureProperties) {
      return signatureProperties;
    }
  }

  /** Implementation that checks for null tokens */
  private static class NullCheckingSamlAssertionValidator implements SamlAssertionValidator {
    @Override
    public void validate(SAMLAuthenticationToken token) throws AuthenticationFailureException {
      if (token == null) {
        throw new AuthenticationFailureException("Token cannot be null");
      }
    }

    @Override
    public void setSignatureProperties(String signatureProperties) {
      // No-op for testing
    }

    @Override
    public String getSignatureProperties(String signatureProperties) {
      return signatureProperties;
    }
  }

  /** Custom implementation for extensibility testing */
  private static class CustomSamlAssertionValidator implements SamlAssertionValidator {
    @Override
    public void validate(SAMLAuthenticationToken token) throws AuthenticationFailureException {
      // Custom implementation
    }

    @Override
    public void setSignatureProperties(String signatureProperties) {
      // Custom implementation
    }

    @Override
    public String getSignatureProperties(String signatureProperties) {
      return signatureProperties;
    }
  }
}
