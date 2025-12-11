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
package org.codice.ddf.confluence.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

/** Tests for {@link Confluence} class. */
public class ConfluenceTest {

  @Test
  public void testBodyTextConstant() {
    assertThat(Confluence.BODY_TEXT, is("confluence.body-text"));
  }

  @Test
  public void testJsonResponseConstant() {
    assertThat(Confluence.JSON_RESPONSE, is("confluence.json-response"));
  }

  @Test
  public void testConfluenceTagConstant() {
    assertThat(Confluence.CONFLUENCE_TAG, is("confluence"));
  }

  @Test
  public void testConstantsHaveConfluencePrefix() {
    assertThat(Confluence.BODY_TEXT, startsWith("confluence"));
    assertThat(Confluence.JSON_RESPONSE, startsWith("confluence"));
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<Confluence> constructor = Confluence.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));

    // Make it accessible to verify it doesn't throw
    constructor.setAccessible(true);
    Confluence instance = constructor.newInstance();
    assertThat(instance != null, is(true));
  }
}
