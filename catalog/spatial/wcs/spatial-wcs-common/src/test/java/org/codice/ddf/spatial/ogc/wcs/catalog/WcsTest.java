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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.opengis.wcs.v_1_0_0.CoverageDescription;
import net.opengis.wcs.v_1_0_0.DescribeCoverage;
import net.opengis.wcs.v_1_0_0.GetCapabilities;
import net.opengis.wcs.v_1_0_0.GetCoverage;
import net.opengis.wcs.v_1_0_0.WCSCapabilitiesType;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Wcs} interface. */
public class WcsTest {

  @Test
  public void testWcsInterfaceExists() {
    assertThat(Wcs.class, is(notNullValue()));
  }

  @Test
  public void testWcsIsInterface() {
    assertThat(Wcs.class.isInterface(), is(true));
  }

  @Test
  public void testWcsHasPathAnnotation() {
    Path pathAnnotation = Wcs.class.getAnnotation(Path.class);
    assertThat(pathAnnotation, is(notNullValue()));
    assertThat(pathAnnotation.value(), is("/"));
  }

  @Test
  public void testGetCapabilitiesWithRequestBeanMethodExists() throws NoSuchMethodException {
    assertThat(
        Wcs.class.getMethod("getCapabilities", GetCapabilitiesRequest.class), is(notNullValue()));
  }

  @Test
  public void testGetCapabilitiesWithRequestBeanHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCapabilities", GetCapabilitiesRequest.class);

    assertThat(method.getAnnotation(GET.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));

    String[] consumesTypes = method.getAnnotation(Consumes.class).value();
    assertThat(consumesTypes.length, is(2));
    assertThat(consumesTypes[0], is(MediaType.TEXT_XML));
    assertThat(consumesTypes[1], is(MediaType.APPLICATION_XML));

    String[] producesTypes = method.getAnnotation(Produces.class).value();
    assertThat(producesTypes.length, is(2));
    assertThat(producesTypes[0], is(MediaType.TEXT_XML));
    assertThat(producesTypes[1], is(MediaType.APPLICATION_XML));
  }

  @Test
  public void testGetCapabilitiesWithJaxbObjectMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCapabilities", GetCapabilities.class), is(notNullValue()));
  }

  @Test
  public void testGetCapabilitiesWithJaxbObjectHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCapabilities", GetCapabilities.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));
  }

  @Test
  public void testDescribeCoverageWithRequestBeanMethodExists() throws NoSuchMethodException {
    assertThat(
        Wcs.class.getMethod("describeCoverage", DescribeCoverageRequest.class), is(notNullValue()));
  }

  @Test
  public void testDescribeCoverageWithRequestBeanHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("describeCoverage", DescribeCoverageRequest.class);

    assertThat(method.getAnnotation(GET.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));
  }

  @Test
  public void testDescribeCoverageWithJaxbObjectMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("describeCoverage", DescribeCoverage.class), is(notNullValue()));
  }

  @Test
  public void testDescribeCoverageWithJaxbObjectHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("describeCoverage", DescribeCoverage.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithRequestBeanMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCoverage", GetCoverageRequest.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithRequestBeanHasCorrectAnnotations() throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCoverage", GetCoverageRequest.class);

    assertThat(method.getAnnotation(GET.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithJaxbObjectMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCoverage", GetCoverage.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithJaxbObjectHasCorrectAnnotations() throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCoverage", GetCoverage.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithStringMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCoverage", String.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageWithStringHasCorrectAnnotations() throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCoverage", String.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageMultiPartWithJaxbObjectMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCoverageMultiPart", GetCoverage.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageMultiPartWithJaxbObjectHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCoverageMultiPart", GetCoverage.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));

    String producesType = method.getAnnotation(Produces.class).value()[0];
    assertThat(producesType, is("multipart/mixed"));

    String consumesType = method.getAnnotation(Consumes.class).value()[0];
    assertThat(consumesType, is("text/xml"));
  }

  @Test
  public void testGetCoverageMultiPartWithStringMethodExists() throws NoSuchMethodException {
    assertThat(Wcs.class.getMethod("getCoverageMultiPart", String.class), is(notNullValue()));
  }

  @Test
  public void testGetCoverageMultiPartWithStringHasCorrectAnnotations()
      throws NoSuchMethodException {
    var method = Wcs.class.getMethod("getCoverageMultiPart", String.class);

    assertThat(method.getAnnotation(POST.class), is(notNullValue()));
    assertThat(method.getAnnotation(Produces.class), is(notNullValue()));
    assertThat(method.getAnnotation(Consumes.class), is(notNullValue()));
  }

  @Test
  public void testAllMethodsThrowWcsException() throws NoSuchMethodException {
    // Test that methods declare WcsException
    assertThat(
        Wcs.class
            .getMethod("getCapabilities", GetCapabilitiesRequest.class)
            .getExceptionTypes()
            .length,
        is(1));
    assertThat(
        Wcs.class.getMethod("getCapabilities", GetCapabilities.class).getExceptionTypes().length,
        is(1));
    assertThat(
        Wcs.class
            .getMethod("describeCoverage", DescribeCoverageRequest.class)
            .getExceptionTypes()
            .length,
        is(1));
    assertThat(
        Wcs.class.getMethod("describeCoverage", DescribeCoverage.class).getExceptionTypes().length,
        is(1));
    assertThat(
        Wcs.class.getMethod("getCoverage", GetCoverageRequest.class).getExceptionTypes().length,
        is(1));
    assertThat(
        Wcs.class.getMethod("getCoverage", GetCoverage.class).getExceptionTypes().length, is(1));
    assertThat(Wcs.class.getMethod("getCoverage", String.class).getExceptionTypes().length, is(1));
    assertThat(
        Wcs.class.getMethod("getCoverageMultiPart", GetCoverage.class).getExceptionTypes().length,
        is(1));
    assertThat(
        Wcs.class.getMethod("getCoverageMultiPart", String.class).getExceptionTypes().length,
        is(1));
  }

  @Test
  public void testWcsImplementationCanBeCreated() throws WcsException {
    // Create a mock implementation to verify interface can be implemented
    Wcs mockWcs = mock(Wcs.class);

    // Use real JAXB instances rather than Mockito mocks. These schema-generated classes
    // (wcs-v_1_0_0-schema 1.1.0) were compiled against the legacy javax.xml.bind API, whose
    // ValidationEventLocator is not present on the JDK 21 / jakarta.xml.bind classpath. Mockito's
    // bytebuddy mock maker fails because it must instrument the entire type hierarchy (which
    // references the missing javax.xml.bind types). A plain no-arg construction does not, so this
    // still exercises the interface stubbing without an unmockable type.
    WCSCapabilitiesType mockCapabilities = new WCSCapabilitiesType();
    when(mockWcs.getCapabilities(any(GetCapabilitiesRequest.class))).thenReturn(mockCapabilities);
    when(mockWcs.getCapabilities(any(GetCapabilities.class))).thenReturn(mockCapabilities);

    CoverageDescription mockDescription = new CoverageDescription();
    when(mockWcs.describeCoverage(any(DescribeCoverageRequest.class))).thenReturn(mockDescription);
    when(mockWcs.describeCoverage(any(DescribeCoverage.class))).thenReturn(mockDescription);

    GetCoverageResponse mockResponse = mock(GetCoverageResponse.class);
    when(mockWcs.getCoverage(any(GetCoverageRequest.class))).thenReturn(mockResponse);
    when(mockWcs.getCoverage(any(GetCoverage.class))).thenReturn(mockResponse);
    when(mockWcs.getCoverage(any(String.class))).thenReturn(mockResponse);

    MultipartBody mockMultipart = mock(MultipartBody.class);
    when(mockWcs.getCoverageMultiPart(any(GetCoverage.class))).thenReturn(mockMultipart);
    when(mockWcs.getCoverageMultiPart(any(String.class))).thenReturn(mockMultipart);

    // Verify mock works
    assertThat(mockWcs.getCapabilities(new GetCapabilitiesRequest()), is(notNullValue()));
    assertThat(mockWcs.describeCoverage(new DescribeCoverageRequest()), is(notNullValue()));
    assertThat(mockWcs.getCoverage(new GetCoverageRequest("id", "format")), is(notNullValue()));
  }
}
