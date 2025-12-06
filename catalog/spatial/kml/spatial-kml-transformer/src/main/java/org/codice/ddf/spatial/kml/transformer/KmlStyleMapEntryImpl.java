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
package org.codice.ddf.spatial.kml.transformer;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Implentation of {@link KmlStyleMapEntry}.
 *
 * @author Keith C Wire
 */
public class KmlStyleMapEntryImpl implements KmlStyleMapEntry {

  private static final Logger LOGGER = LoggerFactory.getLogger(KmlStyleMapEntryImpl.class);

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("GMT"));

  private String attributeName;

  private String attributeValue;

  private String styleUrl;

  public KmlStyleMapEntryImpl() {}

  // For Unit Testing
  public KmlStyleMapEntryImpl(String name, String value, String url) {
    this.attributeName = name;
    this.attributeValue = value;
    this.styleUrl = url;
  }

  public void init() {
    LOGGER.debug(
        "Creating {} with {}, {}, {}",
        KmlStyleMapEntryImpl.class.getName(),
        attributeName,
        attributeValue,
        styleUrl);
  }

  @Override
  public boolean metacardMatch(Metacard metacard) {
    if (Metacard.SOURCE_ID.equals(attributeName)) {
      return metacard.getSourceId().equals(attributeValue);
    } else {
      Attribute attribute = metacard.getAttribute(attributeName);
      if (attribute != null) {
        if (attributeValueMatch(
            attribute, metacard.getMetacardType().getAttributeDescriptor(attributeName))) {
          LOGGER.debug(
              "Found match for Attribute: {} Value: {} URL: {}",
              attributeName,
              attributeValue,
              styleUrl);
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String getAttributeName() {
    return this.attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  @Override
  public String getAttributeValue() {
    return this.attributeValue;
  }

  public void setAttributeValue(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  @Override
  public String getStyleUrl() {
    return this.styleUrl;
  }

  public void setStyleUrl(String styleUrl) {
    this.styleUrl = styleUrl;
  }

  private boolean attributeValueMatch(Attribute attribute, AttributeDescriptor descriptor) {

    switch (descriptor.getType().getAttributeFormat()) {
      case STRING:
      case XML:
      case GEOMETRY:
        return attributeValue.equals(attribute.getValue());
      case BOOLEAN:
        return Boolean.valueOf(attributeValue).equals(attribute.getValue());
      case DATE:
        try {
          String mappedDate = DATE_FORMATTER.format(DATE_FORMATTER.parse(attributeValue));
          String metacardDate = DATE_FORMATTER.format(((Date) attribute.getValue()).toInstant());
          return mappedDate.equals(metacardDate);
        } catch (DateTimeParseException e) {
          LOGGER.debug("Unable to parse date and perform comparison.", e);
          return false;
        }
      case SHORT:
        return Short.valueOf(attributeValue).equals(attribute.getValue());
      case INTEGER:
        return Integer.valueOf(attributeValue).equals(attribute.getValue());
      case LONG:
        return Long.valueOf(attributeValue).equals(attribute.getValue());
      case FLOAT:
        return Precision.equals(
            Float.valueOf(attributeValue), (Float) attribute.getValue(), .0000001);
      case DOUBLE:
        return Precision.equals(
            Double.valueOf(attributeValue), (Double) attribute.getValue(), .0000000001);
      case BINARY:
      case OBJECT:
      default:
        LOGGER.debug("Unsupported Attribute Format was attempted for KML Style Mapping.");
        return false;
    }
  }
}
