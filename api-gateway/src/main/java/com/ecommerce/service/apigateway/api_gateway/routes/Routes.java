package com.ecommerce.service.apigateway.api_gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * This class is responsible for creating endpoints for our services
 */
@Configuration
public class Routes {
    @Bean
    public RouteLocator routeLocatorProduct(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product_service", r -> r.path("/api/product/**")
                        .filters(f -> f
                                .filter((exchange, chain) -> {
                                    // Extract the JWT token from the incoming request
                                    String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                                    if (authorizationHeader != null) {
                                        // Log the incoming Authorization header
                                        System.out.println("Forwarding Authorization Header: " + authorizationHeader);
                                    }
                                    // Add the Authorization header to the request
                                    return chain.filter(exchange.mutate()
                                            .request(exchange.getRequest()
                                                    .mutate()
                                                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                                                    .build())
                                            .build());
                                })
                    )
                    .uri("http://localhost:8080")) // URL of your Product Service
            .build();
    }
    @Bean
    public RouteLocator routeLocatorOrder(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order_service", r -> r.path("/api/order")
                        .filters(f -> f
                                .filter((exchange, chain) -> {
                                    // Extract the JWT token from the incoming request
                                    String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                                    if (authorizationHeader != null) {
                                        // Log the incoming Authorization header
                                        System.out.println("Forwarding Authorization Header: " + authorizationHeader);
                                    }
                                    // Add the Authorization header to the request
                                    return chain.filter(exchange.mutate()
                                            .request(exchange.getRequest()
                                                    .mutate()
                                                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                                                    .build())
                                            .build());
                                })
                        )
                        .uri("http://localhost:8082")) // URL of your Order Service
                .build();
    }
    @Bean
    public RouteLocator routeLocatorUser(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user_service", r -> r.path("/api/user/**")
                        .uri("http://localhost:8083")) // URL of your User Service
                .build();
    }
}
