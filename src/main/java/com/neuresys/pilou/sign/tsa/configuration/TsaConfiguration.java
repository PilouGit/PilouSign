package com.neuresys.pilou.sign.tsa.configuration;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "tsa")
public class TsaConfiguration {
 
	String digest;
	private DefaultDigestAlgorithmIdentifierFinder finder;
	
	public TsaConfiguration()
	{
		this.finder=new DefaultDigestAlgorithmIdentifierFinder();
	}
	public AlgorithmIdentifier getDigestAlgorithmIdentifier()
	{
	return this.finder.find(digest);
	
	}

	
}
