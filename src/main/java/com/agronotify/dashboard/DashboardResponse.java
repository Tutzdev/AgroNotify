package com.agronotify.dashboard;

public class DashboardResponse {

    private long totalClientes;
    private long mensagensPendentes;
    private long mensagensEnviadas;

    public DashboardResponse(
            long totalClientes,
            long mensagensPendentes,
            long mensagensEnviadas
    ) {
        this.totalClientes = totalClientes;
        this.mensagensPendentes = mensagensPendentes;
        this.mensagensEnviadas = mensagensEnviadas;
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
}