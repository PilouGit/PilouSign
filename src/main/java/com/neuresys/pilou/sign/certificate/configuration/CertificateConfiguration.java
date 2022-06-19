package com.neuresys.pilou.sign.certificate.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "certificate")
public class CertificateConfiguration {
	CertificateProperties ca;
	CertificateProperties timestamp;
	
}
