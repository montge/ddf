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
package ddf.catalog.operation.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SourceInfoRequestImplTest {

  @Test
  void testSourceInfoRequestLocalWithIncludeContentTypesTrue() {
    SourceInfoRequestLocal request = new SourceInfoRequestLocal(true);

    assertThat(request.includeContentTypes(), is(true));
  }

  @Test
  void testSourceInfoRequestLocalWithIncludeContentTypesFalse() {
    SourceInfoRequestLocal request = new SourceInfoRequestLocal(false);

    assertThat(request.includeContentTypes(), is(false));
  }

  @Test
  void testSourceInfoRequestLocalGetSourceIdsReturnsNull() {
    SourceInfoRequestLocal request = new SourceInfoRequestLocal(true);

    assertThat(request.getSourceIds(), is(nullValue()));
  }

  @Test
  void testSourceInfoRequestLocalIsEnterpriseReturnsFalse() {
    SourceInfoRequestLocal request = new SourceInfoRequestLocal(true);

    assertThat(request.isEnterprise(), is(false));
  }

  @Test
  void testSourceInfoRequestSourcesWithIncludeContentTypesTrue() {
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");

    SourceInfoRequestSources request = new SourceInfoRequestSources(true, sourceIds);

    assertThat(request.includeContentTypes(), is(true));
  }

  @Test
  void testSourceInfoRequestSourcesWithIncludeContentTypesFalse() {
    Set<String> sourceIds = new HashSet<>();

    SourceInfoRequestSources request = new SourceInfoRequestSources(false, sourceIds);

    assertThat(request.includeContentTypes(), is(false));
  }

  @Test
  void testSourceInfoRequestSourcesGetSourceIds() {
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");
    sourceIds.add("source2");

    SourceInfoRequestSources request = new SourceInfoRequestSources(true, sourceIds);

    assertThat(request.getSourceIds(), is(sameInstance(sourceIds)));
    assertThat(request.getSourceIds(), hasSize(2));
    assertThat(request.getSourceIds(), containsInAnyOrder("source1", "source2"));
  }

  @Test
  void testSourceInfoRequestSourcesWithNullSourceIds() {
    SourceInfoRequestSources request = new SourceInfoRequestSources(true, null);

    assertThat(request.getSourceIds(), is(nullValue()));
  }

  @Test
  void testSourceInfoRequestSourcesWithEmptySourceIds() {
    Set<String> sourceIds = new HashSet<>();

    SourceInfoRequestSources request = new SourceInfoRequestSources(true, sourceIds);

    assertThat(request.getSourceIds(), hasSize(0));
  }

  @Test
  void testSourceInfoRequestSourcesIsEnterpriseReturnsFalse() {
    Set<String> sourceIds = new HashSet<>();
    sourceIds.add("source1");

    SourceInfoRequestSources request = new SourceInfoRequestSources(true, sourceIds);

    assertThat(request.isEnterprise(), is(false));
  }

  @Test
  void testSourceInfoRequestEnterpriseWithIncludeContentTypesTrue() {
    SourceInfoRequestEnterprise request = new SourceInfoRequestEnterprise(true);

    assertThat(request.includeContentTypes(), is(true));
  }

  @Test
  void testSourceInfoRequestEnterpriseWithIncludeContentTypesFalse() {
    SourceInfoRequestEnterprise request = new SourceInfoRequestEnterprise(false);

    assertThat(request.includeContentTypes(), is(false));
  }

  @Test
  void testSourceInfoRequestEnterpriseIsEnterpriseReturnsTrue() {
    SourceInfoRequestEnterprise request = new SourceInfoRequestEnterprise(true);

    assertThat(request.isEnterprise(), is(true));
  }

  @Test
  void testSourceInfoRequestEnterpriseGetSourceIdsReturnsNull() {
    SourceInfoRequestEnterprise request = new SourceInfoRequestEnterprise(true);

    assertThat(request.getSourceIds(), is(nullValue()));
  }
}
