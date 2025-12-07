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
package org.codice.ddf.spatial.ogc.wcs.catalog;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Test;

/** Unit tests for {@link GetCoverageResponse}. */
public class GetCoverageResponseTest {

  @Test
  public void testGetCoverageResponseInstantiation() {
    GetCoverageResponse response = new GetCoverageResponse();
    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testSetAndGetInputStream() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream inputStream = new ByteArrayInputStream("test data".getBytes());

    response.setInputStream(inputStream);
    InputStream retrievedStream = response.getInputStream();

    assertThat(retrievedStream, is(notNullValue()));
    assertThat(retrievedStream, is(sameInstance(inputStream)));
  }

  @Test
  public void testSetAndGetContentDisposition() {
    GetCoverageResponse response = new GetCoverageResponse();
    String contentDisposition = "attachment; filename=\"coverage.tif\"";

    response.setContentDisposition(contentDisposition);
    String retrievedDisposition = response.getContentDisposition();

    assertThat(retrievedDisposition, is(notNullValue()));
    assertThat(retrievedDisposition, is(contentDisposition));
  }

  @Test
  public void testSetNullInputStream() {
    GetCoverageResponse response = new GetCoverageResponse();
    response.setInputStream(null);

    assertThat(response.getInputStream(), is(nullValue()));
  }

  @Test
  public void testSetNullContentDisposition() {
    GetCoverageResponse response = new GetCoverageResponse();
    response.setContentDisposition(null);

    assertThat(response.getContentDisposition(), is(nullValue()));
  }

  @Test
  public void testSetEmptyContentDisposition() {
    GetCoverageResponse response = new GetCoverageResponse();
    response.setContentDisposition("");

    assertThat(response.getContentDisposition(), is(""));
  }

  @Test
  public void testDefaultValuesAreNull() {
    GetCoverageResponse response = new GetCoverageResponse();

    assertThat(response.getInputStream(), is(nullValue()));
    assertThat(response.getContentDisposition(), is(nullValue()));
  }

  @Test
  public void testSetBothProperties() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream inputStream = new ByteArrayInputStream("coverage data".getBytes());
    String contentDisposition = "attachment; filename=\"test.tiff\"";

    response.setInputStream(inputStream);
    response.setContentDisposition(contentDisposition);

    assertThat(response.getInputStream(), is(sameInstance(inputStream)));
    assertThat(response.getContentDisposition(), is(contentDisposition));
  }

  @Test
  public void testOverwriteInputStream() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream firstStream = new ByteArrayInputStream("first".getBytes());
    InputStream secondStream = new ByteArrayInputStream("second".getBytes());

    response.setInputStream(firstStream);
    assertThat(response.getInputStream(), is(sameInstance(firstStream)));

    response.setInputStream(secondStream);
    assertThat(response.getInputStream(), is(sameInstance(secondStream)));
  }

  @Test
  public void testOverwriteContentDisposition() {
    GetCoverageResponse response = new GetCoverageResponse();

    response.setContentDisposition("attachment; filename=\"first.tif\"");
    assertThat(response.getContentDisposition(), is("attachment; filename=\"first.tif\""));

    response.setContentDisposition("attachment; filename=\"second.tif\"");
    assertThat(response.getContentDisposition(), is("attachment; filename=\"second.tif\""));
  }

  @Test
  public void testContentDispositionWithVariousFormats() {
    GetCoverageResponse response = new GetCoverageResponse();

    String inlineDisposition = "inline; filename=\"coverage.png\"";
    response.setContentDisposition(inlineDisposition);
    assertThat(response.getContentDisposition(), is(inlineDisposition));

    String attachmentDisposition =
        "attachment; filename=\"coverage.geotiff\"; filename*=UTF-8''coverage.geotiff";
    response.setContentDisposition(attachmentDisposition);
    assertThat(response.getContentDisposition(), is(attachmentDisposition));
  }

  @Test
  public void testMultipleInstancesAreIndependent() {
    GetCoverageResponse response1 = new GetCoverageResponse();
    GetCoverageResponse response2 = new GetCoverageResponse();

    InputStream stream1 = new ByteArrayInputStream("data1".getBytes());
    InputStream stream2 = new ByteArrayInputStream("data2".getBytes());

    response1.setInputStream(stream1);
    response1.setContentDisposition("disposition1");

    response2.setInputStream(stream2);
    response2.setContentDisposition("disposition2");

    assertThat(response1.getInputStream(), is(sameInstance(stream1)));
    assertThat(response1.getContentDisposition(), is("disposition1"));

    assertThat(response2.getInputStream(), is(sameInstance(stream2)));
    assertThat(response2.getContentDisposition(), is("disposition2"));
  }

  @Test
  public void testEmptyInputStream() {
    GetCoverageResponse response = new GetCoverageResponse();
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

    response.setInputStream(emptyStream);

    assertThat(response.getInputStream(), is(notNullValue()));
    assertThat(response.getInputStream(), is(sameInstance(emptyStream)));
  }
}
