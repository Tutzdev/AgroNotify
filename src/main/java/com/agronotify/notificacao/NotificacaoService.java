package com.agronotify.notificacao;

import com.agronotify.cliente.Cliente;
import com.agronotify.cliente.ClienteRepository;
import com.agronotify.cliente.StatusEnvio;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoService {
    private final ClienteRepository clienteRepository;

    public NotificacaoService(ClienteRepository clienteRepository) { 
        this.clienteRepository = clienteRepository;
    }

    public void processarNotificacoesPendentes() { 
        List<Cliente> clientes = clienteRepository
        .findByStatusEnvioAndDataEnvioLessThanEqual(StatusEnvio.PENDENTE,LocalDate.now());

        for (Cliente cliente : clientes) {
            enviarMensagem(cliente);
            cliente.setStatusEnvio(StatusEnvio.ENVIADO);
            clienteRepository.save(cliente);
        }
    }

    private void enviarMensagem(Cliente cliente) { 
        String mensagem = "Olá " + cliente.getNome()
                + ", tudo bem? Aqui é da Agropecuária. "
                + "Já fazem 7 dias desde a compra de "
                + cliente.getProduto()
                + ". Deseja repor sua ração?";

        System.out.println("Enviando para: " + cliente.getTelefone());
        System.out.println(mensagem);
    }
}