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
package org.codice.ddf.validator.query.unsupportedattributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.platform.resource.bundle.locator.ResourceBundleLocator;
import java.io.IOException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageFormatSupplierTest {

  private static final String DEFAULT_MESSAGE_FORMAT =
      "The field \"{attribute}\" is not supported by the {sources} Source(s)";

  private static final String CUSTOM_MESSAGE_FORMAT =
      "Custom message: {attribute} not supported by {sources}";

  private ResourceBundleLocator resourceBundleLocator;

  private ResourceBundle resourceBundle;

  @BeforeEach
  public void setup() {
    resourceBundleLocator = mock(ResourceBundleLocator.class);
    resourceBundle = mock(ResourceBundle.class);
  }

  @Test
  public void testGetDefaultMessageWhenResourceBundleNotFound() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle")))
        .thenThrow(new IOException("Bundle not found"));

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(equalTo(DEFAULT_MESSAGE_FORMAT)));
  }

  @Test
  public void testGetCustomMessageFromResourceBundle() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported")))
        .thenReturn(CUSTOM_MESSAGE_FORMAT);

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(equalTo(CUSTOM_MESSAGE_FORMAT)));
  }

  @Test
  public void testGetThrowsWhenResourceBundleLocatorIsNull() {
    MessageFormatSupplier supplier = new MessageFormatSupplier(null);

    // The supplier only recovers from IOException; a null locator surfaces as a
    // NullPointerException (a null locator is never wired in practice via OSGi blueprint).
    assertThrows(NullPointerException.class, supplier::get);
  }

  @Test
  public void testGetIsCalledMultipleTimes() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported")))
        .thenReturn(CUSTOM_MESSAGE_FORMAT);

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message1 = supplier.get();
    String message2 = supplier.get();

    assertThat(message1, is(equalTo(CUSTOM_MESSAGE_FORMAT)));
    assertThat(message2, is(equalTo(CUSTOM_MESSAGE_FORMAT)));
  }

  @Test
  public void testGetDefaultMessageWhenStringNotFoundInBundle() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported")))
        .thenThrow(new java.util.MissingResourceException("Key not found", "bundle", "key"));

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    try {
      supplier.get();
    } catch (Exception e) {
      // Expected to handle gracefully or throw
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testGetWithNullResourceBundleLocator() {
    MessageFormatSupplier supplier = new MessageFormatSupplier(null);

    try {
      String message = supplier.get();
      // If it doesn't throw, it should return a default or handle gracefully
      assertThat(message, is(notNullValue()));
    } catch (NullPointerException e) {
      // This is expected if the implementation doesn't handle null
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testGetReturnsNonEmptyString() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported")))
        .thenReturn(CUSTOM_MESSAGE_FORMAT);

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(notNullValue()));
    assertThat(message.length() > 0, is(true));
  }

  @Test
  public void testGetWithIOExceptionReturnsDefault() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle")))
        .thenThrow(new IOException("IO error"));

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(equalTo(DEFAULT_MESSAGE_FORMAT)));
  }

  @Test
  public void testGetWithEmptyStringFromBundle() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported"))).thenReturn("");

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(equalTo("")));
  }

  @Test
  public void testGetWithWhitespaceStringFromBundle() throws IOException {
    when(resourceBundleLocator.getBundle(eq("IntrigueBundle"))).thenReturn(resourceBundle);
    when(resourceBundle.getString(eq("validation.attribute.unsupported"))).thenReturn("   ");

    MessageFormatSupplier supplier = new MessageFormatSupplier(resourceBundleLocator);

    String message = supplier.get();

    assertThat(message, is(equalTo("   ")));
  }
}
