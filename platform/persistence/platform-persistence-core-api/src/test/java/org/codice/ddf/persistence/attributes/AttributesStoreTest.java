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
package org.codice.ddf.persistence.attributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

public class AttributesStoreTest {

  @Test
  public void testDataUsageKeyConstant() {
    assertThat(AttributesStore.DATA_USAGE_KEY, is("data_usage"));
  }

  @Test
  public void testDataUsageLimitKeyConstant() {
    assertThat(AttributesStore.DATA_USAGE_LIMIT_KEY, is("data_limit"));
  }

  @Test
  public void testUserKeyConstant() {
    assertThat(AttributesStore.USER_KEY, is("user"));
  }

  @Test
  public void testConstantsNotNull() {
    assertThat(AttributesStore.DATA_USAGE_KEY, notNullValue());
    assertThat(AttributesStore.DATA_USAGE_LIMIT_KEY, notNullValue());
    assertThat(AttributesStore.USER_KEY, notNullValue());
  }

  @Test
  public void testConstantsAreDistinct() {
    String dataUsageKey = AttributesStore.DATA_USAGE_KEY;
    String dataLimitKey = AttributesStore.DATA_USAGE_LIMIT_KEY;
    String userKey = AttributesStore.USER_KEY;

    assertThat(dataUsageKey.equals(dataLimitKey), is(false));
    assertThat(dataUsageKey.equals(userKey), is(false));
    assertThat(dataLimitKey.equals(userKey), is(false));
  }
}
