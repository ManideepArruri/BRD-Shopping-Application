package com.airawat.shoppingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airawat.shoppingapp.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
}