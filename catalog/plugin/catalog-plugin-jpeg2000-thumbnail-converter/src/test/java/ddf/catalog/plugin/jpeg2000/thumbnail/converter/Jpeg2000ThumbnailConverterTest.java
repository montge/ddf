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
package ddf.catalog.plugin.jpeg2000.thumbnail.converter;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.impl.QueryResponseImpl;
import ddf.catalog.plugin.PluginExecutionException;
import ddf.catalog.plugin.StopProcessingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import org.junit.jupiter.api.Test;

public class Jpeg2000ThumbnailConverterTest {
  private final Jpeg2000ThumbnailConverter jpeg2000ThumbnailConverter =
      new Jpeg2000ThumbnailConverter();

  @Test
  public void testConversion()
      throws IOException, StopProcessingException, PluginExecutionException {
    IIORegistry.getDefaultInstance().registerServiceProvider(jpeg2000ThumbnailConverter);
    List<Result> resultList = new ArrayList<>();
    Metacard metacard = new MetacardImpl();
    byte[] j2kbytes = new byte[0];
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);
    // there are two possible byte signatures, so test an example of each one
    for (String image : new String[] {"/Bretagne2.j2k", "/Cevennes2.jp2"}) {
      URL imageResource = Jpeg2000ThumbnailConverterTest.class.getResource(image);
      if (imageResource == null) {
        fail("The Image Resource came back null. Was the resources folder removed?");
      }
      String imageResourcePath = new File(imageResource.getFile()).getAbsolutePath();
      j2kbytes = Files.readAllBytes(Paths.get(imageResourcePath));
      metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, j2kbytes));
      jpeg2000ThumbnailConverter.process(queryResponse);
      // verify the plugin converted the j2k/jp2 image
      assertTrue(!Arrays.equals(j2kbytes, metacard.getThumbnail()));
    }
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(ImageIO.read(new ByteArrayInputStream(j2kbytes)), "gif", output);
    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, output.toByteArray()));
    jpeg2000ThumbnailConverter.process(queryResponse);
    // verify the plugin ignored  the non-j2k
    assertTrue(Arrays.equals(output.toByteArray(), metacard.getThumbnail()));
  }

  @Test
  public void testEmptyThumbnail() throws Exception {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, new byte[0]));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    jpeg2000ThumbnailConverter.process(queryResponse);
  }

  @Test
  public void testNullThumbnail() throws Exception {
    Metacard metacard = new MetacardImpl();
    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, (byte[]) null));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    jpeg2000ThumbnailConverter.process(queryResponse);

    // Verify thumbnail remains null
    byte[] thumbnail = metacard.getThumbnail();
    assertTrue(thumbnail == null);
  }

  @Test
  public void testMetacardWithoutThumbnail() throws Exception {
    Metacard metacard = new MetacardImpl();

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    jpeg2000ThumbnailConverter.process(queryResponse);

    // Verify thumbnail remains null
    assertTrue(metacard.getThumbnail() == null);
  }

  @Test
  public void testEmptyResultsList() throws Exception {
    List<Result> resultList = new ArrayList<>();
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 0);

    jpeg2000ThumbnailConverter.process(queryResponse);

    // Should process without exception
    assertTrue(queryResponse.getResults().isEmpty());
  }

  @Test
  public void testMultipleMetacardsWithMixedThumbnails()
      throws IOException, StopProcessingException, PluginExecutionException {
    List<Result> resultList = new ArrayList<>();

    // First metacard with J2K thumbnail
    Metacard metacard1 = new MetacardImpl();
    URL imageResource1 = Jpeg2000ThumbnailConverterTest.class.getResource("/Bretagne2.j2k");
    if (imageResource1 != null) {
      byte[] j2kbytes =
          Files.readAllBytes(Paths.get(new File(imageResource1.getFile()).getAbsolutePath()));
      metacard1.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, j2kbytes));
      resultList.add(new ResultImpl(metacard1));
    }

    // Second metacard with null thumbnail
    Metacard metacard2 = new MetacardImpl();
    resultList.add(new ResultImpl(metacard2));

    // Third metacard with regular JPEG thumbnail
    Metacard metacard3 = new MetacardImpl();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] regularBytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG header
    metacard3.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, regularBytes));
    resultList.add(new ResultImpl(metacard3));

    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, resultList.size());
    jpeg2000ThumbnailConverter.process(queryResponse);

    // Verify first was converted, second is null, third unchanged
    if (imageResource1 != null) {
      assertTrue(
          !Arrays.equals(
              Files.readAllBytes(Paths.get(new File(imageResource1.getFile()).getAbsolutePath())),
              metacard1.getThumbnail()));
    }
    assertTrue(metacard2.getThumbnail() == null);
    assertTrue(Arrays.equals(regularBytes, metacard3.getThumbnail()));
  }

  @Test
  public void testJP2SignatureOnly() throws Exception {
    Metacard metacard = new MetacardImpl();

    // Create minimal JP2 signature bytes
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(new byte[] {0x00, 0x00, 0x00, 0x0c}); // OTHER_JP2_SIGNATURE
    baos.write(new byte[] {0x6a, 0x50, 0x20, 0x20}); // JP2_SIGNATURE_BOX
    baos.write(new byte[] {0x0d, 0x0a, (byte) 0x87, 0x0a}); // OFFICIAL_JP2_SIGNATURE

    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, baos.toByteArray()));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    // Should process without exception even if signature is present but no valid image data
    jpeg2000ThumbnailConverter.process(queryResponse);
  }

  @Test
  public void testCodestreamMarkerOnly() throws Exception {
    Metacard metacard = new MetacardImpl();

    // Create codestream marker
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(new byte[] {(byte) 0xff, 0x4f}); // START_OF_CODESTREAM_MARKER

    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, baos.toByteArray()));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    // Should process without exception
    jpeg2000ThumbnailConverter.process(queryResponse);
  }

  @Test
  public void testInvalidSignature() throws Exception {
    Metacard metacard = new MetacardImpl();

    // Create invalid signature
    byte[] invalidBytes =
        new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, invalidBytes));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    jpeg2000ThumbnailConverter.process(queryResponse);

    // Verify thumbnail remains unchanged (not a J2K/JP2)
    assertTrue(Arrays.equals(invalidBytes, metacard.getThumbnail()));
  }

  @Test
  public void testIOExceptionDuringConversion() throws Exception {
    Metacard metacard = new MetacardImpl();

    // Create JP2 signature but with insufficient data to read as image
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(new byte[] {0x00, 0x00, 0x00, 0x0c}); // OTHER_JP2_SIGNATURE
    baos.write(new byte[] {0x6a, 0x50, 0x20, 0x20}); // JP2_SIGNATURE_BOX
    baos.write(new byte[] {0x0d, 0x0a, (byte) 0x87, 0x0a}); // OFFICIAL_JP2_SIGNATURE
    baos.write(new byte[100]); // Some invalid data

    metacard.setAttribute(new AttributeImpl(Metacard.THUMBNAIL, baos.toByteArray()));

    List<Result> resultList = new ArrayList<>();
    resultList.add(new ResultImpl(metacard));
    QueryResponseImpl queryResponse = new QueryResponseImpl(null, resultList, 1);

    // Should throw PluginExecutionException due to IOException during processing
    assertThrows(
        PluginExecutionException.class, () -> jpeg2000ThumbnailConverter.process(queryResponse));
  }
}
