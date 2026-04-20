package com.airawat.shoppingapp.service;

import java.util.List;

import com.airawat.shoppingapp.dto.ProductRequestDTO;
import com.airawat.shoppingapp.dto.ProductResponseDTO;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDto);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDto);
    void deleteProduct(Long id);
}
