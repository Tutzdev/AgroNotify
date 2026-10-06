package com.agronotify.auth;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres";

    @Test
    void geraTokenQueDevolveOEmailDoUsuario() {
        JwtService jwtService = new JwtService(SEGREDO, Duration.ofHours(2));

        String token = jwtService.gerarToken(usuario("admin@agro.com"));

        assertThat(jwtService.validarToken(token)).contains("admin@agro.com");
    }

    @Test
    void recusaTokenAssinadoComOutroSegredo() {
        JwtService emissor = new JwtService("outro-segredo-tambem-com-mais-de-32-caracteres", Duration.ofHours(2));
        JwtService validador = new JwtService(SEGREDO, Duration.ofHours(2));

        String token = emissor.gerarToken(usuario("admin@agro.com"));

        assertThat(validador.validarToken(token)).isEmpty();
    }

    @Test
    void recusaTokenExpirado() {
        JwtService jwtService = new JwtService(SEGREDO, Duration.ofSeconds(-1));

        String token = jwtService.gerarToken(usuario("admin@agro.com"));

        assertThat(jwtService.validarToken(token)).isEmpty();
    }

    @Test
    void naoSobeComSegredoCurto() {
        assertThatThrownBy(() -> new JwtService("curto", Duration.ofHours(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    private static Usuario usuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setRole("ADMIN");
        return usuario;
    }
}
