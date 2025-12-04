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
package org.codice.ddf.admin.core.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/** Tests for {@link ConfigurationAdmin} interface constants. */
public class ConfigurationAdminTest {

  @Test
  public void testNoMatchFilterConstant() {
    assertThat(ConfigurationAdmin.NO_MATCH_FILTER, is("(service.pid=0)"));
  }

  @Test
  public void testNoMatchFilterFormat() {
    // Verify the filter is a valid LDAP filter format
    String filter = ConfigurationAdmin.NO_MATCH_FILTER;
    assertThat(filter.startsWith("("), is(true));
    assertThat(filter.endsWith(")"), is(true));
    assertThat(filter.contains("="), is(true));
  }
}
