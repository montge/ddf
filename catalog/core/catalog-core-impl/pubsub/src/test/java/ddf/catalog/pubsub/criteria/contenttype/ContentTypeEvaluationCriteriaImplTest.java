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
package ddf.catalog.pubsub.criteria.contenttype;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import ddf.catalog.pubsub.predicate.ContentTypePredicate;
import org.junit.jupiter.api.Test;

class ContentTypeEvaluationCriteriaImplTest {

  private static final String TEST_CONTENT_TYPE = "application/json";

  @Test
  void testConstructor() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, TEST_CONTENT_TYPE);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, TEST_CONTENT_TYPE);

    assertThat(criteria, instanceOf(ContentTypeEvaluationCriteria.class));
  }

  @Test
  void testGetContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, TEST_CONTENT_TYPE);

    assertThat(criteria.getContentType(), is(sameInstance(predicate)));
  }

  @Test
  void testGetInputContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, TEST_CONTENT_TYPE);

    assertThat(criteria.getInputContentType(), is(TEST_CONTENT_TYPE));
  }

  @Test
  void testWithNullPredicate() {
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(null, TEST_CONTENT_TYPE);

    assertThat(criteria.getContentType(), is(nullValue()));
    assertThat(criteria.getInputContentType(), is(TEST_CONTENT_TYPE));
  }

  @Test
  void testWithNullInputContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, null);

    assertThat(criteria.getContentType(), is(sameInstance(predicate)));
    assertThat(criteria.getInputContentType(), is(nullValue()));
  }

  @Test
  void testWithBothNull() {
    ContentTypeEvaluationCriteriaImpl criteria = new ContentTypeEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getContentType(), is(nullValue()));
    assertThat(criteria.getInputContentType(), is(nullValue()));
  }

  @Test
  void testWithEmptyInputContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, "");

    assertThat(criteria.getInputContentType(), is(""));
  }

  @Test
  void testWithXmlContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    String xmlContentType = "application/xml";
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, xmlContentType);

    assertThat(criteria.getInputContentType(), is(xmlContentType));
  }

  @Test
  void testWithTextPlainContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    String textContentType = "text/plain";
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, textContentType);

    assertThat(criteria.getInputContentType(), is(textContentType));
  }

  @Test
  void testWithHtmlContentType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    String htmlContentType = "text/html";
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, htmlContentType);

    assertThat(criteria.getInputContentType(), is(htmlContentType));
  }

  @Test
  void testWithComplexMimeType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    String complexContentType = "multipart/form-data; boundary=----WebKitFormBoundary";
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, complexContentType);

    assertThat(criteria.getInputContentType(), is(complexContentType));
  }

  @Test
  void testWithVendorMimeType() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    String vendorContentType = "application/vnd.ms-excel";
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, vendorContentType);

    assertThat(criteria.getInputContentType(), is(vendorContentType));
  }

  @Test
  void testPredicateIsSameReference() {
    ContentTypePredicate predicate = mock(ContentTypePredicate.class);
    ContentTypeEvaluationCriteriaImpl criteria =
        new ContentTypeEvaluationCriteriaImpl(predicate, TEST_CONTENT_TYPE);

    ContentTypePredicate retrieved1 = criteria.getContentType();
    ContentTypePredicate retrieved2 = criteria.getContentType();

    assertThat(retrieved1, is(sameInstance(retrieved2)));
  }

  @Test
  void testMultipleInstancesAreIndependent() {
    ContentTypePredicate predicate1 = mock(ContentTypePredicate.class);
    ContentTypePredicate predicate2 = mock(ContentTypePredicate.class);

    ContentTypeEvaluationCriteriaImpl criteria1 =
        new ContentTypeEvaluationCriteriaImpl(predicate1, "type1");
    ContentTypeEvaluationCriteriaImpl criteria2 =
        new ContentTypeEvaluationCriteriaImpl(predicate2, "type2");

    assertThat(criteria1.getContentType(), is(sameInstance(predicate1)));
    assertThat(criteria2.getContentType(), is(sameInstance(predicate2)));
    assertThat(criteria1.getInputContentType(), is("type1"));
    assertThat(criteria2.getInputContentType(), is("type2"));
  }
}
