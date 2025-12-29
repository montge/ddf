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
package org.codice.ddf.libs.klv;

import com.google.common.base.Preconditions;
import jakarta.xml.bind.DatatypeConverter;
import java.util.List;
import java.util.Map;
import org.codice.ddf.libs.klv.data.Klv;

/** Decodes bytes that contain KLV-encoded data. */
public class KlvDecoder {
  /**
   * Maximum recursion depth for nested KLV Local Sets. This limit prevents stack overflow attacks
   * from maliciously crafted KLV data with deeply nested structures. Legitimate KLV data rarely
   * exceeds 5-10 levels of nesting.
   */
  public static final int DEFAULT_MAX_RECURSION_DEPTH = 32;

  private final KlvContext klvContext;
  private final int maxRecursionDepth;
  private final int currentDepth;

  /**
   * Creates a {@code KlvDecoder} with the given {@link KlvContext}.
   *
   * @param klvContext the {@code KlvContext} containing the properties of the KLV data to be
   *     decoded by this {@code KlvDecoder}
   */
  public KlvDecoder(final KlvContext klvContext) {
    this(klvContext, DEFAULT_MAX_RECURSION_DEPTH, 0);
  }

  /**
   * Creates a {@code KlvDecoder} with the given {@link KlvContext} and maximum recursion depth.
   *
   * @param klvContext the {@code KlvContext} containing the properties of the KLV data to be
   *     decoded by this {@code KlvDecoder}
   * @param maxRecursionDepth the maximum depth of nested KLV Local Sets allowed
   */
  public KlvDecoder(final KlvContext klvContext, final int maxRecursionDepth) {
    this(klvContext, maxRecursionDepth, 0);
  }

  /**
   * Internal constructor for tracking recursion depth.
   *
   * @param klvContext the {@code KlvContext} containing the properties of the KLV data
   * @param maxRecursionDepth the maximum depth of nested KLV Local Sets allowed
   * @param currentDepth the current recursion depth
   */
  KlvDecoder(final KlvContext klvContext, final int maxRecursionDepth, final int currentDepth) {
    Preconditions.checkArgument(maxRecursionDepth > 0, "Maximum recursion depth must be positive.");
    this.klvContext = klvContext;
    this.maxRecursionDepth = maxRecursionDepth;
    this.currentDepth = currentDepth;
  }

  /**
   * Returns the maximum recursion depth for this decoder.
   *
   * @return the maximum recursion depth
   */
  public int getMaxRecursionDepth() {
    return maxRecursionDepth;
  }

  /**
   * Returns the current recursion depth.
   *
   * @return the current recursion depth
   */
  int getCurrentDepth() {
    return currentDepth;
  }

  /**
   * Creates a child decoder for decoding nested KLV Local Sets. The child decoder has the same
   * maximum recursion depth but an incremented current depth.
   *
   * @param childContext the {@code KlvContext} for the nested structure
   * @return a new {@code KlvDecoder} with incremented depth
   */
  public KlvDecoder createChildDecoder(final KlvContext childContext) {
    return new KlvDecoder(childContext, maxRecursionDepth, currentDepth + 1);
  }

  /**
   * Decodes the KLV data inside {@code klvBytes} according to the properties in the {@link
   * KlvContext} that was provided in the constructor.
   *
   * <p>It will recursively decode any {@link org.codice.ddf.libs.klv.data.set.KlvLocalSet}s it
   * finds, provided that they are given in the {@code KlvContext}.
   *
   * <p>The decoded KLV values are returned in a new {@code KlvContext} object that has the same
   * structure as the {@code KlvContext} that was provided in the constructor but contains only the
   * {@link KlvDataElement}s that were found in the given bytes.
   *
   * @param klvBytes bytes encoding data in KLV format
   * @return a new {@code KlvContext} containing the decoded KLV data elements
   * @throws IllegalArgumentException if {@code klvBytes} is null
   * @throws KlvDecodingException if the KLV cannot be decoded using the given context information,
   *     or if the maximum recursion depth is exceeded
   */
  public KlvContext decode(final byte[] klvBytes) throws KlvDecodingException {
    Preconditions.checkArgument(klvBytes != null, "The array of bytes to decode cannot be null.");

    if (currentDepth > maxRecursionDepth) {
      throw new KlvDecodingException(
          String.format(
              "KLV nesting depth %d exceeds maximum allowed depth of %d. "
                  + "This may indicate maliciously crafted KLV data.",
              currentDepth, maxRecursionDepth));
    }

    List<Klv> klvDataElements;

    try {
      klvDataElements =
          Klv.bytesToList(
              klvBytes,
              0,
              klvBytes.length,
              klvContext.getKeyLength(),
              klvContext.getLengthEncoding());
    } catch (RuntimeException e) {
      throw new KlvDecodingException(
          String.format(
              "Could not decode KLV using the given key length %s and length encoding %s",
              klvContext.getKeyLength(), klvContext.getLengthEncoding()),
          e);
    }

    final KlvContext decodedContext =
        new KlvContext(klvContext.getKeyLength(), klvContext.getLengthEncoding());
    final Map<String, KlvDataElement> keyToDataElementMap = klvContext.getKeyToDataElementMap();

    klvDataElements.forEach(
        klv -> {
          final String key = DatatypeConverter.printHexBinary(klv.getFullKey());

          if (keyToDataElementMap.containsKey(key)) {
            final KlvDataElement dataElementCopy = keyToDataElementMap.get(key).copy();
            dataElementCopy.decodeValue(klv, this);
            decodedContext.addDataElement(dataElementCopy);
          }
        });

    return decodedContext;
  }
}
