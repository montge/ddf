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
package org.codice.ddf.spatial.ogc.wfs.featuretransformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

class ExceptionReportExceptionTest {

  @Test
  void testConstructorFormatsMessage() {
    String xmlBody = "<ExceptionReport><Exception>Error occurred</Exception></ExceptionReport>";
    ExceptionReportException exception = new ExceptionReportException(xmlBody);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), containsString("WFS returned an exception report"));
    assertThat(exception.getMessage(), containsString(xmlBody));
  }

  @Test
  void testConstructorWithEmptyBody() {
    ExceptionReportException exception = new ExceptionReportException("");

    assertThat(exception.getMessage(), containsString("WFS returned an exception report"));
  }

  @Test
  void testConstructorWithNullBody() {
    ExceptionReportException exception = new ExceptionReportException(null);

    assertThat(exception.getMessage(), containsString("null"));
  }

  @Test
  void testNoCause() {
    ExceptionReportException exception = new ExceptionReportException("test");

    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testExceptionIsCheckedException() {
    ExceptionReportException exception = new ExceptionReportException("test");

    assertThat(exception instanceof Exception, is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }
}
