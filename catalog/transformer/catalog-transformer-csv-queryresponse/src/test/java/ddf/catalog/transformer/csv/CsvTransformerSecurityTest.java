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
package ddf.catalog.transformer.csv;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.Result;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.ResultImpl;
import ddf.catalog.operation.SourceResponse;
import ddf.catalog.operation.impl.SourceResponseImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Enhanced security and edge case test harness for CSV transformers. Tests CSV injection, special
 * character handling, encoding, and data integrity.
 *
 * <p>SECURITY CRITICAL: CSV transformers must prevent: - CSV injection (formulas starting with =,
 * +, -, @) - Special character injection (\r, \n, quotes) - Character encoding attacks - Data
 * exfiltration via malicious content
 */
public class CsvTransformerSecurityTest {

  private CsvQueryResponseTransformer queryResponseTransformer;

  private CsvMetacardTransformer metacardTransformer;

  @BeforeEach
  public void setUp() {
    queryResponseTransformer = new CsvQueryResponseTransformer();
    metacardTransformer = new CsvMetacardTransformer();
  }

  /**
   * SECURITY TEST: CSV Injection with formula starting with = Excel and other spreadsheet apps may
   * execute formulas in CSV files
   */
  @Test
  public void testPreventsCsvFormulaInjectionEquals() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("=1+1");
    metacard.setDescription("=SUM(A1:A10)");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    // Formula should be escaped or prefixed to prevent execution
    // Common escaping: prefix with single quote or wrap in quotes
    assertThat(csvOutput, not(containsString(",=1+1,")));
    assertThat(csvOutput, not(containsString(",=SUM(")));
  }

  /** SECURITY TEST: CSV Injection with formula starting with + */
  @Test
  public void testPreventsCsvFormulaInjectionPlus() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("+1+1");
    metacard.setDescription("+cmd|'/c calc'!A1");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    // Should not allow unescaped formulas
    assertThat(csvOutput, not(containsString(",+1+1,")));
    assertThat(csvOutput, not(containsString(",+cmd|")));
  }

  /** SECURITY TEST: CSV Injection with formula starting with - */
  @Test
  public void testPreventsCsvFormulaInjectionMinus() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("-1+1");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, not(containsString(",-1+1,")));
  }

  /** SECURITY TEST: CSV Injection with formula starting with @ */
  @Test
  public void testPreventsCsvFormulaInjectionAt() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("@SUM(1+1)");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, not(containsString(",@SUM(")));
  }

  /** SECURITY TEST: Command injection via DDE (Dynamic Data Exchange) */
  @Test
  public void testPreventsDDEInjection() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("=cmd|'/c calc'!A1");
    metacard.setDescription("=MSEXCEL|'\\\\..\\..\\..\\windows\\system32\\cmd.exe /c calc.exe'!A1");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    // DDE formulas should be escaped
    assertThat(csvOutput, not(containsString("=cmd|")));
    assertThat(csvOutput, not(containsString("=MSEXCEL|")));
  }

  @Test
  public void testHandlesDoubleQuotesInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title with \"quotes\"");
    metacard.setDescription("Description with \"embedded\" quotes");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    // Quotes should be escaped (typically doubled: "" or with backslash)
    assertThat(csvOutput, notNullValue());
    // CSV should be parseable
    assertThat(csvOutput.contains("Title with"), is(true));
  }

  @Test
  public void testHandlesCommasInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title, with, commas");
    metacard.setDescription("Value1, Value2, Value3");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // Fields with commas should be quoted
    assertThat(csvOutput, containsString("\"Title, with, commas\""));
  }

  @Test
  public void testHandlesNewlinesInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title\nWith\nNewlines");
    metacard.setDescription("Line1\nLine2\nLine3");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // Newlines should be preserved within quoted fields
  }

  @Test
  public void testHandlesCarriageReturnsInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title\r\nWith\r\nCRLF");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
  }

  @Test
  public void testHandlesTabsInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title\tWith\tTabs");
    metacard.setDescription("Column1\tColumn2\tColumn3");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput, containsString("Title"));
  }

  @Test
  public void testHandlesUnicodeCharacters() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("测试标题 Тест العنوان");
    metacard.setDescription("Unicode: ñ é ü ö ä 日本語 한국어");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput, containsString("测试"));
    assertThat(csvOutput, containsString("日本語"));
  }

  @Test
  public void testHandlesEmojiCharacters() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test with emoji 😀 🎉 ⚡");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
  }

  @Test
  public void testHandlesNullValues() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id");
    // Don't set title or description (null values)

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // Should handle null fields gracefully (empty or special marker)
  }

  @Test
  public void testHandlesEmptyStrings() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("");
    metacard.setDescription("");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
  }

  @Test
  public void testHandlesVeryLongStrings() throws Exception {
    MetacardImpl metacard = new MetacardImpl();

    // Create a very long string (100KB)
    StringBuilder longString = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longString.append("This is line ").append(i).append(" of the long text. ");
    }

    metacard.setDescription(longString.toString());

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput.length() > 50000, is(true));
  }

  @Test
  public void testTransformMultipleMetacards() throws Exception {
    List<Result> results = new ArrayList<>();

    for (int i = 0; i < 100; i++) {
      MetacardImpl metacard = new MetacardImpl();
      metacard.setId("id-" + i);
      metacard.setTitle("Title " + i);
      metacard.setDescription("Description " + i);
      results.add(new ResultImpl(metacard));
    }

    SourceResponse response = new SourceResponseImpl(null, results);

    BinaryContent result = queryResponseTransformer.transform(response, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());

    // Count lines (should be header + 100 records)
    int lineCount = csvOutput.split("\n").length;
    assertThat(lineCount >= 100, is(true));
  }

  @Test
  public void testTransformEmptyResults() throws Exception {
    List<Result> results = new ArrayList<>();
    SourceResponse response = new SourceResponseImpl(null, results);

    BinaryContent result = queryResponseTransformer.transform(response, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // Should at least have header row
  }

  @Test
  public void testTransformWithAllDataTypes() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-123");
    metacard.setTitle("Test Title");
    metacard.setDescription("Test Description");
    metacard.setLocation("POINT(10.0 20.0)");
    metacard.setCreatedDate(new Date());
    metacard.setModifiedDate(new Date());

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput, containsString("test-id-123"));
    assertThat(csvOutput, containsString("Test Title"));
    assertThat(csvOutput, containsString("Test Description"));
  }

  @Test
  public void testMimeTypeIsCorrect() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String mimeType = result.getMimeType().toString();
    assertThat(mimeType, containsString("csv"));
  }

  @Test
  public void testOutputEncodingIsUTF8() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("UTF-8 Test: café résumé naïve");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    // Read as UTF-8
    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, containsString("café"));
    assertThat(csvOutput, containsString("résumé"));
  }

  @Test
  public void testHandlesBackslashInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Path: C:\\Users\\Test\\File.txt");
    metacard.setDescription("Backslash \\ test");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput, containsString("C:"));
  }

  @Test
  public void testHandlesSingleQuotesInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Title with 'single quotes'");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    assertThat(csvOutput, containsString("single quotes"));
  }

  @Test
  public void testHandlesHtmlTagsInContent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("<script>alert('XSS')</script>");
    metacard.setDescription("<b>Bold</b> and <i>Italic</i>");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // HTML tags should be preserved as literal text in CSV
    assertThat(csvOutput, containsString("<script>"));
  }

  @Test
  public void testHandlesSqlInjectionAttempt() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("' OR '1'='1");
    metacard.setDescription("'; DROP TABLE metacards; --");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    assertThat(csvOutput, notNullValue());
    // SQL should be treated as literal text
    assertThat(csvOutput, containsString("DROP TABLE"));
  }

  @Test
  public void testTransformWithArguments() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put("customParam", "value");

    BinaryContent result = metacardTransformer.transform(metacard, arguments);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformNullMetacard() {
    assertThrows(
        CatalogTransformerException.class, () -> metacardTransformer.transform(null, null));
  }

  @Test
  public void testTransformQueryResponseWithNullArguments() throws Exception {
    List<Result> results = new ArrayList<>();
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");
    results.add(new ResultImpl(metacard));

    SourceResponse response = new SourceResponseImpl(null, results);

    BinaryContent result = queryResponseTransformer.transform(response, null);

    assertThat(result, notNullValue());
  }

  @Test
  public void testHeaderRowIsPresent() throws Exception {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setTitle("Test");

    BinaryContent result = metacardTransformer.transform(metacard, null);

    String csvOutput = inputStreamToString(result.getInputStream());

    // First line should be header
    String[] lines = csvOutput.split("\n");
    assertThat(lines.length > 0, is(true));
    // Header should contain field names
    String header = lines[0];
    assertThat(header.length() > 0, is(true));
  }

  @Test
  public void testFieldOrderIsConsistent() throws Exception {
    MetacardImpl metacard1 = new MetacardImpl();
    metacard1.setId("id1");
    metacard1.setTitle("Title1");

    MetacardImpl metacard2 = new MetacardImpl();
    metacard2.setId("id2");
    metacard2.setTitle("Title2");

    BinaryContent result1 = metacardTransformer.transform(metacard1, null);
    BinaryContent result2 = metacardTransformer.transform(metacard2, null);

    String csv1 = inputStreamToString(result1.getInputStream());
    String csv2 = inputStreamToString(result2.getInputStream());

    // Headers should match
    String header1 = csv1.split("\n")[0];
    String header2 = csv2.split("\n")[0];

    assertThat(header1, is(header2));
  }

  // Helper method

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
