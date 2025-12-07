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
package org.codice.ddf.admin.core.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class SystemPropertyDetailsImplTest {

  @Test
  public void testConstructorWithAllParameters() {
    String title = "Test Title";
    String description = "Test Description";
    List<String> options = Arrays.asList("option1", "option2", "option3");
    String key = "test.key";
    String value = "testValue";

    SystemPropertyDetailsImpl details =
        new SystemPropertyDetailsImpl(title, description, options, key, value);

    assertThat(details.getTitle(), is(equalTo(title)));
    assertThat(details.getDescription(), is(equalTo(description)));
    assertThat(details.getOptions(), is(equalTo(options)));
    assertThat(details.getKey(), is(equalTo(key)));
    assertThat(details.getValue(), is(equalTo(value)));
    assertThat(details.getDefaultValue(), is(equalTo(value)));
  }

  @Test
  public void testConstructorWithNullValues() {
    SystemPropertyDetailsImpl details = new SystemPropertyDetailsImpl(null, null, null, null, null);

    assertThat(details.getTitle(), is(nullValue()));
    assertThat(details.getDescription(), is(nullValue()));
    assertThat(details.getOptions(), is(nullValue()));
    assertThat(details.getKey(), is(nullValue()));
    assertThat(details.getValue(), is(nullValue()));
    assertThat(details.getDefaultValue(), is(nullValue()));
  }

  @Test
  public void testSettersAndGetters() {
    SystemPropertyDetailsImpl details =
        new SystemPropertyDetailsImpl("original", "desc", null, "key", "val");

    String newTitle = "Updated Title";
    String newDescription = "Updated Description";
    List<String> newOptions = Arrays.asList("new1", "new2");
    String newKey = "new.key";
    String newValue = "newValue";
    String newDefaultValue = "newDefault";

    details.setTitle(newTitle);
    details.setDescription(newDescription);
    details.setOptions(newOptions);
    details.setKey(newKey);
    details.setValue(newValue);
    details.setDefaultValue(newDefaultValue);

    assertThat(details.getTitle(), is(equalTo(newTitle)));
    assertThat(details.getDescription(), is(equalTo(newDescription)));
    assertThat(details.getOptions(), is(equalTo(newOptions)));
    assertThat(details.getKey(), is(equalTo(newKey)));
    assertThat(details.getValue(), is(equalTo(newValue)));
    assertThat(details.getDefaultValue(), is(equalTo(newDefaultValue)));
  }

  @Test
  public void testConstructorSetsDefaultValueToValue() {
    String value = "testValue";
    SystemPropertyDetailsImpl details =
        new SystemPropertyDetailsImpl("title", "desc", null, "key", value);

    assertThat(details.getValue(), is(equalTo(value)));
    assertThat(details.getDefaultValue(), is(equalTo(value)));
  }

  @Test
  public void testEmptyOptions() {
    List<String> emptyOptions = Arrays.asList();
    SystemPropertyDetailsImpl details =
        new SystemPropertyDetailsImpl("title", "desc", emptyOptions, "key", "value");

    assertThat(details.getOptions(), is(equalTo(emptyOptions)));
    assertThat(details.getOptions().size(), is(0));
  }

  @Test
  public void testMapBehavior() {
    SystemPropertyDetailsImpl details =
        new SystemPropertyDetailsImpl("title", "desc", null, "key", "value");

    assertThat(details.containsKey("title"), is(true));
    assertThat(details.containsKey("description"), is(true));
    assertThat(details.containsKey("options"), is(true));
    assertThat(details.containsKey("key"), is(true));
    assertThat(details.containsKey("value"), is(true));
    assertThat(details.containsKey("defaultValue"), is(true));
    assertThat(details.size(), is(6));
  }
}
