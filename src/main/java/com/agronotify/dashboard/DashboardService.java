package com.agronotify.dashboard;

import com.agronotify.auth.Usuario;
import com.agronotify.cliente.ClienteRepository;
import com.agronotify.cliente.ClienteResponse;
import com.agronotify.cliente.StatusEnvio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;

    public DashboardService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public DashboardResponse buscarDados(Usuario usuario) {

        long totalClientes = clienteRepository.findByUsuario(usuario).size();

        long mensagensPendentes = clienteRepository.countByUsuarioAndStatusEnvio(usuario, StatusEnvio.PENDENTE);

        long mensagensEnviadas = clienteRepository.countByUsuarioAndStatusEnvio(usuario, StatusEnvio.ENVIADO);

        List<ClienteResponse> lembretesRecompra = clienteRepository
                .findTop5ByUsuarioAndStatusEnvioAndDataEnvioLessThanEqualOrderByDataEnvioAsc(
                        usuario, StatusEnvio.PENDENTE,
                        LocalDate.now()
                )
                .stream()
                .map(ClienteResponse::new)
                .toList();

        return new DashboardResponse(
                 totalClientes
                ,mensagensPendentes
                ,mensagensEnviadas
                ,lembretesRecompra
        );
    }
}
