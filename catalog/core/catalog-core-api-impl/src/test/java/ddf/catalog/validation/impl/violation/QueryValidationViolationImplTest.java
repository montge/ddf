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
package ddf.catalog.validation.impl.violation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import ddf.catalog.validation.violation.QueryValidationViolation.Severity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class QueryValidationViolationImplTest {

  @Test
  void testConstructorAndGetters() {
    Map<String, Object> extraData = new HashMap<>();
    extraData.put("key", "value");

    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "Test message", extraData);

    assertThat(violation.getSeverity(), is(Severity.ERROR));
    assertThat(violation.getMessage(), is("Test message"));
    assertThat(violation.getExtraData(), is(extraData));
  }

  @Test
  void testConstructorWithNullValues() {
    QueryValidationViolationImpl violation = new QueryValidationViolationImpl(null, null, null);

    assertThat(violation.getSeverity(), is(nullValue()));
    assertThat(violation.getMessage(), is(nullValue()));
    assertThat(violation.getExtraData(), is(nullValue()));
  }

  @Test
  void testConstructorWithWarningSeverity() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(
            Severity.WARNING, "Warning message", Collections.emptyMap());

    assertThat(violation.getSeverity(), is(Severity.WARNING));
  }

  @Test
  void testEqualsWithSameObject() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "message", Collections.emptyMap());

    assertThat(violation.equals(violation), is(true));
  }

  @Test
  void testEqualsWithEqualObjects() {
    Map<String, Object> extraData = Collections.singletonMap("key", "value");

    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);

    assertThat(violation1, is(equalTo(violation2)));
  }

  @Test
  void testEqualsWithDifferentSeverity() {
    Map<String, Object> extraData = Collections.emptyMap();

    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(Severity.WARNING, "message", extraData);

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithDifferentMessage() {
    Map<String, Object> extraData = Collections.emptyMap();

    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(Severity.ERROR, "message1", extraData);
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(Severity.ERROR, "message2", extraData);

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithDifferentExtraData() {
    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(
            Severity.ERROR, "message", Collections.singletonMap("key", "value1"));
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(
            Severity.ERROR, "message", Collections.singletonMap("key", "value2"));

    assertThat(violation1, is(not(equalTo(violation2))));
  }

  @Test
  void testEqualsWithNull() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "message", Collections.emptyMap());

    assertThat(violation.equals(null), is(false));
  }

  @Test
  void testEqualsWithDifferentClass() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "message", Collections.emptyMap());

    assertThat(violation.equals("not a violation"), is(false));
  }

  @Test
  void testHashCodeConsistentWithEquals() {
    Map<String, Object> extraData = Collections.singletonMap("key", "value");

    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);

    assertThat(violation1.hashCode(), is(violation2.hashCode()));
  }

  @Test
  void testHashCodeDifferentForDifferentViolations() {
    QueryValidationViolationImpl violation1 =
        new QueryValidationViolationImpl(Severity.ERROR, "message1", Collections.emptyMap());
    QueryValidationViolationImpl violation2 =
        new QueryValidationViolationImpl(Severity.WARNING, "message2", Collections.emptyMap());

    assertThat(violation1.hashCode(), is(not(violation2.hashCode())));
  }

  @Test
  void testToStringContainsSeverity() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "test", Collections.emptyMap());

    assertThat(violation.toString(), containsString("ERROR"));
  }

  @Test
  void testToStringContainsMessage() {
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "test message", Collections.emptyMap());

    assertThat(violation.toString(), containsString("test message"));
  }

  @Test
  void testToStringContainsExtraData() {
    Map<String, Object> extraData = Collections.singletonMap("field", "value");
    QueryValidationViolationImpl violation =
        new QueryValidationViolationImpl(Severity.ERROR, "message", extraData);

    assertThat(violation.toString(), containsString("field"));
  }
}
