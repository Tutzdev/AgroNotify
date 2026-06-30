package com.agronotify.dashboard;

import com.agronotify.cliente.ClienteResponse;

import java.util.List;

public class DashboardResponse {

    private long totalClientes;
    private long mensagensPendentes;
    private long mensagensEnviadas;
    private List<ClienteResponse> lembretesRecompra;

    public DashboardResponse(
            long totalClientes,
            long mensagensPendentes,
            long mensagensEnviadas,
            List<ClienteResponse> lembretesRecompra
    ) {
        this.totalClientes = totalClientes;
        this.mensagensPendentes = mensagensPendentes;
        this.mensagensEnviadas = mensagensEnviadas;
        this.lembretesRecompra = lembretesRecompra;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public long getMensagensPendentes() {
        return mensagensPendentes;
    }

    public long getMensagensEnviadas() {
        return mensagensEnviadas;
    }

    public List<ClienteResponse> getLembretesRecompra() {
        return lembretesRecompra;
    }
}
