package com.agronotify.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private AuthService authService;

    @BeforeEach
    void configurar() {
        JwtService jwtService = new JwtService("segredo-de-teste-com-mais-de-32-caracteres", Duration.ofHours(2));
        authService = new AuthService(usuarioRepository, jwtService, passwordEncoder);

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@agro.com");
        usuario.setSenha(passwordEncoder.encode("senha-correta"));
        usuario.setRole("ADMIN");

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("admin@agro.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    void devolveTokenComCredenciaisCorretas() {
        LoginResponse resposta = authService.login(login("admin@agro.com", "senha-correta"));

        assertThat(resposta.getToken()).isNotBlank();
    }

    @Test
    void senhaErradaEEmailInexistenteTemAMesmaMensagem() {
        assertThatThrownBy(() -> authService.login(login("admin@agro.com", "senha-errada")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos.");

        assertThatThrownBy(() -> authService.login(login("ninguem@agro.com", "senha-correta")))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos.");
    }

    private static LoginRequest login(String email, String senha) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "senha", senha);
        return request;
    }
}
