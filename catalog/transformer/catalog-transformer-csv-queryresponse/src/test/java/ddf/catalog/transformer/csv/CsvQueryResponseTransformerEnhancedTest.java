/*
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.catalog.transformer.csv;

import static ddf.catalog.data.impl.BasicTypes.STRING_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CsvQueryResponseTransformerEnhancedTest {

  private CsvQueryResponseTransformer transformer;

  @Before
  public void setUp() {
    transformer = new CsvQueryResponseTransformer();
  }

  @Test
  public void testTransformEmptySourceResponse() throws CatalogTransformerException, IOException {
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Collections.emptyList());

    BinaryContent result = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(result, is(org.hamcrest.Matchers.notNullValue()));
    assertThat(result.getMimeTypeValue(), is("text/csv"));
  }

  @Test
  public void testTransformSingleResult() throws CatalogTransformerException, IOException {
    Metacard metacard = createMetacard("test-title", "test-source");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result));

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(binaryContent, is(org.hamcrest.Matchers.notNullValue()));
    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("title"));
    assertThat(csv, containsString("test-title"));
  }

  @Test
  public void testTransformMultipleResults() throws CatalogTransformerException, IOException {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Metacard metacard = createMetacard("Title " + i, "source-" + i);
      results.add(new ResultImpl(metacard));
    }

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(results);

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    String[] lines = csv.split("\r\n");

    // Should have header + 5 data rows
    assertThat(lines.length, is(6));
    assertThat(csv, containsString("Title 0"));
    assertThat(csv, containsString("Title 4"));
  }

  @Test
  public void testTransformWithRelevanceScores() throws CatalogTransformerException, IOException {
    Metacard metacard1 = createMetacard("First", "source1");
    Metacard metacard2 = createMetacard("Second", "source2");

    ResultImpl result1 = new ResultImpl(metacard1);
    result1.setRelevanceScore(0.95);

    ResultImpl result2 = new ResultImpl(metacard2);
    result2.setRelevanceScore(0.75);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result1, result2));

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("First"));
    assertThat(csv, containsString("Second"));
  }

  @Test
  public void testTransformWithDistanceScores() throws CatalogTransformerException, IOException {
    Metacard metacard1 = createMetacard("Near", "source1");
    Metacard metacard2 = createMetacard("Far", "source2");

    ResultImpl result1 = new ResultImpl(metacard1);
    result1.setDistanceInMeters(100.0);

    ResultImpl result2 = new ResultImpl(metacard2);
    result2.setDistanceInMeters(5000.0);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result1, result2));

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("Near"));
    assertThat(csv, containsString("Far"));
  }

  @Test
  public void testTransformWithColumnOrderArgument()
      throws CatalogTransformerException, IOException {
    Set<AttributeDescriptor> descriptors =
        Sets.newHashSet(
            new AttributeDescriptorImpl("title", false, false, false, false, STRING_TYPE),
            new AttributeDescriptorImpl("description", false, false, false, false, STRING_TYPE),
            new AttributeDescriptorImpl("id", false, false, false, false, STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("title", "Test"));
    metacard.setAttribute(new AttributeImpl("description", "Desc"));
    metacard.setAttribute(new AttributeImpl("id", "123"));

    Result result = new ResultImpl(metacard);
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result));

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(CsvTransformerSupport.COLUMN_ORDER_KEY, "description,title,id");

    BinaryContent binaryContent = transformer.transform(sourceResponse, arguments);

    String csv = new String(binaryContent.getByteArray());
    String[] lines = csv.split("\r\n");
    String[] headers = lines[0].split(",");

    assertThat(headers[0], is("description"));
    assertThat(headers[1], is("title"));
    assertThat(headers[2], is("id"));
  }

  @Test
  public void testTransformWithColumnAliases() throws CatalogTransformerException, IOException {
    Metacard metacard = createMetacard("Test", "source1");

    Result result = new ResultImpl(metacard);
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result));

    Map<String, Serializable> arguments = new HashMap<>();
    Map<String, String> aliases = new HashMap<>();
    aliases.put("title", "Product Name");
    arguments.put(CsvTransformerSupport.COLUMN_ALIAS_KEY, (Serializable) aliases);

    BinaryContent binaryContent = transformer.transform(sourceResponse, arguments);

    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("Product Name"));
  }

  @Test
  public void testTransformWithHiddenFields() throws CatalogTransformerException, IOException {
    Set<AttributeDescriptor> descriptors =
        Sets.newHashSet(
            new AttributeDescriptorImpl("title", false, false, false, false, STRING_TYPE),
            new AttributeDescriptorImpl("secret", false, false, false, false, STRING_TYPE));

    MetacardType type = new MetacardTypeImpl("test", descriptors);
    MetacardImpl metacard = new MetacardImpl(type);
    metacard.setAttribute(new AttributeImpl("title", "Public"));
    metacard.setAttribute(new AttributeImpl("secret", "Classified"));

    Result result = new ResultImpl(metacard);
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result));

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(
        CsvTransformerSupport.HIDDEN_FIELDS_KEY, (Serializable) Sets.newHashSet("secret"));

    BinaryContent binaryContent = transformer.transform(sourceResponse, arguments);

    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("title"));
    assertThat(csv, containsString("Public"));
    assertThat(csv, org.hamcrest.Matchers.not(containsString("secret")));
    assertThat(csv, org.hamcrest.Matchers.not(containsString("Classified")));
  }

  @Test
  public void testTransformWithNullMetacardInResult()
      throws CatalogTransformerException, IOException {
    Result result1 = new ResultImpl(createMetacard("Valid", "source1"));
    Result result2 = new ResultImpl(null); // Null metacard
    Result result3 = new ResultImpl(createMetacard("Also Valid", "source3"));

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result1, result2, result3));

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    String[] lines = csv.split("\r\n");

    // Should have 3 rows: header + 2 valid results (null result skipped)
    assertThat(lines.length, is(3));
    assertThat(csv, containsString("Valid"));
    assertThat(csv, containsString("Also Valid"));
  }

  @Test
  public void testTransformWithLargeResultSet() throws CatalogTransformerException, IOException {
    List<Result> results = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      Metacard metacard = createMetacard("Title " + i, "source");
      results.add(new ResultImpl(metacard));
    }

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(results);

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    String[] lines = csv.split("\r\n");

    // Should have header + 1000 data rows
    assertThat(lines.length, is(1001));
  }

  @Test
  public void testTransformWithNullArguments() throws CatalogTransformerException, IOException {
    Metacard metacard = createMetacard("Test", "source");
    Result result = new ResultImpl(metacard);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result));

    BinaryContent binaryContent = transformer.transform(sourceResponse, null);

    assertThat(binaryContent, is(org.hamcrest.Matchers.notNullValue()));
    String csv = new String(binaryContent.getByteArray());
    assertThat(csv, containsString("Test"));
  }

  @Test
  public void testTransformMimeType() throws CatalogTransformerException {
    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Collections.emptyList());

    BinaryContent result = transformer.transform(sourceResponse, Collections.emptyMap());

    assertThat(result.getMimeType().toString(), is("text/csv"));
    assertThat(result.getMimeTypeValue(), is("text/csv"));
  }

  @Test
  public void testTransformWithMixedMetacardTypes()
      throws CatalogTransformerException, IOException {
    // First metacard with title and description
    Set<AttributeDescriptor> descriptors1 =
        Sets.newHashSet(
            new AttributeDescriptorImpl("title", false, false, false, false, STRING_TYPE),
            new AttributeDescriptorImpl("description", false, false, false, false, STRING_TYPE));
    MetacardType type1 = new MetacardTypeImpl("type1", descriptors1);
    MetacardImpl metacard1 = new MetacardImpl(type1);
    metacard1.setAttribute(new AttributeImpl("title", "First"));
    metacard1.setAttribute(new AttributeImpl("description", "First Desc"));

    // Second metacard with title and category
    Set<AttributeDescriptor> descriptors2 =
        Sets.newHashSet(
            new AttributeDescriptorImpl("title", false, false, false, false, STRING_TYPE),
            new AttributeDescriptorImpl("category", false, false, false, false, STRING_TYPE));
    MetacardType type2 = new MetacardTypeImpl("type2", descriptors2);
    MetacardImpl metacard2 = new MetacardImpl(type2);
    metacard2.setAttribute(new AttributeImpl("title", "Second"));
    metacard2.setAttribute(new AttributeImpl("category", "News"));

    Result result1 = new ResultImpl(metacard1);
    Result result2 = new ResultImpl(metacard2);

    SourceResponse sourceResponse = mock(SourceResponse.class);
    when(sourceResponse.getResults()).thenReturn(Lists.newArrayList(result1, result2));

    BinaryContent binaryContent = transformer.transform(sourceResponse, Collections.emptyMap());

    String csv = new String(binaryContent.getByteArray());
    // Should contain attributes from both metacard types
    assertThat(csv, containsString("title"));
    assertThat(csv, containsString("description"));
    assertThat(csv, containsString("category"));
  }

  private Metacard createMetacard(String title, String sourceId) {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle(title);
    metacard.setSourceId(sourceId);
    return metacard;
  }
}
