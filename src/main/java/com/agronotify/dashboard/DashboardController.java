package com.agronotify.dashboard;

import com.agronotify.auth.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> buscarDados(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(
                dashboardService.buscarDados(usuario)
        );
    }
}
