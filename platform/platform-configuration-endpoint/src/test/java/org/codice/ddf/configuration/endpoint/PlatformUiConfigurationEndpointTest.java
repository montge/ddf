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
package org.codice.ddf.configuration.endpoint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codice.ddf.configuration.service.PlatformUiConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link PlatformUiConfigurationEndpoint} class. */
@ExtendWith(MockitoExtension.class)
public class PlatformUiConfigurationEndpointTest {

  private static final String SAMPLE_JSON = "{\"config\":\"value\"}";

  @Mock private PlatformUiConfigurationService configurationService;

  private PlatformUiConfigurationEndpoint endpoint;

  @BeforeEach
  public void setUp() {
    endpoint = new PlatformUiConfigurationEndpoint(configurationService);
  }

  @Test
  public void testEndpointIsNotNull() {
    assertThat(endpoint, is(notNullValue()));
  }

  @Test
  public void testGetConfigDelegatesToService() {
    when(configurationService.getConfigAsJsonString()).thenReturn(SAMPLE_JSON);

    endpoint.getConfig();

    verify(configurationService).getConfigAsJsonString();
  }

  @Test
  public void testGetConfigReturnsServiceResponse() {
    when(configurationService.getConfigAsJsonString()).thenReturn(SAMPLE_JSON);

    String result = endpoint.getConfig();

    assertThat(result, is(SAMPLE_JSON));
  }

  @Test
  public void testGetConfigReturnsEmptyStringWhenServiceReturnsEmpty() {
    when(configurationService.getConfigAsJsonString()).thenReturn("");

    String result = endpoint.getConfig();

    assertThat(result, is(""));
  }

  @Test
  public void testGetConfigReturnsNullWhenServiceReturnsNull() {
    when(configurationService.getConfigAsJsonString()).thenReturn(null);

    String result = endpoint.getConfig();

    assertThat(result, is((String) null));
  }
}
