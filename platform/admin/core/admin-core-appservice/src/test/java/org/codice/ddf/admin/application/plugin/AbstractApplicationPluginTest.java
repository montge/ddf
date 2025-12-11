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
package org.codice.ddf.admin.application.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test harness for AbstractApplicationPlugin base class.
 *
 * <p>Tests the core functionality of application plugin infrastructure including associations,
 * display properties, JSON serialization, and plugin management.
 *
 * <p>Coverage includes: - Constructor initialization - Display name and iframe location management
 * - Application associations (add, set, match) - JSON serialization (toJSON) - Plugin ID generation
 * - Edge cases and boundary conditions - ALL_ASSOCIATION_KEY default behavior
 */
public class AbstractApplicationPluginTest {

  private TestApplicationPlugin plugin;

  /**
   * Concrete test implementation of AbstractApplicationPlugin for testing.
   *
   * <p>Provides access to protected methods for testing purposes.
   */
  private static class TestApplicationPlugin extends AbstractApplicationPlugin {

    public TestApplicationPlugin() {
      super();
    }

    public void setTestDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public void setTestIframeLocation(URI iframeLocation) {
      this.iframeLocation = iframeLocation;
    }
  }

  @BeforeEach
  public void setUp() {
    plugin = new TestApplicationPlugin();
  }

  /**
   * Tests default constructor initialization.
   *
   * <p>Verifies that constructor sets default ALL_ASSOCIATION_KEY.
   */
  @Test
  public void testConstructorInitialization() {
    assertThat("Plugin should not be null", plugin, notNullValue());
    assertThat("Associations should not be null", plugin.getAssocations(), notNullValue());
    assertThat(
        "Should have default ALL_ASSOCIATION_KEY",
        plugin.getAssocations(),
        contains(ApplicationPlugin.ALL_ASSOCATION_KEY));
    assertThat("ID should be generated", plugin.getID(), notNullValue());
  }

  /**
   * Tests that each plugin gets unique ID.
   *
   * <p>Verifies UUID generation produces unique identifiers.
   */
  @Test
  public void testUniqueIdGeneration() {
    TestApplicationPlugin plugin1 = new TestApplicationPlugin();
    TestApplicationPlugin plugin2 = new TestApplicationPlugin();
    TestApplicationPlugin plugin3 = new TestApplicationPlugin();

    assertThat("ID1 should be unique", plugin1.getID(), not(equalTo(plugin2.getID())));
    assertThat("ID2 should be unique", plugin2.getID(), not(equalTo(plugin3.getID())));
    assertThat("ID3 should be unique", plugin1.getID(), not(equalTo(plugin3.getID())));
  }

  /**
   * Tests display name getter and setter.
   *
   * <p>Verifies display name property management.
   */
  @Test
  public void testDisplayNameProperty() {
    assertThat("Initial display name should be null", plugin.getDisplayName(), nullValue());

    plugin.setTestDisplayName("Test Plugin");
    assertThat("Display name should be set", plugin.getDisplayName(), is("Test Plugin"));

    plugin.setTestDisplayName("Updated Plugin");
    assertThat("Display name should be updated", plugin.getDisplayName(), is("Updated Plugin"));
  }

  /**
   * Tests display name with null value.
   *
   * <p>Verifies that null display name is accepted.
   */
  @Test
  public void testDisplayNameWithNull() {
    plugin.setTestDisplayName("Test Plugin");
    plugin.setTestDisplayName(null);
    assertThat("Display name should be null", plugin.getDisplayName(), nullValue());
  }

  /**
   * Tests display name with empty string.
   *
   * <p>Verifies that empty display name is accepted.
   */
  @Test
  public void testDisplayNameWithEmptyString() {
    plugin.setTestDisplayName("");
    assertThat("Empty display name should be accepted", plugin.getDisplayName(), is(""));
  }

  /**
   * Tests display name with special characters.
   *
   * <p>Verifies that special characters in display name are preserved.
   */
  @Test
  public void testDisplayNameWithSpecialCharacters() {
    String specialName = "Plugin <Name> & \"Quotes\" 100%";
    plugin.setTestDisplayName(specialName);
    assertThat("Special characters should be preserved", plugin.getDisplayName(), is(specialName));
  }

  /**
   * Tests iframe location getter and setter.
   *
   * <p>Verifies iframe URI property management.
   */
  @Test
  public void testIframeLocationProperty() throws URISyntaxException {
    assertThat("Initial iframe location should be null", plugin.getIframeLocation(), nullValue());

    URI testUri = new URI("http://example.com/plugin");
    plugin.setTestIframeLocation(testUri);
    assertThat("Iframe location should be set", plugin.getIframeLocation(), is(testUri.toString()));
  }

  /**
   * Tests iframe location with null value.
   *
   * <p>Verifies that null iframe location returns null.
   */
  @Test
  public void testIframeLocationWithNull() throws URISyntaxException {
    URI testUri = new URI("http://example.com/plugin");
    plugin.setTestIframeLocation(testUri);
    plugin.setTestIframeLocation(null);
    assertThat("Null iframe location should return null", plugin.getIframeLocation(), nullValue());
  }

  /**
   * Tests iframe location with various URI formats.
   *
   * <p>Verifies that different URI formats are handled correctly.
   */
  @Test
  public void testIframeLocationWithVariousFormats() throws URISyntaxException {
    URI httpUri = new URI("http://localhost:8080/admin/plugin");
    plugin.setTestIframeLocation(httpUri);
    assertThat(
        "HTTP URI should work",
        plugin.getIframeLocation(),
        is("http://localhost:8080/admin/plugin"));

    URI httpsUri = new URI("https://secure.example.com/plugin");
    plugin.setTestIframeLocation(httpsUri);
    assertThat(
        "HTTPS URI should work",
        plugin.getIframeLocation(),
        is("https://secure.example.com/plugin"));

    URI relativeUri = new URI("/admin/plugin");
    plugin.setTestIframeLocation(relativeUri);
    assertThat("Relative URI should work", plugin.getIframeLocation(), is("/admin/plugin"));
  }

  /**
   * Tests getAssocations method.
   *
   * <p>Verifies that associations list is returned correctly.
   */
  @Test
  public void testGetAssocations() {
    List<String> associations = plugin.getAssocations();
    assertThat("Associations should not be null", associations, notNullValue());
    assertThat("Should have default association", associations, hasSize(1));
    assertThat(
        "Should contain ALL_ASSOCIATION_KEY",
        associations,
        contains(ApplicationPlugin.ALL_ASSOCATION_KEY));
  }

  /**
   * Tests setAssociations method.
   *
   * <p>Verifies that associations can be replaced entirely.
   */
  @Test
  public void testSetAssociations() {
    List<String> newAssociations = Arrays.asList("app1", "app2", "app3");
    plugin.setAssociations(newAssociations);

    List<String> associations = plugin.getAssocations();
    assertThat("Associations should be updated", associations, hasSize(3));
    assertThat(
        "Should contain new associations",
        associations,
        containsInAnyOrder("app1", "app2", "app3"));
    assertThat(
        "Should not contain ALL_ASSOCIATION_KEY",
        !associations.contains(ApplicationPlugin.ALL_ASSOCATION_KEY));
  }

  /**
   * Tests setAssociations with empty list.
   *
   * <p>Verifies that associations can be cleared.
   */
  @Test
  public void testSetAssociationsWithEmptyList() {
    plugin.setAssociations(new ArrayList<>());
    assertThat("Associations should be empty", plugin.getAssocations(), hasSize(0));
  }

  /**
   * Tests addAssociations method with single association.
   *
   * <p>Verifies that single association can be added.
   */
  @Test
  public void testAddSingleAssociation() {
    plugin.addAssociations("catalog-app");

    List<String> associations = plugin.getAssocations();
    assertThat("Should have 2 associations", associations, hasSize(2));
    assertThat(
        "Should contain both associations",
        associations,
        containsInAnyOrder(ApplicationPlugin.ALL_ASSOCATION_KEY, "catalog-app"));
  }

  /**
   * Tests addAssociations prevents duplicate entries.
   *
   * <p>Verifies that duplicate associations are not added.
   */
  @Test
  public void testAddAssociationPreventsDuplicates() {
    plugin.addAssociations("catalog-app");
    plugin.addAssociations("catalog-app");
    plugin.addAssociations("catalog-app");

    List<String> associations = plugin.getAssocations();
    assertThat("Should not have duplicates", associations, hasSize(2));
    assertThat(
        "Should contain unique associations",
        associations,
        containsInAnyOrder(ApplicationPlugin.ALL_ASSOCATION_KEY, "catalog-app"));
  }

  /**
   * Tests addAssocations method with list.
   *
   * <p>Verifies that multiple associations can be added at once.
   */
  @Test
  public void testAddMultipleAssociations() {
    List<String> newAssociations = Arrays.asList("app1", "app2", "app3");
    plugin.addAssocations(newAssociations);

    List<String> associations = plugin.getAssocations();
    assertThat("Should have 4 associations", associations, hasSize(4));
    assertThat(
        "Should contain all associations",
        associations,
        containsInAnyOrder(ApplicationPlugin.ALL_ASSOCATION_KEY, "app1", "app2", "app3"));
  }

  /**
   * Tests addAssocations with list containing duplicates.
   *
   * <p>Verifies that duplicate prevention works with list additions.
   */
  @Test
  public void testAddMultipleAssociationsWithDuplicates() {
    plugin.addAssociations("app1");
    List<String> newAssociations = Arrays.asList("app1", "app2", "app1", "app3");
    plugin.addAssocations(newAssociations);

    List<String> associations = plugin.getAssocations();
    assertThat("Should not have duplicates", associations, hasSize(4));
    assertThat(
        "Should contain unique associations",
        associations,
        containsInAnyOrder(ApplicationPlugin.ALL_ASSOCATION_KEY, "app1", "app2", "app3"));
  }

  /**
   * Tests addAssocations with empty list.
   *
   * <p>Verifies that empty list addition has no effect.
   */
  @Test
  public void testAddEmptyAssociationsList() {
    List<String> initialAssociations = new ArrayList<>(plugin.getAssocations());
    plugin.addAssocations(new ArrayList<>());

    assertThat(
        "Associations should remain unchanged",
        plugin.getAssocations(),
        equalTo(initialAssociations));
  }

  /**
   * Tests matchesAssocationName with ALL_ASSOCATION_KEY.
   *
   * <p>Verifies that ALL_ASSOCATION_KEY matches any application name.
   */
  @Test
  public void testMatchesAssocationNameWithAllKey() {
    assertThat(
        "Should match any app with ALL_ASSOCIATION_KEY",
        plugin.matchesAssocationName("catalog-app"),
        is(true));
    assertThat(
        "Should match any app with ALL_ASSOCIATION_KEY",
        plugin.matchesAssocationName("search-app"),
        is(true));
    assertThat(
        "Should match any app with ALL_ASSOCIATION_KEY",
        plugin.matchesAssocationName("admin-app"),
        is(true));
    assertThat(
        "Should match any app with ALL_ASSOCIATION_KEY",
        plugin.matchesAssocationName("any-random-app"),
        is(true));
  }

  /**
   * Tests matchesAssocationName with specific association.
   *
   * <p>Verifies that specific associations match correctly.
   */
  @Test
  public void testMatchesAssocationNameWithSpecificAssociation() {
    plugin.setAssociations(Arrays.asList("catalog-app", "search-app"));

    assertThat("Should match catalog-app", plugin.matchesAssocationName("catalog-app"), is(true));
    assertThat("Should match search-app", plugin.matchesAssocationName("search-app"), is(true));
    assertThat("Should not match admin-app", plugin.matchesAssocationName("admin-app"), is(false));
    assertThat("Should not match other-app", plugin.matchesAssocationName("other-app"), is(false));
  }

  /**
   * Tests matchesAssocationName with empty associations.
   *
   * <p>Verifies behavior when no associations are set.
   */
  @Test
  public void testMatchesAssocationNameWithEmptyAssociations() {
    plugin.setAssociations(new ArrayList<>());

    assertThat("Should not match any app", plugin.matchesAssocationName("catalog-app"), is(false));
  }

  /**
   * Tests matchesAssocationName with null app name.
   *
   * <p>Verifies that null app name does not match.
   */
  @Test
  public void testMatchesAssocationNameWithNull() {
    assertThat("Null should not match", plugin.matchesAssocationName(null), is(false));
  }

  /**
   * Tests matchesAssocationName with empty string.
   *
   * <p>Verifies that empty app name does not match.
   */
  @Test
  public void testMatchesAssocationNameWithEmptyString() {
    assertThat("Empty string should not match", plugin.matchesAssocationName(""), is(false));
  }

  /**
   * Tests toJSON method with complete properties.
   *
   * <p>Verifies JSON serialization includes all properties.
   */
  @Test
  public void testToJsonWithCompleteProperties() throws URISyntaxException {
    plugin.setTestDisplayName("Test Plugin");
    plugin.setTestIframeLocation(new URI("http://example.com/plugin"));
    plugin.setAssociations(Arrays.asList("app1", "app2"));

    Map<String, Object> json = plugin.toJSON();

    assertThat("JSON should not be null", json, notNullValue());
    assertThat(
        "JSON should have associations",
        json,
        hasEntry(ApplicationPlugin.APPLICATION_ASSOCIATION_KEY, Arrays.asList("app1", "app2")));
    assertThat(
        "JSON should have display name",
        json,
        hasEntry(ApplicationPlugin.DISPLAY_NAME_KEY, "Test Plugin"));
    assertThat(
        "JSON should have iframe location",
        json,
        hasEntry(ApplicationPlugin.IFRAME_LOCATION_KEY, "http://example.com/plugin"));
    assertThat("JSON should have ID key", json.containsKey(ApplicationPlugin.ID_KEY), is(true));
  }

  /**
   * Tests toJSON with null properties.
   *
   * <p>Verifies JSON serialization handles null values correctly.
   */
  @Test
  public void testToJsonWithNullProperties() {
    plugin.setTestDisplayName(null);
    plugin.setTestIframeLocation(null);

    Map<String, Object> json = plugin.toJSON();

    assertThat("JSON should not be null", json, notNullValue());
    assertThat(
        "Display name should be null in JSON",
        json,
        hasEntry(ApplicationPlugin.DISPLAY_NAME_KEY, null));
    assertThat(
        "Iframe location should be null in JSON",
        json,
        hasEntry(ApplicationPlugin.IFRAME_LOCATION_KEY, null));
  }

  /**
   * Tests toJSON includes valid UUID string.
   *
   * <p>Verifies that ID is serialized as string representation of UUID.
   */
  @Test
  public void testToJsonIncludesValidUuidString() {
    Map<String, Object> json = plugin.toJSON();

    assertThat("JSON should contain ID", json.containsKey(ApplicationPlugin.ID_KEY), is(true));
    Object idObject = json.get(ApplicationPlugin.ID_KEY);
    assertThat("ID should be string", idObject instanceof String, is(true));

    String idString = (String) idObject;
    // Verify it's a valid UUID format
    UUID.fromString(idString); // Will throw if invalid
  }

  /**
   * Tests toJSON with ALL_ASSOCIATION_KEY.
   *
   * <p>Verifies that default association is included in JSON.
   */
  @Test
  public void testToJsonWithDefaultAssociation() {
    Map<String, Object> json = plugin.toJSON();

    assertThat(
        "JSON should have associations",
        json.containsKey(ApplicationPlugin.APPLICATION_ASSOCIATION_KEY),
        is(true));
    @SuppressWarnings("unchecked")
    List<String> associations =
        (List<String>) json.get(ApplicationPlugin.APPLICATION_ASSOCIATION_KEY);
    assertThat(
        "Associations should contain ALL_ASSOCIATION_KEY",
        associations,
        contains(ApplicationPlugin.ALL_ASSOCATION_KEY));
  }

  /**
   * Tests toJSON with empty display name.
   *
   * <p>Verifies that empty string display name is included.
   */
  @Test
  public void testToJsonWithEmptyDisplayName() {
    plugin.setTestDisplayName("");

    Map<String, Object> json = plugin.toJSON();

    assertThat(
        "Empty display name should be in JSON",
        json,
        hasEntry(ApplicationPlugin.DISPLAY_NAME_KEY, ""));
  }

  /**
   * Tests getID returns consistent value.
   *
   * <p>Verifies that ID remains constant across multiple calls.
   */
  @Test
  public void testGetIdConsistency() {
    UUID firstId = plugin.getID();
    UUID secondId = plugin.getID();
    UUID thirdId = plugin.getID();

    assertThat("ID should be consistent", firstId, equalTo(secondId));
    assertThat("ID should be consistent", secondId, equalTo(thirdId));
  }

  /**
   * Tests association modifications don't affect original list.
   *
   * <p>Verifies defensive copying or proper list management.
   */
  @Test
  public void testAssociationListIndependence() {
    List<String> originalAssociations = new ArrayList<>(Arrays.asList("app1", "app2"));
    plugin.setAssociations(originalAssociations);

    // Modify original list
    originalAssociations.add("app3");
    originalAssociations.add("app4");

    // Plugin associations should not be affected
    List<String> pluginAssociations = plugin.getAssocations();
    assertThat("Plugin associations should be independent", pluginAssociations, hasSize(2));
  }

  /**
   * Tests toJSON creates new map each time.
   *
   * <p>Verifies that toJSON returns fresh map on each call.
   */
  @Test
  public void testToJsonCreatesNewMap() {
    Map<String, Object> json1 = plugin.toJSON();
    Map<String, Object> json2 = plugin.toJSON();

    assertThat("Should create new map", json1, not(json2));
    assertThat("Content should be equal", json1, equalTo(json2));
  }

  /**
   * Tests association case sensitivity.
   *
   * <p>Verifies that association matching is case-sensitive.
   */
  @Test
  public void testAssociationCaseSensitivity() {
    plugin.setAssociations(Arrays.asList("Catalog-App"));

    assertThat("Exact case should match", plugin.matchesAssocationName("Catalog-App"), is(true));
    assertThat(
        "Different case should not match", plugin.matchesAssocationName("catalog-app"), is(false));
    assertThat(
        "Different case should not match", plugin.matchesAssocationName("CATALOG-APP"), is(false));
  }

  /**
   * Tests complex association scenarios.
   *
   * <p>Verifies that complex association patterns work correctly.
   */
  @Test
  public void testComplexAssociationScenarios() {
    // Start with ALL_ASSOCIATION_KEY
    assertThat("Should match any initially", plugin.matchesAssocationName("test"), is(true));

    // Add specific association (ALL_ASSOCIATION_KEY still present)
    plugin.addAssociations("specific-app");
    assertThat(
        "Should still match any with ALL key", plugin.matchesAssocationName("test"), is(true));

    // Replace with specific associations only
    plugin.setAssociations(Arrays.asList("app1", "app2"));
    assertThat("Should match app1", plugin.matchesAssocationName("app1"), is(true));
    assertThat("Should match app2", plugin.matchesAssocationName("app2"), is(true));
    assertThat("Should not match test", plugin.matchesAssocationName("test"), is(false));

    // Clear associations
    plugin.setAssociations(new ArrayList<>());
    assertThat("Should not match any", plugin.matchesAssocationName("app1"), is(false));

    // Add ALL_ASSOCIATION_KEY back
    plugin.addAssociations(ApplicationPlugin.ALL_ASSOCATION_KEY);
    assertThat("Should match any again", plugin.matchesAssocationName("test"), is(true));
  }
}
