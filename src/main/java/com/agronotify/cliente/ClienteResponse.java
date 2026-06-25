package com.agronotify.cliente;

import java.time.LocalDate;

public class ClienteResponse {

    private Long id;
    private String nome;
    private String telefone;
    private String produto;
    private LocalDate dataCadastro;
    private LocalDate dataEnvio;
    private StatusEnvio statusEnvio;

    public ClienteResponse(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.telefone = cliente.getTelefone();
        this.produto = cliente.getProduto();
        this.dataCadastro = cliente.getDataCadastro();
        this.dataEnvio = cliente.getDataEnvio();
        this.statusEnvio = cliente.getStatusEnvio();
    }

    public Long getId() {return id;}

    public String getNome() {return nome;}

    public String getTelefone() {return telefone;}

    public String getProduto() {return produto;}

    public LocalDate getDataCadastro() {return dataCadastro;}

    public LocalDate getDataEnvio() {return dataEnvio;}

    public StatusEnvio getStatusEnvio() {return statusEnvio;}
}