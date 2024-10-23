package com.ecommerce.service.inventory.inventory_service.controller;

import com.ecommerce.service.inventory.inventory_service.dto.InventoryResponse;
import com.ecommerce.service.inventory.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        log.info("Received request to check if item with sku code "+skuCode+" is in stock for quantity "+quantity);
        boolean isInStock = inventoryService.isInStock(skuCode, quantity);
        return new InventoryResponse(skuCode,quantity,isInStock);
    }
}
