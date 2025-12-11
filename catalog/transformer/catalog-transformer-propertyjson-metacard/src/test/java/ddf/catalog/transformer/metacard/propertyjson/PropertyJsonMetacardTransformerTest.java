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
package ddf.catalog.transformer.metacard.propertyjson;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive test harness for PropertyJsonMetacardTransformer. Tests JSON serialization, data
 * type handling, encoding, and security.
 *
 * <p>SECURITY CRITICAL: JSON transformers must properly escape: - Script injection attempts -
 * Special characters - Unicode and encoding issues
 */
public class PropertyJsonMetacardTransformerTest {

  private PropertyJsonMetacardTransformer transformer;

  private static final Gson GSON = new Gson();

  @BeforeEach
  public void setUp() {
    transformer = new PropertyJsonMetacardTransformer();
  }

  @Test
  public void testTransformBasicMetacard() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-123");
    metacard.setTitle("Test Title");
    metacard.setDescription("Test Description");

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    assertThat(result.getMimeType().toString(), containsString("application/json"));

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.get("id").getAsString(), is("test-id-123"));
    assertThat(properties.get("title").getAsString(), is("Test Title"));
    assertThat(properties.get("description").getAsString(), is("Test Description"));
  }

  @Test
  public void testTransformWithNullMetacard() {
    try {
      transformer.transform(null, null);
      assertTrue(false, "Should have thrown CatalogTransformerException");
    } catch (CatalogTransformerException e) {
      assertThat(e.getMessage(), containsString("null metacard"));
    }
  }

  @Test
  public void testTransformWithNullArguments() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithEmptyArguments() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    Map<String, Serializable> emptyArgs = new HashMap<>();
    BinaryContent result = transformer.transform(metacard, emptyArgs);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithAllBasicDataTypes() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.addAll(MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
    descriptors.add(
        new AttributeDescriptorImpl(
            "test-string", true, true, true, false, BasicTypes.STRING_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("test-int", true, true, true, false, BasicTypes.INTEGER_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("test-long", true, true, true, false, BasicTypes.LONG_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl(
            "test-double", true, true, true, false, BasicTypes.DOUBLE_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("test-float", true, true, true, false, BasicTypes.FLOAT_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl(
            "test-boolean", true, true, true, false, BasicTypes.BOOLEAN_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl("test-date", true, true, true, false, BasicTypes.DATE_TYPE));
    descriptors.add(
        new AttributeDescriptorImpl(
            "test-binary", true, true, true, false, BasicTypes.BINARY_TYPE));

    MetacardTypeImpl customType = new MetacardTypeImpl("test-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(customType);

    metacard.setAttribute("test-string", "string value");
    metacard.setAttribute("test-int", 42);
    metacard.setAttribute("test-long", 9876543210L);
    metacard.setAttribute("test-double", 3.14159);
    metacard.setAttribute("test-float", 2.71828f);
    metacard.setAttribute("test-boolean", true);
    metacard.setAttribute("test-date", new Date());
    metacard.setAttribute("test-binary", new byte[] {0x01, 0x02, 0x03});

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.get("test-string").getAsString(), is("string value"));
    assertThat(properties.get("test-int").getAsInt(), is(42));
    assertThat(properties.get("test-long").getAsLong(), is(9876543210L));
    assertThat(properties.get("test-double").getAsDouble(), is(3.14159));
    assertThat(properties.get("test-boolean").getAsBoolean(), is(true));
    assertThat(properties.has("test-date"), is(true));
    assertThat(properties.has("test-binary"), is(true));
  }

  @Test
  public void testTransformWithMultiValuedAttributes() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.addAll(MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
    descriptors.add(
        new AttributeDescriptorImpl("tags", true, true, true, true, BasicTypes.STRING_TYPE));

    MetacardTypeImpl customType = new MetacardTypeImpl("test-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(customType);

    List<Serializable> tags = new ArrayList<>();
    tags.add("tag1");
    tags.add("tag2");
    tags.add("tag3");

    metacard.setAttribute("tags", (Serializable) tags);

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.has("tags"), is(true));
    JsonArray tagsArray = properties.getAsJsonArray("tags");
    assertThat(tagsArray.size(), is(3));
    assertThat(tagsArray.get(0).getAsString(), is("tag1"));
    assertThat(tagsArray.get(1).getAsString(), is("tag2"));
    assertThat(tagsArray.get(2).getAsString(), is("tag3"));
  }

  @Test
  public void testTransformWithSpecialCharacters() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test & Title <special> \"quoted\" 'single'");
    metacard.setDescription("Line 1\nLine 2\tTabbed");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // JSON should properly escape special characters
    assertThat(
        properties.get("title").getAsString(), is("Test & Title <special> \"quoted\" 'single'"));
    assertThat(properties.get("description").getAsString(), containsString("Line 1"));
    assertThat(properties.get("description").getAsString(), containsString("Line 2"));
  }

  @Test
  public void testTransformWithUnicodeCharacters() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("测试标题 Тест العنوان");
    metacard.setDescription("Unicode: ñ é ü ö ä 日本語 한국어");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.get("title").getAsString(), is("测试标题 Тест العنوان"));
    assertThat(properties.get("description").getAsString(), containsString("日本語"));
  }

  @Test
  public void testTransformWithEmojiCharacters() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test with emoji 😀 🎉 ⚡");
    metacard.setDescription("Description with 🔥 emojis 🚀");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // Emojis should be preserved in JSON
    assertThat(properties.get("title").getAsString(), containsString("emoji"));
  }

  @Test
  public void testTransformWithNullAttributeValues() throws Exception {
    Set<AttributeDescriptor> descriptors = new HashSet<>();
    descriptors.addAll(MetacardImpl.BASIC_METACARD.getAttributeDescriptors());
    descriptors.add(
        new AttributeDescriptorImpl(
            "nullable-field", true, true, true, false, BasicTypes.STRING_TYPE));

    MetacardTypeImpl customType = new MetacardTypeImpl("test-type", descriptors);
    MetacardImpl metacard = new MetacardImpl(customType);

    metacard.setId("test-id");
    metacard.setAttribute("nullable-field", null);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // Should have id but nullable-field might not be present or be null
    assertThat(properties.has("id"), is(true));
  }

  @Test
  public void testTransformWithEmptyStringAttributes() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("");
    metacard.setDescription("");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.get("title").getAsString(), is(""));
    assertThat(properties.get("description").getAsString(), is(""));
  }

  @Test
  public void testTransformWithGeometry() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setLocation("POINT(10.0 20.0)");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.has("location"), is(true));
    assertThat(properties.get("location").getAsString(), containsString("POINT"));
  }

  @Test
  public void testTransformWithComplexGeometry() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    String wkt = "POLYGON((30 10, 40 40, 20 40, 10 20, 30 10))";
    metacard.setLocation(wkt);

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.get("location").getAsString(), is(wkt));
  }

  @Test
  public void testTransformWithDateFormatting() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    Date testDate = new Date(1609459200000L); // 2021-01-01 00:00:00 UTC
    metacard.setCreatedDate(testDate);
    metacard.setModifiedDate(testDate);

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // Date should be formatted as ISO 8601
    String createdDate = properties.get("created").getAsString();
    assertThat(createdDate, notNullValue());
    assertThat(createdDate, containsString("2021")); // Should contain the year
  }

  @Test
  public void testTransformWithBinaryData() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    byte[] thumbnailData = new byte[] {0x00, 0x01, 0x02, 0x03, (byte) 0xFF};
    metacard.setThumbnail(thumbnailData);

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // Binary data should be Base64 encoded
    assertThat(properties.has("thumbnail"), is(true));
    String thumbnailEncoded = properties.get("thumbnail").getAsString();
    assertThat(thumbnailEncoded, notNullValue());
    assertThat(thumbnailEncoded.length() > 0, is(true));
  }

  @Test
  public void testTransformIncludesMetacardType() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.has("metacard-type"), is(true));
    assertThat(properties.get("metacard-type").getAsString(), notNullValue());
  }

  @Test
  public void testTransformIncludesSourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setSourceId("test-source-123");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    assertThat(properties.has("source-id"), is(true));
    assertThat(properties.get("source-id").getAsString(), is("test-source-123"));
  }

  @Test
  public void testTransformWithoutSourceId() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    // Don't set source ID

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // source-id should not be present or be null
    if (properties.has("source-id")) {
      assertThat(properties.get("source-id").isJsonNull(), is(true));
    }
  }

  /**
   * SECURITY TEST: Verify JSON injection prevention Metacard values should be properly escaped to
   * prevent JSON injection
   */
  @Test
  public void testTransformPreventsJsonInjection() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    // Attempt to inject JSON
    String injectionAttempt = "\", \"injected\": \"hacked\", \"title\": \"";
    metacard.setTitle(injectionAttempt);

    BinaryContent result = transformer.transform(metacard, null);

    String jsonString = inputStreamToString(result.getInputStream());

    // The injection attempt should be escaped, not executed
    assertThat(jsonString, not(containsString("\"injected\": \"hacked\"")));

    // Parse should succeed without injected fields
    JsonObject json = GSON.fromJson(jsonString, JsonObject.class);
    JsonObject properties = json.getAsJsonObject("properties");

    // Should not have the injected field
    assertThat(properties.has("injected"), is(false));
  }

  /** SECURITY TEST: Verify script injection prevention */
  @Test
  public void testTransformPreventsScriptInjection() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    String scriptAttempt = "<script>alert('XSS')</script>";
    metacard.setTitle(scriptAttempt);
    metacard.setDescription("Normal text with <script>malicious()</script> content");

    BinaryContent result = transformer.transform(metacard, null);

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");

    // Script tags should be present but escaped/safe in JSON
    String title = properties.get("title").getAsString();
    assertThat(title, is(scriptAttempt)); // Should be literal string, not executed
  }

  @Test
  public void testTransformWithVeryLongStrings() throws Exception {
    MetacardImpl metacard = new MetacardImpl();

    // Create a very long string (100KB)
    StringBuilder longString = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longString.append("This is line ").append(i).append(" of the long text. ");
    }

    metacard.setDescription(longString.toString());

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    assertThat(result.getSize() > 0, is(true));

    JsonObject json = parseJson(result);
    JsonObject properties = json.getAsJsonObject("properties");
    assertThat(properties.get("description").getAsString().length() > 50000, is(true));
  }

  @Test
  public void testConvertToJsonStaticMethod() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("static-test-id");
    metacard.setTitle("Static Method Test");

    Map<String, Object> json = PropertyJsonMetacardTransformer.convertToJSON(metacard);

    assertThat(json, notNullValue());
    assertThat(json.containsKey("properties"), is(true));

    @SuppressWarnings("unchecked")
    Map<String, Object> properties = (Map<String, Object>) json.get("properties");
    assertThat(properties.get("id"), is("static-test-id"));
    assertThat(properties.get("title"), is("Static Method Test"));
  }

  @Test
  public void testConvertToJsonWithExclusionList() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test");
    metacard.setThumbnail(new byte[] {0x01, 0x02});

    List<AttributeType.AttributeFormat> dontInclude =
        Collections.singletonList(AttributeType.AttributeFormat.BINARY);

    Map<String, Object> json = PropertyJsonMetacardTransformer.convertToJSON(metacard, dontInclude);

    @SuppressWarnings("unchecked")
    Map<String, Object> properties = (Map<String, Object>) json.get("properties");

    // BINARY attributes should be excluded
    assertThat(properties.containsKey("thumbnail"), is(false));
    // Other attributes should be present
    assertThat(properties.containsKey("id"), is(true));
    assertThat(properties.containsKey("title"), is(true));
  }

  @Test
  public void testToStringMethod() {
    String result = transformer.toString();

    assertThat(result, notNullValue());
    assertThat(result, containsString("MetacardTransformer"));
    assertThat(result, containsString("propertyjson"));
    assertThat(result, containsString("application/json"));
  }

  @Test
  public void testMimeTypeIsCorrect() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test");

    BinaryContent result = transformer.transform(metacard, null);

    String mimeType = result.getMimeType().toString();
    assertThat(mimeType, containsString("application"));
    assertThat(mimeType, containsString("json"));
  }

  @Test
  public void testTransformOutputIsValidJson() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    metacard.setTitle("Test Title");
    metacard.setDescription("Test Description");

    BinaryContent result = transformer.transform(metacard, null);

    String jsonString = inputStreamToString(result.getInputStream());

    // Should be parseable as JSON without errors
    JsonElement parsed = JsonParser.parseString(jsonString);
    assertThat(parsed.isJsonObject(), is(true));
  }

  // Helper methods

  private JsonObject parseJson(BinaryContent content) throws IOException {
    String jsonString = inputStreamToString(content.getInputStream());
    return GSON.fromJson(jsonString, JsonObject.class);
  }

  private String inputStreamToString(InputStream is) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length;
    while ((length = is.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    return result.toString(StandardCharsets.UTF_8.name());
  }
}
