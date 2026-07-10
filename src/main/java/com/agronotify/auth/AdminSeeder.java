package com.agronotify.auth;

import com.agronotify.cliente.Cliente;
import com.agronotify.cliente.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) {
        Usuario agro = garantirUsuario("admin@agro.com", "Administrador");
        garantirUsuario("admin@petstore.com", "Administrador Pet Store");
        garantirUsuario("admin@luci.com", "Administrador Luci");

        // Clientes existentes antes do isolamento ficam no administrador original.
        for (Cliente cliente : clienteRepository.findAll()) {
            if (cliente.getUsuario() == null) {
                cliente.setUsuario(agro);
            }
        }
        clienteRepository.flush();
        System.out.println("Administradores verificados.");
    }

    private Usuario garantirUsuario(String email, String nome) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(Usuario::new);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setRole("ADMIN");
        if (usuario.getSenha() == null || !usuario.getSenha().startsWith("$2")) {
            usuario.setSenha(passwordEncoder.encode("123456"));
        }
        return usuarioRepository.save(usuario);
    }
}
