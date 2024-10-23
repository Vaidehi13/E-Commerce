package com.ecommerce.service.product.product_service.dto;

import com.ecommerce.service.product.product_service.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String productId;
    private String productName;
    private ProductCategory productCategory;
    private String skuCode;
    private BigDecimal price;
}
