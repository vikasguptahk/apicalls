/*
package com.example.apicalltrack;

import io.netty.handler.codec.http.HttpRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.littleshoot.proxy.MitmManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CustomMitmManager implements MitmManager {
    private final KeyPair keyPair;
    private final X509Certificate certificate;

    public CustomMitmManager() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(2048);
            this.keyPair = keyPairGenerator.generateKeyPair();

            X500Name issuerName = new X500Name("CN=localhost");
            X500Name subjectName = new X500Name("CN=localhost");
            Date startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
            Date endtime = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
            BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuerName, serialNumber, startDate, endtime, subjectName, (PublicKey) SubjectPublicKeyInfo.getInstance(keyPair.getPublic())
            );
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));
            this.certificate = cert;
        } catch (Exception e) {
            System.out.println("Error: " +e.getMessage());
            throw new RuntimeException("Error generating key pair and certificate", e);
        }
    }

    @Override
    public SSLEngine serverSslEngine(String peerHost, int peerPort) {
        try {
            return SSLContext.getDefault().createSSLEngine(peerHost, peerPort);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SSLEngine serverSslEngine() {
        return null;
    }

    @Override
    public SSLEngine clientSslEngineFor(HttpRequest httpRequest, SSLSession sslSession) {
        return null;
    }
}
*/
package com.example.apicalltrack;

import io.netty.handler.codec.http.HttpRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.littleshoot.proxy.MitmManager;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

public class CustomMitmManager implements MitmManager {
    private final KeyPair keyPair;
    private final X509Certificate certificate;

    public CustomMitmManager() throws NoSuchAlgorithmException {


        System.out.println("custommitmmanger started");
        System.setProperty("javax.net.debug","ssl");

        Security.addProvider(new BouncyCastleProvider());
        try {
            //Load Default Java Keystore
            char[] keystore_password = "changeit".toCharArray();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream("/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts"),keystore_password);



            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(2048);
            this.keyPair = keyPairGenerator.generateKeyPair();
            X500Name issuerName = new X500Name("CN=localhost");
            X500Name subjectName = new X500Name("CN=localhost");
            Date startDate = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
            Date endtime = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
            BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            // Create a SubjectPublicKeyInfo object from the public key
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
            X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                    issuerName, serialNumber, startDate, endtime, subjectName, publicKeyInfo
            );
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(keyPair.getPrivate());
            X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(contentSigner));

            this.certificate = cert;
            //   *****Important
            keyStore.setCertificateEntry("localhost111",cert);
            // Store the generated certificate in a file
            try(FileOutputStream fos1 = new FileOutputStream("/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts")){
                keyStore.store(fos1,keystore_password);
            }
            //<Reitrieve>
            X509Certificate cert1 = (X509Certificate) keyStore.getCertificate("localhost111");
            if (cert1 != null) {
                System.out.println("Certificate found in truststoreLLLL:");
                System.out.println("Subject: " + cert.getSubjectX500Principal());
                System.out.println("Issuer: " + cert.getIssuerX500Principal());
                System.out.println("Serial Number: " + cert.getSerialNumber());
                System.out.println("Valid From: " + cert.getNotBefore());
                System.out.println("Valid Until: " + cert.getNotAfter());
            } else {
                System.out.println("Certificate not found in truststore");
            }

            // Check the certificate's validity
            assert cert1 != null;
            cert1.checkValidity(new Date());

            // Initialize SSLContext with the custom trust manager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // Check if SSLContext is correctly initialized
            SSLEngine sslEngine = sslContext.createSSLEngine();
            if (sslEngine != null) {
                System.out.println("SSLContext initialized successfully");
            } else {
                System.out.println("SSLContext initialization failed");
            }



            //<Retirtive>



            String certFileName = "generated_certificate.pem";

            try (FileOutputStream fos = new FileOutputStream(certFileName)) {
                fos.write(cert.getEncoded());
            }
            System.out.println("Generated certificate saved to: " + certFileName);


            //Retrieve Certificate:



        } catch (Exception e) {
            System.out.println("Error: " +e.getMessage());
            throw new RuntimeException("Error generating key pair and certificate", e);
        }
        System.out.println("Client debub");
        //clientsupportpro();
        clientsupportprocip();
        System.out.println("server debug");
        //serversupppro();
        serversupportcip();
    }
    public static void serversupportcip() throws NoSuchAlgorithmException {
        String[] s1 = SSLContext.getDefault().getServerSocketFactory().getSupportedCipherSuites();
        System.out.println("server cip:" );
        for(String pro: s1){
            System.out.println(pro);
        }
        SSLParameters ser = SSLContext.getDefault().getDefaultSSLParameters();
        System.out.println("server name" + ser.getServerNames());
    }
    public static void clientsupportprocip() throws NoSuchAlgorithmException {
        SSLParameters clientp = SSLContext.getDefault().getDefaultSSLParameters();
        String[] p = clientp.getProtocols();
        String[] s = clientp.getCipherSuites();
        System.out.println("clinet pro ");
        for(int i=0;i<p.length;i++) System.out.println(p[i]);
        System.out.println("client cip");
        for(int i=0;i<s.length;i++) System.out.println(s[i]);
    }
    //testing
    public void testInterceptTraffic(HttpRequest request){
        System.out.println("testing intercepttraffic: "+request.uri());
    }

    @Override
    public SSLEngine serverSslEngine(String peerHost, int peerPort) {
        try {
            System.out.println("ServerSslEngine");
            System.out.println("Connecting to " + peerHost + ":" + peerPort);
            SSLEngine sslEngine = SSLContext.getDefault().createSSLEngine(peerHost,peerPort);
            sslEngine.setUseClientMode(false);
            //sslEngine.setNeedClientAuth(false);
           // sslEngine.setServerNames(Arrays.asList(new SNIHostName(peerHost)));
            /*SSLParameters sslParameters = sslEngine.getSSLParameters();
            sslParameters.setServerNames(Arrays.asList(new SNIHostName(peerHost)));
            sslEngine.setSSLParameters(sslParameters);*/
            return sslEngine;
            //return SSLContext.getDefault().createSSLEngine(peerHost, peerPort);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error creating ssl engind" ,e);
        }
    }

    @Override
    public SSLEngine serverSslEngine() {
        return null;
    }

    @Override
    public SSLEngine clientSslEngineFor(HttpRequest httpRequest, SSLSession sslSession) {
        try{
            //System.out.println("clientSslEngineFor");
            SSLContext sslContext =SSLContext.getInstance("TLS");
            System.out.println("ClientSslEngineFor");
            System.out.println("Request URI: " + httpRequest.uri());
            System.out.println("Request Method: " + httpRequest.method().name());
            System.out.println("Request Headers: ");
            httpRequest.headers().forEach(header -> System.out.println(header.getKey() + ": " + header.getValue()));

            //sslContext.init(null,new TrustManager[]{new InsecureTrustManager()},null);
            sslContext.init(null,null,null);
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);
            sslEngine.setNeedClientAuth(true);
           // sslEngine.setWantClientAuth(false);
            return sslEngine;
        }catch (Exception e){
            throw new RuntimeException("erro client sslengien"+e.getMessage(),e);
        }
    }
    private static class InsecureTrustManager implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers(){
            return new X509Certificate[0];
        }
    }


}
