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
package org.codice.ddf.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.codice.ddf.persistence.PersistentStore.PersistenceType;
import org.junit.jupiter.api.Test;

public class PersistentStoreTest {

  @Test
  public void testPersistenceTypeMetacard() {
    assertThat(PersistenceType.METACARD_TYPE.toString(), is("metacard"));
  }

  @Test
  public void testPersistenceTypeSavedQuery() {
    assertThat(PersistenceType.SAVED_QUERY_TYPE.toString(), is("saved_query"));
  }

  @Test
  public void testPersistenceTypeNotification() {
    assertThat(PersistenceType.NOTIFICATION_TYPE.toString(), is("notification"));
  }

  @Test
  public void testPersistenceTypeActivity() {
    assertThat(PersistenceType.ACTIVITY_TYPE.toString(), is("activity"));
  }

  @Test
  public void testPersistenceTypeWorkspace() {
    assertThat(PersistenceType.WORKSPACE_TYPE.toString(), is("workspace"));
  }

  @Test
  public void testPersistenceTypePreferences() {
    assertThat(PersistenceType.PREFERENCES_TYPE.toString(), is("preferences"));
  }

  @Test
  public void testPersistenceTypeUserAttribute() {
    assertThat(PersistenceType.USER_ATTRIBUTE_TYPE.toString(), is("attributes"));
  }

  @Test
  public void testPersistenceTypeSubscription() {
    assertThat(PersistenceType.SUBSCRIPTION_TYPE.toString(), is("subscriptions"));
  }

  @Test
  public void testPersistenceTypeEventSubscriptions() {
    assertThat(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE.toString(), is("event_subscriptions"));
  }

  @Test
  public void testPersistenceTypeAlert() {
    assertThat(PersistenceType.ALERT_TYPE.toString(), is("alerts"));
  }

  @Test
  public void testPersistenceTypeDecanter() {
    assertThat(PersistenceType.DECANTER_TYPE.toString(), is("decanter"));
  }

  @Test
  public void testPersistenceTypeResults() {
    assertThat(PersistenceType.RESULTS_TYPE.toString(), is("result_cache"));
  }

  @Test
  public void testPersistenceTypeValues() {
    PersistenceType[] types = PersistenceType.values();
    assertNotNull(types);
    assertThat(types.length, is(12));
  }

  @Test
  public void testPersistenceTypeValuesContainsAllTypes() {
    PersistenceType[] types = PersistenceType.values();

    boolean hasMetacard = false;
    boolean hasSavedQuery = false;
    boolean hasNotification = false;
    boolean hasActivity = false;
    boolean hasWorkspace = false;
    boolean hasPreferences = false;
    boolean hasUserAttribute = false;
    boolean hasSubscription = false;
    boolean hasEventSubscriptions = false;
    boolean hasAlert = false;
    boolean hasDecanter = false;
    boolean hasResults = false;

    for (PersistenceType type : types) {
      if (type == PersistenceType.METACARD_TYPE) hasMetacard = true;
      if (type == PersistenceType.SAVED_QUERY_TYPE) hasSavedQuery = true;
      if (type == PersistenceType.NOTIFICATION_TYPE) hasNotification = true;
      if (type == PersistenceType.ACTIVITY_TYPE) hasActivity = true;
      if (type == PersistenceType.WORKSPACE_TYPE) hasWorkspace = true;
      if (type == PersistenceType.PREFERENCES_TYPE) hasPreferences = true;
      if (type == PersistenceType.USER_ATTRIBUTE_TYPE) hasUserAttribute = true;
      if (type == PersistenceType.SUBSCRIPTION_TYPE) hasSubscription = true;
      if (type == PersistenceType.EVENT_SUBSCRIPTIONS_TYPE) hasEventSubscriptions = true;
      if (type == PersistenceType.ALERT_TYPE) hasAlert = true;
      if (type == PersistenceType.DECANTER_TYPE) hasDecanter = true;
      if (type == PersistenceType.RESULTS_TYPE) hasResults = true;
    }

    assertTrue(hasMetacard);
    assertTrue(hasSavedQuery);
    assertTrue(hasNotification);
    assertTrue(hasActivity);
    assertTrue(hasWorkspace);
    assertTrue(hasPreferences);
    assertTrue(hasUserAttribute);
    assertTrue(hasSubscription);
    assertTrue(hasEventSubscriptions);
    assertTrue(hasAlert);
    assertTrue(hasDecanter);
    assertTrue(hasResults);
  }

  @Test
  public void testPersistenceTypeValueOf() {
    assertThat(PersistenceType.valueOf("METACARD_TYPE"), is(PersistenceType.METACARD_TYPE));
    assertThat(PersistenceType.valueOf("SAVED_QUERY_TYPE"), is(PersistenceType.SAVED_QUERY_TYPE));
    assertThat(PersistenceType.valueOf("NOTIFICATION_TYPE"), is(PersistenceType.NOTIFICATION_TYPE));
    assertThat(PersistenceType.valueOf("ACTIVITY_TYPE"), is(PersistenceType.ACTIVITY_TYPE));
    assertThat(PersistenceType.valueOf("WORKSPACE_TYPE"), is(PersistenceType.WORKSPACE_TYPE));
    assertThat(PersistenceType.valueOf("PREFERENCES_TYPE"), is(PersistenceType.PREFERENCES_TYPE));
    assertThat(
        PersistenceType.valueOf("USER_ATTRIBUTE_TYPE"), is(PersistenceType.USER_ATTRIBUTE_TYPE));
    assertThat(PersistenceType.valueOf("SUBSCRIPTION_TYPE"), is(PersistenceType.SUBSCRIPTION_TYPE));
    assertThat(
        PersistenceType.valueOf("EVENT_SUBSCRIPTIONS_TYPE"),
        is(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE));
    assertThat(PersistenceType.valueOf("ALERT_TYPE"), is(PersistenceType.ALERT_TYPE));
    assertThat(PersistenceType.valueOf("DECANTER_TYPE"), is(PersistenceType.DECANTER_TYPE));
    assertThat(PersistenceType.valueOf("RESULTS_TYPE"), is(PersistenceType.RESULTS_TYPE));
  }

  @Test
  public void testPersistenceTypeValueOfInvalidName() {
    assertThrows(IllegalArgumentException.class, () -> PersistenceType.valueOf("INVALID_TYPE"));
  }

  @Test
  public void testPersistenceTypeValueOfNull() {
    assertThrows(NullPointerException.class, () -> PersistenceType.valueOf(null));
  }

  @Test
  public void testPersistenceTypeEnumEquality() {
    PersistenceType type1 = PersistenceType.METACARD_TYPE;
    PersistenceType type2 = PersistenceType.valueOf("METACARD_TYPE");
    assertThat(type1, is(type2));
    assertEquals(type1, type2);
  }

  @Test
  public void testPersistenceTypeEnumInequality() {
    PersistenceType type1 = PersistenceType.METACARD_TYPE;
    PersistenceType type2 = PersistenceType.WORKSPACE_TYPE;
    assertTrue(type1 != type2);
  }

  @Test
  public void testPersistenceTypeCanBeUsedInSwitch() {
    PersistenceType type = PersistenceType.SAVED_QUERY_TYPE;
    String result = null;

    switch (type) {
      case METACARD_TYPE:
        result = "metacard";
        break;
      case SAVED_QUERY_TYPE:
        result = "saved_query";
        break;
      case WORKSPACE_TYPE:
        result = "workspace";
        break;
      default:
        result = "other";
    }

    assertThat(result, is("saved_query"));
  }

  @Test
  public void testPersistenceTypeToStringIsNotEnumName() {
    // Verify that toString() returns the custom string, not the enum constant name
    assertThat(
        PersistenceType.USER_ATTRIBUTE_TYPE.toString(),
        is("attributes")); // not "USER_ATTRIBUTE_TYPE"
    assertThat(
        PersistenceType.EVENT_SUBSCRIPTIONS_TYPE.toString(),
        is("event_subscriptions")); // not "EVENT_SUBSCRIPTIONS_TYPE"
  }

  @Test
  public void testAllPersistenceTypesToString() {
    // Comprehensive test of all toString() values
    assertThat(PersistenceType.METACARD_TYPE.toString(), is("metacard"));
    assertThat(PersistenceType.SAVED_QUERY_TYPE.toString(), is("saved_query"));
    assertThat(PersistenceType.NOTIFICATION_TYPE.toString(), is("notification"));
    assertThat(PersistenceType.ACTIVITY_TYPE.toString(), is("activity"));
    assertThat(PersistenceType.WORKSPACE_TYPE.toString(), is("workspace"));
    assertThat(PersistenceType.PREFERENCES_TYPE.toString(), is("preferences"));
    assertThat(PersistenceType.USER_ATTRIBUTE_TYPE.toString(), is("attributes"));
    assertThat(PersistenceType.SUBSCRIPTION_TYPE.toString(), is("subscriptions"));
    assertThat(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE.toString(), is("event_subscriptions"));
    assertThat(PersistenceType.ALERT_TYPE.toString(), is("alerts"));
    assertThat(PersistenceType.DECANTER_TYPE.toString(), is("decanter"));
    assertThat(PersistenceType.RESULTS_TYPE.toString(), is("result_cache"));
  }

  @Test
  public void testPersistenceTypeOrdinals() {
    // Verify ordinal values are set correctly
    assertThat(PersistenceType.METACARD_TYPE.ordinal(), is(0));
    assertThat(PersistenceType.SAVED_QUERY_TYPE.ordinal(), is(1));
    assertThat(PersistenceType.NOTIFICATION_TYPE.ordinal(), is(2));
    assertThat(PersistenceType.ACTIVITY_TYPE.ordinal(), is(3));
    assertThat(PersistenceType.WORKSPACE_TYPE.ordinal(), is(4));
    assertThat(PersistenceType.PREFERENCES_TYPE.ordinal(), is(5));
    assertThat(PersistenceType.USER_ATTRIBUTE_TYPE.ordinal(), is(6));
    assertThat(PersistenceType.SUBSCRIPTION_TYPE.ordinal(), is(7));
    assertThat(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE.ordinal(), is(8));
    assertThat(PersistenceType.ALERT_TYPE.ordinal(), is(9));
    assertThat(PersistenceType.DECANTER_TYPE.ordinal(), is(10));
    assertThat(PersistenceType.RESULTS_TYPE.ordinal(), is(11));
  }

  @Test
  public void testPersistenceTypeNames() {
    // Verify name() method returns enum constant names
    assertThat(PersistenceType.METACARD_TYPE.name(), is("METACARD_TYPE"));
    assertThat(PersistenceType.SAVED_QUERY_TYPE.name(), is("SAVED_QUERY_TYPE"));
    assertThat(PersistenceType.NOTIFICATION_TYPE.name(), is("NOTIFICATION_TYPE"));
    assertThat(PersistenceType.ACTIVITY_TYPE.name(), is("ACTIVITY_TYPE"));
    assertThat(PersistenceType.WORKSPACE_TYPE.name(), is("WORKSPACE_TYPE"));
    assertThat(PersistenceType.PREFERENCES_TYPE.name(), is("PREFERENCES_TYPE"));
    assertThat(PersistenceType.USER_ATTRIBUTE_TYPE.name(), is("USER_ATTRIBUTE_TYPE"));
    assertThat(PersistenceType.SUBSCRIPTION_TYPE.name(), is("SUBSCRIPTION_TYPE"));
    assertThat(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE.name(), is("EVENT_SUBSCRIPTIONS_TYPE"));
    assertThat(PersistenceType.ALERT_TYPE.name(), is("ALERT_TYPE"));
    assertThat(PersistenceType.DECANTER_TYPE.name(), is("DECANTER_TYPE"));
    assertThat(PersistenceType.RESULTS_TYPE.name(), is("RESULTS_TYPE"));
  }

  @Test
  public void testPersistenceTypeIsEnum() {
    assertTrue(PersistenceType.class.isEnum());
  }

  @Test
  public void testPersistenceTypeConstantsNotNull() {
    assertNotNull(PersistenceType.METACARD_TYPE);
    assertNotNull(PersistenceType.SAVED_QUERY_TYPE);
    assertNotNull(PersistenceType.NOTIFICATION_TYPE);
    assertNotNull(PersistenceType.ACTIVITY_TYPE);
    assertNotNull(PersistenceType.WORKSPACE_TYPE);
    assertNotNull(PersistenceType.PREFERENCES_TYPE);
    assertNotNull(PersistenceType.USER_ATTRIBUTE_TYPE);
    assertNotNull(PersistenceType.SUBSCRIPTION_TYPE);
    assertNotNull(PersistenceType.EVENT_SUBSCRIPTIONS_TYPE);
    assertNotNull(PersistenceType.ALERT_TYPE);
    assertNotNull(PersistenceType.DECANTER_TYPE);
    assertNotNull(PersistenceType.RESULTS_TYPE);
  }
}
