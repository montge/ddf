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
package ddf.catalog.transform.xml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.Result;
import ddf.catalog.transformer.api.PrintWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrintWriterProviderImplTest {

  private PrintWriterProviderImpl provider;

  @Before
  public void setUp() {
    provider = new PrintWriterProviderImpl();
  }

  @Test
  public void testBuildWithMetacardClass() {
    PrintWriter writer = provider.build(Metacard.class);

    assertThat(writer, is(notNullValue()));
    assertThat(writer, is(instanceOf(EscapingPrintWriter.class)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithUnsupportedClass() {
    provider.build(Result.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithNullClass() {
    provider.build(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildWithStringClass() {
    provider.build(String.class);
  }

  @Test
  public void testMultipleBuildCallsReturnNewInstances() {
    PrintWriter writer1 = provider.build(Metacard.class);
    PrintWriter writer2 = provider.build(Metacard.class);

    assertThat(writer1, is(notNullValue()));
    assertThat(writer2, is(notNullValue()));
    // Verify they are different instances
    assertThat(writer1 == writer2, is(false));
  }
}
