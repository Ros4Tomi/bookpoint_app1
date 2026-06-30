package com.bookpoint.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 1. Permite peticiones desde cualquier origen (esencial para desarrollo local)
        // Si tienes la URL exacta de tu frontend (ej. http://localhost:4200), puedes usar .addAllowedOrigin("http://localhost:4200")
        corsConfig.addAllowedOriginPattern("*");
        
        // 2. Permite los métodos HTTP estándar que utilizan tus microservicios de Bookpoint
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("PATCH");
        corsConfig.addAllowedMethod("OPTIONS");
        
        // 3. Permite todas las cabeceras HTTP (Headers) en las peticiones (como Content-Type, Authorization, etc.)
        corsConfig.addAllowedHeader("*");
        
        // 4. Permite el envío de credenciales, cookies o cabeceras de autenticación si fuera necesario
        corsConfig.setAllowCredentials(true);

        // 5. Aplica esta configuración de seguridad a absolutamente todas las rutas que crucen por el Gateway (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}