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

import ddf.catalog.data.Metacard;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.InputTransformer;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class handles .kmz files by unzipping them and passing the first .kml file to the {@link
 * KmlInputTransformer}. All other files within the .kmz are ignored.
 */
public class KmzInputTransformer implements InputTransformer {

  private KmlInputTransformer kmlInputTransformer;

  public static final String KML_EXTENSION = ".kml";

  /**
   * Maximum number of entries to scan in a .kmz (zip) archive before giving up. A legitimate .kmz
   * contains a single .kml plus a handful of supporting resources; this cap guards against a
   * maliciously crafted archive with an enormous number of entries (decompression-bomb / DoS).
   */
  private static final int MAX_ZIP_ENTRIES = 1024;

  /**
   * Maximum number of uncompressed bytes that will be read from the selected .kml entry. KML is a
   * text format; even very large legitimate documents are well under this cap. This guards against
   * a zip-bomb whose .kml entry decompresses to an unbounded size (java:S5042).
   */
  private static final long MAX_KML_UNCOMPRESSED_BYTES = 100L * 1024L * 1024L; // 100 MB

  public KmzInputTransformer(KmlInputTransformer kmlInputTransformer) {
    this.kmlInputTransformer = kmlInputTransformer;
  }

  @Override
  public Metacard transform(InputStream inputStream)
      throws IOException, CatalogTransformerException {
    return transform(inputStream, null);
  }

  @Override
  public Metacard transform(InputStream inputStream, String id)
      throws IOException, CatalogTransformerException {

    ZipInputStream zipInputStream = new ZipInputStream(inputStream);

    ZipEntry entry;
    int entryCount = 0;
    while ((entry = zipInputStream.getNextEntry()) != null) {

      if (++entryCount > MAX_ZIP_ENTRIES) {
        throw new CatalogTransformerException(
            "Unable to parse KMZ file: exceeded maximum allowed number of archive entries ("
                + MAX_ZIP_ENTRIES
                + ").");
      }

      // According to Google, a .kmz should only contain a single .kml file
      // so we stop at the first one we find.
      if (entry.getName().endsWith(KML_EXTENSION)) {
        // Cap the number of uncompressed bytes the downstream transformer can read from the
        // selected entry so a maliciously crafted .kmz (zip-bomb) cannot exhaust disk/memory.
        return kmlInputTransformer.transform(
            new BoundedInputStream(zipInputStream, MAX_KML_UNCOMPRESSED_BYTES), id);
      }
    }

    throw new CatalogTransformerException("Unable to parse any KML from KMZ file");
  }

  /**
   * A {@link FilterInputStream} that throws an {@link IOException} once more than {@code maxBytes}
   * have been read from the wrapped stream. Used to bound how much uncompressed data the downstream
   * transformer will pull from a single .kmz entry, defending against decompression bombs
   * (java:S5042). Closing delegates to the wrapped {@link ZipInputStream}, preserving the original
   * stream-lifecycle behaviour.
   */
  private static final class BoundedInputStream extends FilterInputStream {

    private final long maxBytes;

    private long bytesRead;

    private BoundedInputStream(InputStream in, long maxBytes) {
      super(in);
      this.maxBytes = maxBytes;
    }

    private void checkLimit(long additional) throws IOException {
      if (additional > 0) {
        bytesRead += additional;
        if (bytesRead > maxBytes) {
          throw new IOException(
              "KMZ entry exceeds maximum allowed uncompressed size of " + maxBytes + " bytes.");
        }
      }
    }

    @Override
    public int read() throws IOException {
      int b = super.read();
      if (b != -1) {
        checkLimit(1);
      }
      return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int n = super.read(b, off, len);
      if (n > 0) {
        checkLimit(n);
      }
      return n;
    }
  }
}
