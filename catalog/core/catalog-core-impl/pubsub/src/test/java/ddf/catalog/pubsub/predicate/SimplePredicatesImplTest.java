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
package ddf.catalog.pubsub.predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.osgi.service.event.Event;

class SimplePredicatesImplTest {

  @Test
  void testTruePredicateConstructor() {
    TruePredicate predicate = new TruePredicate();
    assertThat(predicate, is(notNullValue()));
  }

  @Test
  void testTruePredicateImplementsPredicate() {
    TruePredicate predicate = new TruePredicate();
    assertThat(predicate, instanceOf(Predicate.class));
  }

  @Test
  void testTruePredicateMatchesReturnsTrue() {
    TruePredicate predicate = new TruePredicate();
    Event event = mock(Event.class);

    assertThat(predicate.matches(event), is(true));
  }

  @Test
  void testTruePredicateMatchesReturnsTrueWithNullEvent() {
    TruePredicate predicate = new TruePredicate();

    assertThat(predicate.matches(null), is(true));
  }

  @Test
  void testTruePredicateMatchesAlwaysReturnsTrue() {
    TruePredicate predicate = new TruePredicate();
    Event event1 = new Event("topic1", new HashMap<>());
    Event event2 = new Event("topic2", new HashMap<>());

    assertThat(predicate.matches(event1), is(true));
    assertThat(predicate.matches(event2), is(true));
  }

  @Test
  void testFalsePredicateConstructor() {
    FalsePredicate predicate = new FalsePredicate();
    assertThat(predicate, is(notNullValue()));
  }

  @Test
  void testFalsePredicateImplementsPredicate() {
    FalsePredicate predicate = new FalsePredicate();
    assertThat(predicate, instanceOf(Predicate.class));
  }

  @Test
  void testFalsePredicateMatchesReturnsFalse() {
    FalsePredicate predicate = new FalsePredicate();
    Event event = mock(Event.class);

    assertThat(predicate.matches(event), is(false));
  }

  @Test
  void testFalsePredicateMatchesReturnsFalseWithNullEvent() {
    FalsePredicate predicate = new FalsePredicate();

    assertThat(predicate.matches(null), is(false));
  }

  @Test
  void testFalsePredicateMatchesAlwaysReturnsFalse() {
    FalsePredicate predicate = new FalsePredicate();
    Event event1 = new Event("topic1", new HashMap<>());
    Event event2 = new Event("topic2", new HashMap<>());

    assertThat(predicate.matches(event1), is(false));
    assertThat(predicate.matches(event2), is(false));
  }

  @Test
  void testTrueAndFalsePredicatesAreOpposites() {
    TruePredicate truePredicate = new TruePredicate();
    FalsePredicate falsePredicate = new FalsePredicate();
    Event event = mock(Event.class);

    assertThat(truePredicate.matches(event), is(!falsePredicate.matches(event)));
  }

  @Test
  void testMultipleTruePredicateInstances() {
    TruePredicate predicate1 = new TruePredicate();
    TruePredicate predicate2 = new TruePredicate();
    Event event = mock(Event.class);

    assertThat(predicate1.matches(event), is(predicate2.matches(event)));
  }

  @Test
  void testMultipleFalsePredicateInstances() {
    FalsePredicate predicate1 = new FalsePredicate();
    FalsePredicate predicate2 = new FalsePredicate();
    Event event = mock(Event.class);

    assertThat(predicate1.matches(event), is(predicate2.matches(event)));
  }
}
