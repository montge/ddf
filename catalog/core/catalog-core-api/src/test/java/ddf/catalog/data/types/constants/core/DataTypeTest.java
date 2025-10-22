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
package ddf.catalog.data.types.constants.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/** Test class for {@link DataType} enum to ensure all values are properly defined and accessible */
public class DataTypeTest {

  /** Validates that all DataType enum values are correctly defined and accessible */
  @Test
  public void testAllDataTypeValues() {
    DataType[] allTypes = DataType.values();
    assertEquals(12, allTypes.length);

    assertNotNull(DataType.COLLECTION);
    assertNotNull(DataType.DATASET);
    assertNotNull(DataType.EVENT);
    assertNotNull(DataType.IMAGE);
    assertNotNull(DataType.INTERACTIVE_RESOURCE);
    assertNotNull(DataType.MOVING_IMAGE);
    assertNotNull(DataType.PHYSICAL_OBJECT);
    assertNotNull(DataType.SERVICE);
    assertNotNull(DataType.SOFTWARE);
    assertNotNull(DataType.SOUND);
    assertNotNull(DataType.STILL_IMAGE);
    assertNotNull(DataType.TEXT);
  }

  /** Validates that DataType.toString() returns the expected human-readable string values */
  @Test
  public void testDataTypeToString() {
    assertEquals("Collection", DataType.COLLECTION.toString());
    assertEquals("Dataset", DataType.DATASET.toString());
    assertEquals("Event", DataType.EVENT.toString());
    assertEquals("Image", DataType.IMAGE.toString());
    assertEquals("Interactive Resource", DataType.INTERACTIVE_RESOURCE.toString());
    assertEquals("Moving Image", DataType.MOVING_IMAGE.toString());
    assertEquals("Physical Object", DataType.PHYSICAL_OBJECT.toString());
    assertEquals("Service", DataType.SERVICE.toString());
    assertEquals("Software", DataType.SOFTWARE.toString());
    assertEquals("Sound", DataType.SOUND.toString());
    assertEquals("Still Image", DataType.STILL_IMAGE.toString());
    assertEquals("Text", DataType.TEXT.toString());
  }

  /** Validates that DataType.fromValue() correctly converts string values to enum constants */
  @Test
  public void testDataTypeFromValue() {
    assertEquals(DataType.COLLECTION, DataType.fromValue("Collection"));
    assertEquals(DataType.DATASET, DataType.fromValue("Dataset"));
    assertEquals(DataType.EVENT, DataType.fromValue("Event"));
    assertEquals(DataType.IMAGE, DataType.fromValue("Image"));
    assertEquals(DataType.INTERACTIVE_RESOURCE, DataType.fromValue("Interactive Resource"));
    assertEquals(DataType.MOVING_IMAGE, DataType.fromValue("Moving Image"));
    assertEquals(DataType.PHYSICAL_OBJECT, DataType.fromValue("Physical Object"));
    assertEquals(DataType.SERVICE, DataType.fromValue("Service"));
    assertEquals(DataType.SOFTWARE, DataType.fromValue("Software"));
    assertEquals(DataType.SOUND, DataType.fromValue("Sound"));
    assertEquals(DataType.STILL_IMAGE, DataType.fromValue("Still Image"));
    assertEquals(DataType.TEXT, DataType.fromValue("Text"));
  }

  /**
   * Validates that DataType.fromValue() throws IllegalArgumentException for invalid string values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDataTypeFromValueInvalid() {
    DataType.fromValue("Invalid Type");
  }

  /** Validates that DataType.fromValue() throws IllegalArgumentException for null values */
  @Test(expected = IllegalArgumentException.class)
  public void testDataTypeFromValueNull() {
    DataType.fromValue(null);
  }

  /** Validates that DataType.fromValue() throws IllegalArgumentException for empty strings */
  @Test(expected = IllegalArgumentException.class)
  public void testDataTypeFromValueEmptyString() {
    DataType.fromValue("");
  }

  /** Validates that DataType.fromValue() is case-sensitive */
  @Test(expected = IllegalArgumentException.class)
  public void testDataTypeFromValueCaseSensitive() {
    DataType.fromValue("collection"); // lowercase should fail
  }

  /** Validates that DataType.valueOf() works correctly for enum constant names */
  @Test
  public void testDataTypeValueOf() {
    assertEquals(DataType.COLLECTION, DataType.valueOf("COLLECTION"));
    assertEquals(DataType.DATASET, DataType.valueOf("DATASET"));
    assertEquals(DataType.EVENT, DataType.valueOf("EVENT"));
    assertEquals(DataType.IMAGE, DataType.valueOf("IMAGE"));
    assertEquals(DataType.INTERACTIVE_RESOURCE, DataType.valueOf("INTERACTIVE_RESOURCE"));
    assertEquals(DataType.MOVING_IMAGE, DataType.valueOf("MOVING_IMAGE"));
    assertEquals(DataType.PHYSICAL_OBJECT, DataType.valueOf("PHYSICAL_OBJECT"));
    assertEquals(DataType.SERVICE, DataType.valueOf("SERVICE"));
    assertEquals(DataType.SOFTWARE, DataType.valueOf("SOFTWARE"));
    assertEquals(DataType.SOUND, DataType.valueOf("SOUND"));
    assertEquals(DataType.STILL_IMAGE, DataType.valueOf("STILL_IMAGE"));
    assertEquals(DataType.TEXT, DataType.valueOf("TEXT"));
  }
}
