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

import static ddf.catalog.data.impl.BasicTypes.BINARY_TYPE;
import static ddf.catalog.data.impl.BasicTypes.DATE_TYPE;
import static ddf.catalog.data.impl.BasicTypes.DOUBLE_TYPE;
import static ddf.catalog.data.impl.BasicTypes.GEO_TYPE;
import static ddf.catalog.data.impl.BasicTypes.INTEGER_TYPE;
import static ddf.catalog.data.impl.BasicTypes.OBJECT_TYPE;
import static ddf.catalog.data.impl.BasicTypes.STRING_TYPE;
import static ddf.catalog.transformer.csv.CsvTransformerSupport.COLUMN_ALIAS_KEY;
import static ddf.catalog.transformer.csv.CsvTransformerSupport.COLUMN_ORDER_KEY;
import static ddf.catalog.transformer.csv.CsvTransformerSupport.HIDDEN_FIELDS_KEY;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.MetacardType;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CsvTransformerSupportTest {

  private static final String METACARD_TYPE_NAME = "test-type";

  @Test
  public void testTransformWithNoArguments() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("count", INTEGER_TYPE)),
            new AttributeImpl("title", "Test Title"),
            new AttributeImpl("count", 5));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    assertThat(result, is(org.hamcrest.Matchers.notNullValue()));
    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("title"));
    assertThat(csv, containsString("count"));
    assertThat(csv, containsString("Test Title"));
    assertThat(csv, containsString("5"));
  }

  @Test
  public void testTransformWithColumnOrder() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("count", INTEGER_TYPE),
                buildAttributeDescriptor("score", DOUBLE_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("count", 10),
            new AttributeImpl("score", 98.5));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(COLUMN_ORDER_KEY, Lists.newArrayList("score", "title", "count"));

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    String[] lines = csv.split("\r\n");
    String[] headers = lines[0].split(",");

    // Verify order
    assertThat(headers[0], is("score"));
    assertThat(headers[1], is("title"));
    assertThat(headers[2], is("count"));
  }

  @Test
  public void testTransformWithColumnOrderAsString()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("a", STRING_TYPE),
                buildAttributeDescriptor("b", STRING_TYPE),
                buildAttributeDescriptor("c", STRING_TYPE)),
            new AttributeImpl("a", "A"),
            new AttributeImpl("b", "B"),
            new AttributeImpl("c", "C"));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = singletonMap(COLUMN_ORDER_KEY, "c,b,a");

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    String[] lines = csv.split("\r\n");
    String[] headers = lines[0].split(",");

    assertThat(headers[0], is("c"));
    assertThat(headers[1], is("b"));
    assertThat(headers[2], is("a"));
  }

  @Test
  public void testTransformWithColumnAliases() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("count", INTEGER_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("count", 5));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = new HashMap<>();
    Map<String, String> aliases = new HashMap<>();
    aliases.put("title", "Product Name");
    aliases.put("count", "Quantity");
    arguments.put(COLUMN_ALIAS_KEY, (Serializable) aliases);

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("Product Name"));
    assertThat(csv, containsString("Quantity"));
    assertThat(csv, not(containsString("title,")));
    assertThat(csv, not(containsString("count,")));
  }

  @Test
  public void testTransformWithHiddenFields() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("secret", STRING_TYPE),
                buildAttributeDescriptor("count", INTEGER_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("secret", "classified"),
            new AttributeImpl("count", 5));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(HIDDEN_FIELDS_KEY, (Serializable) Sets.newHashSet("secret"));

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("title"));
    assertThat(csv, containsString("count"));
    assertThat(csv, not(containsString("secret")));
    assertThat(csv, not(containsString("classified")));
  }

  @Test
  public void testTransformFiltersOutBinaryAndObjectTypes()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("data", BINARY_TYPE),
                buildAttributeDescriptor("obj", OBJECT_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("data", new byte[] {1, 2, 3}),
            new AttributeImpl("obj", new HashMap<>()));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("title"));
    assertThat(csv, not(containsString("data")));
    assertThat(csv, not(containsString("obj")));
  }

  @Test
  public void testTransformWithMultipleMetacards() throws CatalogTransformerException, IOException {
    Metacard metacard1 =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "First"));

    Metacard metacard2 =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Second"));

    Metacard metacard3 =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Third"));

    List<Metacard> metacards = Lists.newArrayList(metacard1, metacard2, metacard3);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    String[] lines = csv.split("\r\n");

    assertThat(lines.length, is(4)); // 1 header + 3 data rows
    assertThat(csv, containsString("First"));
    assertThat(csv, containsString("Second"));
    assertThat(csv, containsString("Third"));
  }

  @Test
  public void testTransformWithEmptyMetacardList() throws CatalogTransformerException, IOException {
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(
            Collections.emptyList(), Collections.emptyMap());

    String csv = new String(result.getByteArray());
    // Should have empty or minimal content
    assertThat(result, is(org.hamcrest.Matchers.notNullValue()));
  }

  @Test
  public void testTransformIncludesMetacardTypeWhenNoColumnOrderSpecified()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Test"));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString(MetacardType.METACARD_TYPE));
    assertThat(csv, containsString(METACARD_TYPE_NAME));
  }

  @Test
  public void testTransformIncludesMetacardTypeWhenExplicitlyRequested()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Test"));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments =
        singletonMap(COLUMN_ORDER_KEY, Lists.newArrayList("title", MetacardType.METACARD_TYPE));

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString(MetacardType.METACARD_TYPE));
    assertThat(csv, containsString(METACARD_TYPE_NAME));
  }

  @Test
  public void testTransformExcludesMetacardTypeWhenNotInColumnOrder()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Test"));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments =
        singletonMap(COLUMN_ORDER_KEY, Lists.newArrayList("title"));

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    String[] lines = csv.split("\r\n");
    String[] headers = lines[0].split(",");

    // Should only have title, not metacard-type
    assertThat(headers.length, is(1));
    assertThat(headers[0], is("title"));
  }

  @Test
  public void testTransformWithDateAttribute() throws CatalogTransformerException, IOException {
    Date testDate = new Date(1609459200000L); // 2021-01-01T00:00:00.000Z

    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("created", DATE_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("created", testDate));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("created"));
    assertThat(csv, containsString("2021-01-01"));
  }

  @Test
  public void testTransformWithGeometryAttribute() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("location", GEO_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("location", "POINT (1 2)"));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("location"));
    assertThat(csv, containsString("POINT (1 2)"));
  }

  @Test
  public void testTransformWithMultiValuedAttribute()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildMultiValuedAttributeDescriptor("tags", STRING_TYPE)),
            new AttributeImpl("title", "Test"),
            new ddf.catalog.data.impl.AttributeImpl(
                "tags", (Serializable) Lists.newArrayList("tag1", "tag2", "tag3")));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result =
        CsvTransformerSupport.transformWithArguments(metacards, Collections.emptyMap());

    String csv = new String(result.getByteArray());
    assertThat(csv, containsString("tags"));
    // Multi-valued attributes should be pipe-separated
    assertThat(csv, containsString("tag1|tag2|tag3"));
  }

  @Test
  public void testTransformWithNullArguments() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Test"));

    List<Metacard> metacards = singletonList(metacard);
    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, null);

    assertThat(result, is(org.hamcrest.Matchers.notNullValue()));
  }

  @Test
  public void testTransformWithBlankColumnOrderString()
      throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(buildAttributeDescriptor("title", STRING_TYPE)),
            new AttributeImpl("title", "Test"));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = singletonMap(COLUMN_ORDER_KEY, "");

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    // Should default to showing all attributes
    assertThat(csv, containsString("title"));
  }

  @Test
  public void testTransformWithCombinedOptions() throws CatalogTransformerException, IOException {
    Metacard metacard =
        buildMetacard(
            Sets.newHashSet(
                buildAttributeDescriptor("title", STRING_TYPE),
                buildAttributeDescriptor("description", STRING_TYPE),
                buildAttributeDescriptor("secret", STRING_TYPE),
                buildAttributeDescriptor("count", INTEGER_TYPE)),
            new AttributeImpl("title", "Test"),
            new AttributeImpl("description", "Desc"),
            new AttributeImpl("secret", "classified"),
            new AttributeImpl("count", 10));

    List<Metacard> metacards = singletonList(metacard);
    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put(COLUMN_ORDER_KEY, Lists.newArrayList("count", "title", "description"));
    arguments.put(HIDDEN_FIELDS_KEY, (Serializable) Sets.newHashSet("secret"));
    Map<String, String> aliases = new HashMap<>();
    aliases.put("title", "Name");
    aliases.put("count", "Total");
    arguments.put(COLUMN_ALIAS_KEY, (Serializable) aliases);

    BinaryContent result = CsvTransformerSupport.transformWithArguments(metacards, arguments);

    String csv = new String(result.getByteArray());
    String[] lines = csv.split("\r\n");
    String[] headers = lines[0].split(",");

    // Verify order
    assertThat(headers[0], is("Total"));
    assertThat(headers[1], is("Name"));
    assertThat(headers[2], is("description"));

    // Verify hidden field is excluded
    assertThat(csv, not(containsString("secret")));
    assertThat(csv, not(containsString("classified")));
  }

  private static AttributeDescriptor buildAttributeDescriptor(
      String name, ddf.catalog.data.AttributeType<?> type) {
    return new AttributeDescriptorImpl(name, true, true, true, false, type);
  }

  private static AttributeDescriptor buildMultiValuedAttributeDescriptor(
      String name, ddf.catalog.data.AttributeType<?> type) {
    return new AttributeDescriptorImpl(name, true, true, true, true, type);
  }

  private Metacard buildMetacard(
      final Set<AttributeDescriptor> attributeDescriptors,
      final ddf.catalog.data.Attribute... attributes) {
    MetacardType metacardType = new MetacardTypeImpl(METACARD_TYPE_NAME, attributeDescriptors);
    Metacard metacard = new MetacardImpl(metacardType);
    for (ddf.catalog.data.Attribute attribute : attributes) {
      metacard.setAttribute(attribute);
    }
    return metacard;
  }
}
