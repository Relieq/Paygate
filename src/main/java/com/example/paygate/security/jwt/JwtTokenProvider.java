package com.example.paygate.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final byte[] key;
    private final String issuer;
    private final Duration ttl;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.ttl-minutes}") long ttlMinutes
    ) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
        this.issuer = issuer;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public String issue(String userId, String email, List<String> roles) throws JOSEException {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        // Payload: claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .subject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims); // Chưa tạo sign
        jwt.sign(new MACSigner(key)); // Sign the JWT with the secret key but not encrypt
        return jwt.serialize(); // serialize to compact form (encrypt, ready to be used as a token
    }

    public Instant expiresAtFromNow() {
        return Instant.now().plus(ttl);
    }

    public JWTClaimsSet verify(String token)
            throws JOSEException, ParseException, BadJOSEException {
        SignedJWT jwt = SignedJWT.parse(token);
        boolean ok = jwt.verify(new MACVerifier(key));

        if (!ok) {
            throw new BadJOSEException("Invalid signature");
        }

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Date exp = claims.getExpirationTime();

        if (exp == null || exp.before(new Date())) {
            throw new BadJOSEException("Expired");
        }

        return claims;
    }
}
