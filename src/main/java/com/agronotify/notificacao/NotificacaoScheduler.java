package com.agronotify.notificacao;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoScheduler {

    private final NotificacaoService notificacaoService;

    public NotificacaoScheduler(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void verificarNotificacoes() {
        notificacaoService.processarNotificacoesPendentes();
    }
}