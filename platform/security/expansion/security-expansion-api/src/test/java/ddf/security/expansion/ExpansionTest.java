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
package ddf.security.expansion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Expansion} interface contract. Uses a test implementation to verify the
 * interface behavior and contract expectations.
 */
public class ExpansionTest {

  private TestExpansion expansion;

  @Before
  public void setUp() {
    expansion = new TestExpansion();
  }

  @Test
  public void testExpandMapWithEmptyMap() {
    Map<String, Set<String>> emptyMap = new HashMap<>();
    Map<String, Set<String>> result = expansion.expand(emptyMap);

    assertThat(result, is(notNullValue()));
    assertThat(result.entrySet(), is(empty()));
  }

  @Test
  public void testExpandMapWithSingleEntry() {
    Map<String, Set<String>> input = new HashMap<>();
    input.put("role", new HashSet<>(Collections.singletonList("admin")));

    Map<String, Set<String>> result = expansion.expand(input);

    assertThat(result, hasKey("role"));
    assertThat(result.get("role"), containsInAnyOrder("admin", "admin_expanded"));
  }

  @Test
  public void testExpandMapWithMultipleEntries() {
    Map<String, Set<String>> input = new HashMap<>();
    input.put("role", new HashSet<>(Arrays.asList("admin", "user")));
    input.put("group", new HashSet<>(Collections.singletonList("developers")));

    Map<String, Set<String>> result = expansion.expand(input);

    assertThat(result, hasKey("role"));
    assertThat(result, hasKey("group"));
    assertThat(
        result.get("role"), containsInAnyOrder("admin", "admin_expanded", "user", "user_expanded"));
    assertThat(result.get("group"), containsInAnyOrder("developers", "developers_expanded"));
  }

  @Test
  public void testExpandSingleKeyWithEmptyValues() {
    Set<String> emptySet = new HashSet<>();
    Set<String> result = expansion.expand("role", emptySet);

    assertThat(result, is(notNullValue()));
    assertThat(result, is(empty()));
  }

  @Test
  public void testExpandSingleKeyWithSingleValue() {
    Set<String> values = new HashSet<>(Collections.singletonList("admin"));
    Set<String> result = expansion.expand("role", values);

    assertThat(result, containsInAnyOrder("admin", "admin_expanded"));
  }

  @Test
  public void testExpandSingleKeyWithMultipleValues() {
    Set<String> values = new HashSet<>(Arrays.asList("admin", "user", "guest"));
    Set<String> result = expansion.expand("role", values);

    assertThat(result, hasSize(6));
    assertThat(
        result,
        containsInAnyOrder(
            "admin", "admin_expanded",
            "user", "user_expanded",
            "guest", "guest_expanded"));
  }

  @Test
  public void testExpandWithNullKey() {
    Set<String> values = new HashSet<>(Collections.singletonList("admin"));
    Set<String> result = expansion.expand(null, values);

    // Null key should still expand values
    assertThat(result, containsInAnyOrder("admin", "admin_expanded"));
  }

  @Test
  public void testGetExpansionMapReturnsConfiguredExpansions() {
    Map<String, List<String[]>> expansionMap = expansion.getExpansionMap();

    assertThat(expansionMap, is(notNullValue()));
    assertThat(expansionMap, hasKey("role"));
    assertThat(expansionMap.get("role"), hasSize(1));
  }

  @Test
  public void testGetExpansionMapIsNotModifiable() {
    Map<String, List<String[]>> expansionMap = expansion.getExpansionMap();

    // Attempting to modify should either throw or not affect the internal state
    try {
      expansionMap.put("newKey", Collections.emptyList());
    } catch (UnsupportedOperationException e) {
      // Expected for unmodifiable maps
    }

    // Original map should be unchanged
    Map<String, List<String[]>> originalMap = expansion.getExpansionMap();
    assertThat(originalMap.containsKey("newKey"), is(false));
  }

  @Test
  public void testExpandPreservesOriginalValues() {
    Set<String> originalValues = new HashSet<>(Arrays.asList("admin", "user"));
    Set<String> result = expansion.expand("role", originalValues);

    // Original values should be preserved in the result
    assertThat(result.contains("admin"), is(true));
    assertThat(result.contains("user"), is(true));
  }

  @Test
  public void testExpandWithSpecialCharactersInValues() {
    Set<String> values = new HashSet<>(Arrays.asList("role:admin", "group=developers"));
    Set<String> result = expansion.expand("attributes", values);

    assertThat(
        result,
        containsInAnyOrder(
            "role:admin", "role:admin_expanded",
            "group=developers", "group=developers_expanded"));
  }

  @Test
  public void testExpandMapDoesNotModifyOriginal() {
    Map<String, Set<String>> original = new HashMap<>();
    Set<String> originalSet = new HashSet<>(Collections.singletonList("admin"));
    original.put("role", originalSet);

    Map<String, Set<String>> result = expansion.expand(original);

    // Result should be different from original
    assertThat(result.get("role").size() > originalSet.size(), is(true));
  }

  /**
   * Simple test implementation of Expansion interface that adds "_expanded" suffix to each value.
   */
  private static class TestExpansion implements Expansion {
    private final Map<String, List<String[]>> expansionMap;

    public TestExpansion() {
      expansionMap = new HashMap<>();
      expansionMap.put("role", Collections.singletonList(new String[] {"*", "*_expanded"}));
    }

    @Override
    public Map<String, Set<String>> expand(Map<String, Set<String>> map) {
      Map<String, Set<String>> result = new HashMap<>();
      for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
        result.put(entry.getKey(), expand(entry.getKey(), entry.getValue()));
      }
      return result;
    }

    @Override
    public Set<String> expand(String key, Set<String> values) {
      Set<String> expandedValues = new HashSet<>(values);
      for (String value : values) {
        expandedValues.add(value + "_expanded");
      }
      return expandedValues;
    }

    @Override
    public Map<String, List<String[]>> getExpansionMap() {
      return Collections.unmodifiableMap(expansionMap);
    }
  }
}
