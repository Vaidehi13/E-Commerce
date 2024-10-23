package com.ecommerce.service.product.product_service.controller;

import com.ecommerce.service.product.product_service.dto.ProductRequest;
import com.ecommerce.service.product.product_service.dto.ProductResponse;
import com.ecommerce.service.product.product_service.entity.Product;
import com.ecommerce.service.product.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private final ProductService productService;

    // Create New Product
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest){
        log.info("Received product request "+productRequest);
        try{
            productService.createProduct(productRequest);
            log.info("Created product with product request "+productRequest+" successfully");
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Fetch all products
    @GetMapping
    public ResponseEntity<?> getAllProducts(){
        log.info("Request received to fetch all products");
        System.out.println("Request received to fetch all products");
        List<Product> products = productService.fetchAllProducts();
        if(products != null && !products.isEmpty()){
            List<ProductResponse> productResponseList = products.stream().map(product ->
                    ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productCategory(product.getProductCategory())
                            .skuCode(product.getSkuCode())
                            .price(product.getPrice()).build())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(productResponseList,HttpStatus.OK);
        }else
            log.info("No products found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
