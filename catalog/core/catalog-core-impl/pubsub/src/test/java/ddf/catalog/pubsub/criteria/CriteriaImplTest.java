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
package ddf.catalog.pubsub.criteria;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import ddf.catalog.pubsub.criteria.contenttype.ContentTypeEvaluationCriteriaImpl;
import ddf.catalog.pubsub.criteria.entry.DadEvaluationCriteriaImpl;
import ddf.catalog.pubsub.criteria.entry.EntryEvaluationCriteriaImpl;
import ddf.catalog.pubsub.criteria.temporal.TemporalEvaluationCriteriaImpl;
import ddf.catalog.pubsub.predicate.ContentTypePredicate;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;

public class CriteriaImplTest {

  @Test
  public void testTemporalEvaluationCriteriaImplConstructor() {
    Calendar cal = Calendar.getInstance();
    Date start = cal.getTime();
    cal.add(Calendar.DAY_OF_MONTH, 1);
    Date end = cal.getTime();
    cal.add(Calendar.HOUR, -12);
    Date input = cal.getTime();

    TemporalEvaluationCriteriaImpl criteria = new TemporalEvaluationCriteriaImpl(end, start, input);

    assertThat(criteria.getStart(), is(notNullValue()));
    assertThat(criteria.getEnd(), is(notNullValue()));
    assertThat(criteria.getInput(), is(notNullValue()));
    assertThat(criteria.getStart().getTime(), is(equalTo(start.getTime())));
    assertThat(criteria.getEnd().getTime(), is(equalTo(end.getTime())));
    assertThat(criteria.getInput().getTime(), is(equalTo(input.getTime())));
  }

  @Test
  public void testTemporalEvaluationCriteriaImplWithNullEnd() {
    Calendar cal = Calendar.getInstance();
    Date start = cal.getTime();
    Date input = cal.getTime();

    TemporalEvaluationCriteriaImpl criteria =
        new TemporalEvaluationCriteriaImpl(null, start, input);

    assertThat(criteria.getStart(), is(notNullValue()));
    assertThat(criteria.getEnd(), is(nullValue()));
    assertThat(criteria.getInput(), is(notNullValue()));
  }

  @Test(expected = NullPointerException.class)
  public void testTemporalEvaluationCriteriaImplWithNullStart() {
    Date input = new Date();
    new TemporalEvaluationCriteriaImpl(null, null, input);
  }

  @Test(expected = NullPointerException.class)
  public void testTemporalEvaluationCriteriaImplWithNullInput() {
    Date start = new Date();
    new TemporalEvaluationCriteriaImpl(null, start, null);
  }

  @Test
  public void testTemporalEvaluationCriteriaImplGettersReturnDefensiveCopies() {
    Calendar cal = Calendar.getInstance();
    Date start = cal.getTime();
    cal.add(Calendar.DAY_OF_MONTH, 1);
    Date end = cal.getTime();
    Date input = cal.getTime();

    TemporalEvaluationCriteriaImpl criteria = new TemporalEvaluationCriteriaImpl(end, start, input);

    // Get dates
    Date retrievedStart = criteria.getStart();
    Date retrievedEnd = criteria.getEnd();
    Date retrievedInput = criteria.getInput();

    // Modify retrieved dates
    retrievedStart.setTime(0);
    if (retrievedEnd != null) {
      retrievedEnd.setTime(0);
    }
    retrievedInput.setTime(0);

    // Original values should be unchanged
    assertThat(criteria.getStart().getTime(), is(equalTo(start.getTime())));
    assertThat(criteria.getEnd().getTime(), is(equalTo(end.getTime())));
    assertThat(criteria.getInput().getTime(), is(equalTo(input.getTime())));
  }

  @Test
  public void testEntryEvaluationCriteriaImplConstructor() {
    String id = "test-id-123";
    String inputId = "input-id-456";

    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(id, inputId);

    assertThat(criteria.getId(), is(equalTo(id)));
    assertThat(criteria.getInputId(), is(equalTo(inputId)));
  }

  @Test
  public void testEntryEvaluationCriteriaImplWithNullValues() {
    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getId(), is(nullValue()));
    assertThat(criteria.getInputId(), is(nullValue()));
  }

  @Test
  public void testEntryEvaluationCriteriaImplWithEmptyStrings() {
    String id = "";
    String inputId = "";

    EntryEvaluationCriteriaImpl criteria = new EntryEvaluationCriteriaImpl(id, inputId);

    assertThat(criteria.getId(), is(equalTo("")));
    assertThat(criteria.getInputId(), is(equalTo("")));
  }

  @Test
  public void testDadEvaluationCriteriaImplConstructor() throws Exception {
    URI dad = new URI("http://example.com/resource");
    URI inputDad = new URI("http://example.com/input-resource");

    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dad, inputDad);

    assertThat(criteria.getDad(), is(equalTo(dad)));
    assertThat(criteria.getInputDad(), is(equalTo(inputDad)));
  }

  @Test
  public void testDadEvaluationCriteriaImplWithSameUri() throws Exception {
    URI dad = new URI("http://example.com/resource");

    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dad, dad);

    assertThat(criteria.getDad(), is(equalTo(dad)));
    assertThat(criteria.getInputDad(), is(equalTo(dad)));
  }

  @Test
  public void testDadEvaluationCriteriaImplWithNullValues() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getDad(), is(nullValue()));
    assertThat(criteria.getInputDad(), is(nullValue()));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplConstructor() {
    ContentTypePredicate predicate = new ContentTypePredicate("nitf", "v20");
    String inputContentType = "nitf,v20";

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, inputContentType);

    assertThat(criteria.getContentType(), is(equalTo(predicate)));
    assertThat(criteria.getInputContentType(), is(equalTo(inputContentType)));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplWithNullPredicate() {
    String inputContentType = "nitf,v20";

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(null, inputContentType);

    assertThat(criteria.getContentType(), is(nullValue()));
    assertThat(criteria.getInputContentType(), is(equalTo(inputContentType)));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplWithNullInputContentType() {
    ContentTypePredicate predicate = new ContentTypePredicate("nitf", "v20");

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, null);

    assertThat(criteria.getContentType(), is(equalTo(predicate)));
    assertThat(criteria.getInputContentType(), is(nullValue()));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplWithEmptyInputContentType() {
    ContentTypePredicate predicate = new ContentTypePredicate("nitf", "v20");
    String inputContentType = "";

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, inputContentType);

    assertThat(criteria.getContentType(), is(equalTo(predicate)));
    assertThat(criteria.getInputContentType(), is(equalTo("")));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplWithTypeOnly() {
    ContentTypePredicate predicate = new ContentTypePredicate("nitf", null);
    String inputContentType = "nitf,v20";

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, inputContentType);

    assertThat(criteria.getContentType(), is(equalTo(predicate)));
    assertThat(criteria.getInputContentType(), is(equalTo(inputContentType)));
  }

  @Test
  public void testContentTypeEvaluationCriteriaImplWithVersionOnly() {
    ContentTypePredicate predicate = new ContentTypePredicate(null, "v20");
    String inputContentType = "nitf,v20";

    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, inputContentType);

    assertThat(criteria.getContentType(), is(equalTo(predicate)));
    assertThat(criteria.getInputContentType(), is(equalTo(inputContentType)));
  }
}
