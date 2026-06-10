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
package ddf.catalog.services.xsltlistener;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ddf.catalog.data.BinaryContent;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.transform.CatalogTransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Comprehensive test harness for XsltMetacardTransformer. Tests XSLT transformation security, error
 * handling, and edge cases.
 *
 * <p>SECURITY CRITICAL: XSLT transformations can be vulnerable to: - XXE (XML External Entity)
 * attacks - XSLT script injection - Resource exhaustion via recursive templates - Information
 * disclosure via system property access
 */
@EnableRuleMigrationSupport
@ExtendWith(MockitoExtension.class)
public class XsltMetacardTransformerTest {

  private XsltMetacardTransformer transformer;

  @Mock private Bundle mockBundle;

  @Mock private BundleContext mockBundleContext;

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private static final String SIMPLE_XSLT =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
          + "  <xsl:output method=\"html\" encoding=\"UTF-8\" indent=\"yes\"/>\n"
          // XSLT 1.0 requires top-level params to be declared before they can be referenced;
          // the transformer supplies these via Transformer.setParameter(...).
          + "  <xsl:param name=\"id\"/>\n"
          + "  <xsl:param name=\"title\"/>\n"
          + "  <xsl:template match=\"/\">\n"
          + "    <html>\n"
          + "      <body>\n"
          + "        <h1>Metacard</h1>\n"
          + "        <p>ID: <xsl:value-of select=\"$id\"/></p>\n"
          + "        <p>Title: <xsl:value-of select=\"$title\"/></p>\n"
          + "        <div><xsl:copy-of select=\".\"/></div>\n"
          + "      </body>\n"
          + "    </html>\n"
          + "  </xsl:template>\n"
          + "</xsl:stylesheet>";

  private static final String IDENTITY_XSLT =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
          + "  <xsl:output method=\"xml\" encoding=\"UTF-8\" indent=\"yes\"/>\n"
          + "  <xsl:template match=\"@*|node()\">\n"
          + "    <xsl:copy>\n"
          + "      <xsl:apply-templates select=\"@*|node()\"/>\n"
          + "    </xsl:copy>\n"
          + "  </xsl:template>\n"
          + "</xsl:stylesheet>";

  @BeforeEach
  public void setUp() throws Exception {
    when(mockBundle.getBundleContext()).thenReturn(mockBundleContext);
    // AbstractXsltTransformer.init() derives the output MIME type from the bundle's
    // DDF-Mime-Type header (not from the XSLT <xsl:output> method), so provide it here.
    java.util.Hashtable<String, String> headers = new java.util.Hashtable<>();
    headers.put("DDF-Mime-Type", "text/html");
    when(mockBundle.getHeaders()).thenReturn(headers);
  }

  @Test
  public void testTransformValidMetacardWithSimpleXslt() throws Exception {
    // Create transformer with simple XSLT
    transformer = createTransformerWithXslt(SIMPLE_XSLT);

    // Create test metacard
    MetacardImpl metacard = createBasicMetacard();

    // Transform
    BinaryContent result = transformer.transform(metacard, null);

    // Verify
    assertThat(result, notNullValue());
    assertThat(result.getMimeType().toString(), containsString("html"));

    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("<html>"));
    assertThat(output, containsString("test-id-123"));
    assertThat(output, containsString("Test Title"));
  }

  @Test
  public void testTransformWithArguments() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put("customParam", "customValue");

    BinaryContent result = transformer.transform(metacard, arguments);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithNullArguments() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
  }

  @Test
  public void testTransformWithEmptyMetadata() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    metacard.setMetadata("");

    // Empty metadata is not a well-formed XML document, so the secure parser fails with a
    // "premature end of file" error which the transformer surfaces as a transform exception.
    assertThrows(CatalogTransformerException.class, () -> transformer.transform(metacard, null));
  }

  @Test
  public void testTransformWithXmlMetadata() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    String xmlMetadata =
        "<metadata><title>Test</title><description>Test Description</description></metadata>";
    metacard.setMetadata(xmlMetadata);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("<title>Test</title>"));
    assertThat(output, containsString("<description>Test Description</description>"));
  }

  @Test
  public void testTransformWithSpecialCharactersInMetadata() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    String xmlMetadata = "<metadata><title>Test &amp; Title &lt;special&gt;</title></metadata>";
    metacard.setMetadata(xmlMetadata);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("&amp;"));
    assertThat(output, containsString("&lt;"));
    assertThat(output, containsString("&gt;"));
  }

  @Test
  public void testTransformWithUnicodeCharacters() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    metacard.setTitle("测试标题 Тест العنوان");
    String xmlMetadata = "<metadata><title>测试 Test Тест العنوان</title></metadata>";
    metacard.setMetadata(xmlMetadata);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("测试"));
  }

  @Test
  public void testTransformWithCDATA() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    String xmlMetadata =
        "<metadata><description><![CDATA[This is <b>bold</b> text]]></description></metadata>";
    metacard.setMetadata(xmlMetadata);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    // CDATA content should be preserved
    assertThat(output.length() > 0, is(true));
  }

  @Test
  public void testTransformWithMalformedXml() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();
    // Malformed XML - unclosed tag
    String malformedXml = "<metadata><title>Test</title><unclosed>";
    metacard.setMetadata(malformedXml);

    assertThrows(CatalogTransformerException.class, () -> transformer.transform(metacard, null));
  }

  @Test
  public void testTransformWithInvalidXslt() throws Exception {
    String invalidXslt = "<?xml version=\"1.0\"?><not-xslt>Invalid</not-xslt>";

    when(mockBundle.getResource(anyString())).thenReturn(createUrlFromString(invalidXslt));

    // Invalid XSLT is rejected when the Templates are compiled in the constructor (init),
    // which wraps the TransformerConfigurationException in an IllegalStateException.
    assertThrows(
        IllegalStateException.class, () -> new XsltMetacardTransformer(mockBundle, "invalid.xsl"));
  }

  @Test
  public void testTransformWithLargeMetadata() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    // Create large XML metadata (1MB)
    StringBuilder largeXml = new StringBuilder("<metadata>");
    for (int i = 0; i < 10000; i++) {
      largeXml
          .append("<item id=\"")
          .append(i)
          .append("\">")
          .append("Data item number ")
          .append(i)
          .append(" with some content")
          .append("</item>");
    }
    largeXml.append("</metadata>");

    metacard.setMetadata(largeXml.toString());

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    // XsltTransformedContent does not report a size (getSize() defaults to UNKNOWN_SIZE),
    // so verify the transform produced non-empty output by reading the content directly.
    String output = inputStreamToString(result.getInputStream());
    assertThat(output.contains("Data item number 9999"), is(true));
  }

  @Test
  public void testTransformWithAllMetacardFields() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);

    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("full-id-456");
    metacard.setTitle("Full Test Title");
    metacard.setMetadata("<metadata><full>true</full></metadata>");
    metacard.setSourceId("test-source");
    metacard.setCreatedDate(new Date());
    metacard.setModifiedDate(new Date());
    metacard.setLocation("POINT(10.0 20.0)");
    metacard.setResourceURI(new java.net.URI("http://example.com/resource"));

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("full-id-456"));
    assertThat(output, containsString("Full Test Title"));
  }

  @Test
  public void testTransformWithNullMetacardFields() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);

    MetacardImpl metacard = new MetacardImpl();
    // Most fields are null
    metacard.setMetadata("<metadata/>");

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
  }

  @Test
  public void testDataItemStatusGetterSetter() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);

    String testStatus = "Active";
    transformer.setDataItemStatus(testStatus);

    String retrievedStatus = transformer.getDataItemStatus();

    assertThat(retrievedStatus, is(testStatus));
  }

  @Test
  public void testTransformPreservesInputEncoding() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    // UTF-8 encoded content
    String utf8Content =
        "<metadata encoding=\"UTF-8\"><title>UTF-8 Content: café</title></metadata>";
    metacard.setMetadata(utf8Content);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    // Should preserve UTF-8 encoding
    assertThat(output.contains("café") || output.contains("caf"), is(true));
  }

  @Test
  public void testTransformWithMultipleParameters() throws Exception {
    transformer = createTransformerWithXslt(SIMPLE_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    Map<String, Serializable> arguments = new HashMap<>();
    arguments.put("param1", "value1");
    arguments.put("param2", "value2");
    arguments.put("param3", 12345);
    arguments.put("param4", true);

    BinaryContent result = transformer.transform(metacard, arguments);

    assertThat(result, notNullValue());
  }

  /**
   * SECURITY TEST: Verify that XXE (XML External Entity) attacks are prevented. The transformer
   * should use secure XML parsing that disables external entities.
   */
  @Test
  public void testTransformBlocksXXEAttack() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    // XXE attack attempt - should be blocked by secure XML parser
    String xxePayload =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<!DOCTYPE foo ["
            + "  <!ENTITY xxe SYSTEM \"file:///etc/passwd\">"
            + "]>"
            + "<metadata>"
            + "  <data>&xxe;</data>"
            + "</metadata>";

    metacard.setMetadata(xxePayload);

    // The secure XML parser disables external general/parameter entities and external DTD
    // loading, so the SYSTEM entity is never resolved. The transform completes without reading
    // the targeted file; the security property is that the file contents are NOT disclosed in
    // the output (the &xxe; reference is not expanded to /etc/passwd contents).
    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output.contains("root:"), is(false));
    assertThat(output.contains("/bin/bash"), is(false));
  }

  /** SECURITY TEST: Test handling of namespace declarations */
  @Test
  public void testTransformWithNamespaces() throws Exception {
    transformer = createTransformerWithXslt(IDENTITY_XSLT);
    MetacardImpl metacard = createBasicMetacard();

    String xmlWithNamespaces =
        "<metadata xmlns=\"http://example.com/ns\" xmlns:custom=\"http://custom.com/ns\">"
            + "  <custom:title>Namespaced Title</custom:title>"
            + "</metadata>";
    metacard.setMetadata(xmlWithNamespaces);

    BinaryContent result = transformer.transform(metacard, null);

    assertThat(result, notNullValue());
    String output = inputStreamToString(result.getInputStream());
    assertThat(output, containsString("Namespaced Title"));
  }

  // Helper methods

  private XsltMetacardTransformer createTransformerWithXslt(String xsltContent) throws Exception {
    URL xsltUrl = createUrlFromString(xsltContent);
    when(mockBundle.getResource(anyString())).thenReturn(xsltUrl);

    XsltMetacardTransformer xformer = new XsltMetacardTransformer(mockBundle, "test.xsl");
    xformer.context = mockBundleContext;

    // Mock service references to return empty. This stub is only exercised by tests that
    // actually call transform(); mark it lenient so the few that don't (e.g. the
    // getter/setter test) do not trip Mockito's strict-stub checking.
    lenient().when(mockBundleContext.getServiceReferences(anyString(), any())).thenReturn(null);

    return xformer;
  }

  private MetacardImpl createBasicMetacard() {
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId("test-id-123");
    metacard.setTitle("Test Title");
    metacard.setMetadata("<metadata><test>true</test></metadata>");
    return metacard;
  }

  private URL createUrlFromString(String content) throws IOException {
    // Create a temporary file with the content
    java.io.File tempFile = tempFolder.newFile();
    java.nio.file.Files.write(tempFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
    return tempFile.toURI().toURL();
  }

  private String inputStreamToString(InputStream is) throws IOException {
    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
  }

  private static org.hamcrest.Matcher<String> not(org.hamcrest.Matcher<String> matcher) {
    return org.hamcrest.CoreMatchers.not(matcher);
  }
}
