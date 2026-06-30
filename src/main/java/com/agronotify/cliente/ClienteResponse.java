package com.agronotify.cliente;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class ClienteResponse {

    private Long id;
    private String nome;
    private String telefone;
    private String produto;
    private String mensagem;
    private String whatsappLink;
    private LocalDate dataCadastro;
    private LocalDate dataEnvio;
    private StatusEnvio statusEnvio;

    public ClienteResponse(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.telefone = cliente.getTelefone();
        this.produto = cliente.getProduto();
        this.mensagem = mensagemWhatsapp(cliente);
        this.whatsappLink = gerarWhatsappLink(cliente);
        this.dataCadastro = cliente.getDataCadastro();
        this.dataEnvio = cliente.getDataEnvio();
        this.statusEnvio = cliente.getStatusEnvio();
    }

    public Long getId() {return id;}

    public String getNome() {return nome;}

    public String getTelefone() {return telefone;}

    public String getProduto() {return produto;}

    public String getMensagem() {return mensagem;}

    public String getWhatsappLink() {return whatsappLink;}

    public LocalDate getDataCadastro() {return dataCadastro;}

    public LocalDate getDataEnvio() {return dataEnvio;}

    public StatusEnvio getStatusEnvio() {return statusEnvio;}

    private static String gerarWhatsappLink(Cliente cliente) {
        return "https://wa.me/"
                + normalizarTelefone(cliente.getTelefone())
                + "?text="
                + codificarMensagem(mensagemWhatsapp(cliente));
    }

    private static String normalizarTelefone(String telefone) {
        String digitos = telefone == null
                ? ""
                : telefone.replaceAll("\\D", "");

        if (!digitos.startsWith("55")) {
            return "55" + digitos;
        }

        return digitos;
    }

    private static String codificarMensagem(String mensagem) {
        return URLEncoder
                .encode(mensagem, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private static String mensagemWhatsapp(Cliente cliente) {
        if (cliente.getMensagem() != null && !cliente.getMensagem().isBlank()) {
            return cliente.getMensagem().trim();
        }

        String nomeCliente = cliente.getNome() == null || cliente.getNome().isBlank()
                ? "cliente"
                : cliente.getNome().trim();
        String nomeProduto = cliente.getProduto() == null || cliente.getProduto().isBlank()
                ? "o produto"
                : cliente.getProduto().trim();

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
