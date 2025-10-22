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
package ddf.catalog.data.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.types.experimental.Extracted;
import org.junit.Test;

/**
 * Test class for data type constant interfaces to ensure all constants are properly defined and
 * accessible
 */
public class DataTypesConstantsTest {

  /** Validates that all Core interface constants are correctly defined */
  @Test
  public void testCoreConstants() {
    assertEquals("checksum", Core.CHECKSUM);
    assertEquals("checksum-algorithm", Core.CHECKSUM_ALGORITHM);
    assertEquals("created", Core.CREATED);
    assertEquals("description", Core.DESCRIPTION);
    assertEquals("expiration", Core.EXPIRATION);
    assertEquals("id", Core.ID);
    assertEquals("language", Core.LANGUAGE);
    assertEquals("location", Core.LOCATION);
    assertEquals("metadata", Core.METADATA);
    assertEquals("modified", Core.MODIFIED);
    assertEquals(Metacard.DERIVED_RESOURCE_DOWNLOAD_URL, Core.DERIVED_RESOURCE_DOWNLOAD_URL);
    assertEquals(Metacard.DERIVED_RESOURCE_URI, Core.DERIVED_RESOURCE_URI);
    assertEquals("resource-download-url", Core.RESOURCE_DOWNLOAD_URL);
    assertEquals("resource-size", Core.RESOURCE_SIZE);
    assertEquals("resource-uri", Core.RESOURCE_URI);
    assertEquals("source-id", Core.SOURCE_ID);
    assertEquals("thumbnail", Core.THUMBNAIL);
    assertEquals("title", Core.TITLE);
    assertEquals("datatype", Core.DATATYPE);
    assertEquals("metacard.created", Core.METACARD_CREATED);
    assertEquals("metacard.modified", Core.METACARD_MODIFIED);
    assertEquals("metacard.owner", Core.METACARD_OWNER);
    assertEquals(Metacard.TAGS, Core.METACARD_TAGS);
  }

  /** Validates that all Contact interface constants are correctly defined */
  @Test
  public void testContactConstants() {
    assertEquals("contact.contributor-address", Contact.CONTRIBUTOR_ADDRESS);
    assertEquals("contact.contributor-email", Contact.CONTRIBUTOR_EMAIL);
    assertEquals("contact.contributor-name", Contact.CONTRIBUTOR_NAME);
    assertEquals("contact.contributor-phone", Contact.CONTRIBUTOR_PHONE);

    assertEquals("contact.creator-address", Contact.CREATOR_ADDRESS);
    assertEquals("contact.creator-email", Contact.CREATOR_EMAIL);
    assertEquals("contact.creator-name", Contact.CREATOR_NAME);
    assertEquals("contact.creator-phone", Contact.CREATOR_PHONE);

    assertEquals("contact.point-of-contact-address", Contact.POINT_OF_CONTACT_ADDRESS);
    assertEquals("contact.point-of-contact-email", Contact.POINT_OF_CONTACT_EMAIL);
    assertEquals("contact.point-of-contact-name", Contact.POINT_OF_CONTACT_NAME);
    assertEquals("contact.point-of-contact-phone", Contact.POINT_OF_CONTACT_PHONE);

    assertEquals("contact.publisher-address", Contact.PUBLISHER_ADDRESS);
    assertEquals("contact.publisher-email", Contact.PUBLISHER_EMAIL);
    assertEquals("contact.publisher-name", Contact.PUBLISHER_NAME);
    assertEquals("contact.publisher-phone", Contact.PUBLISHER_PHONE);
  }

  /** Validates that all Associations interface constants are correctly defined */
  @Test
  public void testAssociationsConstants() {
    assertEquals("associations.external", Associations.EXTERNAL);
    assertEquals(Metacard.DERIVED, Associations.DERIVED);
    assertEquals(Metacard.RELATED, Associations.RELATED);
  }

  /** Validates that all DateTime interface constants are correctly defined */
  @Test
  public void testDateTimeConstants() {
    assertEquals("datetime.start", DateTime.START);
    assertEquals("datetime.end", DateTime.END);
    assertEquals("datetime.name", DateTime.NAME);
  }

  /** Validates that all Location interface constants are correctly defined */
  @Test
  public void testLocationConstants() {
    assertEquals("location.altitude-meters", Location.ALTITUDE);
    assertEquals("location.country-code", Location.COUNTRY_CODE);
    assertEquals("location.crs-code", Location.COORDINATE_REFERENCE_SYSTEM_CODE);
    assertEquals("location.crs-name", Location.COORDINATE_REFERENCE_SYSTEM_NAME);
  }

  /** Validates that all Media interface constants are correctly defined */
  @Test
  public void testMediaConstants() {
    assertEquals("media.format", Media.FORMAT);
    assertEquals("media.format-version", Media.FORMAT_VERSION);
    assertEquals("media.bit-rate", Media.BITS_PER_SECOND);
    assertEquals("media.bits-per-sample", Media.BITS_PER_SAMPLE);
    assertEquals("media.compression", Media.COMPRESSION);
    assertEquals("media.encoding", Media.ENCODING);
    assertEquals("media.frame-center", Media.FRAME_CENTER);
    assertEquals("media.frame-rate", Media.FRAMES_PER_SECOND);
    assertEquals("media.height-pixels", Media.HEIGHT);
    assertEquals("media.number-of-bands", Media.NUMBER_OF_BANDS);
    assertEquals("media.scanning-mode", Media.SCANNING_MODE);
    assertEquals("media.type", Media.TYPE);
    assertEquals("media.width-pixels", Media.WIDTH);
    assertEquals("media.duration", Media.DURATION);
  }

  /** Validates that all Security interface constants are correctly defined */
  @Test
  public void testSecurityConstants() {
    assertEquals("security.access-administrators", Security.ACCESS_ADMINISTRATORS);
    assertEquals("security.access-groups", Security.ACCESS_GROUPS);
    assertEquals("security.access-groups-read", Security.ACCESS_GROUPS_READ);
    assertEquals("security.access-individuals", Security.ACCESS_INDIVIDUALS);
    assertEquals("security.access-individuals-read", Security.ACCESS_INDIVIDUALS_READ);
  }

  /** Validates that all Topic interface constants are correctly defined */
  @Test
  public void testTopicConstants() {
    assertEquals("topic.category", Topic.CATEGORY);
    assertEquals("topic.keyword", Topic.KEYWORD);
    assertEquals("topic.vocabulary", Topic.VOCABULARY);
  }

  /** Validates that all Validation interface constants are correctly defined */
  @Test
  public void testValidationConstants() {
    assertEquals("validation-errors", Validation.VALIDATION_ERRORS);
    assertEquals("validation-warnings", Validation.VALIDATION_WARNINGS);
    assertEquals("failed-validators-errors", Validation.FAILED_VALIDATORS_ERRORS);
    assertEquals("failed-validators-warnings", Validation.FAILED_VALIDATORS_WARNINGS);
  }

  /** Validates that all Version interface constants are correctly defined */
  @Test
  public void testVersionConstants() {
    assertEquals("version.action", Version.ACTION);
    assertEquals("version.edited-by", Version.EDITED_BY);
    assertEquals("version.id", Version.ID);
    assertEquals("version.tags", Version.TAGS);
    assertEquals("version.types", Version.TYPE);
    assertEquals("version.type-binary", Version.TYPE_BINARY);
    assertEquals("version.versioned-by", Version.VERSIONED_BY);
  }

  /** Validates that Extracted interface constant is correctly defined */
  @Test
  public void testExtractedConstants() {
    assertEquals("ext.extracted.text", Extracted.EXTRACTED_TEXT);
  }

  /** Validates that all constants are not null */
  @Test
  public void testConstantsNotNull() {
    // Core
    assertNotNull(Core.CHECKSUM);
    assertNotNull(Core.ID);
    assertNotNull(Core.TITLE);

    // Contact
    assertNotNull(Contact.CREATOR_NAME);
    assertNotNull(Contact.PUBLISHER_NAME);

    // Associations
    assertNotNull(Associations.EXTERNAL);
    assertNotNull(Associations.RELATED);

    // DateTime
    assertNotNull(DateTime.START);
    assertNotNull(DateTime.END);

    // Location
    assertNotNull(Location.COUNTRY_CODE);

    // Media
    assertNotNull(Media.FORMAT);
    assertNotNull(Media.TYPE);

    // Security
    assertNotNull(Security.ACCESS_GROUPS);

    // Topic
    assertNotNull(Topic.KEYWORD);

    // Validation
    assertNotNull(Validation.VALIDATION_ERRORS);

    // Version
    assertNotNull(Version.ACTION);
    assertNotNull(Version.ID);

    // Extracted
    assertNotNull(Extracted.EXTRACTED_TEXT);
  }
}
