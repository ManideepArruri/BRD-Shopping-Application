package com.airawat.shoppingapp.serviceimpl;

import com.airawat.shoppingapp.dto.InventoryRequestDto;
import com.airawat.shoppingapp.dto.InventoryResponseDto;
import com.airawat.shoppingapp.model.Inventory;
import com.airawat.shoppingapp.model.Product;
import com.airawat.shoppingapp.exception.BadRequestException;
import com.airawat.shoppingapp.exception.ResourceNotFoundException;
import com.airawat.shoppingapp.repository.InventoryRepository;
import com.airawat.shoppingapp.repository.ProductRepository;
import com.airawat.shoppingapp.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public InventoryResponseDto createInventory(InventoryRequestDto requestDto) {
        logger.info("Creating inventory for product id: {}", requestDto.getProductId());
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", requestDto.getProductId());
                    return new ResourceNotFoundException("Product not found with id: " + requestDto.getProductId());
                });

        if (inventoryRepository.findByProduct_ProductId(requestDto.getProductId()).isPresent()) {
            logger.warn("Inventory already exists for product id: {}", requestDto.getProductId());
            throw new BadRequestException("Inventory already exists for product id: " + requestDto.getProductId());
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setAvailableQuantity(requestDto.getAvailableQuantity());
        inventory.setReorderLevel(requestDto.getReorderLevel());

        Inventory savedInventory = inventoryRepository.save(inventory);
        logger.info("Inventory created with id: {} for product: {}", savedInventory.getInventoryId(), product.getProductName());
        return mapToResponse(savedInventory);
    }

    @Override
    public List<InventoryResponseDto> getAllInventory() {
        logger.info("Fetching all inventory records");
        List<InventoryResponseDto> inventories = inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.info("Found {} inventory records", inventories.size());
        return inventories;
    }

    @Override
    public InventoryResponseDto getInventoryByProductId(Long productId) {
        logger.info("Fetching inventory for product id: {}", productId);
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> {
                    logger.error("Inventory not found for product id: {}", productId);
                    return new ResourceNotFoundException("Inventory not found for product id: " + productId);
                });
        return mapToResponse(inventory);
    }

    @Override
    public InventoryResponseDto updateInventory(Long productId, InventoryRequestDto requestDto) {
        logger.info("Updating inventory for product id: {}", productId);
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> {
                    logger.error("Inventory not found for update, product id: {}", productId);
                    return new ResourceNotFoundException("Inventory not found for product id: " + productId);
                });

        inventory.setAvailableQuantity(requestDto.getAvailableQuantity());
        inventory.setReorderLevel(requestDto.getReorderLevel());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        logger.info("Inventory updated for product id: {}, new quantity: {}", productId, updatedInventory.getAvailableQuantity());
        return mapToResponse(updatedInventory);
    }

    @Override
    public List<InventoryResponseDto> getLowStockItems() {
        logger.info("Fetching low stock items");
        List<InventoryResponseDto> lowStockItems = inventoryRepository.findAll()
                .stream()
                .filter(inv -> inv.getAvailableQuantity() <= inv.getReorderLevel())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.warn("Found {} low stock items", lowStockItems.size());
        return lowStockItems;
    }

    private InventoryResponseDto mapToResponse(Inventory inventory) {
        return new InventoryResponseDto(
                inventory.getInventoryId(),
                inventory.getProduct().getProductId(),
                inventory.getProduct().getProductName(),
                inventory.getAvailableQuantity(),
                inventory.getReorderLevel()
        );
    }
}