package com.ecommerce.service.order.order_service.service;

import com.ecommerce.service.order.order_service.client.InventoryResponse;
import com.ecommerce.service.order.order_service.dto.OrderRequest;
import com.ecommerce.service.order.order_service.entity.Order;
import com.ecommerce.service.order.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        String finalUrl = url + "?skuCode="+orderRequest.skuCode()+"&quantity="+orderRequest.quantity();
        String finalUpdateUrl = updateUrl + "?skuCode="+orderRequest.skuCode()+"&quantity="+orderRequest.quantity();

        ResponseEntity<InventoryResponse> response = restTemplate.exchange(finalUrl, HttpMethod.GET,null, InventoryResponse.class);
        InventoryResponse inventoryResponse = response.getBody();
        Boolean isInStock = inventoryResponse.isInStock();
        if(isInStock) {
            log.info("The requested order is in stock");
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
            restTemplate.exchange(finalUpdateUrl,HttpMethod.PUT,null,ResponseEntity.class);
            return true;
        }else{
//            throw new RuntimeException("Product with skuCode "+orderRequest.skuCode()+" is not in stock");
            return false;
        }
    }
}
