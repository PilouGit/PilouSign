package com.neuresys.pilou.sign.tsa.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import com.neuresys.pilou.sign.certificate.service.CertificateService;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neuresys.pilou.sign.certificate.utils.SerialGenerator;
import com.neuresys.pilou.sign.tsa.service.TsaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController
@RequestMapping("/tsa")
@RequiredArgsConstructor
@Slf4j
public class TsaController {

	@Autowired SerialGenerator serialGenerator;
	@Autowired TsaService tsaService;
	@Autowired DigestCalculator digestCalculator;
	@Autowired SignerInfoGenerator signerInfoGenerator;
	private TimeStampResponseGenerator timeStampResponseGenerator;
	@Autowired
	CertificateService certificateService;
	public TimeStampResponse timestamp(InputStream inputStream) throws Exception {
		TimeStampTokenGenerator tokenGenerator=tsaService.buildTimeStampTokenGenerator(digestCalculator, signerInfoGenerator);
		tokenGenerator.addCertificates( certificateService.getStore());

		this.timeStampResponseGenerator=tsaService.buildTimeStampResponseGenerator(tokenGenerator);
		log.debug("TsaController::timestamp");
		ASN1InputStream asnInputStream = new ASN1InputStream(inputStream);
		TimeStampReq timeStampReq = TimeStampReq.getInstance(asnInputStream.readObject());
		TimeStampRequest timeStampRequest=new TimeStampRequest(timeStampReq);

		BigInteger tspResponseSerial = serialGenerator.generateSerial();
		 TimeStampResponse tsResp = this.timeStampResponseGenerator.generate(timeStampRequest, tspResponseSerial, new Date());
		tsResp = new TimeStampResponse(tsResp.getEncoded());
		TimeStampToken tsToken = tsResp.getTimeStampToken();
		tsResp.validate(timeStampRequest);

		return tsResp;
	}
	@PostMapping(
            consumes = "application/timestamp-query",
            produces = "application/timestamp-reply")
public ResponseEntity<byte[]> sign(InputStream requestInputStream) throws Exception {
	TimeStampResponse responseData = timestamp(requestInputStream);
   return ResponseEntity.ok(responseData.getEncoded());
}
}
