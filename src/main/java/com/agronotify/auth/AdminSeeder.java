package com.agronotify.auth;

import com.agronotify.cliente.Cliente;
import com.agronotify.cliente.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final String senhaInicial;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       ClienteRepository clienteRepository,
                       @Value("${app.admin.senha-inicial:}") String senhaInicial) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.clienteRepository = clienteRepository;
        this.senhaInicial = senhaInicial;
    }

    @Override
    public void run(String... args) {
        Optional<Usuario> agro = garantirUsuario("admin@agro.com", "Administrador");
        garantirUsuario("admin@petstore.com", "Administrador Pet Store");
        garantirUsuario("admin@luci.com", "Administrador Luci");

        agro.ifPresent(administrador -> {
            for (Cliente cliente : clienteRepository.findAll()) {
                if (cliente.getUsuario() == null) {
                    cliente.setUsuario(administrador);
                }
            }
            clienteRepository.flush();
        });
    }

     // Cria o administrador se ele ainda não existe. A senha de quem já existe nunca é trocada, exceto quando ela ainda não está em BCrypt.

    private Optional<Usuario> garantirUsuario(String email, String nome) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(Usuario::new);
        boolean precisaDeSenha = usuario.getSenha() == null || !usuario.getSenha().startsWith("$2");

        if (precisaDeSenha && senhaInicial.isBlank()) {
            LOGGER.warn("Administrador {} não configurado: defina ADMIN_INITIAL_PASSWORD.", email);
            return Optional.ofNullable(usuario.getId() == null ? null : usuario);
        }

        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setRole("ADMIN");
        if (precisaDeSenha) {
            usuario.setSenha(passwordEncoder.encode(senhaInicial));
        }
        return Optional.of(usuarioRepository.save(usuario));
    }
}
