package com.fpt.officelink.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {
	public static final String USERNAME = "username";
	public static final String EMAIL = "email";
	public static final String SURVEY_ID = "surveyId";
	public static final String ROLE = "role";
	public static final String SECRET_KEY = "11111111111111111111111111111111";
	public static final int EXPIRE_TIME = 86400000;

	public String generateTokenLogin(String email, String role) throws JOSEException {
		String token = null;
		JWSSigner signer = new MACSigner(generateShareSecret());
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
		builder.claim(EMAIL, email);
		builder.claim(ROLE, role);
		builder.expirationTime(generateExpirationDate());
		JWTClaimsSet claimsSet = builder.build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		signedJWT.sign(signer);
		token = signedJWT.serialize();
		return token;
	}

	public String createSurveyToken(String email, Integer surveyId) throws JOSEException {
		String token = null;
		JWSSigner signer = new MACSigner(generateShareSecret());
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
		builder.claim(EMAIL, email);
		builder.claim(SURVEY_ID, surveyId);
		builder.expirationTime(generateExpirationDate());
		JWTClaimsSet claimsSet = builder.build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
		signedJWT.sign(signer);
		token = signedJWT.serialize();
		return token;
	}

	public String createTokenWithEmail(String email) {
		String token = null;
		try {
			// Create HMAC signer
			JWSSigner signer = new MACSigner(generateShareSecret());
			JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
			builder.claim(EMAIL, email);

			builder.expirationTime(generateExpirationDate());
			JWTClaimsSet claimsSet = builder.build();
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
			signedJWT.sign(signer);
			token = signedJWT.serialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	private JWTClaimsSet getClaimsFromToken(String token) {
		JWTClaimsSet claims = null;
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			JWSVerifier verifier = new MACVerifier(generateShareSecret());
			if (signedJWT.verify(verifier)) {
				claims = signedJWT.getJWTClaimsSet();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return claims;
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + EXPIRE_TIME);
	}

	private Date getExpirationDateFromToken(String token) {
		Date expiration = null;
		JWTClaimsSet claims = getClaimsFromToken(token);
		expiration = claims.getExpirationTime();
		return expiration;
	}

	public Integer getSurveyId(String token) throws ParseException {
		Integer surveyId = null;
		JWTClaimsSet claims = getClaimsFromToken(token);
		surveyId = claims.getIntegerClaim(SURVEY_ID);
		return surveyId;
	}

	public String getEmailFromToken(String token) throws ParseException {
		String email = null;

		JWTClaimsSet claims = getClaimsFromToken(token);
		email = claims.getStringClaim(EMAIL);

		return email;
	}

	private byte[] generateShareSecret() {
		// Generate 256-bit (32-byte) shared secret
		byte[] sharedSecret = new byte[32];
		sharedSecret = SECRET_KEY.getBytes();
		return sharedSecret;
	}

	private Boolean isTokenExpired(String token) {
		Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public Boolean validateTokenEmail(String token) throws ParseException {
		if (token == null || token.trim().length() == 0) {
			return false;
		}
		String email = getEmailFromToken(token);
		if (email == null || email.isEmpty()) {
			return false;
		}
		if (isTokenExpired(token)) {
			return false;
		}
		return true;
	}
	
	public Boolean validateTakeSurveyToken(String token) throws ParseException {
		if (token == null || token.trim().length() == 0) {
			return false;
		}
		String email = getEmailFromToken(token);
		if (email == null || email.isEmpty()) {
			return false;
		}
		Integer id = getSurveyId(token);
		if(id == null) {
			return false;
		}
		if (isTokenExpired(token)) {
			return false;
		}
		return true;
	}
}
