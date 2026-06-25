package com.agronotify.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {
    private static final String SECRET =  "chave-secreta-temporaria";

    public String gerarToken(Usuario usuario) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
        .withSubject(usuario.getEmail())
        .withClaim("role", usuario.getRole())
        .withExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS)).sign(algorithm);
    }

    public String validarToken(String token) {
    Algorithm algorithm = Algorithm.HMAC256(SECRET);

    return JWT.require(algorithm)
            .build()
            .verify(token)
            .getSubject();
}
}