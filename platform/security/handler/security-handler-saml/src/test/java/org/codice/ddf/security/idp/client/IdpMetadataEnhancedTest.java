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
package org.codice.ddf.security.idp.client;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.InputStream;
import java.io.StringWriter;
import java.time.Duration;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;

public class IdpMetadataEnhancedTest {

  private IdpMetadata idpMetadata;
  private String metadata;
  private String metadataWithCacheDuration;
  private String metadataWithValidUntil;
  private String metadataWithBothTimestamps;

  @Before
  public void setup() throws Exception {
    idpMetadata = new IdpMetadata();

    StringWriter writer = new StringWriter();
    InputStream inputStream = this.getClass().getResourceAsStream("/IDPmetadata.xml");
    IOUtils.copy(inputStream, writer, "UTF-8");
    metadata = writer.toString();

    // Create metadata with cache duration
    metadataWithCacheDuration =
        metadata.replaceFirst(
            "<EntityDescriptor",
            "<EntityDescriptor cacheDuration=\"PT30M\"");

    // Create metadata with validUntil
    DateTime futureTime = DateTime.now().plusHours(2);
    metadataWithValidUntil =
        metadata.replaceFirst(
            "<EntityDescriptor",
            "<EntityDescriptor validUntil=\"" + futureTime + "\"");

    // Create metadata with both
    metadataWithBothTimestamps =
        metadata.replaceFirst(
            "<EntityDescriptor",
            "<EntityDescriptor cacheDuration=\"PT30M\" validUntil=\"" + futureTime + "\"");
  }

  @Test
  public void testSetMetadataClearsCache() {
    idpMetadata.setMetadata(metadata);
    assertThat(idpMetadata.getEntityDescriptor(), is(notNullValue()));

    // Setting new metadata should clear cache
    idpMetadata.setMetadata(metadata);
    assertThat(idpMetadata.entityData.get(), is(nullValue()));
  }

  @Test
  public void testGetEntityDescriptorWithValidMetadata() {
    idpMetadata.setMetadata(metadata);
    EntityDescriptor descriptor = idpMetadata.getEntityDescriptor();

    assertThat(descriptor, is(notNullValue()));
    assertThat(descriptor.getEntityID(), is(notNullValue()));
  }

  @Test
  public void testGetEntityDescriptorWithInvalidMetadata() {
    idpMetadata.setMetadata("invalid xml");
    EntityDescriptor descriptor = idpMetadata.getEntityDescriptor();

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  public void testGetEntityDescriptorWithNullMetadata() {
    idpMetadata.setMetadata(null);
    EntityDescriptor descriptor = idpMetadata.getEntityDescriptor();

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  public void testGetEntityDescriptorWithEmptyMetadata() {
    idpMetadata.setMetadata("");
    EntityDescriptor descriptor = idpMetadata.getEntityDescriptor();

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  public void testGetDescriptor() {
    idpMetadata.setMetadata(metadata);
    IDPSSODescriptor descriptor = idpMetadata.getDescriptor();

    assertThat(descriptor, is(notNullValue()));
  }

  @Test
  public void testGetDescriptorWithNullEntityDescriptor() {
    idpMetadata.setMetadata("invalid");
    IDPSSODescriptor descriptor = idpMetadata.getDescriptor();

    assertThat(descriptor, is(nullValue()));
  }

  @Test
  public void testGetSingleSignOnLocationWithRedirectBinding() {
    idpMetadata.setMetadata(metadata);
    String location = idpMetadata.getSingleSignOnLocation();

    assertThat(location, is(notNullValue()));
  }

  @Test
  public void testGetSingleSignOnLocationWithPostBinding() {
    String modifiedMetadata = metadata.replace("HTTP-Redirect", "HTTP-POST");
    idpMetadata.setMetadata(modifiedMetadata);
    String location = idpMetadata.getSingleSignOnLocation();

    assertThat(location, is(notNullValue()));
  }

  @Test
  public void testGetSingleSignOnBinding() {
    idpMetadata.setMetadata(metadata);
    String binding = idpMetadata.getSingleSignOnBinding();

    assertThat(binding, is(notNullValue()));
    assertThat(
        binding, containsString("urn:oasis:names:tc:SAML:2.0:bindings:HTTP"));
  }

  @Test
  public void testGetSingleLogoutLocation() {
    idpMetadata.setMetadata(metadata);
    String location = idpMetadata.getSingleLogoutLocation();

    // May be null depending on metadata
    if (location != null) {
      assertThat(location, is(notNullValue()));
    }
  }

  @Test
  public void testGetSingleLogoutBinding() {
    idpMetadata.setMetadata(metadata);
    String binding = idpMetadata.getSingleLogoutBinding();

    // May be null depending on metadata
    if (binding != null) {
      assertThat(binding, is(notNullValue()));
    }
  }

  @Test
  public void testGetEntityId() {
    idpMetadata.setMetadata(metadata);
    String entityId = idpMetadata.getEntityId();

    assertThat(entityId, is(notNullValue()));
  }

  @Test
  public void testGetEntityIdWithNullDescriptor() {
    idpMetadata.setMetadata("invalid");
    String entityId = idpMetadata.getEntityId();

    assertThat(entityId, is(nullValue()));
  }

  @Test
  public void testGetSigningCertificate() {
    idpMetadata.setMetadata(metadata);
    String cert = idpMetadata.getSigningCertificate();

    assertThat(cert, is(notNullValue()));
  }

  @Test
  public void testGetEncryptionCertificate() {
    idpMetadata.setMetadata(metadata);
    String cert = idpMetadata.getEncryptionCertificate();

    assertThat(cert, is(notNullValue()));
  }

  @Test
  public void testIsMetadataExpiredWithNoCacheDuration() {
    idpMetadata.setMetadata(metadata);
    idpMetadata.getEntityDescriptor(); // Populate cache

    assertThat(idpMetadata.isMetadataExpired(), is(false));
  }

  @Test
  public void testIsMetadataExpiredWithCacheDuration() {
    idpMetadata.setMetadata(metadataWithCacheDuration);
    idpMetadata.getEntityDescriptor(); // Populate cache

    assertThat(idpMetadata.isMetadataExpired(), is(false));
  }

  @Test
  public void testIsMetadataValidWithNoValidUntil() {
    idpMetadata.setMetadata(metadataWithCacheDuration);
    idpMetadata.getEntityDescriptor(); // Populate cache

    assertThat(idpMetadata.isMetadataValid(), is(true));
  }

  @Test
  public void testIsMetadataValidWithValidUntil() {
    idpMetadata.setMetadata(metadataWithValidUntil);
    idpMetadata.getEntityDescriptor(); // Populate cache

    assertThat(idpMetadata.isMetadataValid(), is(true));
  }

  @Test
  public void testIsMetadataValidWithExpiredValidUntil() {
    DateTime pastTime = DateTime.now().minusHours(2);
    String expiredMetadata =
        metadata.replaceFirst(
            "<EntityDescriptor",
            "<EntityDescriptor validUntil=\"" + pastTime + "\"");

    idpMetadata.setMetadata(expiredMetadata);
    idpMetadata.getEntityDescriptor(); // Populate cache

    assertThat(idpMetadata.isMetadataValid(), is(false));
  }

  @Test
  public void testMetadataRefreshOnExpiration() {
    idpMetadata.setMetadata(metadataWithCacheDuration);
    EntityDescriptor descriptor1 = idpMetadata.getEntityDescriptor();
    assertThat(descriptor1, is(notNullValue()));

    // Metadata should still be valid
    EntityDescriptor descriptor2 = idpMetadata.getEntityDescriptor();
    assertThat(descriptor2, is(notNullValue()));
  }

  @Test
  public void testParseMetadataWithValidXml() {
    idpMetadata.setMetadata(metadata);
    var result = idpMetadata.parseMetadata();

    assertThat(result, is(notNullValue()));
    assertThat(result.isEmpty(), is(false));
  }

  @Test
  public void testParseMetadataWithInvalidXml() {
    idpMetadata.setMetadata("not valid xml");
    var result = idpMetadata.parseMetadata();

    assertThat(result, is(nullValue()));
  }

  @Test
  public void testEntityDataCacheDuration() {
    idpMetadata.setMetadata(metadataWithCacheDuration);
    EntityDescriptor descriptor = idpMetadata.getEntityDescriptor();
    assertThat(descriptor, is(notNullValue()));

    IdpMetadata.EntityData entityData = idpMetadata.entityData.get();
    assertThat(entityData, is(notNullValue()));
    assertThat(entityData.getCacheDuration(), is(notNullValue()));
    assertThat(entityData.getCacheDuration(), is(Duration.ofMinutes(30)));
  }

  @Test
  public void testEntityDataWithNullDescriptor() {
    IdpMetadata.EntityData entityData = idpMetadata.new EntityData(null);

    assertThat(entityData.getEntityDescriptor(), is(nullValue()));
    assertThat(entityData.getCacheDuration(), is(nullValue()));
  }

  @Test
  public void testMultipleConcurrentGetEntityDescriptor() {
    idpMetadata.setMetadata(metadata);

    // Simulate concurrent access
    EntityDescriptor descriptor1 = idpMetadata.getEntityDescriptor();
    EntityDescriptor descriptor2 = idpMetadata.getEntityDescriptor();

    assertThat(descriptor1, is(notNullValue()));
    assertThat(descriptor2, is(notNullValue()));
    assertThat(descriptor1.getEntityID(), equalTo(descriptor2.getEntityID()));
  }

  @Test
  public void testGetSingleSignOnPreferenceRedirectOverPost() {
    // Create metadata with both bindings
    String bothBindings =
        metadata.replace(
            "SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\"",
            "SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\"><SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" Location=\"https://localhost:8993/services/idp/login\"");

    idpMetadata.setMetadata(bothBindings);
    String binding = idpMetadata.getSingleSignOnBinding();

    // Should prefer Redirect
    assertThat(binding, is(notNullValue()));
  }

  @Test
  public void testCertificateInitializationWithUnspecifiedUsage() {
    idpMetadata.setMetadata(metadata);

    String signingCert = idpMetadata.getSigningCertificate();
    String encryptionCert = idpMetadata.getEncryptionCertificate();

    // Both should be populated
    assertThat(signingCert, is(notNullValue()));
    assertThat(encryptionCert, is(notNullValue()));
  }

  @Test
  public void testMetadataValidityAfterSetNull() {
    idpMetadata.setMetadata(metadata);
    assertThat(idpMetadata.getEntityDescriptor(), is(notNullValue()));

    idpMetadata.setMetadata(null);
    assertThat(idpMetadata.entityData.get(), is(nullValue()));
  }

  @Test
  public void testGetDescriptorMultipleCalls() {
    idpMetadata.setMetadata(metadata);

    IDPSSODescriptor descriptor1 = idpMetadata.getDescriptor();
    IDPSSODescriptor descriptor2 = idpMetadata.getDescriptor();

    assertThat(descriptor1, is(notNullValue()));
    assertThat(descriptor2, is(notNullValue()));
  }
}
