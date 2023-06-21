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
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

public class BouncyCastleCertificateUtils {

    private static final String SIGALG = "SHA256WithRSAEncryption";

    public static X509Certificate sign(X500Principal subject, PublicKey pubKey,
            X500Principal issuer, PublicKey caPubKey, PrivateKey caKey,
            Date begin, Date ends, BigInteger serialNo)
            throws GeneralSecurityException {

        try {
            Security.addProvider(new BouncyCastleProvider());
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer, serialNo, begin, ends, subject, pubKey);

            // add Extensions
            if (subject == issuer) {
                addCACertificateExtensions(certBuilder);
            } else {
                addCertificateExtensions(pubKey, caPubKey, certBuilder);
            }

            ContentSigner contentSigner = new JcaContentSignerBuilder(SIGALG)
                    .setProvider("BC").build(caKey);

            return new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certBuilder.build(contentSigner));

        } catch (Exception e) {
            e.printStackTrace();
            throw new CertificateEncodingException("generate: "
                    + e.getMessage(), e);
        }
    }

    private static void addCACertificateExtensions(
            X509v3CertificateBuilder certBuilder) throws IOException {
        // Basic Constraints
        certBuilder.addExtension(Extension.basicConstraints, true,
                new BasicConstraints(0));
    }

    private static void addCertificateExtensions(PublicKey pubKey,
            PublicKey caPubKey,  X509v3CertificateBuilder certBuilder)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException {


        JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, extensionUtils.createSubjectKeyIdentifier(pubKey));

        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, extensionUtils.createAuthorityKeyIdentifier(caPubKey));


        certBuilder.addExtension(Extension.basicConstraints, true,
                new BasicConstraints(false));

        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

        Vector<KeyPurposeId> usages = new Vector<KeyPurposeId>();
        usages.add(KeyPurposeId.id_kp_serverAuth);
        usages.add(KeyPurposeId.id_kp_clientAuth);
        certBuilder.addExtension(Extension.extendedKeyUsage, true,
                new ExtendedKeyUsage(usages));

    }
}
