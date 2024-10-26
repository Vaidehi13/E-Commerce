package com.ecommerce.service.order.order_service.service;

import com.ecommerce.service.order.order_service.client.InventoryResponse;
import com.ecommerce.service.order.order_service.dto.OrderRequest;
import com.ecommerce.service.order.order_service.entity.Order;
import com.ecommerce.service.order.order_service.event.OrderPlacedEvent;
import com.ecommerce.service.order.order_service.repository.OrderRepository;
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

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        // Construct URLs
        String finalUrl = url + "?skuCode=" + orderRequest.skuCode() + "&quantity=" + orderRequest.quantity();
        String finalUpdateUrl = updateUrl + "?skuCode=" + orderRequest.skuCode() + "&quantity=" + orderRequest.quantity();

        // Set up headers with JWT token
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Check inventory availability
        ResponseEntity<InventoryResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, InventoryResponse.class);
        InventoryResponse inventoryResponse = response.getBody();

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
            log.info("Sending email events to kafka");
            kafkaTemplate.send("order_placed_event",new OrderPlacedEvent(order.getOrderNumber(),orderRequest.userDetails().email(),orderRequest.userDetails().firstName()));
            log.info("Completed sending email events to kafka");

            // Update inventory after placing the order
            restTemplate.exchange(finalUpdateUrl, HttpMethod.PUT, entity, Void.class);
            return true;
        } else {
            log.warn("Product with skuCode {} is not in stock", orderRequest.skuCode());
            return false;
        }
    }
}
