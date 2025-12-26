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
package org.codice.solr.factory.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

class SolrFactoryExceptionTest {

  private static final String TEST_MESSAGE = "Solr factory creation failed";

  @Test
  void testMessageConstructor() {
    SolrFactoryException exception = new SolrFactoryException(TEST_MESSAGE);

    assertThat(exception, is(notNullValue()));
    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }

  @Test
  void testMessageAndCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SolrFactoryException exception = new SolrFactoryException(TEST_MESSAGE, cause);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testCauseConstructor() {
    Throwable cause = new RuntimeException("root cause");
    SolrFactoryException exception = new SolrFactoryException(cause);

    assertThat(exception.getCause(), is(sameInstance(cause)));
  }

  @Test
  void testExceptionIsCheckedException() {
    SolrFactoryException exception = new SolrFactoryException(TEST_MESSAGE);

    assertThat(Exception.class.isAssignableFrom(exception.getClass()), is(true));
    assertThat(RuntimeException.class.isAssignableFrom(exception.getClass()), is(false));
  }

  @Test
  void testNullMessage() {
    SolrFactoryException exception = new SolrFactoryException((String) null);

    assertThat(exception.getMessage(), is(nullValue()));
  }

  @Test
  void testNullCause() {
    SolrFactoryException exception = new SolrFactoryException(TEST_MESSAGE, null);

    assertThat(exception.getMessage(), is(TEST_MESSAGE));
    assertThat(exception.getCause(), is(nullValue()));
  }
}
