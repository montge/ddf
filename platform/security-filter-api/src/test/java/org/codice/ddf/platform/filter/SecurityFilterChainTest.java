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
public class SecurityFilterChainTest {

  @Test
  public void testInterfaceCanBeImplemented() {
    SecurityFilterChain filterChain = new TestSecurityFilterChain();

    assertThat(filterChain, is(notNullValue()));
  }

  @Test
  public void testDoFilterMethodExists() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    TestSecurityFilterChain filterChain = new TestSecurityFilterChain();

    filterChain.doFilter(request, response);

    assertThat(filterChain.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testDoFilterCanThrowIOException() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain filterChain = mock(SecurityFilterChain.class);

    doThrow(new IOException("IO error")).when(filterChain).doFilter(any(), any());

    try {
      filterChain.doFilter(request, response);
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IO error"));
    }
  }

  @Test
  public void testDoFilterCanThrowAuthenticationException()
      throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);
    SecurityFilterChain filterChain = mock(SecurityFilterChain.class);

    doThrow(new AuthenticationException("Auth error")).when(filterChain).doFilter(any(), any());

    try {
      filterChain.doFilter(request, response);
    } catch (AuthenticationException e) {
      assertThat(e.getMessage(), is("Auth error"));
    }
  }

  @Test
  public void testDoFilterAcceptsNullRequest() throws IOException, AuthenticationException {
    ServletResponse response = mock(ServletResponse.class);
    TestSecurityFilterChain filterChain = new TestSecurityFilterChain();

    filterChain.doFilter(null, response);

    assertThat(filterChain.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testDoFilterAcceptsNullResponse() throws IOException, AuthenticationException {
    ServletRequest request = mock(ServletRequest.class);
    TestSecurityFilterChain filterChain = new TestSecurityFilterChain();

    filterChain.doFilter(request, null);

    assertThat(filterChain.wasDoFilterCalled(), is(true));
  }

  @Test
  public void testMultipleImplementationsCanExist() {
    SecurityFilterChain chain1 = new TestSecurityFilterChain();
    SecurityFilterChain chain2 = new AnotherSecurityFilterChain();

    assertThat(chain1, is(notNullValue()));
    assertThat(chain2, is(notNullValue()));
  }

  @Test
  public void testDoFilterCanBeMocked() throws IOException, AuthenticationException {
    SecurityFilterChain filterChain = mock(SecurityFilterChain.class);
    ServletRequest request = mock(ServletRequest.class);
    ServletResponse response = mock(ServletResponse.class);

    filterChain.doFilter(request, response);

    verify(filterChain).doFilter(request, response);
  }

  // Test implementation classes

  private static class TestSecurityFilterChain implements SecurityFilterChain {
    private boolean doFilterCalled = false;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, AuthenticationException {
      doFilterCalled = true;
    }

    public boolean wasDoFilterCalled() {
      return doFilterCalled;
    }
  }

  private static class AnotherSecurityFilterChain implements SecurityFilterChain {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
        throws IOException, AuthenticationException {
      // Different implementation
    }
  }
}
