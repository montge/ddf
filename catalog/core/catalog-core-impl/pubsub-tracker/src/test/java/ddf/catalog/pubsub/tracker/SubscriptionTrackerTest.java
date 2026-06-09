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
package ddf.catalog.pubsub.tracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.catalog.event.EventProcessor;
import ddf.catalog.event.InvalidSubscriptionException;
import ddf.catalog.event.Subscription;
import ddf.catalog.event.SubscriptionNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionTrackerTest {

  private static final String SERVICE_ID = "service.id";
  private static final String TEST_SERVICE_ID = "test-service-123";
  private static final String TEST_SUBSCRIPTION_ID = "subscription-456";

  @Mock private EventProcessor eventProcessor;

  @Mock private Subscription subscription;

  private SubscriptionTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new SubscriptionTracker();
  }

  @Test
  void testConstructor() {
    assertThat(tracker, is(notNullValue()));
    assertThat(tracker.services, is(notNullValue()));
    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testStartUp() {
    assertDoesNotThrow(() -> tracker.startUp());
    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testCleanUp() {
    assertDoesNotThrow(() -> tracker.cleanUp());
    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testSetProvider() {
    tracker.setProvider(eventProcessor);
    assertThat(tracker.provider, is(sameInstance(eventProcessor)));
  }

  @Test
  void testAddingServiceWithNullProvider() {
    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.addingService(subscription, props);

    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testAddingServiceCreatesSubscription() throws Exception {
    tracker.setProvider(eventProcessor);
    when(eventProcessor.createSubscription(subscription)).thenReturn(TEST_SUBSCRIPTION_ID);

    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.addingService(subscription, props);

    verify(eventProcessor).createSubscription(subscription);
    assertThat(tracker.services.get(TEST_SERVICE_ID), is(TEST_SUBSCRIPTION_ID));
  }

  @Test
  void testAddingServiceHandlesEventException() throws Exception {
    tracker.setProvider(eventProcessor);
    when(eventProcessor.createSubscription(any()))
        .thenThrow(new InvalidSubscriptionException("error"));

    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.addingService(subscription, props);

    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testRemovedServiceWithNullProps() {
    tracker.setProvider(eventProcessor);

    tracker.removedService(subscription, null);

    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testRemovedServiceWithNullServiceId() {
    tracker.setProvider(eventProcessor);
    Map<String, Object> props = new HashMap<>();

    tracker.removedService(subscription, props);

    assertThat(tracker.services.isEmpty(), is(true));
  }

  @Test
  void testRemovedServiceWithNullProvider() {
    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);
    tracker.services.put(TEST_SERVICE_ID, TEST_SUBSCRIPTION_ID);

    tracker.removedService(subscription, props);

    assertThat(tracker.services.containsKey(TEST_SERVICE_ID), is(true));
  }

  @Test
  void testRemovedServiceDeletesSubscription() throws Exception {
    tracker.setProvider(eventProcessor);
    tracker.services.put(TEST_SERVICE_ID, TEST_SUBSCRIPTION_ID);

    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.removedService(subscription, props);

    verify(eventProcessor).deleteSubscription(TEST_SUBSCRIPTION_ID);
    assertThat(tracker.services.containsKey(TEST_SERVICE_ID), is(false));
  }

  @Test
  void testRemovedServiceHandlesEventException() throws Exception {
    tracker.setProvider(eventProcessor);
    tracker.services.put(TEST_SERVICE_ID, TEST_SUBSCRIPTION_ID);
    doThrow(new SubscriptionNotFoundException("error"))
        .when(eventProcessor)
        .deleteSubscription(any());

    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.removedService(subscription, props);

    verify(eventProcessor).deleteSubscription(TEST_SUBSCRIPTION_ID);
    assertThat(tracker.services.containsKey(TEST_SERVICE_ID), is(true));
  }

  @Test
  void testAddingThenRemovingService() throws Exception {
    tracker.setProvider(eventProcessor);
    when(eventProcessor.createSubscription(subscription)).thenReturn(TEST_SUBSCRIPTION_ID);

    Map<String, Object> props = new HashMap<>();
    props.put(SERVICE_ID, TEST_SERVICE_ID);

    tracker.addingService(subscription, props);
    assertThat(tracker.services.size(), is(1));

    tracker.removedService(subscription, props);
    assertThat(tracker.services.isEmpty(), is(true));
    verify(eventProcessor).deleteSubscription(TEST_SUBSCRIPTION_ID);
  }

  @Test
  void testMultipleSubscriptions() throws Exception {
    tracker.setProvider(eventProcessor);

    String serviceId1 = "service-1";
    String serviceId2 = "service-2";
    String subscriptionId1 = "sub-1";
    String subscriptionId2 = "sub-2";

    Subscription sub1 = mock(Subscription.class);
    Subscription sub2 = mock(Subscription.class);

    when(eventProcessor.createSubscription(sub1)).thenReturn(subscriptionId1);
    when(eventProcessor.createSubscription(sub2)).thenReturn(subscriptionId2);

    Map<String, Object> props1 = new HashMap<>();
    props1.put(SERVICE_ID, serviceId1);

    Map<String, Object> props2 = new HashMap<>();
    props2.put(SERVICE_ID, serviceId2);

    tracker.addingService(sub1, props1);
    tracker.addingService(sub2, props2);

    assertThat(tracker.services.size(), is(2));
    assertThat(tracker.services.get(serviceId1), is(subscriptionId1));
    assertThat(tracker.services.get(serviceId2), is(subscriptionId2));

    tracker.removedService(sub1, props1);
    assertThat(tracker.services.size(), is(1));
    assertThat(tracker.services.containsKey(serviceId1), is(false));
    assertThat(tracker.services.containsKey(serviceId2), is(true));
  }
}
