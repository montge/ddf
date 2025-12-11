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
package ddf.catalog.transformer.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

import ddf.catalog.data.Metacard;
import ddf.catalog.transformer.api.PrintWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintWriterProviderImplTest {

  private PrintWriterProviderImpl provider;

  @BeforeEach
  public void setUp() {
    provider = new PrintWriterProviderImpl();
  }

  @Test
  public void testBuildForMetacardClass() {
    PrintWriter writer = provider.build(Metacard.class);

    assertThat(writer, is(notNullValue()));
    assertThat(writer, is(instanceOf(EscapingPrintWriter.class)));
  }

  @Test
  public void testBuildWithNullClass() {
    try {
      provider.build(null);
      fail("Expected NullPointerException or IllegalArgumentException for null class");
    } catch (NullPointerException | IllegalArgumentException e) {
      // Expected either exception type
      assertThat(e != null, is(true));
    }
  }

  @Test
  public void testBuildWithUnsupportedClass() {
    try {
      provider.build(String.class);
      fail("Expected IllegalArgumentException for unsupported class");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage().contains("No PrintWriter for"), is(true));
    }
  }

  @Test
  public void testBuildReturnsNewInstanceEachTime() {
    PrintWriter writer1 = provider.build(Metacard.class);
    PrintWriter writer2 = provider.build(Metacard.class);

    assertThat(writer1, is(notNullValue()));
    assertThat(writer2, is(notNullValue()));
    // Each call should return a new instance
    assertThat(writer1 == writer2, is(false));
  }
}
