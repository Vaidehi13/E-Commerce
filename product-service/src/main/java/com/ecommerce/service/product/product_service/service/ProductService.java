package com.ecommerce.service.product.product_service.service;

import com.ecommerce.service.product.product_service.dto.ProductRequest;
import com.ecommerce.service.product.product_service.entity.Product;
import com.ecommerce.service.product.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest){
        try{
            Product product = Product.builder()
                    .productName(productRequest.productName())
                    .productCategory(productRequest.productCategory())
                    .price(productRequest.price())
                    .skuCode(productRequest.skuCode())
                    .build();
            productRepository.save(product);
            log.info("Product created successfully with id: {}", product.getProductId());
        }catch(Exception e){
            throw new RuntimeException("Unable to create product", e);
        }
    }

    public List<Product> fetchAllProducts(){
        return productRepository.findAll();
    }
}
