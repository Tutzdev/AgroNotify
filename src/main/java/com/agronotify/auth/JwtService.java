package com.agronotify.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class JwtService {

    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final Algorithm algorithm;
    private final Duration expiracao;

    public JwtService(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiracao:PT2H}") Duration expiracao
    ) {
        if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException(
                    "JWT_SECRET precisa ter pelo menos " + TAMANHO_MINIMO_SEGREDO + " caracteres.");
        }

        this.algorithm = Algorithm.HMAC256(segredo);
        this.expiracao = expiracao;
    }

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("role", usuario.getRole())
                .withExpiresAt(Instant.now().plus(expiracao))
                .sign(algorithm);
    }

    /** Retorna o e-mail do token, ou vazio se ele for inválido ou estiver expirado. */
    public Optional<String> validarToken(String token) {
        try {
            return Optional.of(JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject());
        } catch (JWTVerificationException exception) {
            return Optional.empty();
        }
    }
}
