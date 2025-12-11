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

import org.junit.jupiter.api.Test;

public class LogoutWrapperImplTest {

  @Test
  public void testConstructorWithString() {
    String message = "test logout message";
    LogoutWrapperImpl<String> wrapper = new LogoutWrapperImpl<>(message);

    assertThat(wrapper, is(notNullValue()));
  }

  @Test
  public void testGetMessageWithString() {
    String message = "test logout message";
    LogoutWrapperImpl<String> wrapper = new LogoutWrapperImpl<>(message);

    String result = wrapper.getMessage();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(message));
  }

  @Test
  public void testGetMessageWithObject() {
    TestLogoutRequest request = new TestLogoutRequest("requestId123");
    LogoutWrapperImpl<TestLogoutRequest> wrapper = new LogoutWrapperImpl<>(request);

    TestLogoutRequest result = wrapper.getMessage();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(request));
    assertThat(result.getId(), is("requestId123"));
  }

  @Test
  public void testConstructorWithNull() {
    LogoutWrapperImpl<String> wrapper = new LogoutWrapperImpl<>(null);

    assertThat(wrapper, is(notNullValue()));
    assertThat(wrapper.getMessage(), is((String) null));
  }

  @Test
  public void testGenericTypeInteger() {
    Integer value = 42;
    LogoutWrapperImpl<Integer> wrapper = new LogoutWrapperImpl<>(value);

    Integer result = wrapper.getMessage();
    assertThat(result, is(notNullValue()));
    assertThat(result, is(42));
  }

  @Test
  public void testMultipleInstances() {
    LogoutWrapperImpl<String> wrapper1 = new LogoutWrapperImpl<>("message1");
    LogoutWrapperImpl<String> wrapper2 = new LogoutWrapperImpl<>("message2");

    assertThat(wrapper1.getMessage(), is("message1"));
    assertThat(wrapper2.getMessage(), is("message2"));
  }

  private static class TestLogoutRequest {
    private final String id;

    public TestLogoutRequest(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
  }
}
