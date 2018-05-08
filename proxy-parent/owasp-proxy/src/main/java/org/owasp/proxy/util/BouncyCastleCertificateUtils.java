/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

@SuppressWarnings("deprecation")
public class BouncyCastleCertificateUtils {

    private static final String SIGALG = "SHA256WithRSAEncryption";

    public static X509Certificate sign(X500Principal subject, PublicKey pubKey,
            X500Principal issuer, PublicKey caPubKey, PrivateKey caKey,
            Date begin, Date ends, BigInteger serialNo)
            throws GeneralSecurityException {

        try {
            // X500Name issuerName = new X500Name(issuer.getName());
            // X500Name subjectName = new X500Name(subject.getName());
            // AlgorithmIdentifier algId = new
            // AlgorithmIdentifier(pubKey.getAlgorithm());
            // SubjectPublicKeyInfo publicKeyInfo = new
            // SubjectPublicKeyInfo(algId, pubKey.getEncoded());
            //
            // X509v1CertificateBuilder certBuilder = new
            // X509v1CertificateBuilder(issuerName, serialNo, begin, ends,
            // subjectName, publicKeyInfo);
            // ContentSigner cs = new
            // JcaContentSignerBuilder(SIGALG).setProvider("BC").build(caKey);
            // X509CertificateHolder holder = certBuilder.build(cs);
            // Certificate bcCert = holder.toASN1Structure();
            // bcCert.get
            // X509V1CertificateGenerator certGen = new
            // X509V1CertificateGenerator();
            X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

            certGen.setSerialNumber(serialNo);
            certGen.setIssuerDN(issuer);
            certGen.setNotBefore(begin);
            certGen.setNotAfter(ends);
            certGen.setSubjectDN(subject);
            certGen.setPublicKey(pubKey);

            certGen.setSignatureAlgorithm(SIGALG);

            // add Extensions
            if (subject == issuer) {
                addCACertificateExtensions(certGen);
            } else {
                addCertificateExtensions(pubKey, caPubKey, certGen);
            }

            return certGen.generate(caKey, "BC"); // note:
                                                     // private
                                                     // key of CA
        } catch (Exception e) {
            e.printStackTrace();
            throw new CertificateEncodingException("generate: "
                    + e.getMessage(), e);
        }
    }

    private static void addCACertificateExtensions(
            X509V3CertificateGenerator certGen) throws IOException {
        // Basic Constraints
        certGen.addExtension(X509Extensions.BasicConstraints, true,
                new BasicConstraints(0));
    }

    private static void addCertificateExtensions(PublicKey pubKey,
            PublicKey caPubKey, X509V3CertificateGenerator certGen)
            throws IOException, InvalidKeyException {

        // CertificateExtensions ext = new CertificateExtensions();
        //
        // ext.set(SubjectKeyIdentifierExtension.NAME,
        // new SubjectKeyIdentifierExtension(new KeyIdentifier(pubKey)
        // .getIdentifier()));
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
                new SubjectKeyIdentifierStructure(pubKey));
        //
        // ext.set(AuthorityKeyIdentifierExtension.NAME,
        // new AuthorityKeyIdentifierExtension(
        // new KeyIdentifier(caPubKey), null, null));
        //
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
                new AuthorityKeyIdentifierStructure(caPubKey));
        // // Basic Constraints
        // ext.set(BasicConstraintsExtension.NAME, new
        // BasicConstraintsExtension(
        // /* isCritical */true, /* isCA */false, /* pathLen */5));
        //
        certGen.addExtension(X509Extensions.BasicConstraints, true,
                new BasicConstraints(false));

        // Netscape Cert Type Extension
        // boolean[] ncteOk = new boolean[8];
        // ncteOk[0] = true; // SSL_CLIENT
        // ncteOk[1] = true; // SSL_SERVER
        // NetscapeCertTypeExtension ncte = new
        // NetscapeCertTypeExtension(ncteOk);
        // ncte = new NetscapeCertTypeExtension(false,
        // ncte.getExtensionValue());
        // ext.set(NetscapeCertTypeExtension.NAME, ncte);

        // Key Usage Extension
        // boolean[] kueOk = new boolean[9];
        // kueOk[0] = true;
        // kueOk[2] = true;
        // "digitalSignature", // (0),
        // "nonRepudiation", // (1)
        // "keyEncipherment", // (2),
        // "dataEncipherment", // (3),
        // "keyAgreement", // (4),
        // "keyCertSign", // (5),
        // "cRLSign", // (6),
        // "encipherOnly", // (7),
        // "decipherOnly", // (8)
        // "contentCommitment" // also (1)
        // KeyUsageExtension kue = new KeyUsageExtension(kueOk);
        // ext.set(KeyUsageExtension.NAME, kue);
        certGen.addExtension(X509Extensions.KeyUsage, true, new X509KeyUsage(
                X509KeyUsage.digitalSignature + X509KeyUsage.keyEncipherment));

        // Extended Key Usage Extension
        // int[] serverAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 1 };
        // ObjectIdentifier serverAuthOid = new
        // ObjectIdentifier(serverAuthOidData);
        // int[] clientAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 2 };
        // ObjectIdentifier clientAuthOid = new
        // ObjectIdentifier(clientAuthOidData);
        // Vector<ObjectIdentifier> v = new Vector<ObjectIdentifier>();
        // v.add(serverAuthOid);
        // v.add(clientAuthOid);
        // ExtendedKeyUsageExtension ekue = new ExtendedKeyUsageExtension(false,
        // v);
        // ext.set(ExtendedKeyUsageExtension.NAME, ekue);
        // ExtendedKeyUsage extendedKeyUsage = new
        // ExtendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage);
        Vector<KeyPurposeId> usages = new Vector<KeyPurposeId>();
        usages.add(KeyPurposeId.id_kp_serverAuth);
        usages.add(KeyPurposeId.id_kp_clientAuth);
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
                new ExtendedKeyUsage(usages));

    }
}
