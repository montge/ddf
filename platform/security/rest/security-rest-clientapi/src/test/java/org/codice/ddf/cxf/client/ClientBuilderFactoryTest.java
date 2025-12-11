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
package org.codice.ddf.cxf.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientBuilderFactoryTest {

  @Mock private ClientBuilderFactory clientBuilderFactory;

  @Mock private ClientBuilder<TestService> clientBuilder;

  @BeforeEach
  public void setUp() {
    // Setup common mock behavior
    when(clientBuilderFactory.<TestService>getClientBuilder()).thenReturn(clientBuilder);
  }

  @Test
  public void testGetClientBuilder() {
    ClientBuilder<TestService> result = clientBuilderFactory.getClientBuilder();

    assertThat(result, is(notNullValue()));
    assertThat(result, is(sameInstance(clientBuilder)));
    verify(clientBuilderFactory).getClientBuilder();
  }

  @Test
  public void testGetClientBuilderMultipleCalls() {
    ClientBuilder<TestService> result1 = clientBuilderFactory.getClientBuilder();
    ClientBuilder<TestService> result2 = clientBuilderFactory.getClientBuilder();

    assertThat(result1, is(notNullValue()));
    assertThat(result2, is(notNullValue()));
    verify(clientBuilderFactory, times(2)).getClientBuilder();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetClientBuilderForDifferentServiceTypes() {
    ClientBuilder<AnotherTestService> anotherBuilder = mock(ClientBuilder.class);
    when(clientBuilderFactory.<AnotherTestService>getClientBuilder()).thenReturn(anotherBuilder);

    ClientBuilder<AnotherTestService> result = clientBuilderFactory.getClientBuilder();

    assertThat(result, is(notNullValue()));
    verify(clientBuilderFactory).getClientBuilder();
  }

  @Test
  public void testClientBuilderFactoryInterface() {
    // Verify the interface contract exists and can be mocked
    assertThat(clientBuilderFactory, is(notNullValue()));
    assertThat(ClientBuilderFactory.class.isInterface(), is(true));
  }

  @Test
  public void testClientBuilderFactoryHasGetClientBuilderMethod() throws NoSuchMethodException {
    // Verify the interface has the expected method
    assertThat(ClientBuilderFactory.class.getMethod("getClientBuilder"), is(notNullValue()));
  }

  // Test interfaces to use as generic type parameters
  private interface TestService {
    String getData();
  }

  private interface AnotherTestService {
    void processData(String data);
  }
}
