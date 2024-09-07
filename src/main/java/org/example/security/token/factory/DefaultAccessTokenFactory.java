package org.example.security.token.factory;


import lombok.Setter;
import org.example.security.token.Token;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

@Setter
public class DefaultAccessTokenFactory implements Function<Token, Token> {

    private Duration tokenTTL = Duration.ofMinutes(5);

    @Override
    public Token apply(Token token) {
        var now = Instant.now();
        return new Token(token.id(), token.subject(),
                token.authorities().stream()
                        .filter(authority -> authority.startsWith("GRANT_"))
                        .map(authority -> authority.replace("GRANT_", ""))
                        .toList(), now, now.plus(tokenTTL));
    }
}
