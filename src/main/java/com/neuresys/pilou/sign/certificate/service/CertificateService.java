package com.neuresys.pilou.sign.certificate.service;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neuresys.pilou.sign.certificate.configuration.CertificateConfiguration;

@Service
public class CertificateService {

	@Autowired KeyStore keyStore;
	@Autowired CertificateConfiguration configuration;
	
	public X509Certificate readTimeStampCertificate() throws KeyStoreException
	{
		X509Certificate x509Certificate=(X509Certificate) keyStore.getCertificate(configuration.getTimestamp().getAlias());
		return x509Certificate;
	}
	public PrivateKey readTimeStampPrivateKey() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
	{
		PrivateKey x509Certificate=(PrivateKey) keyStore.getKey(
				configuration.getTimestamp().getAlias(),configuration.getTimestamp().getPassword());
		return x509Certificate;
	}
}
