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
package ddf.security.pdp.realm.xacml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ddf.security.audit.SecurityLogger;
import ddf.security.pdp.realm.xacml.processor.PdpException;
import ddf.security.permission.KeyValueCollectionPermission;
import ddf.security.permission.KeyValuePermission;
import ddf.security.permission.impl.KeyValueCollectionPermissionImpl;
import ddf.security.permission.impl.KeyValuePermissionImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.codice.ddf.parser.xml.XmlParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XacmlPdpEnhancedTest {

  private static final String USER_NAME = "TestUser";
  private static final String EMPTY_USER = "";
  private static final String NULL_USER = null;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private XacmlPdp pdp;
  private SecurityLogger securityLogger;
  private File policyDir;

  @Before
  public void setup() throws IOException, PdpException {
    temporaryFolder.create();
    policyDir = temporaryFolder.newFolder("policies");
    File policy = new File(policyDir, "test-policy.xml");
    FileOutputStream policyOutStream = new FileOutputStream(policy);
    InputStream policyStream = XacmlPdp.class.getResourceAsStream("/policies/test-policy.xml");
    IOUtils.copy(policyStream, policyOutStream);
    policyOutStream.close();

    securityLogger = mock(SecurityLogger.class);
    pdp =
        new XacmlPdp(
            policyDir.getAbsolutePath(), new XmlParser(), new ArrayList<>(), securityLogger);
  }

  @After
  public void tearDown() {
    temporaryFolder.delete();
  }

  @Test
  public void testIsPermittedWithEmptyPermissionList() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission emptyPermission = new KeyValueCollectionPermissionImpl("query");

    boolean result = pdp.isPermitted(USER_NAME, info, emptyPermission);

    assertThat(result, is(true));
    verify(securityLogger).audit(any(String.class));
  }

  @Test
  public void testIsPermittedShortCircuitDeniedNoUserPermissions() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.emptySet());
    info.setObjectPermissions(Collections.emptySet());

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("resource", Arrays.asList("A", "B"));
    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("query", security);

    boolean result = pdp.isPermitted(USER_NAME, info, permission);

    assertThat(result, is(false));
    verify(securityLogger).audit(any(String.class));
  }

  @Test
  public void testIsPermittedShortCircuitPermittedEmptyResourcePermissions() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("admin"));
    Set<Permission> permissions = new HashSet<>();
    KeyValuePermission kvp = new KeyValuePermissionImpl("attr");
    kvp.addValue("value");
    permissions.add(kvp);
    info.setObjectPermissions(permissions);

    KeyValueCollectionPermission emptyPermission = new KeyValueCollectionPermissionImpl("query");

    boolean result = pdp.isPermitted(USER_NAME, info, emptyPermission);

    assertThat(result, is(true));
    verify(securityLogger).audit(any(String.class));
  }

  @Test
  public void testIsPermittedWithNullPrimaryPrincipal() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    boolean result = pdp.isPermitted(NULL_USER, info, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testIsPermittedWithEmptyPrimaryPrincipal() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    boolean result = pdp.isPermitted(EMPTY_USER, info, permission);

    assertThat(result, is(true));
  }

  @Test
  public void testCreateXACMLRequestWithMultipleRoles() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    Set<String> roles = new HashSet<>();
    roles.add("admin");
    roles.add("user");
    roles.add("manager");
    info.setRoles(roles);
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributes().size() > 0, is(true));
  }

  @Test
  public void testCreateXACMLRequestWithNoRoles() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.emptySet());
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testCreateXACMLRequestWithComplexPermissions() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));

    Set<Permission> permissions = new HashSet<>();
    KeyValuePermission perm1 = new KeyValuePermissionImpl("attr1");
    perm1.addValue("val1");
    perm1.addValue("val2");
    perm1.addValue("val3");
    permissions.add(perm1);

    KeyValuePermission perm2 = new KeyValuePermissionImpl("attr2");
    perm2.addValue("valA");
    perm2.addValue("valB");
    permissions.add(perm2);

    info.setObjectPermissions(permissions);

    HashMap<String, List<String>> resourcePerms = new HashMap<>();
    resourcePerms.put("resource1", Arrays.asList("A", "B", "C"));
    resourcePerms.put("resource2", Arrays.asList("X", "Y"));
    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("query", resourcePerms);

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributes().size() > 0, is(true));
  }

  @Test
  public void testCreateXACMLRequestWithEnvironmentAttributes() throws PdpException, IOException {
    List<String> envAttrs = new ArrayList<>();
    envAttrs.add("envAttr1=envVal1");
    envAttrs.add("envAttr2=envVal2,envVal3");
    envAttrs.add("envAttr3=envVal4,envVal5,envVal6");

    XacmlPdp pdpWithEnv =
        new XacmlPdp(policyDir.getAbsolutePath(), new XmlParser(), envAttrs, securityLogger);

    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdpWithEnv.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
    assertThat(request.getAttributes().size() > 0, is(true));
  }

  @Test
  public void testCreateXACMLRequestWithMalformedEnvironmentAttribute()
      throws PdpException, IOException {
    List<String> envAttrs = new ArrayList<>();
    envAttrs.add("malformed");
    envAttrs.add("validAttr=validVal");

    XacmlPdp pdpWithEnv =
        new XacmlPdp(policyDir.getAbsolutePath(), new XmlParser(), envAttrs, securityLogger);

    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdpWithEnv.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testGetXacmlDataTypeForAllTypes() {
    // Boolean
    assertThat(pdp.getXacmlDataType("true"), is("http://www.w3.org/2001/XMLSchema#boolean"));
    assertThat(pdp.getXacmlDataType("false"), is("http://www.w3.org/2001/XMLSchema#boolean"));
    assertThat(pdp.getXacmlDataType("TRUE"), is("http://www.w3.org/2001/XMLSchema#boolean"));
    assertThat(pdp.getXacmlDataType("FALSE"), is("http://www.w3.org/2001/XMLSchema#boolean"));

    // Integer
    assertThat(pdp.getXacmlDataType("123"), is("http://www.w3.org/2001/XMLSchema#integer"));
    assertThat(pdp.getXacmlDataType("-456"), is("http://www.w3.org/2001/XMLSchema#integer"));
    assertThat(pdp.getXacmlDataType("0"), is("http://www.w3.org/2001/XMLSchema#integer"));

    // String
    assertThat(
        pdp.getXacmlDataType("simple string"), is("http://www.w3.org/2001/XMLSchema#string"));
    assertThat(
        pdp.getXacmlDataType("string with 123 numbers"),
        is("http://www.w3.org/2001/XMLSchema#string"));
  }

  @Test
  public void testGetXacmlDataTypeEdgeCases() {
    // Empty string
    assertThat(pdp.getXacmlDataType(""), is("http://www.w3.org/2001/XMLSchema#string"));

    // Whitespace
    assertThat(pdp.getXacmlDataType("   "), is("http://www.w3.org/2001/XMLSchema#string"));

    // Special characters
    assertThat(pdp.getXacmlDataType("!@#$%^&*()"), is("http://www.w3.org/2001/XMLSchema#string"));
  }

  @Test
  public void testIsPermittedWithPdpException() throws PdpException {
    // Create a request that will fail
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("invalid-resource", Arrays.asList("INVALID"));
    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("invalid-action", security);

    boolean result = pdp.isPermitted(USER_NAME, info, permission);

    // Should return false on exception
    assertThat(result, is(false));
  }

  @Test
  public void testIsPermittedWithNonKeyValuePermission() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));

    Set<Permission> permissions = new HashSet<>();
    // Add a non-KeyValuePermission
    Permission regularPerm = mock(Permission.class);
    permissions.add(regularPerm);
    info.setObjectPermissions(permissions);

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testIsPermittedAuditLoggingOnDeny() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.emptySet());
    info.setObjectPermissions(Collections.emptySet());

    HashMap<String, List<String>> security = new HashMap<>();
    security.put("restricted-resource", Arrays.asList("TOP_SECRET"));
    KeyValueCollectionPermission permission =
        new KeyValueCollectionPermissionImpl("query", security);

    pdp.isPermitted(USER_NAME, info, permission);

    verify(securityLogger, times(1)).audit(any(String.class));
  }

  @Test
  public void testIsPermittedAuditLoggingOnPermit() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("admin"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    pdp.isPermitted(USER_NAME, info, permission);

    verify(securityLogger, times(1)).audit(any(String.class));
  }

  @Test
  public void testCreateXACMLRequestCombinedDecisionFalse() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request.isCombinedDecision(), is(false));
  }

  @Test
  public void testCreateXACMLRequestReturnPolicyIdListFalse() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));
    info.setObjectPermissions(new HashSet<>());

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request.isReturnPolicyIdList(), is(false));
  }

  @Test
  public void testIsPermittedWithKeyValuePermissionEmptyValues() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));

    Set<Permission> permissions = new HashSet<>();
    KeyValuePermission emptyPerm = new KeyValuePermissionImpl("empty-attr");
    // Don't add any values
    permissions.add(emptyPerm);
    info.setObjectPermissions(permissions);

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testIsPermittedWithMixedPermissionTypes() {
    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setRoles(Collections.singleton("user"));

    Set<Permission> permissions = new HashSet<>();
    KeyValuePermission kvp1 = new KeyValuePermissionImpl("attr1");
    kvp1.addValue("val1");
    permissions.add(kvp1);

    // Add a non-KeyValuePermission
    Permission otherPerm = mock(Permission.class);
    permissions.add(otherPerm);

    info.setObjectPermissions(permissions);

    KeyValueCollectionPermission permission = new KeyValueCollectionPermissionImpl("query");

    RequestType request = pdp.createXACMLRequest(USER_NAME, info, permission);

    assertThat(request, is(notNullValue()));
  }

  @Test
  public void testDataTypeDetectionPrecedence() {
    // Boolean has precedence
    assertThat(pdp.getXacmlDataType("true"), is("http://www.w3.org/2001/XMLSchema#boolean"));

    // Integer has precedence over string
    assertThat(pdp.getXacmlDataType("42"), is("http://www.w3.org/2001/XMLSchema#integer"));

    // Email detection
    assertThat(
        pdp.getXacmlDataType("test@example.com"),
        is("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name"));

    // URL detection
    assertThat(
        pdp.getXacmlDataType("http://www.example.com"),
        is("http://www.w3.org/2001/XMLSchema#anyURI"));

    // IP address detection
    assertThat(
        pdp.getXacmlDataType("192.168.1.1"),
        is("urn:oasis:names:tc:xacml:2.0:data-type:ipAddress"));

    // X500 name detection
    assertThat(
        pdp.getXacmlDataType("cn=John Doe,ou=People,dc=example,dc=com"),
        is("urn:oasis:names:tc:xacml:1.0:data-type:x500Name"));
  }

  @Test
  public void testInvalidX500NameFallsBackToString() {
    // Actually "cn=invalid" is a valid X500 name, so test with truly invalid format
    assertThat(
        pdp.getXacmlDataType("not an x500 name at all"),
        is("http://www.w3.org/2001/XMLSchema#string"));
  }
}
