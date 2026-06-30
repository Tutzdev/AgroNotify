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
        this.mensagem = MensagemCliente.resolverMensagem(cliente);
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
                + codificarMensagem(MensagemCliente.resolverMensagem(cliente));
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

}
