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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PkiToolsTest {

  @Mock private X509Certificate mockCert;

  @Mock private PrivateKey mockKey;

  @Test
  public void testDerToPrivateKey() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          PkiTools.derToPrivateKey(new byte[] {0});
        });
  }

  @Test
  public void nameIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeDistinguishedName(null);
        });
  }

  @Test
  public void nameIsEmptyString() throws CertificateEncodingException {

    X500Name name = PkiTools.makeDistinguishedName("");
    assertThat(name.toString(), equalTo("cn="));
  }

  @Test
  public void nameIsNotEmpty() throws CertificateEncodingException {
    String host = "host.domain.tld";
    X500Name name = PkiTools.makeDistinguishedName(host);
    assertThat(name.toString(), equalTo("cn=" + host));
  }

  @Test
  public void dnIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.convertDistinguishedName((String[]) null);
        });
  }

  @Test
  public void dnIsEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.convertDistinguishedName("");
        });
  }

  @Test
  public void dnIsNotValidFormat() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.convertDistinguishedName("cnIsSomething", "l=london");
        });
  }

  @Test
  public void dnHasInvalidRDN() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.convertDistinguishedName("cnxxx=IsSomething", "l=london");
        });
  }

  @Test
  public void dnIsValidFormat() throws CertificateEncodingException {
    X500Name name =
        PkiTools.convertDistinguishedName(
            "cn=john.smith", "o=police box", "o = Tardis", "l= London", "c=UK");
    assertThat(name.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString(), equalTo("john.smith"));
    assertThat(name.getRDNs(BCStyle.O).length, equalTo(2));
    assertThat(name.getRDNs(BCStyle.C)[0].getFirst().getValue().toString(), equalTo("UK"));
  }

  @Test
  public void convertCertificate() throws CertificateException {
    String originalCert = DemoCertificateAuthority.pemDemoCaCertificate;
    assertThat(originalCert, not(equalTo("")));
    assertThat(
        PkiTools.certificateToPem(PkiTools.pemToCertificate(originalCert)), equalTo(originalCert));
  }

  @Test
  public void hostName() {
    assertThat(PkiTools.getHostName(), not(equalTo("")));
  }

  @Test
  public void exception() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          throw new CertificateGeneratorException("", new Exception());
        });
  }

  private String getPathTo(String path) {
    return getClass().getClassLoader().getResource(path).getPath();
  }

  @Test
  public void testFormatPassword() throws Exception {
    assertThat(
        "formatPassword() failed to return empty character array",
        PkiTools.formatPassword(null),
        instanceOf(char[].class));

    char[] pw = "password".toCharArray();
    assertThat(
        "formatPassword() should not modify the password",
        new String(PkiTools.formatPassword(pw)),
        equalTo("password"));
  }

  // Null path to keyStore file.
  @Test
  public void nullPath() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.createFileObject(null);
        });
  }

  // Test constructor. Invalid path to keyStore file.
  @Test
  public void invalidPath() {
    assertThrows(
        FileNotFoundException.class,
        () -> {
          PkiTools.createFileObject("");
        });
  }

  // Test Constructor. Path is a directory, not a file.
  @Test
  public void pathIsDirectory() {
    String anyDirectory = getPathTo("");
    assertThrows(
        FileNotFoundException.class,
        () -> {
          PkiTools.createFileObject("");
        });
  }

  @Test
  public void realFile() throws IOException {
    assertThat(
        "Should have returned a new File object. Is the file in the test resources directory?",
        PkiTools.createFileObject(getPathTo("not_keystore.jks")),
        instanceOf(File.class));
  }

  @Test
  public void badKey() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          PkiTools.pemToPrivateKey("YmFkc3RyaW5n");
        });
  }

  @Test
  public void badCert() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          PkiTools.pemToCertificate("YmFkc3RyaW5n");
        });
  }

  @Test
  public void testDerToCertificate() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.derToCertificate(null);
        });
  }

  @Test
  public void testDerToPem() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.derToPem(null);
        });
  }

  @Test
  public void testNullToPrivateKey() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.derToPrivateKey(null);
        });
  }

  @Test
  public void testCertificateToPem() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.certificateToPem(null);
        });
  }

  @Test
  public void testKeyToDer() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.keyToDer(null);
        });
  }

  @Test
  public void testPemToDer() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.pemToDer(null);
        });
  }

  @Test
  public void testKeyToPem() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.keyToPem(null);
        });
  }

  @Test
  public void test() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          PkiTools.certificateToPem(mockCert);
        });
  }

  @Test
  public void testKeyConversion() {
    when(mockKey.getEncoded()).thenReturn(new byte[] {0});
    byte[] der = PkiTools.keyToDer(mockKey);
    assertThat(
        "keyToDer() should round-trip the encoded key bytes through PEM and back",
        der,
        equalTo(new byte[] {0}));
  }

  @Test
  public void testDerToCert() {
    assertThrows(
        CertificateGeneratorException.class,
        () -> {
          PkiTools.derToCertificate(new byte[] {0});
        });
  }

  @Test
  public void testMakeGeneralNameNullName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName(null);
        });
  }

  @Test
  public void testMakeGeneralNameMissingSeparator() {
    final String name = "A";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName(name);
        });
  }

  @Test
  public void testMakeGeneralNameEmptyValue() {
    final String name = "A:";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName(name);
        });
  }

  @Test
  public void testMakeGeneralNameUnkownTag() {
    final String name = "A:A";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName(name);
        });
  }

  @Test
  public void testMakeGeneralNameForEmail() {
    final String value = "a@host.com";

    final GeneralName gname = PkiTools.makeGeneralName("email:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.rfc822Name));
    assertThat(gname.getName().toString(), equalTo(value));
  }

  @Test
  public void testMakeGeneralNameForURI() {
    final String value = "http://ocsp.my.host/";

    final GeneralName gname = PkiTools.makeGeneralName("URI:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.uniformResourceIdentifier));
    assertThat(gname.getName().toString(), equalTo(value));
  }

  @Test
  public void testMakeGeneralNameForRID() {
    final String value = "0.2.1.4";

    final GeneralName gname = PkiTools.makeGeneralName("RID:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.registeredID));
    assertThat(gname.getName().toString(), equalTo(value));
  }

  @Test
  public void testMakeGeneralNameForRIDWithInvalidID() {
    final String value = "3.2.1.4";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName("RID:" + value);
        });
  }

  @Test
  public void testMakeGeneralNameForDNS() {
    final String value = "A";

    final GeneralName gname = PkiTools.makeGeneralName("DNS:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.dNSName));
    assertThat(gname.getName().toString(), equalTo(value));
  }

  @Test
  public void testMakeGeneralNameForIP() {
    final String value = "1.2.3.4";

    final GeneralName gname = PkiTools.makeGeneralName("IP:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.iPAddress));
    assertThat(gname.getName().toString(), equalTo("#01020304"));
  }

  @Test
  public void testMakeGeneralNameForIPWithInvalidIP() {
    final String value = "1.2.3";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName("IP:" + value);
        });
  }

  @Test
  public void testMakeGeneralNameForDirName() {
    final String value = "C=UK+CN=My Name+OU=My Unit+O=My Organization";

    final GeneralName gname = PkiTools.makeGeneralName("dirName:" + value);

    assertThat(gname.getTagNo(), equalTo(GeneralName.directoryName));
    assertThat(gname.getName().toString(), equalTo(value));
  }

  @Test
  public void testMakeGeneralNameForDirNameWithInvalidName() {
    final String value = "A";
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          PkiTools.makeGeneralName("dirName:" + value);
        });
  }
}
