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
package org.codice.ddf.admin.insecure.defaults.service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Helper class for generating test certificates using Bouncy Castle. This class provides utilities
 * for creating self-signed X.509 certificates for testing purposes.
 */
public class TestCertificateGenerator {

  private static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";
  private static final String KEY_ALGORITHM = "RSA";
  private static final int KEY_SIZE = 2048;

  static {
    // Register Bouncy Castle provider
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Generates an RSA key pair for testing.
   *
   * @return a new RSA key pair with 2048-bit keys
   * @throws Exception if key pair generation fails
   */
  public static KeyPair generateKeyPair() throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    keyGen.initialize(KEY_SIZE);
    return keyGen.generateKeyPair();
  }

  /**
   * Generates a self-signed X.509 certificate using Bouncy Castle.
   *
   * @param dn the distinguished name for the certificate subject
   * @param keyPair the key pair to use for the certificate
   * @param validityDays the number of days the certificate should be valid
   * @return a self-signed X.509 certificate
   * @throws Exception if certificate generation fails
   */
  public static X509Certificate generateCertificate(String dn, KeyPair keyPair, int validityDays)
      throws Exception {
    PrivateKey privateKey = keyPair.getPrivate();
    PublicKey publicKey = keyPair.getPublic();

    // Create X500 names for issuer and subject (same for self-signed)
    X500Name x500Subject = new X500Name(dn);
    X500Name x500Issuer = x500Subject; // Self-signed, so issuer = subject

    // Calculate validity dates
    Date notBefore = new Date();
    Date notAfter = new Date(notBefore.getTime() + (validityDays * 86400000L));

    // Generate a unique serial number
    BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

    // Build the certificate using Bouncy Castle
    JcaX509v3CertificateBuilder certBuilder =
        new JcaX509v3CertificateBuilder(
            x500Issuer, serialNumber, notBefore, notAfter, x500Subject, publicKey);

    // Create content signer for signing the certificate
    ContentSigner contentSigner =
        new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .build(privateKey);

    // Build and convert the certificate
    X509CertificateHolder certHolder = certBuilder.build(contentSigner);
    JcaX509CertificateConverter certConverter =
        new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);

    return certConverter.getCertificate(certHolder);
  }
}
