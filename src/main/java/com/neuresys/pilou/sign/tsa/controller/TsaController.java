package com.neuresys.pilou.sign.tsa.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.TimeStampRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.neuresys.pilou.sign.certificate.utils.SerialGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController("/tsa")
@RequiredArgsConstructor
@Slf4j
public class TsaController {

	@Autowired SerialGenerator serialGenerator;
	@Autowired DigestCalculator digestCalculator;
	
	public byte [] timestamp(InputStream inputStream) throws IOException
	{
		log.debug("TsaController::timestamp");
		ASN1InputStream asnInputStream = new ASN1InputStream(inputStream);
		TimeStampReq timeStampReq = TimeStampReq.getInstance(asnInputStream.readObject());
		TimeStampRequest timeStampRequest=new TimeStampRequest(timeStampReq);
		
		BigInteger tspResponseSerial = serialGenerator.generateSerial();
		
        return null;
	}
}
