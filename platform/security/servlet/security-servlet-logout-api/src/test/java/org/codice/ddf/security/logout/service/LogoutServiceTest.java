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
package org.codice.ddf.security.logout.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import ddf.security.service.SecurityServiceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link LogoutService} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {

  private static final String SAML_LOGOUT_ACTION =
      "{\"type\":\"SAML\",\"url\":\"https://idp/logout\"}";
  private static final String LOCAL_LOGOUT_ACTION =
      "{\"type\":\"LOCAL\",\"url\":\"/logout/local\"}";

  @Mock private HttpServletRequest mockRequest;

  @Mock private HttpServletResponse mockResponse;

  private TestLogoutService logoutService;

  @BeforeEach
  public void setUp() {
    logoutService = new TestLogoutService();
  }

  @Test
  public void testGetActionProvidersReturnsJsonArray() throws SecurityServiceException {
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, is(notNullValue()));
    assertThat(result.startsWith("["), is(true));
    assertThat(result.endsWith("]"), is(true));
  }

  @Test
  public void testGetActionProvidersContainsSamlAction() throws SecurityServiceException {
    logoutService.addActionProvider("SAML", "https://idp/logout");
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, containsString("SAML"));
  }

  @Test
  public void testGetActionProvidersContainsLocalAction() throws SecurityServiceException {
    logoutService.addActionProvider("LOCAL", "/logout/local");
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, containsString("LOCAL"));
  }

  @Test
  public void testGetActionProvidersEmptyWhenNoProviders() throws SecurityServiceException {
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, is("[]"));
  }

  @Test
  public void testGetActionProvidersWithMultipleProviders() throws SecurityServiceException {
    logoutService.addActionProvider("SAML", "https://idp/logout");
    logoutService.addActionProvider("LOCAL", "/logout/local");
    logoutService.addActionProvider("OIDC", "https://oidc/logout");
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, containsString("SAML"));
    assertThat(result, containsString("LOCAL"));
    assertThat(result, containsString("OIDC"));
  }

  @Test
  public void testGetActionProvidersWithNullRequest() throws SecurityServiceException {
    logoutService.addActionProvider("LOCAL", "/logout/local");
    String result = logoutService.getActionProviders(null, mockResponse);
    // Should still return providers even with null request
    assertThat(result, containsString("LOCAL"));
  }

  @Test
  public void testGetActionProvidersThrowsExceptionOnError() {
    assertThrows(
        SecurityServiceException.class,
        () -> {
          logoutService.setThrowException(true);
          logoutService.getActionProviders(mockRequest, mockResponse);
        });
  }

  @Test
  public void testGetActionProvidersWithDifferentRequestContexts() throws SecurityServiceException {
    HttpServletRequest adminRequest = mock(HttpServletRequest.class);
    HttpServletResponse adminResponse = mock(HttpServletResponse.class);

    logoutService.addActionProvider("SAML", "https://idp/logout");
    String result = logoutService.getActionProviders(adminRequest, adminResponse);
    assertThat(result, is(notNullValue()));
  }

  @Test
  public void testGetActionProvidersContainsUrlField() throws SecurityServiceException {
    logoutService.addActionProvider("SAML", "https://idp/logout");
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, containsString("url"));
  }

  @Test
  public void testGetActionProvidersContainsTypeField() throws SecurityServiceException {
    logoutService.addActionProvider("SAML", "https://idp/logout");
    String result = logoutService.getActionProviders(mockRequest, mockResponse);
    assertThat(result, containsString("type"));
  }

  /** Simple test implementation of LogoutService interface. */
  private static class TestLogoutService implements LogoutService {
    private final java.util.List<ActionProvider> actionProviders = new java.util.ArrayList<>();
    private boolean throwException = false;

    public void addActionProvider(String type, String url) {
      actionProviders.add(new ActionProvider(type, url));
    }

    public void setThrowException(boolean throwException) {
      this.throwException = throwException;
    }

    @Override
    public String getActionProviders(HttpServletRequest request, HttpServletResponse response)
        throws SecurityServiceException {
      if (throwException) {
        throw new SecurityServiceException("Test exception");
      }
      if (actionProviders.isEmpty()) {
        return "[]";
      }
      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < actionProviders.size(); i++) {
        if (i > 0) {
          sb.append(",");
        }
        ActionProvider provider = actionProviders.get(i);
        sb.append("{\"type\":\"")
            .append(provider.type)
            .append("\",\"url\":\"")
            .append(provider.url)
            .append("\"}");
      }
      sb.append("]");
      return sb.toString();
    }

    private static class ActionProvider {
      final String type;
      final String url;

      ActionProvider(String type, String url) {
        this.type = type;
        this.url = url;
      }
    }
  }
}
