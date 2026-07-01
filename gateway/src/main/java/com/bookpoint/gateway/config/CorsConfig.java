package com.bookpoint.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales (cookies, headers de autenticación)
        config.setAllowCredentials(true);
        
        // Permitir cualquier origen de desarrollo (ej. Angular, React, Vue o Postman)
        // Si tienes un puerto específico de frontend, puedes ponerlo aquí, ej: "http://localhost:5173"
        config.addAllowedOriginPattern("*"); 
        
        // Permitir todos los Headers cotidianos
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, PATCH, OPTIONS)
        config.addAllowedMethod("*");

        // Registrar la configuración para todas las rutas del Gateway
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Retornamos el CorsFilter estándar de WebMVC (NO el CorsWebFilter reactivo)
        return new CorsFilter(source);
    }
}