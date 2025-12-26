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
package ddf.catalog.history;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.configuration.HistorianConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistorianConfigurationImplTest {

  private HistorianConfigurationImpl historianConfiguration;
  private Historian historian;

  @BeforeEach
  void setUp() {
    historianConfiguration = new HistorianConfigurationImpl();
    historian = mock(Historian.class);
    historianConfiguration.setHistorian(historian);
  }

  @Test
  void testImplementsHistorianConfiguration() {
    assertThat(historianConfiguration, instanceOf(HistorianConfiguration.class));
  }

  @Test
  void testIsHistoryEnabledReturnsTrue() {
    when(historian.isHistoryEnabled()).thenReturn(true);

    boolean result = historianConfiguration.isHistoryEnabled();

    assertThat(result, is(true));
    verify(historian).isHistoryEnabled();
  }

  @Test
  void testIsHistoryEnabledReturnsFalse() {
    when(historian.isHistoryEnabled()).thenReturn(false);

    boolean result = historianConfiguration.isHistoryEnabled();

    assertThat(result, is(false));
    verify(historian).isHistoryEnabled();
  }

  @Test
  void testDelegatesToHistorian() {
    when(historian.isHistoryEnabled()).thenReturn(true);

    historianConfiguration.isHistoryEnabled();

    verify(historian).isHistoryEnabled();
  }
}
