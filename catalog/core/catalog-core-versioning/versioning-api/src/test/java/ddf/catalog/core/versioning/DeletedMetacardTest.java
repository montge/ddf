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
package ddf.catalog.core.versioning;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/** Tests for {@link DeletedMetacard} interface constants. */
public class DeletedMetacardTest {

  @Test
  public void testPrefixConstant() {
    assertThat(DeletedMetacard.PREFIX, is("metacard.deleted"));
  }

  @Test
  public void testPrefixerFunction() {
    String result = DeletedMetacard.PREFIXER.apply("test");
    assertThat(result, is("metacard.deleted.test"));
  }

  @Test
  public void testDeletedTagConstant() {
    assertThat(DeletedMetacard.DELETED_TAG, is("deleted"));
  }

  @Test
  public void testDeletedByAttributeName() {
    assertThat(DeletedMetacard.DELETED_BY, is("metacard.deleted.deleted-by"));
  }

  @Test
  public void testDeletionOfIdAttributeName() {
    assertThat(DeletedMetacard.DELETION_OF_ID, is("metacard.deleted.id"));
  }

  @Test
  public void testLastVersionIdAttributeName() {
    assertThat(DeletedMetacard.LAST_VERSION_ID, is("metacard.deleted.version"));
  }

  @Test
  public void testDeletedMetacardTagsAttributeName() {
    assertThat(DeletedMetacard.DELETED_METACARD_TAGS, is("metacard.deleted.tags"));
  }

  @Test
  public void testMetacardDeletedDateAttributeName() {
    assertThat(DeletedMetacard.METACARD_DELETED_DATE, is("metacard.deleted.date"));
  }

  @Test
  public void testPrefixerWithMultipleValues() {
    assertThat(DeletedMetacard.PREFIXER.apply("foo"), is("metacard.deleted.foo"));
    assertThat(DeletedMetacard.PREFIXER.apply("bar"), is("metacard.deleted.bar"));
    assertThat(DeletedMetacard.PREFIXER.apply("baz"), is("metacard.deleted.baz"));
  }

  @Test
  public void testPrefixerWithEmptyString() {
    String result = DeletedMetacard.PREFIXER.apply("");
    assertThat(result, is("metacard.deleted."));
  }

  @Test
  public void testPrefixerWithSpecialCharacters() {
    String result = DeletedMetacard.PREFIXER.apply("special-char_test");
    assertThat(result, is("metacard.deleted.special-char_test"));
  }
}
