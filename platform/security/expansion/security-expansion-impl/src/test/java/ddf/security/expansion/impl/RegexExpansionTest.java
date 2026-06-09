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
package ddf.security.expansion.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegexExpansionTest {
  public List<String[]> rulesList1 = new ArrayList<String[]>();

  public List<String[]> rulesList2 = new ArrayList<String[]>();

  public Map<String, List<String[]>> testmap = new HashMap<String, List<String[]>>();

  /*
   * Rules for the various tests \A and \z represent the start of input and end of input
   */
  private String[] rule1 = new String[] {"VP-(Sales|Engineering|Finance)", "VP $0 $1"};

  private String[] rule2 = new String[] {"\\AVP\\z", "$0 Manager"};

  private String[] rule3 = new String[] {"Manager", "$0 Employee"};

  private String[] rule4 = new String[] {"\\AVP\\z", ""};

  private String[] rule5 = new String[] {"\\AAZ\\z", "$0 Arizona"};

  private String[] rule6 = new String[] {"\\A(AZ|VA|MD)\\z", "$0 USA"};

  @BeforeEach
  public void setupData() {
    rulesList1.add(rule1);
    rulesList1.add(rule2);
    rulesList1.add(rule3);

    rulesList2.add(rule4);

    testmap.put("role", rulesList1);
  }

  private void assertMapsAreEqual(Map<String, List<String[]>> m1, Map<String, List<String[]>> m2) {
    assert (m1.size() == m2.size());
    assert (m1.keySet().equals(m2.keySet()));
    for (String k : m1.keySet()) {
      assert (m1.get(k).equals(m2.get(k)));
    }
  }

  @Test
  public void testExpandSimple() throws Exception {
    HashSet<String> testSet = new HashSet<String>();
    AbstractExpansion exp = new RegexExpansion();
    Set<String> result;

    testSet.add("staff");
    // no rules defined - should return the same set
    exp.expand(null, testSet);
    assertEquals(1, testSet.size());
    assertTrue(testSet.contains("staff"));

    // add a rule for the "role" key
    String[] rule = new String[] {"staff", "staff employee"};
    exp.addExpansionRule("role", rule);

    // calling with unknown key - should return the same set
    exp.expand("name", testSet);
    assertEquals(1, testSet.size());
    assertTrue(testSet.contains("staff"));

    // calling with null values - returns null
    result = exp.expand("role", null);
    assertNull(result);

    exp.removeExpansionRule("role", rule);
    testSet.clear();
    exp.addExpansionRule("role", rule1);
    exp.addExpansionRule("role", rule2);
    exp.addExpansionRule("role", rule3);

    testSet.add("VP-Sales");
    exp.expand("role", testSet);
    assertEquals(5, testSet.size());
    assertTrue(testSet.contains("VP-Sales"));
    assertTrue(testSet.contains("VP"));
    assertTrue(testSet.contains("Sales"));
    assertTrue(testSet.contains("Manager"));
    assertTrue(testSet.contains("Employee"));

    testSet.clear();
    exp = new RegexExpansion();
    exp.addExpansionRule("role", new String[] {"\\AVP\\z", ""});
    testSet.add("VP-Sales");
    testSet.add("VP");
    // calling with empty replacement string - should delete matched string
    exp.expand("role", testSet);
    assertEquals(1, testSet.size());
    assertTrue(testSet.contains("VP-Sales"));
  }

  @Test
  public void testExpandMap() throws Exception {
    AbstractExpansion exp = new RegexExpansion();

    // build the data map
    Map<String, Set<String>> map = new HashMap<String, Set<String>>();
    HashSet<String> roleSet = new HashSet<String>();
    roleSet.add("VP-Sales");
    HashSet<String> locSet = new HashSet<String>();
    locSet.add("Goodyear");
    locSet.add("AZ");
    map.put("role", roleSet);
    map.put("location", locSet);

    // test with null rule set and valid data - should leave data untouched
    exp.expand(map);
    Set<String> result = map.get("role");
    assertEquals(1, result.size());
    assertTrue(result.contains("VP-Sales"));
    result = map.get("location");
    assertEquals(2, result.size());
    assertTrue(result.contains("Goodyear"));
    assertTrue(result.contains("AZ"));

    // build the expansion rule set
    exp.setExpansionMap(testmap);
    exp.addExpansionRule("location", rule5);
    exp.addExpansionRule("location", rule6);

    // test with valid rules but null data - should return null
    Map<String, Set<String>> resultMap = exp.expand(null);
    assertNull(resultMap);

    // test with valid rules but empty data - should return empty data
    resultMap = exp.expand(new HashMap<String, Set<String>>());
    assertEquals(0, resultMap.size());

    // test with valid rules and valid data
    resultMap = exp.expand(map);
    // should return the exact same object (updated)
    assertSame(map, resultMap);

    result = resultMap.get("role");
    assertEquals(5, result.size());
    assertTrue(result.contains("VP-Sales"));
    assertTrue(result.contains("VP"));
    assertTrue(result.contains("Sales"));
    assertTrue(result.contains("Manager"));
    assertTrue(result.contains("Employee"));

    result = resultMap.get("location");
    assertEquals(4, result.size());
    assertTrue(result.contains("Goodyear"));
    assertTrue(result.contains("AZ"));
    assertTrue(result.contains("Arizona"));
    assertTrue(result.contains("USA"));

    // verify the regex rules work - don't expand unless the entire token
    // build the data map
    map.clear();
    locSet.clear();
    locSet.add("AZTEC");
    map.put("location", locSet);

    resultMap = exp.expand(map);
    result = resultMap.get("location");
    assertEquals(1, result.size());
    assertTrue(result.contains("AZTEC"));
  }
}
