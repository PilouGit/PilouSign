package com.neuresys.pilou.sign.certificate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.neuresys.pilou.sign.certificate.configuration.KeyStoreConfiguration;
import com.neuresys.pilou.sign.certificate.utils.SerialGenerator;

@Service
public class CertificateGenerator {
	private static final String BC_PROVIDER = "BC";
	 private static final String KEY_ALGORITHM = "RSA";
	    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	    @Autowired SerialGenerator serialGenerator;
		@Autowired KeyStoreConfiguration keyStoreConfiguration;
	public X509Certificate generateRootCertificate() throws OperatorCreationException, NoSuchAlgorithmException, NoSuchProviderException, CertIOException, CertificateException
	{
		 KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
	        keyPairGenerator.initialize(2048);
	        // Setup start date to yesterday and end date for 1 year validity
	        Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, -1);
	        Date startDate = calendar.getTime();

	        calendar.add(Calendar.YEAR, 1);
	        Date endDate = calendar.getTime();

	        // First step is to create a root certificate
	        // First Generate a KeyPair,
	        // then a random serial number
	        // then generate a certificate using the KeyPair
	        KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
	        BigInteger rootSerialNum = serialGenerator.generateSerial();

	        // Issued By and Issued To same for root certificate
	        X500Name rootCertIssuer = new X500Name("CN=root-cert");
	        X500Name rootCertSubject = rootCertIssuer;
	        ContentSigner rootCertContentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER).build(rootKeyPair.getPrivate());
	        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer, rootSerialNum, startDate, endDate, rootCertSubject, rootKeyPair.getPublic());

	        // Add Extensions
	        // A BasicConstraint to mark root certificate as CA certificate
	        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
	        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
	        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(rootKeyPair.getPublic()));
	        // Create a cert holder and export to X509Certificate
	        X509CertificateHolder rootCertHolder = rootCertBuilder.build(rootCertContentSigner);
	        X509Certificate rootCert = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(rootCertHolder);
	        return rootCert;
	}
	public X509Certificate generateSigningCertificate(X509Certificate certificate) throws NoSuchAlgorithmException, NoSuchProviderException
	{
		 KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, BC_PROVIDER);
	        keyPairGenerator.initialize(2048);
	    X500Name issuedCertSubject = new X500Name("CN=issued-cert");
	        BigInteger issuedCertSerialNum = serialGenerator.generateSerial();
	        KeyPair issuedCertKeyPair = keyPairGenerator.generateKeyPair();
	        return null;
	}
	public KeyStore loadKeyStore(InputStream stream, String storeType,char [] password) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException
	{
		 KeyStore sslKeyStore = KeyStore.getInstance(storeType, BC_PROVIDER);
		 sslKeyStore.load(stream, password);
		 return sslKeyStore;
  
	}
	public void exportKeyPairToKeystoreFile(KeyStore keyStore,String alias,
			KeyPair keyPair, Certificate certificate, 
			 char []  keyPass) throws Exception {
		keyStore.setKeyEntry(alias, keyPair.getPrivate(),keyPass, new Certificate[]{certificate});
       
        
    }

	
	@Bean
	public KeyStore readOrCreateKeyStore() {
		File keyStoreFile = new File(this.keyStoreConfiguration.getFilename());
		if (keyStoreFile.exists()) {
			try (FileInputStream stream = new FileInputStream(this.keyStoreConfiguration.getFilename())) {
				return loadKeyStore(stream, this.keyStoreConfiguration.getType(),
						this.keyStoreConfiguration.getPassword());

			} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException
					| IOException e) {
				
			}
		}
		try {
			return   KeyStore.getInstance( this.keyStoreConfiguration.getType(), BC_PROVIDER);
		} catch (KeyStoreException | NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
		
	}
}
