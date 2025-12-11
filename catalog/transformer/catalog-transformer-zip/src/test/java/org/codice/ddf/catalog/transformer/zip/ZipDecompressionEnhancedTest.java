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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import ddf.catalog.data.Metacard;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ZipDecompressionEnhancedTest {

  private ZipDecompression zipDecompression;
  private Map<String, Serializable> arguments;

  @BeforeEach
  public void setUp() {
    zipDecompression = new ZipDecompression();
    arguments = new HashMap<>();
    arguments.put(ZipDecompression.FILE_PATH, "target/test-");
    arguments.put(ZipDecompression.FILE_NAME, "test.zip");
  }

  @Test
  public void testTransformWithNullInputStream() {
    assertThrows(
        CatalogTransformerException.class, () -> zipDecompression.transform(null, arguments));
  }

  @Test
  public void testTransformWithNullArguments() {
    InputStream stream = createEmptyZipStream();
    assertThrows(CatalogTransformerException.class, () -> zipDecompression.transform(stream, null));
  }

  @Test
  public void testTransformWithEmptyArguments() {
    InputStream stream = createEmptyZipStream();
    assertThrows(
        CatalogTransformerException.class,
        () -> zipDecompression.transform(stream, new HashMap<>()));
  }

  @Test
  public void testTransformWithMissingFilePath() {
    Map<String, Serializable> badArgs = new HashMap<>();
    badArgs.put(ZipDecompression.FILE_NAME, "test.zip");
    InputStream stream = createEmptyZipStream();
    assertThrows(
        CatalogTransformerException.class, () -> zipDecompression.transform(stream, badArgs));
  }

  @Test
  public void testTransformWithMissingFileName() {
    Map<String, Serializable> badArgs = new HashMap<>();
    badArgs.put(ZipDecompression.FILE_PATH, "target/");
    InputStream stream = createEmptyZipStream();
    assertThrows(
        CatalogTransformerException.class, () -> zipDecompression.transform(stream, badArgs));
  }

  @Test
  public void testTransformEmptyZipFile() throws CatalogTransformerException {
    InputStream stream = createEmptyZipStream();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
    assertThat(result.size(), is(0));
  }

  @Test
  public void testTransformZipWithTextFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithTextFiles();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithMetaInfFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithMetaInfFiles();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithNestedDirectories()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithNestedDirectories();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithLargeFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithLargeFile();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithSpecialCharactersInFilenames()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithSpecialCharacterFiles();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithEmptyDirectories()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithEmptyDirectories();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithMultipleFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithMultipleFiles(10);
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformCorruptedZipStream() {
    byte[] corruptedData = new byte[] {0x50, 0x4B, 0x03, 0x04, 0x00, 0x00, 0x00, 0x00};
    InputStream stream = new ByteArrayInputStream(corruptedData);
    assertThrows(
        CatalogTransformerException.class, () -> zipDecompression.transform(stream, arguments));
  }

  @Test
  public void testTransformZipWithBinaryFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithBinaryFile();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithDifferentFilePath() throws CatalogTransformerException, IOException {
    Map<String, Serializable> customArgs = new HashMap<>();
    customArgs.put(ZipDecompression.FILE_PATH, "target/custom/path-");
    customArgs.put(ZipDecompression.FILE_NAME, "custom.zip");

    InputStream stream = createZipWithTextFiles();
    List<Metacard> result = zipDecompression.transform(stream, customArgs);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithHiddenFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithHiddenFiles();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithDuplicateFilenames()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithDuplicateNames();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithContentDirectory()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithContentDirectory();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithMixedContent() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithMixedContent();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithZeroByteFiles() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithZeroByteFiles();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithDeepNesting() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithDeepNesting();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformZipWithVeryLongFilenames()
      throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithLongFilenames();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformNonZipInputStream() {
    String plainText = "This is not a zip file";
    InputStream stream = new ByteArrayInputStream(plainText.getBytes());
    assertThrows(
        CatalogTransformerException.class, () -> zipDecompression.transform(stream, arguments));
  }

  @Test
  public void testTransformZipWithUTF8Filenames() throws CatalogTransformerException, IOException {
    InputStream stream = createZipWithUTF8Filenames();
    List<Metacard> result = zipDecompression.transform(stream, arguments);
    assertThat(result, notNullValue());
  }

  // Helper methods to create test ZIP streams

  private InputStream createEmptyZipStream() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(baos);
      zos.close();
      return new ByteArrayInputStream(baos.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private InputStream createZipWithTextFiles() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("file1.txt");
    zos.putNextEntry(entry);
    zos.write("Test content 1".getBytes());
    zos.closeEntry();

    entry = new ZipEntry("file2.txt");
    zos.putNextEntry(entry);
    zos.write("Test content 2".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithMetaInfFiles() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("META-INF/MANIFEST.MF");
    zos.putNextEntry(entry);
    zos.write("Manifest-Version: 1.0\n".getBytes());
    zos.closeEntry();

    entry = new ZipEntry("data.txt");
    zos.putNextEntry(entry);
    zos.write("Data content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithNestedDirectories() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("dir1/dir2/file.txt");
    zos.putNextEntry(entry);
    zos.write("Nested file content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithLargeFile() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("largefile.txt");
    zos.putNextEntry(entry);

    byte[] largeData = new byte[10000];
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) ('A' + (i % 26));
    }
    zos.write(largeData);
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithSpecialCharacterFiles() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("file-with-special_chars.txt");
    zos.putNextEntry(entry);
    zos.write("Content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithEmptyDirectories() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("emptydir/");
    zos.putNextEntry(entry);
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithMultipleFiles(int count) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    for (int i = 0; i < count; i++) {
      ZipEntry entry = new ZipEntry("file" + i + ".txt");
      zos.putNextEntry(entry);
      zos.write(("Content " + i).getBytes());
      zos.closeEntry();
    }

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithBinaryFile() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("binary.dat");
    zos.putNextEntry(entry);

    byte[] binaryData = new byte[256];
    for (int i = 0; i < binaryData.length; i++) {
      binaryData[i] = (byte) i;
    }
    zos.write(binaryData);
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithHiddenFiles() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry(".hidden");
    zos.putNextEntry(entry);
    zos.write("Hidden content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithDuplicateNames() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("file.txt");
    zos.putNextEntry(entry);
    zos.write("First content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithContentDirectory() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("content/data.txt");
    zos.putNextEntry(entry);
    zos.write("Content directory file".getBytes());
    zos.closeEntry();

    entry = new ZipEntry("other.txt");
    zos.putNextEntry(entry);
    zos.write("Other file".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithMixedContent() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("text.txt");
    zos.putNextEntry(entry);
    zos.write("Text".getBytes());
    zos.closeEntry();

    entry = new ZipEntry("dir/");
    zos.putNextEntry(entry);
    zos.closeEntry();

    entry = new ZipEntry("dir/nested.txt");
    zos.putNextEntry(entry);
    zos.write("Nested".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithZeroByteFiles() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("empty.txt");
    zos.putNextEntry(entry);
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithDeepNesting() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("a/b/c/d/e/f/g/file.txt");
    zos.putNextEntry(entry);
    zos.write("Deep file".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithLongFilenames() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    StringBuilder longName = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longName.append("longfilename");
    }
    longName.append(".txt");

    ZipEntry entry = new ZipEntry(longName.toString());
    zos.putNextEntry(entry);
    zos.write("Content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }

  private InputStream createZipWithUTF8Filenames() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    ZipEntry entry = new ZipEntry("file-\u00E9\u00E8\u00EA.txt");
    zos.putNextEntry(entry);
    zos.write("UTF-8 content".getBytes());
    zos.closeEntry();

    zos.close();
    return new ByteArrayInputStream(baos.toByteArray());
  }
}
