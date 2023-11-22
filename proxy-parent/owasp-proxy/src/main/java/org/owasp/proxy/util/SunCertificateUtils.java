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

//import java.io.IOException;
//import java.math.BigInteger;
//import java.security.GeneralSecurityException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.Signature;
//import java.security.cert.CertificateEncodingException;
//import java.security.cert.X509Certificate;
//import java.util.Date;
//import java.util.Vector;
//
//import jakarta.security.auth.x500.X500Principal;
//
//import sun.security.util.ObjectIdentifier;
//import sun.security.x509.AlgorithmId;
//import sun.security.x509.AuthorityKeyIdentifierExtension;
//import sun.security.x509.BasicConstraintsExtension;
//import sun.security.x509.CertificateAlgorithmId;
//import sun.security.x509.CertificateExtensions;
//import sun.security.x509.CertificateIssuerName;
//import sun.security.x509.CertificateSerialNumber;
//import sun.security.x509.CertificateSubjectName;
//import sun.security.x509.CertificateValidity;
//import sun.security.x509.CertificateVersion;
//import sun.security.x509.CertificateX509Key;
//import sun.security.x509.ExtendedKeyUsageExtension;
//import sun.security.x509.KeyIdentifier;
//import sun.security.x509.KeyUsageExtension;
//import sun.security.x509.NetscapeCertTypeExtension;
//import sun.security.x509.SubjectKeyIdentifierExtension;
//import sun.security.x509.X500Name;
//import sun.security.x509.X500Signer;
//import sun.security.x509.X509CertImpl;
//import sun.security.x509.X509CertInfo;

@SuppressWarnings("restriction")
public class SunCertificateUtils {

    // private static final String SIGALG = "SHA1withRSA";
    //
    // public static X509Certificate sign(X500Principal subject, PublicKey pubKey,
    // X500Principal issuer, PublicKey caPubKey, PrivateKey caKey,
    // Date begin, Date ends, BigInteger serialNo)
    // throws GeneralSecurityException {
    //
    // try {
    // X500Name subjectName = new X500Name(subject.getName());
    // X500Name issuerName = new X500Name(issuer.getName());
    // Signature signature = Signature.getInstance(SIGALG);
    //
    // signature.initSign(caKey);
    // X500Signer signer = new X500Signer(signature, issuerName);
    //
    // CertificateValidity valid = new CertificateValidity(begin, ends);
    //
    // X509CertInfo info = new X509CertInfo();
    // // Add all mandatory attributes
    // info.set(X509CertInfo.VERSION, new CertificateVersion(
    // CertificateVersion.V3));
    // info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
    // serialNo));
    // AlgorithmId algID = signer.getAlgorithmId();
    // info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
    // algID));
    // info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(
    // subjectName));
    // info.set(X509CertInfo.KEY, new CertificateX509Key(pubKey));
    // info.set(X509CertInfo.VALIDITY, valid);
    // info.set(X509CertInfo.ISSUER, new CertificateIssuerName(signer
    // .getSigner()));
    //
    // // add Extensions
    // CertificateExtensions ext = (subject == issuer) ? getCACertificateExtensions()
    // : getCertificateExtensions(pubKey, caPubKey);
    // info.set(X509CertInfo.EXTENSIONS, ext);
    //
    // X509CertImpl cert = new X509CertImpl(info);
    // cert.sign(caKey, SIGALG);
    //
    // return cert;
    // } catch (IOException e) {
    // throw new CertificateEncodingException("generate: "
    // + e.getMessage(), e);
    // }
    // }
    //
    // private static CertificateExtensions getCACertificateExtensions()
    // throws IOException {
    // CertificateExtensions ext = new CertificateExtensions();
    //
    // // Basic Constraints
    // ext.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(
    // /* isCritical */true, /* isCA */true, 0));
    //
    // return ext;
    // }
    //
    // private static CertificateExtensions getCertificateExtensions(
    // PublicKey pubKey, PublicKey caPubKey) throws IOException {
    // CertificateExtensions ext = new CertificateExtensions();
    //
    // ext.set(SubjectKeyIdentifierExtension.NAME,
    // new SubjectKeyIdentifierExtension(new KeyIdentifier(pubKey)
    // .getIdentifier()));
    //
    // ext.set(AuthorityKeyIdentifierExtension.NAME,
    // new AuthorityKeyIdentifierExtension(
    // new KeyIdentifier(caPubKey), null, null));
    //
    // // Basic Constraints
    // ext.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(
    // /* isCritical */true, /* isCA */false, /* pathLen */5));
    //
    // // Netscape Cert Type Extension
    // boolean[] ncteOk = new boolean[8];
    // ncteOk[0] = true; // SSL_CLIENT
    // ncteOk[1] = true; // SSL_SERVER
    // NetscapeCertTypeExtension ncte = new NetscapeCertTypeExtension(ncteOk);
    // ncte = new NetscapeCertTypeExtension(false, ncte.getExtensionValue());
    // ext.set(NetscapeCertTypeExtension.NAME, ncte);
    //
    // // Key Usage Extension
    // boolean[] kueOk = new boolean[9];
    // kueOk[0] = true;
    // kueOk[2] = true;
    // // "digitalSignature", // (0),
    // // "nonRepudiation", // (1)
    // // "keyEncipherment", // (2),
    // // "dataEncipherment", // (3),
    // // "keyAgreement", // (4),
    // // "keyCertSign", // (5),
    // // "cRLSign", // (6),
    // // "encipherOnly", // (7),
    // // "decipherOnly", // (8)
    // // "contentCommitment" // also (1)
    // KeyUsageExtension kue = new KeyUsageExtension(kueOk);
    // ext.set(KeyUsageExtension.NAME, kue);
    //
    // // Extended Key Usage Extension
    // int[] serverAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 1 };
    // ObjectIdentifier serverAuthOid = new ObjectIdentifier(serverAuthOidData);
    // int[] clientAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 2 };
    // ObjectIdentifier clientAuthOid = new ObjectIdentifier(clientAuthOidData);
    // Vector<ObjectIdentifier> v = new Vector<ObjectIdentifier>();
    // v.add(serverAuthOid);
    // v.add(clientAuthOid);
    // ExtendedKeyUsageExtension ekue = new ExtendedKeyUsageExtension(false, v);
    // ext.set(ExtendedKeyUsageExtension.NAME, ekue);
    //
    // return ext;
    //
    // }

}
