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
package ddf.catalog.pubsub;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import ddf.catalog.data.impl.MetacardImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventProcessorImplTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessorImplTest.class);

  @BeforeAll
  public static void setUpBeforeClass() throws Exception {}

  @AfterAll
  public static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  public void setUp() throws Exception {}

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void testNullMetacard() {
    EventAdmin eventAdmin = new MockEventAdmin();
    try {
      EventProcessorImpl.processEntry(null, "Operation", eventAdmin);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testNullEventAdmin() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setContentTypeName("Nitf");
    metacard.setContentTypeVersion("2.0");
    metacard.setMetadata("<xml/>");
    try {
      EventProcessorImpl.processEntry(metacard, "Operation", null);
    } catch (Exception e) {
      LOGGER.error("Unexpected exception.", e);
      fail();
    }
  }

  @Test
  public void testNullOperation() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setContentTypeName("Nitf");
    metacard.setContentTypeVersion("2.0");
    metacard.setMetadata("<xml/>");
    EventAdmin eventAdmin = new MockEventAdmin();

    try {
      EventProcessorImpl.processEntry(metacard, null, eventAdmin);
    } catch (Exception e) {
      LOGGER.error("Unexpected exception.", e);
      fail();
    }
  }

  @Test
  public void testDateType() throws Exception {
    for (EventProcessorImpl.DateType dt : EventProcessorImpl.DateType.values()) {
      // for each DateType, verify enum is the same if derived from attribute string or enum name
      assertThat(
          EventProcessorImpl.DateType.valueOf(dt.name()),
          equalTo(EventProcessorImpl.DateType.getDateType(dt.getAttributeName())));
    }
  }

  @Test
  public void testDateTypeNullAttr() {
    assertThrows(NullPointerException.class, () -> EventProcessorImpl.DateType.getDateType(null));
  }

  @Test
  public void testDateTypeInvalidAttr() {
    assertThrows(
        IllegalArgumentException.class,
        () -> EventProcessorImpl.DateType.getDateType("some obviously invalid attribute."));
  }
}
