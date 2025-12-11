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
package ddf.catalog.endpoint.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CatalogEndpointImplTest {

  private static final String DEFAULT_BINDING_TYPE = "defaultBindingType";

  private static final String DEFAULT_DESCRIPTION = "description";

  private static final String DEFAULT_ID = "id";

  private static final String DEFAULT_NAME = "name";

  private static final String DEFAULT_URL = "url";

  private static final String DEFAULT_URL_BINDING_NAME = "urlBindingName";

  private static final String DEFAULT_VERSION = "version";

  @Test
  public void testSettingEndpointProperties() {
    Map<String, String> propertiesMap = getTestEndpointPropertiesWithDefaults();

    CatalogEndpointImpl catalogEndpoint = new CatalogEndpointImpl();
    Map<String, String> endpointProperties = catalogEndpoint.getEndpointProperties();
    assertTrue(endpointProperties.isEmpty(), "Endpoint Properties should be empty.");

    catalogEndpoint.setEndpointProperties(propertiesMap);
    endpointProperties = catalogEndpoint.getEndpointProperties();
    assertFalse(endpointProperties.isEmpty(), "Endpoint Properties shouldn't be empty.");

    assertEndpointPropertiesContainDefaults(endpointProperties);
  }

  @Test
  public void testSettingEndpointPropertiesPassedInConstructor() {
    Map<String, String> endpointProps = getTestEndpointPropertiesWithDefaults();
    CatalogEndpointImpl catalogEndpoint = new CatalogEndpointImpl(endpointProps);

    Map<String, String> endpointProperties = catalogEndpoint.getEndpointProperties();
    assertFalse(endpointProperties.isEmpty(), "Endpoint Properties shouldn't be empty.");

    assertEndpointPropertiesContainDefaults(endpointProperties);
  }

  @Test
  public void testSettingEndpointPropertiesIndividually() {
    CatalogEndpointImpl catalogEndpoint = getTestCatalogEndpointWithDefaults();

    Map<String, String> endpointProperties = catalogEndpoint.getEndpointProperties();
    assertEndpointPropertiesContainDefaults(endpointProperties);
  }

  @Test
  public void testSettingEndpointPropertiesIndividuallyToNull() {
    CatalogEndpointImpl catalogEndpoint = getTestCatalogEndpointWithDefaults();

    Map<String, String> endpointProperties = catalogEndpoint.getEndpointProperties();
    assertFalse(endpointProperties.isEmpty(), "Endpoint Properties shouldn't be empty.");

    // Make sure the values were actually set
    assertEndpointPropertiesContainDefaults(endpointProperties);

    catalogEndpoint.setBindingType(null);
    catalogEndpoint.setDescription(null);
    catalogEndpoint.setId(null);
    catalogEndpoint.setName(null);
    catalogEndpoint.setUrl(null);
    catalogEndpoint.setUrlBindingName(null);
    catalogEndpoint.setVersion(null);
    assertTrue(endpointProperties.isEmpty(), "Endpoint Properties should be empty.");
  }

  private CatalogEndpointImpl getTestCatalogEndpointWithDefaults() {
    CatalogEndpointImpl catalogEndpoint = new CatalogEndpointImpl();

    catalogEndpoint.setBindingType(DEFAULT_BINDING_TYPE);
    catalogEndpoint.setDescription(DEFAULT_DESCRIPTION);
    catalogEndpoint.setId(DEFAULT_ID);
    catalogEndpoint.setName(DEFAULT_NAME);
    catalogEndpoint.setUrl(DEFAULT_URL);
    catalogEndpoint.setUrlBindingName(DEFAULT_URL_BINDING_NAME);
    catalogEndpoint.setVersion(DEFAULT_VERSION);

    return catalogEndpoint;
  }

  private Map<String, String> getTestEndpointPropertiesWithDefaults() {
    Map<String, String> endpointPropertiesWithDefaults = new HashMap<>();

    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.BINDING_TYPE_KEY, DEFAULT_BINDING_TYPE);
    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.DESCRIPTION_KEY, DEFAULT_DESCRIPTION);
    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.ID_KEY, DEFAULT_ID);
    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.NAME_KEY, DEFAULT_NAME);
    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.URL_KEY, DEFAULT_URL);
    endpointPropertiesWithDefaults.put(
        CatalogEndpointImpl.URL_BINDING_NAME_KEY, DEFAULT_URL_BINDING_NAME);
    endpointPropertiesWithDefaults.put(CatalogEndpointImpl.VERSION_KEY, DEFAULT_VERSION);

    return endpointPropertiesWithDefaults;
  }

  private void assertEndpointPropertiesContainDefaults(Map<String, String> endpointProperties) {
    assertEquals(
        DEFAULT_BINDING_TYPE,
        endpointProperties.get(CatalogEndpointImpl.BINDING_TYPE_KEY),
        "Binding Type");
    assertEquals(
        DEFAULT_DESCRIPTION,
        endpointProperties.get(CatalogEndpointImpl.DESCRIPTION_KEY),
        "Description");
    assertEquals(DEFAULT_ID, endpointProperties.get(CatalogEndpointImpl.ID_KEY), "Id");
    assertEquals(DEFAULT_NAME, endpointProperties.get(CatalogEndpointImpl.NAME_KEY), "Name");
    assertEquals(DEFAULT_URL, endpointProperties.get(CatalogEndpointImpl.URL_KEY), "Url");
    assertEquals(
        DEFAULT_URL_BINDING_NAME,
        endpointProperties.get(CatalogEndpointImpl.URL_BINDING_NAME_KEY),
        "Url Binding Name");
    assertEquals(
        DEFAULT_VERSION, endpointProperties.get(CatalogEndpointImpl.VERSION_KEY), "Version");
  }
}
