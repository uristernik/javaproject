package com.example.ordermanagementservice.model;

import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Order Model
 * 
 * This class represents an order in the system and is used for:
 * - Displaying order information in the order history
 * - Managing order status and tracking
 * - Storing order details and items
 * 
 * In our microservices architecture:
 * - This model is used by the Order Management Service for order display
 * - It corresponds to the 'orders' table in the database
 * - It's populated from data retrieved from the Data Access Service
 * - It's used for both display and business logic
 */
public class Order {
    /**
     * Unique identifier for the order
     */
    private Long id;
    
    /**
     * ID of the user who placed the order
     */
    private Long userId;
    
    /**
     * Delivery address for the order
     */
    private String deliveryAddress;
    
    /**
     * Total price of the order in cents
     */
    private Integer totalPrice;
    
    /**
     * Date when the order was placed
     */
    private Date orderDate;
    
    /**
     * Current status of the order (e.g., "Processing", "Shipped", "Delivered")
     */
    private String status;
    
    /**
     * List of items in the order
     */
    private List<Map<String, Object>> items;

    /**
     * Default constructor
     * Required for object creation and mapping from database
     */
    public Order() {}

    /**
     * Gets the order ID
     * @return The unique identifier for the order
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order ID
     * @param id The unique identifier for the order
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user ID
     * @return The ID of the user who placed the order
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID
     * @param userId The ID of the user who placed the order
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the delivery address
     * @return The delivery address for the order
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Sets the delivery address
     * @param deliveryAddress The delivery address for the order
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Gets the total price
     * @return The total price of the order in cents
     */
    public Integer getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price
     * @param totalPrice The total price of the order in cents
     */
    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the order date
     * @return The date when the order was placed
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the order date
     * @param orderDate The date when the order was placed
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Gets the order status
     * @return The current status of the order
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the order status
     * @param status The current status of the order
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the order items
     * @return List of items in the order
     */
    public List<Map<String, Object>> getItems() {
        return items;
    }

    /**
     * Sets the order items
     * @param items List of items in the order
     */
    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
