package com.micache.mi_cache.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 1. Establecemos el status 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // 2. Escribimos un JSON limpio (sin usar librerías extra si no quieres)
        // Puedes usar Jackson (ObjectMapper) si lo prefieres para ser más elegante.
        String jsonError = """
                {
                    "title": "Unauthorized",
                    "status": 401,
                    "detail": "Acceso denegado. Token inválido o no proporcionado.",
                    "path": "%s"
                }
                """.formatted(request.getRequestURI());

        response.getWriter().write(jsonError);
    }
}
