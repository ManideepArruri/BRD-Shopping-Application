package com.Springboot.ShoppingApp.serviceimpl;

import com.Springboot.ShoppingApp.DTO.ProductRequestDto;
import com.Springboot.ShoppingApp.DTO.ProductResponseDto;
import com.Springboot.ShoppingApp.entity.Product;
import com.Springboot.ShoppingApp.exception.BadRequestException;
import com.Springboot.ShoppingApp.exception.ResourceNotFoundException;
import com.Springboot.ShoppingApp.repository.ProductRepository;
import com.Springboot.ShoppingApp.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDto) {
        logger.info("Creating product with SKU: {}", requestDto.getSku());
        if (productRepository.existsBySku(requestDto.getSku())) {
            logger.warn("Duplicate SKU detected: {}", requestDto.getSku());
            throw new BadRequestException("SKU already exists");
        }

        Product product = new Product();
        product.setProductName(requestDto.getProductName());
        product.setDescription(requestDto.getDescription());
        product.setCategory(requestDto.getCategory());
        product.setPrice(requestDto.getPrice());
        product.setSku(requestDto.getSku());

        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with id: {}", savedProduct.getProductId());
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        logger.info("Fetching all products");
        List<ProductResponseDTO> products = productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.info("Found {} products", products.size());
        return products;
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        logger.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        return mapToResponse(product);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDto) {
        logger.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found for update with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });

        product.setProductName(requestDto.getProductName());
        product.setDescription(requestDto.getDescription());
        product.setCategory(requestDto.getCategory());
        product.setPrice(requestDto.getPrice());
        product.setSku(requestDto.getSku());

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated successfully with id: {}", updatedProduct.getProductId());
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found for delete with id: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        productRepository.delete(product);
        logger.info("Product deleted successfully with id: {}", id);
    }

    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getSku()
        );
    }
}