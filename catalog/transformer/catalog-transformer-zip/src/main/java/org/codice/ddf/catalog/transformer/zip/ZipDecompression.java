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
package org.codice.ddf.catalog.transformer.zip;

import ddf.catalog.data.Metacard;
import ddf.catalog.transform.CatalogTransformerException;
import ddf.catalog.transform.InputCollectionTransformer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipDecompression implements InputCollectionTransformer {

  public static final String FILE_PATH = "filePath";

  public static final String FILE_NAME = "fileName";

  public static final String CONTENT = "content";

  public static final int BUFFER_SIZE = 4096;

  /**
   * Decompression-bomb guards. The zip ingested here is expected to be a (signed) DDF catalog
   * export, so these caps are deliberately generous: they never trip on a legitimate export but
   * abort extraction of a maliciously crafted archive (e.g. a highly-compressible "zip bomb" whose
   * uncompressed size is orders of magnitude larger than the archive, or an archive with an
   * explosive number of entries) before it can exhaust disk or memory.
   */
  private static final long MAX_ENTRY_UNCOMPRESSED_BYTES = 5L * 1024 * 1024 * 1024; // 5 GiB / entry

  private static final long MAX_TOTAL_UNCOMPRESSED_BYTES = 10L * 1024 * 1024 * 1024; // 10 GiB total

  private static final long MAX_ENTRY_COUNT = 100_000L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ZipCompression.class);

  /**
   * Transforms a Zip InputStream into a List of {@link Metacard}s. This method expects there to be
   * a filePath and fileName key-value pair passed in the arguments map.
   *
   * @param inputStream - the InputStream to transform
   * @param arguments - the arguments for the transformation ("filePath" and "fileName").
   * @return the List of {@link Metacard}s produced from the transformation.
   * @throws CatalogTransformerException when the transformation fails.
   */
  @Override
  public List<Metacard> transform(InputStream inputStream, Map<String, Serializable> arguments)
      throws CatalogTransformerException {

    if (inputStream == null) {
      throw new CatalogTransformerException(
          "Unable to transform InputStream : InputStream was null.");
    }

    if (MapUtils.isEmpty(arguments)
        || !arguments.containsKey(FILE_PATH)
        || !arguments.containsKey(FILE_NAME)) {
      throw new CatalogTransformerException(
          "Unable to transform InputStream : Invalid arguments passed.");
    }

    String zipFileName = (String) arguments.get(FILE_PATH);

    Map<String, Metacard> metacards = decompressFile(inputStream, zipFileName);
    return metacards.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  private Map<String, Metacard> decompressFile(InputStream inputStream, String zipFileName)
      throws CatalogTransformerException {
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
      Map<String, Metacard> metacardMap = new HashMap<>();
      long entryCount = 0;
      long totalUncompressedBytes = 0;
      ZipEntry zipEntry = zipInputStream.getNextEntry();

      while (zipEntry != null) {
        if (++entryCount > MAX_ENTRY_COUNT) {
          throw new CatalogTransformerException(
              "Refusing to expand archive: entry count exceeds the maximum of " + MAX_ENTRY_COUNT);
        }
        String filename = zipEntry.getName();

        if (!filename.contains("META-INF")) {

          // Zip Slip guard: the entry name is attacker-controlled; reject any name that would
          // traverse outside the extraction root (absolute path or a leading "..") before it is
          // concatenated onto the destination prefix. Legitimate relative names (e.g.
          // "dir/file.txt") are unaffected.
          Path normalizedEntry = Paths.get(filename).normalize();
          if (normalizedEntry.isAbsolute() || normalizedEntry.startsWith("..")) {
            throw new CatalogTransformerException("Zip entry has an unsafe path: " + filename);
          }

          File zipEntryFile = new File(zipFileName + filename);

          if (!new File(zipEntryFile.getParent()).mkdirs()) {
            LOGGER.debug("File directory already exists in {}", zipFileName);
          }

          if (!zipEntryFile.isDirectory()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(zipEntryFile)) {

              // Bounded copy: never trust the declared/compressed sizes in the zip header. Count
              // the bytes actually inflated and abort if this entry (or the archive as a whole)
              // exceeds the decompression-bomb caps, guarding against archives that expand far
              // beyond their on-disk size.
              totalUncompressedBytes +=
                  copyWithLimit(zipInputStream, fileOutputStream, filename, totalUncompressedBytes);
            }
            if (!zipEntryFile.getPath().contains(CONTENT)) {
              metacardMap.put(zipEntryFile.getName(), readMetacard(zipEntryFile));
            }
          }
        }
        zipEntry = zipInputStream.getNextEntry();
      }
      return metacardMap;
    } catch (IOException e) {
      throw new CatalogTransformerException(
          String.format("Unable to transform InputStream for %s.", zipFileName));
    }
  }

  /**
   * Copies the current zip entry to {@code out} while enforcing the per-entry and cumulative
   * uncompressed-size caps, so a decompression bomb cannot exhaust disk or memory.
   *
   * @param in the zip entry stream to read from
   * @param out the destination stream
   * @param filename the entry name (for error reporting only)
   * @param priorTotalBytes uncompressed bytes already written for previous entries
   * @return the number of uncompressed bytes written for this entry
   * @throws IOException if reading/writing fails
   * @throws CatalogTransformerException if a size cap is exceeded
   */
  private long copyWithLimit(
      InputStream in, FileOutputStream out, String filename, long priorTotalBytes)
      throws IOException, CatalogTransformerException {
    byte[] buffer = new byte[BUFFER_SIZE];
    long entryBytes = 0;
    int read;
    while ((read = in.read(buffer)) != -1) {
      entryBytes += read;
      if (entryBytes > MAX_ENTRY_UNCOMPRESSED_BYTES) {
        throw new CatalogTransformerException(
            "Refusing to expand archive: entry '"
                + filename
                + "' exceeds the maximum uncompressed size of "
                + MAX_ENTRY_UNCOMPRESSED_BYTES
                + " bytes");
      }
      if (priorTotalBytes + entryBytes > MAX_TOTAL_UNCOMPRESSED_BYTES) {
        throw new CatalogTransformerException(
            "Refusing to expand archive: total uncompressed size exceeds the maximum of "
                + MAX_TOTAL_UNCOMPRESSED_BYTES
                + " bytes");
      }
      out.write(buffer, 0, read);
    }
    return entryBytes;
  }

  private Metacard readMetacard(File file) {
    Metacard result = null;
    try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
      result = (Metacard) objectInputStream.readObject();
    } catch (IOException | IllegalArgumentException | ClassNotFoundException e) {
      LOGGER.debug("Unable to create metacard from file {}", file.getName(), e);
    }
    return result;
  }
}
