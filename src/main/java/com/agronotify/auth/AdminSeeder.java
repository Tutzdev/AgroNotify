package com.agronotify.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Usuario admin = usuarioRepository
                .findByEmail("admin@agro.com")
                .orElse(null);

        if (admin == null) {
            admin = new Usuario();
            admin.setEmail("admin@agro.com");
        }

        admin.setNome("Administrador");
        admin.setRole("ADMIN");

        // Só altera a senha se ela ainda não estiver criptografada
        if (admin.getSenha() == null || !admin.getSenha().startsWith("$2a$")) {
            admin.setSenha(passwordEncoder.encode("123456"));
        }

        usuarioRepository.save(admin);

        System.out.println("Administrador verificado.");
    }
    }