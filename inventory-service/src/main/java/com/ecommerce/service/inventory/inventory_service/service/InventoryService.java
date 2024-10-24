package com.ecommerce.service.inventory.inventory_service.service;

import com.ecommerce.service.inventory.inventory_service.entity.Inventory;
import com.ecommerce.service.inventory.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String skuCode, Integer quantity) {
        boolean isInStock = inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
        return isInStock;
    }

    public void updateInventoryAfterPlacingOrder(String skuCode, Integer quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode);
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.saveAndFlush(inventory);
    }
}
