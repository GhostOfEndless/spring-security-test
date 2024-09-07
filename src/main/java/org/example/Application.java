package org.example;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.example.security.JwtAuthenticationConfigurer;
import org.example.security.token.serializer.AccessTokenJwsStringSerializer;
import org.example.security.token.serializer.RefreshTokenJweStringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.text.ParseException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            @Value("${jwt.access-token-key}") String accessTokeKey,
            @Value("${jwt.refresh-token-key}") String refreshTokenKey
    ) throws ParseException, JOSEException {
        return new JwtAuthenticationConfigurer()
                .accessTokenStringSerializer(new AccessTokenJwsStringSerializer(
                        new MACSigner(OctetSequenceKey.parse(accessTokeKey))
                ))
                .refreshTokenStringSerializer(new RefreshTokenJweStringSerializer(
                        new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey))
                ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConfigurer jwtAuthenticationConfigurer)
            throws Exception {

        http.with(jwtAuthenticationConfigurer, Customizer.withDefaults());

        return http
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/manager.html").hasRole("MANAGER")
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated())
                .build();
    }
}