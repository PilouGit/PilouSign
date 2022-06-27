package com.neuresys.pilou.sign.tsa.service;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponseGenerator;
import org.bouncycastle.tsp.TimeStampTokenGenerator;
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
	         String signingAlgorithmName = signingCertificate.getSigAlgName();
	        PrivateKey signingPrivateKey = certificateService.readTimeStampPrivateKey();
	        return new JcaSimpleSignerInfoGeneratorBuilder().build(signingAlgorithmName, signingPrivateKey, signingCertificate);
	    }

	
	public TimeStampTokenGenerator buildTimeStampTokenGenerator(DigestCalculator digestCalulator,SignerInfoGenerator infoGenerator) throws IllegalArgumentException, TSPException
	{
		TimeStampTokenGenerator result=  new TimeStampTokenGenerator(infoGenerator, digestCalulator,
                new ASN1ObjectIdentifier(tsaConfiguration.getOid()));
		return result;
	}
	public TimeStampResponseGenerator buildTimeStampResponseGenerator (TimeStampTokenGenerator timeStampTokenGenerator)
	{
		TimeStampResponseGenerator timeStampResponseGenerator = new TimeStampResponseGenerator(timeStampTokenGenerator, TSPAlgorithms.ALLOWED);
		return timeStampResponseGenerator;
	}

}
