package com.agronotify.cliente;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MensagemClienteTest {

    private static final LocalDate COMPRA = LocalDate.of(2026, 6, 1);
    private static final LocalDate ENVIO = LocalDate.of(2026, 6, 30);

    @Test
    void mensagemPadraoCitaClienteProdutoEDiasDesdeACompra() {
        String mensagem = MensagemCliente.mensagemPadrao("Maria", "Ração Golden 15 kg", COMPRA, ENVIO);

        assertThat(mensagem)
                .startsWith("Olá, Maria! Tudo bem?")
                .contains("já se passaram 29 dias desde a sua última compra de Ração Golden 15 kg")
                .contains("realizada em 01/06/2026");
    }

    @Test
    void semDatasUsaTextoGenerico() {
        String mensagem = MensagemCliente.mensagemPadrao("Maria", "Vermífugo", null, null);

        assertThat(mensagem).contains("já faz algum tempo desde a sua última compra de Vermífugo.");
    }

    @Test
    void preservaMensagemPersonalizada() {
        Cliente cliente = cliente("Oi Maria, chegou a ração que você pediu!");

        assertThat(MensagemCliente.resolverMensagem(cliente))
                .isEqualTo("Oi Maria, chegou a ração que você pediu!");
    }

    @Test
    void reconheceAMensagemAutomaticaEOCampoVazio() {
        String automatica = MensagemCliente.mensagemPadrao("Maria", "Ração Golden 15 kg", COMPRA, ENVIO);

        assertThat(MensagemCliente.ehMensagemAutomatica(automatica, "Maria", "Ração Golden 15 kg", COMPRA, ENVIO))
                .isTrue();
        assertThat(MensagemCliente.ehMensagemAutomatica("  ", "Maria", "Ração Golden 15 kg", COMPRA, ENVIO))
                .isTrue();
        assertThat(MensagemCliente.ehMensagemAutomatica("Oi Maria!", "Maria", "Ração Golden 15 kg", COMPRA, ENVIO))
                .isFalse();
    }

    private static Cliente cliente(String mensagem) {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");
        cliente.setProduto("Ração Golden 15 kg");
        cliente.setDataCadastro(COMPRA);
        cliente.setDataEnvio(ENVIO);
        cliente.setMensagem(mensagem);
        return cliente;
    }
}
