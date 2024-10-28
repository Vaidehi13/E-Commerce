package com.ecommerce.service.apigateway.api_gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
                                .circuitBreaker(c -> c.setName("productServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback")) //set name and url for fallback to set a circuit breaker
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
                                .circuitBreaker(c -> c.setName("orderServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                        )
                        .uri("http://localhost:8082")) // URL of your Order Service
                .build();
    }
    @Bean
    public RouteLocator routeLocatorUser(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user_service", r -> r.path("/api/user/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("userServiceCircuitBreaker")
                                .setFallbackUri("forward:/fallback")))
                        .uri("http://localhost:8083")) // URL of your User Service
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallback() {
        return route(GET("/fallback"), request ->
                ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Mono.just("Service is temporarily unavailable. Please try again later."), String.class));
    }
}
