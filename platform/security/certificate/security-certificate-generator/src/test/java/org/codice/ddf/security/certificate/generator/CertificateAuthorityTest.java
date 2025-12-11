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
package org.codice.ddf.security.certificate.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CertificateAuthorityTest {

  @Mock CertificateSigningRequest csr;

  @Mock JcaX509v3CertificateBuilder mockBuilder;

  @Mock X509CertificateHolder mockHolder;

  @Mock JcaX509CertificateConverter mockConverter;

  @Mock X509Certificate mockCert;

  @Mock PrivateKey mockPrivateKey;

  @Mock PublicKey mockPublicKey;

  @Test
  public void demoCa() {
    DemoCertificateAuthority demoCa = new DemoCertificateAuthority();
    assertNotNull(demoCa.getCertificate());
    assertNotNull(demoCa.getPrivateKey());
    assertNotNull(demoCa.getContentSigner());
  }

  @Test
  public void testSign() throws Exception {

    DemoCertificateAuthority demoCa =
        new DemoCertificateAuthority() {
          JcaX509CertificateConverter newCertConverter() {
            return mockConverter;
          }
        };

    X509Certificate mockSignedCert = demoCa.getCertificate();
    X509Certificate mockIssuerCert = demoCa.getCertificate();

    when(csr.newCertificateBuilder(any(X509Certificate.class))).thenReturn(mockBuilder);
    when(mockBuilder.build(any(ContentSigner.class))).thenReturn(mockHolder);
    when(mockConverter.getCertificate(any(X509CertificateHolder.class))).thenReturn(mockSignedCert);
    when(csr.getSubjectPrivateKey()).thenReturn(mockPrivateKey);
    when(mockPrivateKey.getAlgorithm()).thenReturn("RSA");

    KeyStore.PrivateKeyEntry newObject = demoCa.sign(csr);
    assertThat(
        "Expected instance of a different class",
        newObject,
        instanceOf(KeyStore.PrivateKeyEntry.class));
  }

  @Test
  public void testSignWithCertificateException() throws Exception {
    DemoCertificateAuthority demoCa =
        new DemoCertificateAuthority() {
          JcaX509CertificateConverter newCertConverter() {
            return mockConverter;
          }
        };

    when(csr.newCertificateBuilder(any(X509Certificate.class))).thenReturn(mockBuilder);
    when(mockBuilder.build(any(ContentSigner.class)))
        .thenAnswer(
            invocation -> {
              throw new CertificateException();
            });

    assertThrows(CertificateGeneratorException.class, () -> demoCa.sign(csr));
  }

  @Test
  public void testSignWithCertIOException() throws Exception {
    DemoCertificateAuthority demoCa =
        new DemoCertificateAuthority() {
          JcaX509CertificateConverter newCertConverter() {
            return mockConverter;
          }
        };

    when(csr.newCertificateBuilder(any(X509Certificate.class))).thenReturn(mockBuilder);
    when(mockBuilder.build(any(ContentSigner.class)))
        .thenAnswer(
            invocation -> {
              throw new CertIOException("test");
            });

    assertThrows(CertificateGeneratorException.class, () -> demoCa.sign(csr));
  }

  @Test
  public void testConstructor() {
    assertNotNull(new CertificateAuthority(mockCert, mockPrivateKey));
  }

  @Test
  public void constructNull1() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new CertificateAuthority(null, mockPrivateKey);
        });
  }

  @Test
  public void constructNull2() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new CertificateAuthority(mockCert, null);
        });
  }

  @Test
  public void makeCertConverter() {
    assertThat(
        "Could not create new certificate converter",
        new DemoCertificateAuthority().newCertConverter(),
        instanceOf(JcaX509CertificateConverter.class));
  }
}
