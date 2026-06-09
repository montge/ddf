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
package org.codice.ddf.security.servlet.whoami;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SubjectOperations;
import ddf.security.assertion.SecurityAssertion;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/** Tests for {@link WhoAmIServlet} class. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class WhoAmIServletTest {

  @Mock private SubjectOperations subjectOperations;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private SecurityManager securityManager;

  @Mock private Subject subject;

  @Mock private PrincipalCollection principals;

  @Mock private SecurityAssertion securityAssertion;

  private WhoAmIServlet servlet;

  private StringWriter responseWriter;

  @BeforeEach
  public void setUp() throws IOException {
    servlet = new WhoAmIServlet(subjectOperations);

    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

    ThreadContext.bind(securityManager);
    ThreadContext.bind(subject);
  }

  @AfterEach
  public void tearDown() {
    ThreadContext.unbindSecurityManager();
    ThreadContext.unbindSubject();
  }

  @Test
  public void testConstructor() {
    WhoAmIServlet servlet = new WhoAmIServlet(subjectOperations);
    assertThat(servlet, is(notNullValue()));
  }

  @Test
  public void testDoGetSetsCacheControlHeader() throws Exception {
    setupValidSubject();

    servlet.doGet(request, response);

    verify(response).setHeader("Cache-Control", "no-cache, no-store");
  }

  @Test
  public void testDoGetSetsPragmaHeader() throws Exception {
    setupValidSubject();

    servlet.doGet(request, response);

    verify(response).setHeader("Pragma", "no-cache");
  }

  @Test
  public void testDoGetSetsContentType() throws Exception {
    setupValidSubject();

    servlet.doGet(request, response);

    verify(response).setContentType("application/json");
  }

  @Test
  public void testDoGetWritesJsonResponse() throws Exception {
    setupValidSubject();

    servlet.doGet(request, response);

    String responseContent = responseWriter.toString();
    assertThat(responseContent, containsString("default"));
    assertThat(responseContent, containsString("whoAmISubjects"));
  }

  @Test
  public void testDoGetWritesJsonWithName() throws Exception {
    setupValidSubject();
    when(subjectOperations.getName(subject)).thenReturn("testuser");

    servlet.doGet(request, response);

    String responseContent = responseWriter.toString();
    assertThat(responseContent, containsString("testuser"));
  }

  @Test
  public void testDoGetWritesJsonWithEmail() throws Exception {
    setupValidSubject();
    when(subjectOperations.getEmailAddress(subject)).thenReturn("test@example.com");

    servlet.doGet(request, response);

    String responseContent = responseWriter.toString();
    assertThat(responseContent, containsString("test@example.com"));
  }

  @Test
  public void testDoGetWritesJsonWithIssuer() throws Exception {
    setupValidSubject();
    when(securityAssertion.getIssuer()).thenReturn("https://idp.example.com");

    servlet.doGet(request, response);

    String responseContent = responseWriter.toString();
    assertThat(responseContent, containsString("https://idp.example.com"));
  }

  @Test
  public void testDoGetHandlesIOException() throws Exception {
    setupValidSubject();
    when(response.getWriter()).thenThrow(new IOException("Test exception"));

    // doGet must catch and log the IOException internally rather than propagate it.
    assertDoesNotThrow(() -> servlet.doGet(request, response));
  }

  @Test
  public void testSerialVersionUID() throws NoSuchFieldException {
    Field field = WhoAmIServlet.class.getDeclaredField("serialVersionUID");
    assertThat(field, is(notNullValue()));
  }

  @Test
  public void testExtendsHttpServlet() {
    assertThat(servlet instanceof javax.servlet.http.HttpServlet, is(true));
  }

  private void setupValidSubject() {
    when(subject.getPrincipals()).thenReturn(principals);
    when(principals.byType(SecurityAssertion.class))
        .thenReturn(Collections.singleton(securityAssertion));
    when(securityAssertion.getAttributeStatements()).thenReturn(Collections.emptyList());
    when(securityAssertion.getAuthnStatements()).thenReturn(Collections.emptyList());
  }
}
