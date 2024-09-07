package org.example.security.token.serializer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.security.token.Token;

import java.util.Date;
import java.util.function.Function;

@Slf4j
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AccessTokenJwsStringSerializer implements Function<Token, String> {

    private final JWSSigner jwsSigner;
    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    @Override
    public String apply(Token token) {
        var jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
                .keyID(token.id().toString())
                .build();
        var jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.subject())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.createdAt()))
                .claim("authorities", token.authorities())
                .build();

        var signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        try {
            signedJWT.sign(this.jwsSigner);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
