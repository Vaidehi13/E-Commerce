package com.ecommerce.service.order.order_service.service;

import com.ecommerce.service.order.order_service.client.InventoryResponse;
import com.ecommerce.service.order.order_service.dto.OrderRequest;
import com.ecommerce.service.order.order_service.entity.Order;
import com.ecommerce.service.order.order_service.event.OrderPlacedEvent;
import com.ecommerce.service.order.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${inventory.api.url}")
    private String url;

    @Value("${inventory.api.update.url}")
    private String updateUrl;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private InventoryResponse inventoryResponse;

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        // Construct URLs
        String finalUrl = url + "?skuCode=" + orderRequest.skuCode() + "&quantity=" + orderRequest.quantity();
        String finalUpdateUrl = updateUrl + "?skuCode=" + orderRequest.skuCode() + "&quantity=" + orderRequest.quantity();

        // Set up headers with JWT token
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //Set the Circuit Breaker for Inventory Client
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("inventory");

        // Check inventory availability
//        ResponseEntity<InventoryResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, InventoryResponse.class);
//        InventoryResponse inventoryResponse = response.getBody();
        // Check inventory availability with CircuitBreaker
        try {
            // Check inventory availability with CircuitBreaker
            inventoryResponse = circuitBreaker.executeSupplier(() -> {
                ResponseEntity<InventoryResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, InventoryResponse.class);
                return response.getBody();
            });
        } catch (Exception e) {
            // Call fallback method if there's an exception
            handleInventoryFallback();
        };

        // Handle cases where inventory response is null or product is out of stock
        if (inventoryResponse != null && Boolean.TRUE.equals(inventoryResponse.isInStock())) {
            log.info("The requested order is in stock");

            // Create and save the order
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);

            //Send asynchronous event to notification-service using Kafka
            try {
                log.info("Sending email events to kafka");
                kafkaTemplate.send("order_placed_event",new OrderPlacedEvent(order.getOrderNumber(),orderRequest.userDetails().email(),orderRequest.userDetails().firstName()));
                log.info("Completed sending email events to kafka");
            }catch (Exception e){
                log.info("Failed to send order placed event to Kafka");
            }

            // Update inventory after placing the order with CircuitBreaker
            circuitBreaker.executeRunnable(() -> {
                restTemplate.exchange(finalUpdateUrl, HttpMethod.PUT, entity, Void.class);
            });
            return true;
        } else {
            log.warn("Product with skuCode {} is not in stock", orderRequest.skuCode());
            return false;
        }
    }
    public boolean handleInventoryFallback() {
        log.warn("Inventory service is down. Please try again later");
        return false;
    }
}
