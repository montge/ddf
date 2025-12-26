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
package ddf.catalog.plugin.facetattributeaccess;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class FacetWhitelistConfigurationTest {

  @Test
  void testDefaultWhitelistIsEmpty() {
    FacetWhitelistConfiguration config = new FacetWhitelistConfiguration();

    assertThat(config.getFacetAttributeWhitelist(), is(empty()));
  }

  @Test
  void testSetAndGetFacetAttributeWhitelist() {
    FacetWhitelistConfiguration config = new FacetWhitelistConfiguration();
    List<String> whitelist = Arrays.asList("attr1", "attr2", "attr3");

    config.setFacetAttributeWhitelist(whitelist);

    assertThat(config.getFacetAttributeWhitelist(), is(sameInstance(whitelist)));
  }

  @Test
  void testSetWhitelistWithSingleAttribute() {
    FacetWhitelistConfiguration config = new FacetWhitelistConfiguration();
    List<String> whitelist = Collections.singletonList("singleAttr");

    config.setFacetAttributeWhitelist(whitelist);

    assertThat(config.getFacetAttributeWhitelist(), hasSize(1));
    assertThat(config.getFacetAttributeWhitelist(), contains("singleAttr"));
  }

  @Test
  void testSetWhitelistWithEmptyList() {
    FacetWhitelistConfiguration config = new FacetWhitelistConfiguration();
    config.setFacetAttributeWhitelist(Arrays.asList("attr1"));

    config.setFacetAttributeWhitelist(Collections.emptyList());

    assertThat(config.getFacetAttributeWhitelist(), is(empty()));
  }

  @Test
  void testSetWhitelistOverwritesPrevious() {
    FacetWhitelistConfiguration config = new FacetWhitelistConfiguration();
    config.setFacetAttributeWhitelist(Arrays.asList("old1", "old2"));

    List<String> newWhitelist = Arrays.asList("new1", "new2", "new3");
    config.setFacetAttributeWhitelist(newWhitelist);

    assertThat(config.getFacetAttributeWhitelist(), hasSize(3));
    assertThat(config.getFacetAttributeWhitelist(), contains("new1", "new2", "new3"));
  }
}
