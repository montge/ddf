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
package org.codice.ddf.platform.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityFilterTest {

  @Test
  public void testInterfaceCanBeImplemented() {
    SecurityFilter filter = new TestSecurityFilter();

    assertThat(filter, is(notNullValue()));
  }

  @Test
  public void testInitMethodExists() {
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.init();

    assertThat(filter.wasInitCalled(), is(true));
  }

  @Test
  public void testDoFilterMethodExists() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.doFilter(request, response, chain);

    assertThat(filter.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testDestroyMethodExists() {
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.destroy();

    assertThat(filter.wasDestroyCalled(), is(true));
  }

  @Test
  public void testLifecycleMethods() throws IOException, AuthenticationException {
    TestSecurityFilter filter = new TestSecurityFilter();
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);

    filter.init();
    filter.doFilter(request, response, chain);
    filter.destroy();

    assertThat(filter.wasInitCalled(), is(true));
    assertThat(filter.wasDoFilterCalled(), is(true));
    assertThat(filter.wasDestroyCalled(), is(true));
  }

  @Test
  public void testDoFilterCanThrowIOException() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    SecurityFilter filter = mock(SecurityFilter.class);

    doThrow(new IOException("IO error")).when(filter).doFilter(any(), any(), any());

    try {
      filter.doFilter(request, response, chain);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IO error"));
    }
  }

  @Test
  public void testDoFilterCanThrowAuthenticationException()
      throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    SecurityFilter filter = mock(SecurityFilter.class);

    doThrow(new AuthenticationException("Auth error")).when(filter).doFilter(any(), any(), any());

    try {
      filter.doFilter(request, response, chain);
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(), is("Auth error"));
    }
  }

  @Test
  public void testDoFilterAcceptsNullRequest() throws IOException, AuthenticationException {
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.doFilter(null, response, chain);

    assertThat(filter.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testDoFilterAcceptsNullResponse() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.doFilter(request, null, chain);

    assertThat(filter.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testDoFilterAcceptsNullChain() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    TestSecurityFilter filter = new TestSecurityFilter();

    filter.doFilter(request, response, null);

    assertThat(filter.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testMultipleImplementationsCanExist() {
    SecurityFilter filter1 = new TestSecurityFilter();
    SecurityFilter filter2 = new AnotherSecurityFilter();

    assertThat(filter1, is(notNullValue()));
    assertThat(filter2, is(notNullValue()));
  }

  @Test
  public void testFilterCanBeMocked() throws IOException, AuthenticationException {
    SecurityFilter filter = mock(SecurityFilter.class);
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);

    filter.init();
    filter.doFilter(request, response, chain);
    filter.destroy();

    verify(filter).init();
    verify(filter).doFilter(request, response, chain);
    verify(filter).destroy();
  }

  @Test
  public void testFilterChainCanBeInvoked() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain chain = mock(SecurityFilterChain.class);
    TestSecurityFilter filter = new TestSecurityFilter();
    filter.setShouldCallChain(true);

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  // Test implementation classes

  private static class TestSecurityFilter implements SecurityFilter {
    private boolean initCalled = false;
    private boolean doFilterCalled = false;
    private boolean destroyCalled = false;
    private boolean shouldCallChain = false;

    @Override
    public void init() {
      initCalled = true;
    }

    @Override
    public void doFilter(
        ServletRequest request, ServletResponse response, SecurityFilterChain chain)
        throws IOException, AuthenticationException {
      doFilterCalled = true;
      if (shouldCallChain && chain != null) {
        chain.doFilter(request, response);
      }
    }

    @Override
    public void destroy() {
      destroyCalled = true;
    }

    public boolean wasInitCalled() {
      return initCalled;
    }

    public boolean wasDoFilterCalled() {
      return doFilterCalled;
    }

    public boolean wasDestroyCalled() {
      return destroyCalled;
    }

    public void setShouldCallChain(boolean shouldCallChain) {
      this.shouldCallChain = shouldCallChain;
    }
  }

  private static class AnotherSecurityFilter implements SecurityFilter {
    @Override
    public void init() {
      // Different implementation
    }

    @Override
    public void doFilter(
        ServletRequest request, ServletResponse response, SecurityFilterChain chain)
        throws IOException, AuthenticationException {
      // Different implementation
    }

    @Override
    public void destroy() {
      // Different implementation
    }
  }
}
