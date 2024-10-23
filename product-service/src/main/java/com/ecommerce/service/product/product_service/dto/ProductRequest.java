package com.ecommerce.service.product.product_service.dto;

import com.ecommerce.service.product.product_service.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
public record ProductRequest(String productName,
                             ProductCategory productCategory,
                             String skuCode,
                             BigDecimal price) {
}
