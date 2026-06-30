package com.agronotify.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Produto é obrigatório")
    private String produto;

    private String mensagem;

    @NotNull(message = "Data de envio e obrigatoria")
    private LocalDate dataEnvio;

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getProduto() {
        return produto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDate getDataEnvio() {
        return dataEnvio;
    }
}
