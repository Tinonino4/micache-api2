package com.micache.mi_cache.dashboard.infrastructure;

import com.micache.mi_cache.dashboard.application.DashboardService;
import com.micache.mi_cache.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoint agregado para la pantalla Home")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Obtener Home Screen", description = "Devuelve perfil, métricas (estrellas) y experiencias ordenadas")
    public ResponseEntity<DashboardResponse> getHome(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getUserDashboard(email));
    }
}
