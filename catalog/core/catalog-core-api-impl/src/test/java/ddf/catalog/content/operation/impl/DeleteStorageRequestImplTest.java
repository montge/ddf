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
package ddf.catalog.content.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.catalog.data.Metacard;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Comprehensive test suite for {@link DeleteStorageRequestImpl} class.
 *
 * <p>Tests all constructors, getters, ID generation, and edge cases.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteStorageRequestImplTest {

  // ====================
  // Constructor Tests
  // ====================

  /**
   * Tests constructor with properties only.
   *
   * <p>Verifies ID is auto-generated and metacards list is empty.
   */
  @Test
  public void testConstructorWithPropertiesOnly() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("key1", "value1");

    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(properties);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getMetacards(), is(notNullValue()));
    assertThat(request.getMetacards(), is(empty()));
    assertThat(request.hasProperties(), is(true));
  }

  /**
   * Tests constructor with null properties.
   *
   * <p>Verifies null properties are handled gracefully.
   */
  @Test
  public void testConstructorWithNullProperties() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null);

    assertThat(request, is(notNullValue()));
    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getMetacards(), is(empty()));
  }

  /**
   * Tests constructor with metacards and properties.
   *
   * <p>Verifies metacards are stored and ID is auto-generated.
   */
  @Test
  public void testConstructorWithMetacardsAndProperties() {
    Metacard metacard1 = mock(Metacard.class);
    Metacard metacard2 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(metacard1, metacard2);
    Map<String, Serializable> properties = new HashMap<>();

    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(metacards, properties);

    assertThat(request.getMetacards(), hasSize(2));
    assertThat(request.getMetacards().get(0), is(equalTo(metacard1)));
    assertThat(request.getMetacards().get(1), is(equalTo(metacard2)));
    assertThat(request.getId(), is(notNullValue()));
  }

  /**
   * Tests constructor with null metacards.
   *
   * <p>Verifies null metacards result in empty list.
   */
  @Test
  public void testConstructorWithNullMetacards() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, new HashMap<>());

    assertThat(request.getMetacards(), is(notNullValue()));
    assertThat(request.getMetacards(), is(empty()));
  }

  /**
   * Tests full constructor with metacards, ID, and properties.
   *
   * <p>Verifies all parameters are properly initialized.
   */
  @Test
  public void testFullConstructor() {
    Metacard metacard = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(metacard);
    String customId = "delete-id-456";
    Map<String, Serializable> properties = new HashMap<>();

    DeleteStorageRequestImpl request =
        new DeleteStorageRequestImpl(metacards, customId, properties);

    assertThat(request.getMetacards(), hasSize(1));
    assertThat(request.getId(), is(equalTo(customId)));
  }

  /**
   * Tests constructor with null ID generates UUID.
   *
   * <p>Verifies UUID generation when ID is null.
   */
  @Test
  public void testConstructorWithNullIdGeneratesUuid() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, null, null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36)));
  }

  /**
   * Tests constructor with empty ID generates UUID.
   *
   * <p>Verifies UUID generation when ID is empty.
   */
  @Test
  public void testConstructorWithEmptyIdGeneratesUuid() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, "", null);

    assertThat(request.getId(), is(notNullValue()));
    assertThat(request.getId().length(), is(equalTo(36)));
  }

  /**
   * Tests constructor with whitespace ID is preserved.
   *
   * <p>Verifies whitespace ID is kept (StringUtils.isEmpty doesn't trim).
   */
  @Test
  public void testConstructorWithWhitespaceIdIsPreserved() {
    String whitespaceId = "  ";
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, whitespaceId, null);

    assertThat(request.getId(), is(equalTo(whitespaceId)));
  }

  /**
   * Tests constructor with custom ID is preserved.
   *
   * <p>Verifies provided ID is used.
   */
  @Test
  public void testConstructorWithCustomIdIsPreserved() {
    String customId = "my-delete-id";
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, customId, null);

    assertThat(request.getId(), is(equalTo(customId)));
  }

  // ====================
  // Metacards Tests
  // ====================

  /**
   * Tests getMetacards returns correct list.
   *
   * <p>Verifies metacards retrieval.
   */
  @Test
  public void testGetMetacards() {
    Metacard mc1 = mock(Metacard.class);
    Metacard mc2 = mock(Metacard.class);
    Metacard mc3 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(mc1, mc2, mc3);

    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(metacards, null);

    assertThat(request.getMetacards(), hasSize(3));
    assertThat(request.getMetacards().get(0), is(equalTo(mc1)));
    assertThat(request.getMetacards().get(1), is(equalTo(mc2)));
    assertThat(request.getMetacards().get(2), is(equalTo(mc3)));
  }

  /**
   * Tests getMetacards with empty list.
   *
   * <p>Verifies empty metacards list.
   */
  @Test
  public void testGetMetacardsEmpty() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(new ArrayList<>(), null);

    assertThat(request.getMetacards(), is(empty()));
  }

  // ====================
  // ID Tests
  // ====================

  /**
   * Tests getId returns correct ID.
   *
   * <p>Verifies ID retrieval.
   */
  @Test
  public void testGetIdWithCustomId() {
    String customId = "test-delete-id";
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null, customId, null);

    assertThat(request.getId(), is(equalTo(customId)));
  }

  /**
   * Tests getId returns generated UUID.
   *
   * <p>Verifies auto-generated ID has UUID format.
   */
  @Test
  public void testGetIdWithGeneratedUuid() {
    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(null);

    String id = request.getId();
    assertThat(id, is(notNullValue()));
    assertThat(
        id.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"), is(true));
  }

  /**
   * Tests each request gets unique ID.
   *
   * <p>Verifies ID uniqueness across requests.
   */
  @Test
  public void testEachRequestGetsUniqueId() {
    DeleteStorageRequestImpl req1 = new DeleteStorageRequestImpl(null);
    DeleteStorageRequestImpl req2 = new DeleteStorageRequestImpl(null);

    assertThat(req1.getId().equals(req2.getId()), is(false));
  }

  // ====================
  // Properties Tests
  // ====================

  /**
   * Tests properties are properly handled.
   *
   * <p>Verifies property storage and retrieval.
   */
  @Test
  public void testPropertiesHandling() {
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("force", true);
    properties.put("reason", "cleanup");

    DeleteStorageRequestImpl request = new DeleteStorageRequestImpl(properties);

    assertThat(request.hasProperties(), is(true));
    assertThat(request.getPropertyValue("force"), is(equalTo(true)));
    assertThat(request.getPropertyValue("reason"), is(equalTo("cleanup")));
  }

  // ====================
  // Integration Tests
  // ====================

  /**
   * Tests complete delete storage request.
   *
   * <p>Simulates a real delete storage scenario.
   */
  @Test
  public void testCompleteDeleteStorageRequest() {
    Metacard mc1 = mock(Metacard.class);
    Metacard mc2 = mock(Metacard.class);
    List<Metacard> metacards = Arrays.asList(mc1, mc2);
    String requestId = "del-req-001";
    Map<String, Serializable> properties = new HashMap<>();
    properties.put("skipHooks", false);

    DeleteStorageRequestImpl request =
        new DeleteStorageRequestImpl(metacards, requestId, properties);

    assertThat(request.getId(), is(equalTo(requestId)));
    assertThat(request.getMetacards(), hasSize(2));
    assertThat(request.getPropertyValue("skipHooks"), is(equalTo(false)));
  }
}
