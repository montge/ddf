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
package ddf.catalog.pubsub.criteria.entry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DadEvaluationCriteriaImplTest {

  private static final String DAD_URI_STRING = "content:abc123";
  private static final String INPUT_DAD_URI_STRING = "content:xyz789";

  private URI dadUri;
  private URI inputDadUri;

  @BeforeEach
  void setUp() throws URISyntaxException {
    dadUri = new URI(DAD_URI_STRING);
    inputDadUri = new URI(INPUT_DAD_URI_STRING);
  }

  @Test
  void testConstructor() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria, is(notNullValue()));
  }

  @Test
  void testImplementsInterface() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria, instanceOf(DadEvaluationCriteria.class));
  }

  @Test
  void testGetDad() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria.getDad(), is(sameInstance(dadUri)));
  }

  @Test
  void testGetInputDad() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria.getInputDad(), is(sameInstance(inputDadUri)));
  }

  @Test
  void testGetDadReturnsCorrectUri() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria.getDad().toString(), is(DAD_URI_STRING));
  }

  @Test
  void testGetInputDadReturnsCorrectUri() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);

    assertThat(criteria.getInputDad().toString(), is(INPUT_DAD_URI_STRING));
  }

  @Test
  void testWithNullDad() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(null, inputDadUri);

    assertThat(criteria.getDad(), is(nullValue()));
    assertThat(criteria.getInputDad(), is(sameInstance(inputDadUri)));
  }

  @Test
  void testWithNullInputDad() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, null);

    assertThat(criteria.getDad(), is(sameInstance(dadUri)));
    assertThat(criteria.getInputDad(), is(nullValue()));
  }

  @Test
  void testWithBothNullUris() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(null, null);

    assertThat(criteria.getDad(), is(nullValue()));
    assertThat(criteria.getInputDad(), is(nullValue()));
  }

  @Test
  void testWithSameUri() {
    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(dadUri, dadUri);

    assertThat(criteria.getDad(), is(sameInstance(criteria.getInputDad())));
  }

  @Test
  void testWithHttpUri() throws URISyntaxException {
    URI httpDad = new URI("http://example.com/resource/123");
    URI httpInputDad = new URI("http://example.com/resource/456");

    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(httpDad, httpInputDad);

    assertThat(criteria.getDad().getScheme(), is("http"));
    assertThat(criteria.getInputDad().getScheme(), is("http"));
  }

  @Test
  void testWithFileUri() throws URISyntaxException {
    URI fileDad = new URI("file:///path/to/file.txt");
    URI fileInputDad = new URI("file:///path/to/other.txt");

    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(fileDad, fileInputDad);

    assertThat(criteria.getDad().getScheme(), is("file"));
    assertThat(criteria.getInputDad().getScheme(), is("file"));
  }

  @Test
  void testMultipleInstancesAreIndependent() {
    DadEvaluationCriteriaImpl criteria1 = new DadEvaluationCriteriaImpl(dadUri, inputDadUri);
    DadEvaluationCriteriaImpl criteria2 = new DadEvaluationCriteriaImpl(inputDadUri, dadUri);

    assertThat(criteria1.getDad(), is(criteria2.getInputDad()));
    assertThat(criteria1.getInputDad(), is(criteria2.getDad()));
  }

  @Test
  void testWithUrnUri() throws URISyntaxException {
    URI urnDad = new URI("urn:catalog:id:abc123");
    URI urnInputDad = new URI("urn:catalog:id:xyz789");

    DadEvaluationCriteriaImpl criteria = new DadEvaluationCriteriaImpl(urnDad, urnInputDad);

    assertThat(criteria.getDad().getScheme(), is("urn"));
    assertThat(criteria.getInputDad().getScheme(), is("urn"));
  }
}
