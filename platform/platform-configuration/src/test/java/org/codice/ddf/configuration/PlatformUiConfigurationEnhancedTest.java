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
package org.codice.ddf.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.codice.ddf.branding.BrandingPlugin;
import org.codice.ddf.branding.BrandingRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Enhanced tests for {@link PlatformUiConfiguration} class.
 *
 * <p>This test suite validates:
 *
 * <ul>
 *   <li>JSON configuration generation
 *   <li>Branding integration
 *   <li>System usage configuration
 *   <li>Timeout validation and boundary conditions
 *   <li>Property getters and setters
 *   <li>Edge cases and null handling
 * </ul>
 */
@RunWith(MockitoJUnitRunner.class)
public class PlatformUiConfigurationEnhancedTest {

  private static final int DEFAULT_TIMEOUT_MINUTES = 15;
  private static final int MINIMUM_TIMEOUT_MINUTES = 2;
  private static final String TEST_HEADER = "Test Header";
  private static final String TEST_FOOTER = "Test Footer";
  private static final String TEST_COLOR = "#FF0000";
  private static final String TEST_BACKGROUND = "#FFFFFF";
  private static final String TEST_USAGE_TITLE = "System Usage Title";
  private static final String TEST_USAGE_MESSAGE = "This is a system usage message.";
  private static final String TEST_PRODUCT_NAME = "Test Product";
  private static final String TEST_VERSION = "1.0.0";
  private static final String TEST_PRODUCT_IMAGE = "base64ProductImage";
  private static final String TEST_FAV_ICON = "base64FavIcon";
  private static final String TEST_VENDOR_IMAGE = "base64VendorImage";

  @Mock private BrandingRegistry mockBrandingRegistry;

  @Mock private BrandingPlugin mockBrandingPlugin;

  private PlatformUiConfiguration configuration;

  @Before
  public void setUp() {
    configuration = new PlatformUiConfiguration();
  }

  @Test
  public void testDefaultConfiguration() throws ParseException {
    String jsonString = configuration.getConfigAsJsonString();

    assertThat(jsonString, is(notNullValue()));

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject, is(notNullValue()));
    assertThat(
        (Integer) jsonObject.get(PlatformUiConfiguration.TIMEOUT_CONFIG_KEY),
        is(DEFAULT_TIMEOUT_MINUTES));
  }

  @Test
  public void testSetAndGetHeader() {
    configuration.setHeader(TEST_HEADER);

    assertThat(configuration.getHeader(), is(TEST_HEADER));
  }

  @Test
  public void testSetAndGetFooter() {
    configuration.setFooter(TEST_FOOTER);

    assertThat(configuration.getFooter(), is(TEST_FOOTER));
  }

  @Test
  public void testSetAndGetColor() {
    configuration.setColor(TEST_COLOR);

    assertThat(configuration.getColor(), is(TEST_COLOR));
  }

  @Test
  public void testSetAndGetBackground() {
    configuration.setBackground(TEST_BACKGROUND);

    assertThat(configuration.getBackground(), is(TEST_BACKGROUND));
  }

  @Test
  public void testSetAndGetSystemUsageEnabled() {
    configuration.setSystemUsageEnabled(true);

    assertThat(configuration.getSystemUsageEnabled(), is(true));

    configuration.setSystemUsageEnabled(false);

    assertThat(configuration.getSystemUsageEnabled(), is(false));
  }

  @Test
  public void testSetAndGetSystemUsageTitle() {
    configuration.setSystemUsageTitle(TEST_USAGE_TITLE);

    assertThat(configuration.getSystemUsageTitle(), is(TEST_USAGE_TITLE));
  }

  @Test
  public void testSetAndGetSystemUsageMessage() {
    configuration.setSystemUsageMessage(TEST_USAGE_MESSAGE);

    assertThat(configuration.getSystemUsageMessage(), is(TEST_USAGE_MESSAGE));
  }

  @Test
  public void testSetAndGetSystemUsageOncePerSession() {
    configuration.setSystemUsageOncePerSession(true);

    assertThat(configuration.getSystemUsageOncePerSession(), is(true));

    configuration.setSystemUsageOncePerSession(false);

    assertThat(configuration.getSystemUsageOncePerSession(), is(false));
  }

  @Test
  public void testSetTimeoutWithValidValue() {
    int validTimeout = 30;
    configuration.setTimeout(validTimeout);

    assertThat(configuration.getTimeout(), is(validTimeout));
  }

  @Test
  public void testSetTimeoutAtMinimumBoundary() {
    configuration.setTimeout(MINIMUM_TIMEOUT_MINUTES);

    assertThat(configuration.getTimeout(), is(MINIMUM_TIMEOUT_MINUTES));
  }

  @Test
  public void testSetTimeoutBelowMinimumDefaultsToDefault() {
    configuration.setTimeout(1);

    assertThat(configuration.getTimeout(), is(DEFAULT_TIMEOUT_MINUTES));
  }

  @Test
  public void testSetTimeoutWithZeroDefaultsToDefault() {
    configuration.setTimeout(0);

    assertThat(configuration.getTimeout(), is(DEFAULT_TIMEOUT_MINUTES));
  }

  @Test
  public void testSetTimeoutWithNegativeDefaultsToDefault() {
    configuration.setTimeout(-5);

    assertThat(configuration.getTimeout(), is(DEFAULT_TIMEOUT_MINUTES));
  }

  @Test
  public void testSetTimeoutWithLargeValue() {
    int largeTimeout = 1440; // 24 hours
    configuration.setTimeout(largeTimeout);

    assertThat(configuration.getTimeout(), is(largeTimeout));
  }

  @Test
  public void testSetBrandingWithValidRegistry() throws ParseException {
    when(mockBrandingRegistry.getProductName()).thenReturn(TEST_PRODUCT_NAME);
    configuration.setBranding(mockBrandingRegistry);

    // Branding should be set, verify by checking methods that use it
    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(TEST_PRODUCT_NAME));
  }

  @Test
  public void testSetBrandingWithNull() throws ParseException {
    configuration.setBranding(null);

    // Should not throw exception, returns empty strings
    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(""));
    assertThat(jsonObject.get(PlatformUiConfiguration.VERSION_CONFIG_KEY), is(""));
  }

  @Test
  public void testGetTitleWithBranding() throws ParseException {
    when(mockBrandingRegistry.getProductName()).thenReturn(TEST_PRODUCT_NAME);
    configuration.setBranding(mockBrandingRegistry);

    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(TEST_PRODUCT_NAME));
  }

  @Test
  public void testGetTitleWithoutBranding() throws ParseException {
    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(""));
  }

  @Test
  public void testGetVersionWithBranding() throws ParseException {
    when(mockBrandingRegistry.getProductVersion()).thenReturn(TEST_VERSION);
    configuration.setBranding(mockBrandingRegistry);

    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.VERSION_CONFIG_KEY), is(TEST_VERSION));
  }

  @Test
  public void testGetVersionWithoutBranding() throws ParseException {
    String jsonString = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.VERSION_CONFIG_KEY), is(""));
  }

  @Test
  public void testGetProductImageWithBranding() {
    when(mockBrandingRegistry.getAttributeFromBranding(any())).thenReturn(TEST_PRODUCT_IMAGE);
    configuration.setBranding(mockBrandingRegistry);

    assertThat(configuration.getProductImage(), is(TEST_PRODUCT_IMAGE));
  }

  @Test
  public void testGetProductImageWithoutBranding() {
    assertThat(configuration.getProductImage(), is(""));
  }

  @Test
  public void testGetFavIconWithBranding() {
    when(mockBrandingRegistry.getAttributeFromBranding(any())).thenReturn(TEST_FAV_ICON);
    configuration.setBranding(mockBrandingRegistry);

    assertThat(configuration.getFavIcon(), is(TEST_FAV_ICON));
  }

  @Test
  public void testGetFavIconWithoutBranding() {
    assertThat(configuration.getFavIcon(), is(""));
  }

  @Test
  public void testGetVendorImageWithBranding() {
    when(mockBrandingRegistry.getAttributeFromBranding(any())).thenReturn(TEST_VENDOR_IMAGE);
    configuration.setBranding(mockBrandingRegistry);

    assertThat(configuration.getVendorImage(), is(TEST_VENDOR_IMAGE));
  }

  @Test
  public void testGetVendorImageWithoutBranding() {
    assertThat(configuration.getVendorImage(), is(""));
  }

  @Test
  public void testGetConfigAsJsonStringWithSystemUsageEnabled() throws ParseException {
    configuration.setSystemUsageEnabled(true);
    configuration.setSystemUsageTitle(TEST_USAGE_TITLE);
    configuration.setSystemUsageMessage(TEST_USAGE_MESSAGE);
    configuration.setSystemUsageOncePerSession(true);

    String jsonString = configuration.getConfigAsJsonString();

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(
        jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY),
        is(TEST_USAGE_TITLE));
    assertThat(
        jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_MESSAGE_CONFIG_KEY),
        is(TEST_USAGE_MESSAGE));
    assertThat(
        (Boolean) jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_ONCE_PER_SESSION_CONFIG_KEY),
        is(true));
  }

  @Test
  public void testGetConfigAsJsonStringWithSystemUsageDisabled() throws ParseException {
    configuration.setSystemUsageEnabled(false);
    configuration.setSystemUsageTitle(TEST_USAGE_TITLE);
    configuration.setSystemUsageMessage(TEST_USAGE_MESSAGE);

    String jsonString = configuration.getConfigAsJsonString();

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    // System usage fields should not be in JSON when disabled
    assertThat(
        jsonObject.containsKey(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY), is(false));
    assertThat(
        jsonObject.containsKey(PlatformUiConfiguration.SYSTEM_USAGE_MESSAGE_CONFIG_KEY), is(false));
  }

  @Test
  public void testGetConfigAsJsonStringWithAllPropertiesSet() throws ParseException {
    when(mockBrandingRegistry.getProductName()).thenReturn(TEST_PRODUCT_NAME);
    when(mockBrandingRegistry.getProductVersion()).thenReturn(TEST_VERSION);
    when(mockBrandingRegistry.getAttributeFromBranding(any())).thenReturn(TEST_PRODUCT_IMAGE);

    configuration.setBranding(mockBrandingRegistry);
    configuration.setHeader(TEST_HEADER);
    configuration.setFooter(TEST_FOOTER);
    configuration.setColor(TEST_COLOR);
    configuration.setBackground(TEST_BACKGROUND);
    configuration.setTimeout(20);
    configuration.setSystemUsageEnabled(true);
    configuration.setSystemUsageTitle(TEST_USAGE_TITLE);
    configuration.setSystemUsageMessage(TEST_USAGE_MESSAGE);
    configuration.setSystemUsageOncePerSession(false);

    String jsonString = configuration.getConfigAsJsonString();

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.get(PlatformUiConfiguration.HEADER_CONFIG_KEY), is(TEST_HEADER));
    assertThat(jsonObject.get(PlatformUiConfiguration.FOOTER_CONFIG_KEY), is(TEST_FOOTER));
    assertThat(jsonObject.get(PlatformUiConfiguration.COLOR_CONFIG_KEY), is(TEST_COLOR));
    assertThat(jsonObject.get(PlatformUiConfiguration.BACKGROUND_CONFIG_KEY), is(TEST_BACKGROUND));
    assertThat(jsonObject.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(TEST_PRODUCT_NAME));
    assertThat(jsonObject.get(PlatformUiConfiguration.VERSION_CONFIG_KEY), is(TEST_VERSION));
    assertThat((Integer) jsonObject.get(PlatformUiConfiguration.TIMEOUT_CONFIG_KEY), is(20));
    assertThat(
        jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY),
        is(TEST_USAGE_TITLE));
    assertThat(
        jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_MESSAGE_CONFIG_KEY),
        is(TEST_USAGE_MESSAGE));
    assertThat(
        (Boolean) jsonObject.get(PlatformUiConfiguration.SYSTEM_USAGE_ONCE_PER_SESSION_CONFIG_KEY),
        is(false));
  }

  @Test
  public void testGetConfigAsJsonStringReturnsValidJson() {
    String jsonString = configuration.getConfigAsJsonString();

    assertThat(jsonString, containsString("{"));
    assertThat(jsonString, containsString("}"));
  }

  @Test
  public void testSetNullValues() {
    configuration.setHeader(null);
    configuration.setFooter(null);
    configuration.setColor(null);
    configuration.setBackground(null);
    configuration.setSystemUsageTitle(null);
    configuration.setSystemUsageMessage(null);

    // Should not throw exception
    String jsonString = configuration.getConfigAsJsonString();
    assertThat(jsonString, is(notNullValue()));
  }

  @Test
  public void testSetEmptyStringValues() {
    configuration.setHeader("");
    configuration.setFooter("");
    configuration.setColor("");
    configuration.setBackground("");
    configuration.setSystemUsageTitle("");
    configuration.setSystemUsageMessage("");

    assertThat(configuration.getHeader(), is(""));
    assertThat(configuration.getFooter(), is(""));
    assertThat(configuration.getColor(), is(""));
    assertThat(configuration.getBackground(), is(""));
  }

  @Test
  public void testSetSpecialCharactersInStrings() {
    String specialChars = "Special: <>&\"'\\n\\t";
    configuration.setHeader(specialChars);
    configuration.setFooter(specialChars);
    configuration.setSystemUsageTitle(specialChars);

    assertThat(configuration.getHeader(), is(specialChars));
    assertThat(configuration.getFooter(), is(specialChars));
    assertThat(configuration.getSystemUsageTitle(), is(specialChars));
  }

  @Test
  public void testMultipleTimeoutUpdates() {
    configuration.setTimeout(10);
    assertThat(configuration.getTimeout(), is(10));

    configuration.setTimeout(20);
    assertThat(configuration.getTimeout(), is(20));

    configuration.setTimeout(1); // Below minimum
    assertThat(configuration.getTimeout(), is(DEFAULT_TIMEOUT_MINUTES));

    configuration.setTimeout(30);
    assertThat(configuration.getTimeout(), is(30));
  }

  @Test
  public void testConfigurationConstants() {
    assertThat(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY, is("systemUsageTitle"));
    assertThat(PlatformUiConfiguration.SYSTEM_USAGE_MESSAGE_CONFIG_KEY, is("systemUsageMessage"));
    assertThat(
        PlatformUiConfiguration.SYSTEM_USAGE_ONCE_PER_SESSION_CONFIG_KEY,
        is("systemUsageOncePerSession"));
    assertThat(PlatformUiConfiguration.HEADER_CONFIG_KEY, is("header"));
    assertThat(PlatformUiConfiguration.FOOTER_CONFIG_KEY, is("footer"));
    assertThat(PlatformUiConfiguration.COLOR_CONFIG_KEY, is("color"));
    assertThat(PlatformUiConfiguration.BACKGROUND_CONFIG_KEY, is("background"));
    assertThat(PlatformUiConfiguration.TITLE_CONFIG_KEY, is("title"));
    assertThat(PlatformUiConfiguration.VERSION_CONFIG_KEY, is("version"));
    assertThat(PlatformUiConfiguration.PRODUCT_IMAGE_CONFIG_KEY, is("productImage"));
    assertThat(PlatformUiConfiguration.FAV_ICON_CONFIG_KEY, is("favIcon"));
    assertThat(PlatformUiConfiguration.VENDOR_IMAGE_CONFIG_KEY, is("vendorImage"));
    assertThat(PlatformUiConfiguration.TIMEOUT_CONFIG_KEY, is("timeout"));
  }

  @Test
  public void testBrandingReplacementWithNull() throws ParseException {
    when(mockBrandingRegistry.getProductName()).thenReturn(TEST_PRODUCT_NAME);
    configuration.setBranding(mockBrandingRegistry);

    String jsonString1 = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject1 = (JSONObject) parser.parse(jsonString1);

    assertThat(jsonObject1.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(TEST_PRODUCT_NAME));

    configuration.setBranding(null);

    String jsonString2 = configuration.getConfigAsJsonString();
    JSONObject jsonObject2 = (JSONObject) parser.parse(jsonString2);

    assertThat(jsonObject2.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(""));
  }

  @Test
  public void testBrandingReplacementWithDifferentRegistry() throws ParseException {
    BrandingRegistry firstRegistry = mock(BrandingRegistry.class);
    BrandingRegistry secondRegistry = mock(BrandingRegistry.class);

    when(firstRegistry.getProductName()).thenReturn("First Product");
    when(secondRegistry.getProductName()).thenReturn("Second Product");

    configuration.setBranding(firstRegistry);
    String jsonString1 = configuration.getConfigAsJsonString();
    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject1 = (JSONObject) parser.parse(jsonString1);

    assertThat(jsonObject1.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is("First Product"));

    configuration.setBranding(secondRegistry);
    String jsonString2 = configuration.getConfigAsJsonString();
    JSONObject jsonObject2 = (JSONObject) parser.parse(jsonString2);

    assertThat(jsonObject2.get(PlatformUiConfiguration.TITLE_CONFIG_KEY), is("Second Product"));
  }

  @Test
  public void testJsonStringContainsAllRequiredFields() throws ParseException {
    String jsonString = configuration.getConfigAsJsonString();

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

    assertThat(jsonObject.containsKey(PlatformUiConfiguration.HEADER_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.FOOTER_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.COLOR_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.BACKGROUND_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.TITLE_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.VERSION_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.PRODUCT_IMAGE_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.FAV_ICON_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.VENDOR_IMAGE_CONFIG_KEY), is(true));
    assertThat(jsonObject.containsKey(PlatformUiConfiguration.TIMEOUT_CONFIG_KEY), is(true));
  }

  @Test
  public void testVeryLongStrings() {
    String longString = new String(new char[10000]).replace('\0', 'A');

    configuration.setHeader(longString);
    configuration.setFooter(longString);
    configuration.setSystemUsageMessage(longString);

    assertThat(configuration.getHeader(), is(longString));
    assertThat(configuration.getFooter(), is(longString));
    assertThat(configuration.getSystemUsageMessage(), is(longString));
  }

  @Test
  public void testSystemUsageToggling() throws ParseException {
    configuration.setSystemUsageTitle(TEST_USAGE_TITLE);
    configuration.setSystemUsageMessage(TEST_USAGE_MESSAGE);

    configuration.setSystemUsageEnabled(true);
    String enabledJson = configuration.getConfigAsJsonString();

    configuration.setSystemUsageEnabled(false);
    String disabledJson = configuration.getConfigAsJsonString();

    // The JSON strings should be different
    assertThat(enabledJson, is(not(equalTo(disabledJson))));

    JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    JSONObject enabledObject = (JSONObject) parser.parse(enabledJson);
    JSONObject disabledObject = (JSONObject) parser.parse(disabledJson);

    assertThat(
        enabledObject.containsKey(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY), is(true));
    assertThat(
        disabledObject.containsKey(PlatformUiConfiguration.SYSTEM_USAGE_TITLE_CONFIG_KEY),
        is(false));
  }
}
