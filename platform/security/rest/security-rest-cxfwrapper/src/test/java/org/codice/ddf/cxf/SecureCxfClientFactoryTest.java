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
package org.codice.ddf.cxf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ddf.security.SecurityConstants;
import ddf.security.Subject;
import ddf.security.assertion.SecurityAssertion;
import ddf.security.audit.SecurityLogger;
import ddf.security.service.SecurityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.UUID;
import javax.net.ssl.X509KeyManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.cxf.client.SecureCxfClientFactory;
import org.codice.ddf.cxf.client.impl.ClientBuilderImpl;
import org.codice.ddf.cxf.client.impl.ClientKeyInfo;
import org.codice.ddf.cxf.client.impl.SecureCxfClientFactoryImpl;
import org.codice.ddf.cxf.client.impl.SecureCxfClientFactoryImpl.AliasSelectorKeyManager;
import org.codice.ddf.cxf.paos.PaosInInterceptor;
import org.codice.ddf.cxf.paos.PaosOutInterceptor;
import org.codice.ddf.security.jaxrs.impl.SamlSecurity;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Element;

@EnableRuleMigrationSupport
public class SecureCxfClientFactoryTest {

  private static final String INSECURE_ENDPOINT = "http://some.url.com/query";

  private static final String SECURE_ENDPOINT = "https://some.url.com/query";

  File systemKeystoreFile = null;

  File systemTruststoreFile = null;

  String password = "changeit";

  private SamlSecurity samlSecurity = new SamlSecurity();

  private SecurityLogger securityLogger = mock(SecurityLogger.class);

  private SecurityManager securityManager = mock(SecurityManager.class);

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @BeforeEach
  public void setup() throws IOException {
    systemKeystoreFile = temporaryFolder.newFile("serverKeystore.jks");
    FileOutputStream systemKeyOutStream = new FileOutputStream(systemKeystoreFile);
    InputStream systemKeyStream =
        SecureCxfClientFactoryTest.class.getResourceAsStream("/serverKeystore.jks");
    IOUtils.copy(systemKeyStream, systemKeyOutStream);

    systemTruststoreFile = temporaryFolder.newFile("serverTruststore.jks");
    FileOutputStream systemTrustOutStream = new FileOutputStream(systemTruststoreFile);
    InputStream systemTrustStream =
        SecureCxfClientFactoryTest.class.getResourceAsStream("/serverTruststore.jks");
    IOUtils.copy(systemTrustStream, systemTrustOutStream);

    System.setProperty(SecurityConstants.KEYSTORE_TYPE, "jks");
    System.setProperty(SecurityConstants.TRUSTSTORE_TYPE, "jks");
    System.setProperty("ddf.home", "");
    System.setProperty(SecurityConstants.KEYSTORE_PATH, systemKeystoreFile.getAbsolutePath());
    System.setProperty(SecurityConstants.TRUSTSTORE_PATH, systemTruststoreFile.getAbsolutePath());
    System.setProperty(SecurityConstants.KEYSTORE_PASSWORD, password);
    System.setProperty(SecurityConstants.TRUSTSTORE_PASSWORD, password);
  }

  @Test
  public void testConstructorNegativeCases() {
    // negative tests
    SecureCxfClientFactory<IDummy> secureCxfClientFactory;
    boolean invalid = false;
    try { // null for url
      secureCxfClientFactory =
          new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
              .endpoint(null)
              .interfaceClass(IDummy.class)
              .build();
    } catch (IllegalArgumentException e) {
      invalid = true;
    }
    assertThat(invalid, is(true));
    invalid = false;
    try { // null for url and class
      secureCxfClientFactory =
          new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
              .endpoint(null)
              .interfaceClass(null)
              .build();
    } catch (IllegalArgumentException e) {
      invalid = true;
    }
    assertThat(invalid, is(true));
    invalid = false;
    try { // null for class
      secureCxfClientFactory =
          new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
              .endpoint(INSECURE_ENDPOINT)
              .interfaceClass(null)
              .build();
    } catch (IllegalArgumentException e) {
      invalid = true;
    }
    assertThat(invalid, is(true));
    invalid = false;
    try {
      secureCxfClientFactory =
          new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
              .endpoint(null)
              .interfaceClass(null)
              .entityProviders(null)
              .interceptor(null)
              .disableCnCheck(false)
              .allowRedirects(false)
              .connectionTimeout(0)
              .receiveTimeout(0)
              .clientKeyInfo("alias", Paths.get("file:keystore"))
              .sslProtocol("TLSv1.1")
              .build();
    } catch (IllegalArgumentException e) {
      invalid = true;
    }
    assertThat(invalid, is(true));
  }

  @Test
  public void testInsecureWebClient() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(INSECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();
    WebClient client = secureCxfClientFactory.getWebClient();

    assertThat(hasEcpEnabled(client), is(false));
    assertThat(client.getBaseURI().toASCIIString().equals(INSECURE_ENDPOINT), is(true));
  }

  @Test
  public void testSecureClient() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .useSamlEcp(true)
            .build();
    IDummy client = secureCxfClientFactory.getClient();

    assertThat(hasEcpEnabled(client), is(true));
  }

  @Test
  public void testSecureWebClient() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .useSamlEcp(true)
            .build();
    WebClient client = secureCxfClientFactory.getWebClient();

    assertThat(hasEcpEnabled(client), is(true));
    assertThat(client.getBaseURI().toASCIIString().equals(SECURE_ENDPOINT), is(true));
  }

  @Test
  public void validateConduit() {
    IDummy clientForSubject =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .entityProviders(null)
            .interceptor(null)
            .disableCnCheck(true)
            .allowRedirects(true)
            .build()
            .getClient();
    HTTPConduit httpConduit =
        WebClient.getConfig(WebClient.client(clientForSubject)).getHttpConduit();
    assertThat(httpConduit.getTlsClientParameters().isDisableCNCheck(), is(true));
  }

  @Test
  public void testHttpsClientWithSystemProperty() {
    PropertyResolver mockPropertyResolver = mock(PropertyResolver.class);
    when(mockPropertyResolver.getResolvedString()).thenReturn(SECURE_ENDPOINT);
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .entityProviders(null)
            .interceptor(null)
            .disableCnCheck(false)
            .allowRedirects(false)
            .propertyResolver(mockPropertyResolver)
            .build();
    Client unsecuredClient = WebClient.client(secureCxfClientFactory.getClient());
    assertThat(unsecuredClient.getBaseURI().toASCIIString(), is(SECURE_ENDPOINT));
    verify(mockPropertyResolver).getResolvedString();
  }

  @Test
  public void testKeyInfo() {
    String alias = "alias";
    String keystorePath = Paths.get("/path/to/keystore").toString();

    ClientKeyInfo keyInfo = new ClientKeyInfo(alias, Paths.get(keystorePath));
    assertThat(keyInfo.getAlias(), is(alias));
    assertThat(keyInfo.getKeystorePath().toString(), is(keystorePath));
  }

  @Test
  public void testAliasSelectorKeyManager() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    String[] aliases = new String[] {alias};
    when(keyManager.chooseClientAlias(any(), any(), any())).thenReturn(alias);
    when(keyManager.getClientAliases(any(), any())).thenReturn(aliases);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String chosenAlias =
        aliasSelectorKeyManager.chooseClientAlias(new String[] {"x509"}, null, null);
    assertThat(chosenAlias, is(alias));
  }

  @Test
  public void testAliasSelectorKeyManagerWithNullKeyManager() {
    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(null, "testAlias");
    String chosenAlias =
        aliasSelectorKeyManager.chooseClientAlias(new String[] {"x509"}, null, null);
    assertThat(chosenAlias, is(nullValue()));
  }

  @Test
  public void testAliasSelectorKeyManagerWithNullAlias() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String expectedAlias = "defaultAlias";
    when(keyManager.chooseClientAlias(any(), any(), any())).thenReturn(expectedAlias);

    AliasSelectorKeyManager aliasSelectorKeyManager = new AliasSelectorKeyManager(keyManager, null);
    String chosenAlias =
        aliasSelectorKeyManager.chooseClientAlias(new String[] {"x509"}, null, null);
    assertThat(chosenAlias, is(expectedAlias));
  }

  @Test
  public void testAliasSelectorKeyManagerAliasNotFound() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    // Return aliases that don't match
    when(keyManager.getClientAliases(any(), any())).thenReturn(new String[] {"otherAlias"});

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String chosenAlias =
        aliasSelectorKeyManager.chooseClientAlias(new String[] {"x509"}, null, null);
    assertThat(chosenAlias, is(nullValue()));
  }

  @Test
  public void testAliasSelectorKeyManagerNullAliasesList() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    when(keyManager.getClientAliases(any(), any())).thenReturn(null);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String chosenAlias =
        aliasSelectorKeyManager.chooseClientAlias(new String[] {"x509"}, null, null);
    assertThat(chosenAlias, is(nullValue()));
  }

  @Test
  public void testAliasSelectorKeyManagerChooseServerAlias() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    String expectedServerAlias = "serverAlias";
    Socket socket = mock(Socket.class);
    Principal[] issuers = new Principal[0];
    when(keyManager.chooseServerAlias(eq("RSA"), any(), any())).thenReturn(expectedServerAlias);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String serverAlias = aliasSelectorKeyManager.chooseServerAlias("RSA", issuers, socket);
    assertThat(serverAlias, is(expectedServerAlias));
    verify(keyManager).chooseServerAlias(eq("RSA"), eq(issuers), eq(socket));
  }

  @Test
  public void testAliasSelectorKeyManagerGetCertificateChain() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    X509Certificate[] expectedCerts = new X509Certificate[] {mock(X509Certificate.class)};
    when(keyManager.getCertificateChain(anyString())).thenReturn(expectedCerts);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    X509Certificate[] certs = aliasSelectorKeyManager.getCertificateChain(alias);
    assertThat(certs, is(expectedCerts));
    verify(keyManager).getCertificateChain(alias);
  }

  @Test
  public void testAliasSelectorKeyManagerGetClientAliases() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    String[] expectedAliases = new String[] {"alias1", "alias2"};
    Principal[] issuers = new Principal[0];
    when(keyManager.getClientAliases(eq("RSA"), any())).thenReturn(expectedAliases);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String[] aliases = aliasSelectorKeyManager.getClientAliases("RSA", issuers);
    assertThat(aliases, is(expectedAliases));
    verify(keyManager).getClientAliases(eq("RSA"), eq(issuers));
  }

  @Test
  public void testAliasSelectorKeyManagerGetPrivateKey() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    PrivateKey expectedKey = mock(PrivateKey.class);
    when(keyManager.getPrivateKey(anyString())).thenReturn(expectedKey);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    PrivateKey key = aliasSelectorKeyManager.getPrivateKey(alias);
    assertThat(key, is(expectedKey));
    verify(keyManager).getPrivateKey(alias);
  }

  @Test
  public void testAliasSelectorKeyManagerGetServerAliases() {
    X509KeyManager keyManager = mock(X509KeyManager.class);
    String alias = "testAlias";
    String[] expectedAliases = new String[] {"serverAlias1", "serverAlias2"};
    Principal[] issuers = new Principal[0];
    when(keyManager.getServerAliases(eq("RSA"), any())).thenReturn(expectedAliases);

    AliasSelectorKeyManager aliasSelectorKeyManager =
        new AliasSelectorKeyManager(keyManager, alias);
    String[] aliases = aliasSelectorKeyManager.getServerAliases("RSA", issuers);
    assertThat(aliases, is(expectedAliases));
    verify(keyManager).getServerAliases(eq("RSA"), eq(issuers));
  }

  @Test
  public void testGetSameUriRedirectMax() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();
    // Default value should be 3 (SAME_URI_REDIRECT_MAX)
    assertThat(secureCxfClientFactory.getSameUriRedirectMax(), is(3));
  }

  @Test
  public void testWebClientWithUsernamePassword() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .username("testUser")
            .password("testPass")
            .build();
    WebClient client = secureCxfClientFactory.getWebClient();
    assertThat(client, is(notNullValue()));
    assertThat(client.getBaseURI().toASCIIString(), is(SECURE_ENDPOINT));
  }

  @Test
  public void testAddOutInterceptors() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();

    @SuppressWarnings("unchecked")
    Interceptor<Message> mockInterceptor = mock(Interceptor.class);
    secureCxfClientFactory.addOutInterceptors(mockInterceptor);
    // Verify the client still works after adding interceptor
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));
  }

  @Test
  public void testClientWithKeyInfo() {
    String alias = "localhost";
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .disableCnCheck(true)
            .clientKeyInfo(alias, systemKeystoreFile.toPath())
            .sslProtocol("TLSv1.2")
            .build();
    WebClient client = secureCxfClientFactory.getWebClient();
    assertThat(client, is(notNullValue()));
  }

  @Test
  public void testClientWithTimeouts() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .connectionTimeout(5000)
            .receiveTimeout(10000)
            .build();
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));

    HTTPConduit httpConduit = WebClient.getConfig(WebClient.client(client)).getHttpConduit();
    assertThat(httpConduit.getClient().getConnectionTimeout(), is(5000L));
    assertThat(httpConduit.getClient().getReceiveTimeout(), is(10000L));
  }

  @Test
  public void testClientWithNullTimeouts() {
    // Test that null timeouts use defaults
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));

    HTTPConduit httpConduit = WebClient.getConfig(WebClient.client(client)).getHttpConduit();
    // Default timeouts: connection=30000, receive=60000
    assertThat(httpConduit.getClient().getConnectionTimeout(), is(30000L));
    assertThat(httpConduit.getClient().getReceiveTimeout(), is(60000L));
  }

  @Test
  public void testWebClientClass() {
    // Test using WebClient.class directly as interfaceClass
    SecureCxfClientFactory<WebClient> secureCxfClientFactory =
        new ClientBuilderImpl<WebClient>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(WebClient.class)
            .build();
    WebClient client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));
    assertThat(client.getBaseURI().toASCIIString(), is(SECURE_ENDPOINT));
  }

  @Test
  public void testClientWithCustomInterceptor() {
    @SuppressWarnings("unchecked")
    Interceptor<Message> customInterceptor = mock(Interceptor.class);

    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .interceptor(customInterceptor)
            .build();
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));

    ClientConfiguration config = WebClient.getConfig(WebClient.client(client));
    assertThat(config.getInInterceptors().contains(customInterceptor), is(true));
  }

  @Test
  public void testClientWithOauthDisabled() {
    // Test without OAuth (useOAuth=false)
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .useOAuth(false)
            .build();
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));
  }

  @Test
  public void testSecureClientWithAllFeaturesDisabled() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .useOAuth(false)
            .useSamlEcp(false)
            .build();
    IDummy client = secureCxfClientFactory.getClient();
    assertThat(client, is(notNullValue()));
    // Check that no PAOS interceptors were added
    assertThat(hasEcpEnabled(client), is(false));
  }

  @Test
  public void testSetSameUriRedirectMaxWithValidValue() {
    SecureCxfClientFactoryImpl<IDummy> secureCxfClientFactory =
        (SecureCxfClientFactoryImpl<IDummy>)
            new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
                .endpoint(SECURE_ENDPOINT)
                .interfaceClass(IDummy.class)
                .build();

    secureCxfClientFactory.setSameUriRedirectMax(5);
    assertThat(secureCxfClientFactory.getSameUriRedirectMax(), is(5));
  }

  @Test
  public void testSetSameUriRedirectMaxWithNullValue() {
    SecureCxfClientFactoryImpl<IDummy> secureCxfClientFactory =
        (SecureCxfClientFactoryImpl<IDummy>)
            new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
                .endpoint(SECURE_ENDPOINT)
                .interfaceClass(IDummy.class)
                .build();

    secureCxfClientFactory.setSameUriRedirectMax(null);
    // Should not change from default value (3)
    assertThat(secureCxfClientFactory.getSameUriRedirectMax(), is(3));
  }

  @Test
  public void testSetSameUriRedirectMaxWithZeroValue() {
    SecureCxfClientFactoryImpl<IDummy> secureCxfClientFactory =
        (SecureCxfClientFactoryImpl<IDummy>)
            new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
                .endpoint(SECURE_ENDPOINT)
                .interfaceClass(IDummy.class)
                .build();

    secureCxfClientFactory.setSameUriRedirectMax(0);
    // Should not change from default value (3)
    assertThat(secureCxfClientFactory.getSameUriRedirectMax(), is(3));
  }

  @Test
  public void testSetSameUriRedirectMaxWithNegativeValue() {
    SecureCxfClientFactoryImpl<IDummy> secureCxfClientFactory =
        (SecureCxfClientFactoryImpl<IDummy>)
            new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
                .endpoint(SECURE_ENDPOINT)
                .interfaceClass(IDummy.class)
                .build();

    secureCxfClientFactory.setSameUriRedirectMax(-5);
    // Should not change from default value (3)
    assertThat(secureCxfClientFactory.getSameUriRedirectMax(), is(3));
  }

  @Test
  public void testGetClientForSystemSubject() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();

    DummySubject subject = getSubject();
    IDummy client = secureCxfClientFactory.getClientForSystemSubject(subject);
    assertThat(client, is(notNullValue()));
  }

  @Test
  public void testGetWebSystemClient() {
    SecureCxfClientFactory<IDummy> secureCxfClientFactory =
        new ClientBuilderImpl<IDummy>(null, samlSecurity, securityLogger, securityManager)
            .endpoint(SECURE_ENDPOINT)
            .interfaceClass(IDummy.class)
            .build();

    WebClient client = secureCxfClientFactory.getWebSystemClient();
    assertThat(client, is(notNullValue()));
    assertThat(client.getBaseURI().toASCIIString(), is(SECURE_ENDPOINT));
  }

  private boolean hasEcpEnabled(Object client) {
    ClientConfiguration clientConfig = WebClient.getConfig(client);
    return clientConfig.getOutInterceptors().stream().anyMatch(i -> i instanceof PaosOutInterceptor)
        && clientConfig.getInInterceptors().stream().anyMatch(i -> i instanceof PaosInInterceptor);
  }

  private DummySubject getSubject() {
    return new DummySubject(new DefaultSecurityManager(), new SimplePrincipalCollection());
  }

  private Subject setupMockSubject() throws Exception {
    Subject mockSubject = mock(Subject.class);
    PrincipalCollection mockPrincipals = mock(PrincipalCollection.class);
    SecurityAssertion mockSecurityAssertion = mock(SecurityAssertion.class);
    SecurityToken mockToken = mock(SecurityToken.class);

    when(mockSubject.getPrincipals()).thenReturn(mockPrincipals);
    when(mockPrincipals.asList()).thenReturn(Arrays.asList(mockSecurityAssertion));
    when(mockSecurityAssertion.getToken()).thenReturn(mockToken);
    when(mockToken.getToken()).thenReturn(getAssertionElement());

    return mockSubject;
  }

  private Element getAssertionElement() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    dbf.setValidating(false);
    dbf.setIgnoringComments(false);
    dbf.setIgnoringElementContentWhitespace(true);
    dbf.setNamespaceAware(true);

    DocumentBuilder db = dbf.newDocumentBuilder();
    db.setEntityResolver(new DOMUtils.NullResolver());

    return db.parse(SecureCxfClientFactoryTest.class.getResourceAsStream("/SAMLAssertion.xml"))
        .getDocumentElement();
  }

  private interface IDummy {
    @GET
    public Response ok();
  }

  private class IDummyImpl implements IDummy {

    @Override
    public Response ok() {
      return Response.ok().build();
    }
  }

  private class DummySubject extends DelegatingSubject implements Subject {

    public DummySubject(
        org.apache.shiro.mgt.SecurityManager manager, PrincipalCollection principals) {
      super(principals, true, null, new SimpleSession(UUID.randomUUID().toString()), manager);
    }

    @Override
    public String getName() {
      return "Dummy Subject";
    }
  }
}
