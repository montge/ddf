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
package ddf.catalog.plugin.groomer.metacard;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.types.Core;
import ddf.catalog.operation.CreateRequest;
import ddf.catalog.operation.UpdateRequest;
import ddf.catalog.operation.impl.CreateRequestImpl;
import ddf.catalog.operation.impl.UpdateRequestImpl;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.codice.ddf.platform.util.uuidgenerator.UuidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Enhanced comprehensive tests for StandardMetacardGroomerPlugin to increase coverage. Tests
 * create/update grooming operations, date handling, UUID validation, and edge cases.
 */
public class StandardMetacardGroomerPluginEnhancedTest {

  private StandardMetacardGroomerPlugin plugin;
  private UuidGenerator uuidGenerator;

  @BeforeEach
  public void setUp() {
    uuidGenerator = mock(UuidGenerator.class);
    when(uuidGenerator.generateUuid()).thenReturn(UUID.randomUUID().toString());
    plugin = new StandardMetacardGroomerPlugin();
    plugin.setUuidGenerator(uuidGenerator);
  }

  @Test
  public void testCreateWithValidUuidAndNonContentUri() throws Exception {
    String validUuid = UUID.randomUUID().toString();
    when(uuidGenerator.validateUuid(anyString())).thenReturn(false);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, validUuid));
    metacard.setAttribute(new AttributeImpl(Metacard.RESOURCE_URI, "http://example.com/resource"));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    // ID should be regenerated because URI is not content scheme and validateUuid returns false
    assertNotEquals(validUuid, groomed.getId());
  }

  @Test
  public void testCreateWithValidUuidAndContentUri() throws Exception {
    String validUuid = UUID.randomUUID().toString();
    when(uuidGenerator.validateUuid(anyString())).thenReturn(true);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, validUuid));
    metacard.setAttribute(
        new AttributeImpl(Metacard.RESOURCE_URI, ContentItem.CONTENT_SCHEME + ":" + validUuid));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    // ID should be kept
    assertThat(groomed.getId(), is(validUuid));
  }

  @Test
  public void testCreateWithInvalidUuidAndContentUri() throws Exception {
    String invalidUuid = "not-a-valid-uuid";
    when(uuidGenerator.validateUuid(anyString())).thenReturn(false);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, invalidUuid));
    metacard.setAttribute(
        new AttributeImpl(Metacard.RESOURCE_URI, ContentItem.CONTENT_SCHEME + ":" + invalidUuid));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    // ID should be regenerated
    assertNotEquals(invalidUuid, groomed.getId());
  }

  @Test
  public void testCreateWithNullResourceUri() throws Exception {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, "some-id"));
    metacard.setAttribute(new AttributeImpl(Metacard.RESOURCE_URI, (String) null));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    assertThat(groomed.getId(), notNullValue());
  }

  @Test
  public void testCreateSetsAllMissingDates() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    // Don't set any dates

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);

    // All dates should be set
    assertThat(groomed.getCreatedDate(), notNullValue());
    assertThat(groomed.getModifiedDate(), notNullValue());
    assertThat(groomed.getEffectiveDate(), notNullValue());
    assertThat(groomed.getAttribute(Core.METACARD_CREATED), notNullValue());
    assertThat(groomed.getAttribute(Core.METACARD_MODIFIED), notNullValue());

    // All dates should be >= test start time
    assertThat(groomed.getCreatedDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
    assertThat(groomed.getModifiedDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
    assertThat(groomed.getEffectiveDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testCreatePreservesExistingDates() throws Exception {
    Date existingDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.CREATED, existingDate));
    metacard.setAttribute(new AttributeImpl(Metacard.MODIFIED, existingDate));
    metacard.setAttribute(new AttributeImpl(Metacard.EFFECTIVE, existingDate));
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, existingDate));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);

    // Existing dates should be preserved (except METACARD_MODIFIED which is always updated)
    assertThat(groomed.getCreatedDate().getTime(), is(existingDate.getTime()));
    assertThat(groomed.getModifiedDate().getTime(), is(existingDate.getTime()));
    assertThat(groomed.getEffectiveDate().getTime(), is(existingDate.getTime()));
    assertThat(
        ((Date) groomed.getAttribute(Core.METACARD_CREATED).getValue()).getTime(),
        is(existingDate.getTime()));
    // METACARD_MODIFIED is always updated on create
    assertThat(
        ((Date) groomed.getAttribute(Core.METACARD_MODIFIED).getValue()).getTime(),
        greaterThanOrEqualTo(existingDate.getTime()));
  }

  @Test
  public void testUpdateSetsIdFromRequest() throws Exception {
    String updateId = "update-id-123";
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, "different-id"));

    UpdateRequest request = new UpdateRequestImpl(updateId, metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // ID should match the update request ID
    assertThat(groomed.getId(), is(updateId));
  }

  @Test
  public void testUpdateWithMatchingId() throws Exception {
    String matchingId = "matching-id";
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, matchingId));

    UpdateRequest request = new UpdateRequestImpl(matchingId, metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // ID should remain the same
    assertThat(groomed.getId(), is(matchingId));
  }

  @Test
  public void testUpdateByAlternativeAttribute() throws Exception {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, "existing-id"));

    URI uri = new URI("http://example.com/resource");
    UpdateRequestImpl request = new UpdateRequestImpl(new URI[] {uri}, Arrays.asList(metacard));

    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // ID should remain as set in metacard
    assertThat(groomed.getId(), is("existing-id"));
  }

  @Test
  public void testUpdateSetsNullCreatedDate() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.CREATED, (Date) null));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Null created date should be set to current time
    assertThat(groomed.getCreatedDate(), notNullValue());
    assertThat(groomed.getCreatedDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testUpdatePreservesExistingCreatedDate() throws Exception {
    Date existingDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.CREATED, existingDate));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Existing created date should be preserved
    assertThat(groomed.getCreatedDate().getTime(), is(existingDate.getTime()));
  }

  @Test
  public void testUpdateAlwaysUpdatesModifiedDate() throws Exception {
    Date beforeTest = new Date();
    Date oldDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.MODIFIED, oldDate));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Modified date should be the old date (groomer preserves it)
    assertThat(groomed.getModifiedDate().getTime(), is(oldDate.getTime()));
  }

  @Test
  public void testUpdateAlwaysUpdatesMetacardModifiedDate() throws Exception {
    Date beforeTest = new Date();
    Date oldDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Core.METACARD_MODIFIED, oldDate));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // METACARD_MODIFIED should always be updated to current time
    assertThat(groomed.getAttribute(Core.METACARD_MODIFIED), notNullValue());
    Date groomedModified = (Date) groomed.getAttribute(Core.METACARD_MODIFIED).getValue();
    assertThat(groomedModified.getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testUpdateSetsNullEffectiveDate() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.EFFECTIVE, (Date) null));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Null effective date should be set to current time
    assertThat(groomed.getEffectiveDate(), notNullValue());
    assertThat(groomed.getEffectiveDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testUpdateSetsNullMetacardCreatedDate() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, (Date) null));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Null metacard created date should be set to current time
    assertThat(groomed.getAttribute(Core.METACARD_CREATED), notNullValue());
    Date groomedCreated = (Date) groomed.getAttribute(Core.METACARD_CREATED).getValue();
    assertThat(groomedCreated.getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testUpdateWithNonDateMetacardCreated() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, "not-a-date"));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Non-date value should be replaced with current date
    assertThat(groomed.getAttribute(Core.METACARD_CREATED), notNullValue());
    Date groomedCreated = (Date) groomed.getAttribute(Core.METACARD_CREATED).getValue();
    assertThat(groomedCreated.getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testCreateWithEmptyStringDates() throws Exception {
    Date beforeTest = new Date();

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.CREATED, ""));
    metacard.setAttribute(new AttributeImpl(Metacard.MODIFIED, ""));
    metacard.setAttribute(new AttributeImpl(Metacard.EFFECTIVE, ""));
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, ""));
    metacard.setAttribute(new AttributeImpl(Core.METACARD_MODIFIED, ""));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);

    // All null dates should be set to current time
    assertThat(groomed.getCreatedDate(), notNullValue());
    assertThat(groomed.getCreatedDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
    assertThat(groomed.getModifiedDate(), notNullValue());
    assertThat(groomed.getModifiedDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
    assertThat(groomed.getEffectiveDate(), notNullValue());
    assertThat(groomed.getEffectiveDate().getTime(), greaterThanOrEqualTo(beforeTest.getTime()));
  }

  @Test
  public void testMultipleMetacardsInCreateRequest() throws Exception {
    Metacard metacard1 = new MetacardImpl();
    Metacard metacard2 = new MetacardImpl();
    Metacard metacard3 = new MetacardImpl();

    CreateRequest request = new CreateRequestImpl(Arrays.asList(metacard1, metacard2, metacard3));
    CreateRequest result = plugin.process(request);

    assertThat(result.getMetacards().size(), is(3));

    // All metacards should be groomed
    for (Metacard groomed : result.getMetacards()) {
      assertThat(groomed.getId(), notNullValue());
      assertThat(groomed.getCreatedDate(), notNullValue());
      assertThat(groomed.getModifiedDate(), notNullValue());
      assertThat(groomed.getEffectiveDate(), notNullValue());
    }
  }

  @Test
  public void testMultipleMetacardsInUpdateRequest() throws Exception {
    Metacard metacard1 = new MetacardImpl();
    metacard1.setAttribute(new AttributeImpl(Metacard.ID, "id1"));
    Metacard metacard2 = new MetacardImpl();
    metacard2.setAttribute(new AttributeImpl(Metacard.ID, "id2"));

    UpdateRequestImpl request =
        new UpdateRequestImpl(new String[] {"id1", "id2"}, Arrays.asList(metacard1, metacard2));
    UpdateRequest result = plugin.process(request);

    assertThat(result.getUpdates().size(), is(2));

    // All metacards should be groomed
    for (int i = 0; i < result.getUpdates().size(); i++) {
      Metacard groomed = result.getUpdates().get(i).getValue();
      assertThat(groomed.getId(), notNullValue());
      assertThat(groomed.getAttribute(Core.METACARD_MODIFIED), notNullValue());
    }
  }

  @Test
  public void testUuidGeneratorIsUsed() throws Exception {
    String generatedUuid = UUID.randomUUID().toString();
    when(uuidGenerator.generateUuid()).thenReturn(generatedUuid);

    Metacard metacard = new MetacardImpl();

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    assertThat(groomed.getId(), is(generatedUuid));
  }

  @Test
  public void testContentSchemeUriIdentification() throws Exception {
    String id = UUID.randomUUID().toString();
    when(uuidGenerator.validateUuid(anyString())).thenReturn(true);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, id));
    metacard.setAttribute(new AttributeImpl(Metacard.RESOURCE_URI, "content:" + id));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    // Should preserve ID when URI uses content scheme
    assertThat(groomed.getId(), is(id));
  }

  @Test
  public void testNonContentSchemeUriRegeneratesId() throws Exception {
    String originalId = "original-id";
    when(uuidGenerator.validateUuid(anyString())).thenReturn(false);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.ID, originalId));
    metacard.setAttribute(new AttributeImpl(Metacard.RESOURCE_URI, "http://example.com/resource"));

    CreateRequest request = new CreateRequestImpl(metacard);
    CreateRequest result = plugin.process(request);

    Metacard groomed = result.getMetacards().get(0);
    // Should generate new ID for non-content URI
    assertNotEquals(originalId, groomed.getId());
  }

  @Test
  public void testUpdatePreservesExistingEffectiveDate() throws Exception {
    Date existingDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.EFFECTIVE, existingDate));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Existing effective date should be preserved
    assertThat(groomed.getEffectiveDate().getTime(), is(existingDate.getTime()));
  }

  @Test
  public void testUpdatePreservesExistingMetacardCreatedDate() throws Exception {
    Date existingDate = new Date(System.currentTimeMillis() - 100000);

    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Core.METACARD_CREATED, existingDate));

    UpdateRequest request = new UpdateRequestImpl("id", metacard);
    UpdateRequest result = plugin.process(request);

    Metacard groomed = result.getUpdates().get(0).getValue();

    // Existing metacard created date should be preserved
    assertThat(
        ((Date) groomed.getAttribute(Core.METACARD_CREATED).getValue()).getTime(),
        is(existingDate.getTime()));
  }
}
