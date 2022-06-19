package com.neuresys.pilou.sign.tsa.service;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.neuresys.pilou.sign.certificate.service.CertificateService;
import com.neuresys.pilou.sign.tsa.configuration.TsaConfiguration;

@Service
public class TsaService {
	
	@Autowired TsaConfiguration tsaConfiguration;
	@Autowired CertificateService certificateService;
	@Bean
	public DigestCalculator buildSignerCertDigestCalculator() throws Exception {
        return new JcaDigestCalculatorProviderBuilder().build()
            .get(tsaConfiguration.getDigestAlgorithmIdentifier());
    }
	
	@Bean
	public  SignerInfoGenerator buildSignerInfoGenerator() throws Exception {
	        X509Certificate signingCertificate = certificateService.readTimeStampCertificate();
	         String signingAlgorithmName = signingCertificate.getPublicKey().getAlgorithm();

	        PrivateKey signingPrivateKey = certificateService.readTimeStampPrivateKey();
	        /*String signingAlgorithmName = bouncyCastleSignatureAlgorithmName(publicKeyAlgorithm);
	        log.info("Public key algorithm is '{}', using signing algorithm '{}'.", publicKeyAlgorithm.getJcaName(),
	            signingAlgorithmName);*/

	        return new JcaSimpleSignerInfoGeneratorBuilder().build(signingAlgorithmName, signingPrivateKey, signingCertificate);
	    }

	

}
