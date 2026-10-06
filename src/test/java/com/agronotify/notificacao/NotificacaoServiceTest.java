package com.agronotify.notificacao;

import com.agronotify.cliente.Cliente;
import com.agronotify.cliente.ClienteRepository;
import com.agronotify.cliente.StatusEnvio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificacaoServiceTest {

    private final ClienteRepository clienteRepository = mock(ClienteRepository.class);
    private final NotificacaoService notificacaoService = new NotificacaoService(clienteRepository);

    @Test
    void marcaComoEnviadosOsLembretesVencidos() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setProduto("Ração Golden 15 kg");
        cliente.setStatusEnvio(StatusEnvio.PENDENTE);
        when(clienteRepository.findByStatusEnvioAndDataEnvioLessThanEqual(StatusEnvio.PENDENTE, LocalDate.now()))
                .thenReturn(List.of(cliente));

        notificacaoService.processarNotificacoesPendentes();

        assertThat(cliente.getStatusEnvio()).isEqualTo(StatusEnvio.ENVIADO);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void naoFazNadaSemLembretesVencidos() {
        when(clienteRepository.findByStatusEnvioAndDataEnvioLessThanEqual(StatusEnvio.PENDENTE, LocalDate.now()))
                .thenReturn(List.of());

        notificacaoService.processarNotificacoesPendentes();

        verify(clienteRepository, never()).save(any());
    }
}
