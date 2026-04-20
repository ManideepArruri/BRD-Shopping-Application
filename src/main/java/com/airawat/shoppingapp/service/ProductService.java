package com.airawat.shoppingapp.service;

import java.util.List;

import com.airawat.shoppingapp.dto.ProductRequestDto;
import com.airawat.shoppingapp.dto.ProductResponseDto;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto getProductById(Long id);
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    void deleteProduct(Long id);
}
