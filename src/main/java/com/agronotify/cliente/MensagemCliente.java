package com.agronotify.cliente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

final class MensagemCliente {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MensagemCliente() {
    }

    static String resolverMensagem(Cliente cliente) {
        String mensagem = cliente.getMensagem();

        if (mensagem != null
                && !mensagem.isBlank()
                && !ehMensagemAutomatica(
                        mensagem,
                        cliente.getNome(),
                        cliente.getProduto(),
                        cliente.getDataCadastro(),
                        cliente.getDataEnvio()
                )) {
            return mensagem.trim();
        }

        return mensagemPadrao(
                cliente.getNome(),
                cliente.getProduto(),
                cliente.getDataCadastro(),
                cliente.getDataEnvio()
        );
    }

    static String mensagemPadrao(
            String nome,
            String produto,
            LocalDate dataCompra,
            LocalDate dataEnvio
    ) {
        String nomeCliente = nome == null || nome.isBlank()
                ? "cliente"
                : nome.trim();
        String nomeProduto = produto == null || produto.isBlank()
                ? "o produto"
                : produto.trim();

        return "Ol\u00e1, " + nomeCliente + "! Tudo bem?\n\n"
                + "Aqui \u00e9 da Agropecu\u00e1ria Nossos Bichos. \uD83D\uDE0A\n\n"
                + textoUltimaCompra(nomeProduto, dataCompra, dataEnvio)
                + "\n\n"
                + "Gostar\u00edamos de saber se seu pet j\u00e1 est\u00e1 precisando de uma nova reposi\u00e7\u00e3o. "
                + "Caso queira, estamos \u00e0 disposi\u00e7\u00e3o para atend\u00ea-lo novamente!\n\n"
                + "Agradecemos pela prefer\u00eancia e esperamos falar com voc\u00ea em breve.\n\n"
                + "Equipe Agropecu\u00e1ria Nossos Bichos.";
    }

    static boolean ehMensagemAutomatica(
            String mensagem,
            String nome,
            String produto,
            LocalDate dataCompra,
            LocalDate dataEnvio
    ) {
        if (mensagem == null || mensagem.isBlank()) {
            return true;
        }

        String mensagemNormalizada = mensagem.trim();

        return mensagemNormalizada.equals(mensagemPadrao(nome, produto, dataCompra, dataEnvio))
                || mensagemNormalizada.equals(mensagemPadraoAntiga(nome, produto));
    }

    private static String textoUltimaCompra(
            String nomeProduto,
            LocalDate dataCompra,
            LocalDate dataEnvio
    ) {
        if (dataCompra == null || dataEnvio == null) {
            return "Notamos que j\u00e1 faz algum tempo desde a sua \u00faltima compra de "
                    + nomeProduto
                    + ".";
        }

        long dias = Math.max(0, ChronoUnit.DAYS.between(dataCompra, dataEnvio));

        return "Notamos que j\u00e1 se passaram "
                + textoDias(dias)
                + " desde a sua \u00faltima compra de "
                + nomeProduto
                + ", realizada em "
                + FORMATO_DATA.format(dataCompra)
                + ".";
    }

    private static String textoDias(long dias) {
        if (dias == 1) {
            return "1 dia";
        }

        return dias + " dias";
    }

    private static String mensagemPadraoAntiga(String nome, String produto) {
        String nomeCliente = nome == null || nome.isBlank()
                ? "cliente"
                : nome.trim();
        String nomeProduto = produto == null || produto.isBlank()
                ? "o produto"
                : produto.trim();

        return "Ol\u00e1, " + nomeCliente + "! Tudo bem?\n\n"
                + "Aqui \u00e9 da Agropecu\u00e1ria Nossos Bichos. \uD83D\uDE0A\n\n"
                + "Notamos que j\u00e1 faz algum tempo desde a sua \u00faltima compra de "
                + nomeProduto
                + ".\n\n"
                + "Gostar\u00edamos de saber se seu pet j\u00e1 est\u00e1 precisando de uma nova reposi\u00e7\u00e3o. "
                + "Caso queira, estamos \u00e0 disposi\u00e7\u00e3o para atend\u00ea-lo novamente!\n\n"
                + "Agradecemos pela prefer\u00eancia e esperamos falar com voc\u00ea em breve.\n\n"
                + "Equipe Agropecu\u00e1ria Nossos Bichos.";
    }
}
