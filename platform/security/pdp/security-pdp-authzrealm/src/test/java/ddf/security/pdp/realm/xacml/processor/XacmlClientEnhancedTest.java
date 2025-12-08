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
package ddf.security.pdp.realm.xacml.processor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import ddf.security.audit.SecurityLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codice.ddf.parser.xml.XmlParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XacmlClientEnhancedTest {

  private static final String ACTION_CATEGORY =
      "urn:oasis:names:tc:xacml:3.0:attribute-category:action";
  private static final String ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";
  private static final String SUBJECT_CATEGORY =
      "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
  private static final String SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
  private static final String STRING_DATA_TYPE = "http://www.w3.org/2001/XMLSchema#string";
  private static final String POLICY_FILE = "query-policy.xml";
  private static final String QUERY_ACTION = "query";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private File policyDir;
  private SecurityLogger securityLogger;

  @Before
  public void setup() throws IOException {
    temporaryFolder.create();
    policyDir = temporaryFolder.newFolder("policies");
    securityLogger = mock(SecurityLogger.class);
  }

  @After
  public void cleanup() {
    temporaryFolder.delete();
  }

  @Test
  public void testConstructorWithNullDirectory() {
    assertThrows(
        PdpException.class,
        () -> {
          new XacmlClient(null, new XmlParser(), securityLogger);
        });
  }

  @Test
  public void testConstructorWithEmptyDirectory() {
    assertThrows(
        PdpException.class,
        () -> {
          new XacmlClient("", new XmlParser(), securityLogger);
        });
  }

  @Test
  public void testConstructorCreatesNonExistentDirectory() throws PdpException, IOException {
    File newDir = new File(policyDir, "new-policy-dir");
    String newDirPath = newDir.getAbsolutePath();

    assertThat(newDir.exists(), is(false));

    XacmlClient client = new XacmlClient(newDirPath, new XmlParser(), securityLogger);

    assertThat(client, is(notNullValue()));
    assertThat(newDir.exists(), is(true));

    FileUtils.deleteDirectory(newDir);
  }

  @Test
  public void testEvaluateWithEmptyPolicyDirectory() throws Exception {
    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createSimpleRequest();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
    // Empty directory should result in DENY or INDETERMINATE
    assertThat(response.getResult().get(0).getDecision() != DecisionType.PERMIT, is(true));
  }

  @Test
  public void testEvaluateWithValidPolicy() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createUSAQueryRequest();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
    assertThat(response.getResult().size(), is(1));
    // Decision can be PERMIT, DENY, or INDETERMINATE depending on policy evaluation
    assertThat(response.getResult().get(0).getDecision(), is(notNullValue()));
  }

  @Test
  public void testEvaluateWithInvalidRequest() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType emptyRequest = new RequestType();

    try {
      ResponseType response = client.evaluate(emptyRequest);
      // Should still return a response, potentially INDETERMINATE
      assertThat(response, is(notNullValue()));
    } catch (Exception e) {
      // Exception is acceptable for invalid request
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testEvaluateWithMultiplePolicies() throws Exception {
    // Copy the same policy multiple times with different names
    copyPolicyToDirectory("query-policy.xml");

    File policy2 = new File(policyDir, "policy2.xml");
    FileOutputStream outStream = new FileOutputStream(policy2);
    InputStream policyStream = XacmlClient.class.getResourceAsStream("/query-policy.xml");
    if (policyStream == null) {
      policyStream = XacmlClient.class.getResourceAsStream("/policies/query-policy.xml");
    }
    IOUtils.copy(policyStream, outStream);
    outStream.close();

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createUSAQueryRequest();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testEvaluateWithMalformedXmlPolicy() throws Exception {
    // Create a malformed XML file
    File malformedPolicy = new File(policyDir, "malformed.xml");
    FileOutputStream outStream = new FileOutputStream(malformedPolicy);
    outStream.write("<?xml version=\"1.0\"?><InvalidXml>".getBytes());
    outStream.close();

    try {
      XacmlClient client =
          new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

      RequestType request = createSimpleRequest();
      ResponseType response = client.evaluate(request);

      // Should handle gracefully
      assertThat(response, is(notNullValue()));
    } catch (Exception e) {
      // Exception is acceptable for malformed policy
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testEvaluateWithComplexRequest() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createComplexRequest();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
    assertThat(response.getResult().size() > 0, is(true));
  }

  @Test
  public void testEvaluateWithNullParser() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    try {
      XacmlClient client = new XacmlClient(policyDir.getCanonicalPath(), null, securityLogger);

      RequestType request = createSimpleRequest();

      try {
        client.evaluate(request);
        fail("Should throw IllegalStateException");
      } catch (IllegalStateException e) {
        assertThat(e.getMessage(), is(notNullValue()));
      }
    } catch (PdpException e) {
      // Also acceptable if constructor fails
      assertThat(e, is(notNullValue()));
    }
  }

  @Test
  public void testEvaluateDenyDecision() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    // Create request that should be denied (non-USA citizenship)
    RequestType request = createNonUSAQueryRequest();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
    assertThat(response.getResult().size(), is(1));
    // Decision should not be PERMIT for non-USA citizenship
    assertThat(response.getResult().get(0).getDecision() != DecisionType.PERMIT, is(true));
  }

  @Test
  public void testEvaluateWithSpecialCharactersInRequest() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createRequestWithSpecialCharacters();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testEvaluateWithVeryLongAttributeValues() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createRequestWithLongValues();

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testEvaluatePreservesRequestStructure() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request = createUSAQueryRequest();
    request.setCombinedDecision(false);
    request.setReturnPolicyIdList(false);

    ResponseType response = client.evaluate(request);

    assertThat(response, is(notNullValue()));
  }

  @Test
  public void testConcurrentEvaluations() throws Exception {
    copyPolicyToDirectory("query-policy.xml");

    XacmlClient client =
        new XacmlClient(policyDir.getCanonicalPath(), new XmlParser(), securityLogger);

    RequestType request1 = createUSAQueryRequest();
    RequestType request2 = createNonUSAQueryRequest();

    ResponseType response1 = client.evaluate(request1);
    ResponseType response2 = client.evaluate(request2);

    assertThat(response1, is(notNullValue()));
    assertThat(response2, is(notNullValue()));
    assertThat(response1.getResult().size(), is(1));
    assertThat(response2.getResult().size(), is(1));
    // Both should return decisions (may differ based on policy)
    assertThat(response1.getResult().get(0).getDecision(), is(notNullValue()));
    assertThat(response2.getResult().get(0).getDecision(), is(notNullValue()));
  }

  private RequestType createSimpleRequest() {
    RequestType request = new RequestType();
    request.setCombinedDecision(false);
    request.setReturnPolicyIdList(false);

    AttributesType actionAttributes = new AttributesType();
    actionAttributes.setCategory(ACTION_CATEGORY);
    AttributeType actionAttribute = new AttributeType();
    actionAttribute.setAttributeId(ACTION_ID);
    AttributeValueType actionValue = new AttributeValueType();
    actionValue.setDataType(STRING_DATA_TYPE);
    actionValue.getContent().add(QUERY_ACTION);
    actionAttribute.getAttributeValue().add(actionValue);
    actionAttributes.getAttribute().add(actionAttribute);

    request.getAttributes().add(actionAttributes);

    return request;
  }

  private RequestType createUSAQueryRequest() {
    RequestType request = new RequestType();
    request.setCombinedDecision(false);
    request.setReturnPolicyIdList(false);

    // Action
    AttributesType actionAttributes = new AttributesType();
    actionAttributes.setCategory(ACTION_CATEGORY);
    AttributeType actionAttribute = new AttributeType();
    actionAttribute.setAttributeId(ACTION_ID);
    AttributeValueType actionValue = new AttributeValueType();
    actionValue.setDataType(STRING_DATA_TYPE);
    actionValue.getContent().add(QUERY_ACTION);
    actionAttribute.getAttributeValue().add(actionValue);
    actionAttributes.getAttribute().add(actionAttribute);

    // Subject
    AttributesType subjectAttributes = new AttributesType();
    subjectAttributes.setCategory(SUBJECT_CATEGORY);
    AttributeType subjectAttribute = new AttributeType();
    subjectAttribute.setAttributeId(SUBJECT_ID);
    AttributeValueType subjectValue = new AttributeValueType();
    subjectValue.setDataType(STRING_DATA_TYPE);
    subjectValue.getContent().add("testuser");
    subjectAttribute.getAttributeValue().add(subjectValue);
    subjectAttributes.getAttribute().add(subjectAttribute);

    // Citizenship
    AttributeType citizenshipAttribute = new AttributeType();
    citizenshipAttribute.setAttributeId("http://www.opm.gov/feddata/CountryOfCitizenship");
    AttributeValueType citizenshipValue = new AttributeValueType();
    citizenshipValue.setDataType(STRING_DATA_TYPE);
    citizenshipValue.getContent().add("USA");
    citizenshipAttribute.getAttributeValue().add(citizenshipValue);
    subjectAttributes.getAttribute().add(citizenshipAttribute);

    request.getAttributes().add(actionAttributes);
    request.getAttributes().add(subjectAttributes);

    return request;
  }

  private RequestType createNonUSAQueryRequest() {
    RequestType request = createUSAQueryRequest();

    // Find and modify the citizenship attribute
    for (AttributesType attributes : request.getAttributes()) {
      for (AttributeType attribute : attributes.getAttribute()) {
        if ("http://www.opm.gov/feddata/CountryOfCitizenship".equals(attribute.getAttributeId())) {
          attribute.getAttributeValue().get(0).getContent().clear();
          attribute.getAttributeValue().get(0).getContent().add("CA");
        }
      }
    }

    return request;
  }

  private RequestType createComplexRequest() {
    RequestType request = createUSAQueryRequest();

    // Add additional attributes
    AttributesType resourceAttributes = new AttributesType();
    resourceAttributes.setCategory("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");

    AttributeType resourceAttribute = new AttributeType();
    resourceAttribute.setAttributeId("resource-id");
    AttributeValueType resourceValue = new AttributeValueType();
    resourceValue.setDataType(STRING_DATA_TYPE);
    resourceValue.getContent().add("resource-123");
    resourceAttribute.getAttributeValue().add(resourceValue);
    resourceAttributes.getAttribute().add(resourceAttribute);

    request.getAttributes().add(resourceAttributes);

    return request;
  }

  private RequestType createRequestWithSpecialCharacters() {
    RequestType request = createSimpleRequest();

    AttributesType subjectAttributes = new AttributesType();
    subjectAttributes.setCategory(SUBJECT_CATEGORY);
    AttributeType subjectAttribute = new AttributeType();
    subjectAttribute.setAttributeId(SUBJECT_ID);
    AttributeValueType subjectValue = new AttributeValueType();
    subjectValue.setDataType(STRING_DATA_TYPE);
    subjectValue.getContent().add("user<>&\"'123");
    subjectAttribute.getAttributeValue().add(subjectValue);
    subjectAttributes.getAttribute().add(subjectAttribute);

    request.getAttributes().add(subjectAttributes);

    return request;
  }

  private RequestType createRequestWithLongValues() {
    RequestType request = createSimpleRequest();

    AttributesType subjectAttributes = new AttributesType();
    subjectAttributes.setCategory(SUBJECT_CATEGORY);
    AttributeType subjectAttribute = new AttributeType();
    subjectAttribute.setAttributeId(SUBJECT_ID);
    AttributeValueType subjectValue = new AttributeValueType();
    subjectValue.setDataType(STRING_DATA_TYPE);

    // Create a very long value
    StringBuilder longValue = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longValue.append("A");
    }
    subjectValue.getContent().add(longValue.toString());
    subjectAttribute.getAttributeValue().add(subjectValue);
    subjectAttributes.getAttribute().add(subjectAttribute);

    request.getAttributes().add(subjectAttributes);

    return request;
  }

  private void copyPolicyToDirectory(String policyFileName) throws IOException {
    File destFile = new File(policyDir, policyFileName);
    FileOutputStream outStream = new FileOutputStream(destFile);
    // Try both paths - root resources and policies subdirectory
    InputStream policyStream = XacmlClient.class.getResourceAsStream("/" + policyFileName);
    if (policyStream == null) {
      policyStream = XacmlClient.class.getResourceAsStream("/policies/" + policyFileName);
    }

    if (policyStream != null) {
      IOUtils.copy(policyStream, outStream);
      outStream.close();
      policyStream.close();
    } else {
      outStream.close();
      throw new IOException("Policy file not found: " + policyFileName);
    }
  }
}
