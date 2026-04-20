package com.airawat.shoppingapp.serviceimpl;
import com.airawat.shoppingapp.dto.NotificationRequestDTO;
import com.airawat.shoppingapp.dto.OrderItemResponseDTO;
import com.airawat.shoppingapp.dto.CheckoutItemRequestDTO;
import com.airawat.shoppingapp.dto.CheckoutRequestDTO;
import com.airawat.shoppingapp.dto.OrderItemResponseDTO;
import com.airawat.shoppingapp.dto.OrderResponseDTO;
import com.airawat.shoppingapp.dto.OrderResponseDTO;

import com.airawat.shoppingapp.model.Inventory;
import com.airawat.shoppingapp.model.Notification;
import com.airawat.shoppingapp.model.Order;
import com.airawat.shoppingapp.model.OrderItem;
import com.airawat.shoppingapp.model.Product;
import com.airawat.shoppingapp.model.User;

import com.airawat.shoppingapp.exception.InsufficientStockException;
import com.airawat.shoppingapp.exception.ResourceNotFoundException;

import com.airawat.shoppingapp.repository.InventoryRepository;
import com.airawat.shoppingapp.repository.NotificationRepository;
import com.airawat.shoppingapp.repository.OrderRepository;
import com.airawat.shoppingapp.repository.ProductRepository;
import com.airawat.shoppingapp.repository.UserRepository;

import com.airawat.shoppingapp.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    public OrderServiceImpl(UserRepository userRepository,
                            ProductRepository productRepository,
                            InventoryRepository inventoryRepository,
                            OrderRepository orderRepository,
                            NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public OrderResponseDTO checkout(CheckoutRequestDTO requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDto.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus("PLACED");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CheckoutItemRequestDTO itemRequest : requestDto.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            Inventory inventory = inventoryRepository.findByProduct_ProductId(product.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + product.getProductId()));

            if (inventory.getAvailableQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - itemRequest.getQuantity());
            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());

            orderItems.add(orderItem);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            if (inventory.getAvailableQuantity() <= inventory.getReorderLevel()) {
                Notification lowStock = new Notification();
                lowStock.setNotificationType("LOW_STOCK");
                lowStock.setRecipientReference("ADMIN");
                lowStock.setMessage("Low stock alert for product: " + product.getProductName());
                lowStock.setStatus("SENT");
                lowStock.setUser(user);
                notificationRepository.save(lowStock);
            }
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        Notification checkoutNotification = new Notification();
        checkoutNotification.setNotificationType("CHECKOUT_CONFIRMATION");
        checkoutNotification.setRecipientReference(user.getEmail());
        checkoutNotification.setMessage("Order placed successfully. Order ID: " + savedOrder.getOrderId());
        checkoutNotification.setStatus("SENT");
        checkoutNotification.setUser(user);
        notificationRepository.save(checkoutNotification);

        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> itemDtos = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getUser().getFirstName() + " " + order.getUser().getLastName(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getOrderDate(),
                itemDtos
        );
    }
}