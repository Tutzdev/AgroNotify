package com.agronotify.dashboard;

import com.agronotify.cliente.ClienteRepository;
import com.agronotify.cliente.StatusEnvio;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;

    public DashboardService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public DashboardResponse buscarDados() {

        long totalClientes = clienteRepository.count();

        long mensagensPendentes = clienteRepository.countByStatusEnvio(StatusEnvio.PENDENTE);

        long mensagensEnviadas = clienteRepository.countByStatusEnvio(StatusEnvio.ENVIADO);

        return new DashboardResponse(
                 totalClientes
                ,mensagensPendentes
                ,mensagensEnviadas
        );
    }
}