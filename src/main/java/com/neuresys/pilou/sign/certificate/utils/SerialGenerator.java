package com.neuresys.pilou.sign.certificate.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class SerialGenerator {
	SecureRandom secureRandom=new SecureRandom();

	public BigInteger generateSerial()
	{
		return BigInteger.valueOf(secureRandom.nextLong());
	}
}
