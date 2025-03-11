package com.example.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("static_content", r -> r
                        .path("/login.html", "/register.html", "/css/**", "/js/**")
                        .uri("http://user-management-service:8083"))
                .route("auth_routes", r -> r
                        .path("/api/auth/**")
                        .uri("http://user-management-service:8083"))
                .route("root_path", r -> r
                        .path("/", "/index.html")
                        .filters(f -> f.redirect(302, "/login.html"))
                        .uri("http://user-management-service:8083"))
                .route("admin_service", r -> r
                        .path("/admin/**")
                        .filters(f -> f.filter(new AuthenticationFilter()))
                        .uri("http://admin-service:8080"))
                .route("inventory_service", r -> r
                        .path("/inventory/**")
                        .filters(f -> f.filter(new AuthenticationFilter()))
                        .uri("http://inventory-service:8081"))
                .route("product_service", r -> r
                        .path("/products/**")
                        .filters(f -> f.filter(new AuthenticationFilter()))
                        .uri("http://product-catalog-service:8082"))
                .route("order_service", r -> r
                        .path("/orders/**")
                        .filters(f -> f.filter(new AuthenticationFilter()))
                        .uri("http://order-management-service:8084"))
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:80", "http://localhost:3000"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }
}