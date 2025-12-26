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
package ddf.catalog.util.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import org.codice.ddf.platform.util.SortedServiceList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.blueprint.container.ReifiedType;

class ListConverterTest {

  private ListConverter converter;

  @BeforeEach
  void setUp() {
    converter = new ListConverter();
  }

  @Test
  void testCanConvertReturnsTrueForSortedServiceList() {
    SortedServiceList list = new SortedServiceList();
    ReifiedType targetType = mock(ReifiedType.class);

    assertThat(converter.canConvert(list, targetType), is(true));
  }

  @Test
  void testCanConvertReturnsFalseForRegularList() {
    ArrayList<String> list = new ArrayList<>();
    ReifiedType targetType = mock(ReifiedType.class);

    assertThat(converter.canConvert(list, targetType), is(false));
  }

  @Test
  void testCanConvertReturnsFalseForString() {
    ReifiedType targetType = mock(ReifiedType.class);

    assertThat(converter.canConvert("string", targetType), is(false));
  }

  @Test
  void testCanConvertWithNullTargetType() {
    SortedServiceList list = new SortedServiceList();

    assertThat(converter.canConvert(list, null), is(true));
  }

  @Test
  void testConvertReturnsSameInstance() throws Exception {
    SortedServiceList list = new SortedServiceList();
    ReifiedType targetType = mock(ReifiedType.class);

    Object result = converter.convert(list, targetType);

    assertThat(result, is(sameInstance(list)));
  }
}
