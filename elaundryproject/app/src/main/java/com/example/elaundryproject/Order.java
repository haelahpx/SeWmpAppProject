package com.example.elaundryproject;

public class Order {
    private String categoryName;
    private String orderDate;
    private String orderId;
    private String orderStatus;
    private String userId;
    private Long price; // Add price field
    private String shopId; // Add shopId field

    // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    public Order() { }

    // Parameterized constructor
    public Order(String orderId, String orderDate, String userId, String orderStatus,
                 String categoryName, Long price, String shopId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.categoryName = categoryName;
        this.price = price;
        this.shopId = shopId;
    }

    // Getters and Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getShopId() { return shopId; }
    public void setShopId(String shopId) { this.shopId = shopId; }
}
