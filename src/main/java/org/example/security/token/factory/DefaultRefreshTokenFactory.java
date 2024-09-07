package org.example.security.token.factory;

import lombok.Setter;
import org.example.security.token.Token;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Function;

@Setter
public class DefaultRefreshTokenFactory implements Function<Authentication, Token> {

    private Duration tokenTTL = Duration.ofDays(1);

    @Override
    public Token apply(Authentication authentication) {
        var authorities = new LinkedList<String>();
        authorities.add("JWT_REFRESH");
        authorities.add("JWT_LOGOUT");
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).map(authority -> "GRANT_" + authority)
                .forEach(authorities::add);

        var now = Instant.now();
        return new Token(UUID.randomUUID(), authentication.getName(), authorities, now, now.plus(this.tokenTTL));
    }
}
