package com.neuresys.pilou.sign.certificate.configuration;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
public class CertificateProperties {

	String alias;
	char [] password;
}
