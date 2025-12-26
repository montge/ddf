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
package ddf.catalog.event.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.event.DeliveryMethod;
import java.util.HashSet;
import java.util.Set;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterVisitor;
import org.junit.jupiter.api.Test;

class SubscriptionImplTest {

  @Test
  void testConstructorWithAllParameters() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");
    sourceIds.add("source2");

    SubscriptionImpl subscription = new SubscriptionImpl(filter, deliveryMethod, sourceIds, true);

    assertThat(subscription.getSourceIds(), containsInAnyOrder("source1", "source2"));
    assertThat(subscription.isEnterprise(), is(true));
    assertThat(subscription.getDeliveryMethod(), is(sameInstance(deliveryMethod)));
  }

  @Test
  void testConstructorWithNullSourceIds() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);

    SubscriptionImpl subscription = new SubscriptionImpl(filter, deliveryMethod, null, false);

    assertThat(subscription.getSourceIds(), is(empty()));
    assertThat(subscription.isEnterprise(), is(false));
  }

  @Test
  void testConstructorWithEmptySourceIds() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
    Set<String> emptySourceIds = new HashSet<>();

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, emptySourceIds, false);

    assertThat(subscription.getSourceIds(), is(empty()));
  }

  @Test
  void testGetSourceIdsReturnsUnmodifiableSet() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");

    SubscriptionImpl subscription = new SubscriptionImpl(filter, deliveryMethod, sourceIds, true);

    Set<String> returnedSet = subscription.getSourceIds();

    assertThrows(UnsupportedOperationException.class, () -> returnedSet.add("newSource"));
  }

  @Test
  void testEvaluateDelegatesToFilter() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
    Object testObject = new Object();
    when(filter.evaluate(testObject)).thenReturn(true);

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, new HashSet<>(), false);

    boolean result = subscription.evaluate(testObject);

    assertThat(result, is(true));
    verify(filter).evaluate(testObject);
  }

  @Test
  void testAcceptDelegatesToFilter() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
    FilterVisitor visitor = mock(FilterVisitor.class);
    Object extraData = new Object();
    Object expectedResult = "result";
    when(filter.accept(any(FilterVisitor.class), any())).thenReturn(expectedResult);

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, new HashSet<>(), false);

    Object result = subscription.accept(visitor, extraData);

    assertThat(result, is(expectedResult));
    verify(filter).accept(visitor, extraData);
  }

  @Test
  void testIsEnterpriseTrue() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, new HashSet<>(), true);

    assertThat(subscription.isEnterprise(), is(true));
  }

  @Test
  void testIsEnterpriseFalse() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, new HashSet<>(), false);

    assertThat(subscription.isEnterprise(), is(false));
  }

  @Test
  void testGetDeliveryMethod() {
    Filter filter = mock(Filter.class);
    DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);

    SubscriptionImpl subscription =
        new SubscriptionImpl(filter, deliveryMethod, new HashSet<>(), false);

    assertThat(subscription.getDeliveryMethod(), is(sameInstance(deliveryMethod)));
  }
}
