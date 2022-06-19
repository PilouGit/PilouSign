package com.neuresys.pilou.sign.certificate.configuration;

import java.security.KeyStore;

import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
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
@ConfigurationProperties(prefix = "keystore")
public class KeyStoreConfiguration {
	String type;
	String filename;
	char [] password;

	
}
